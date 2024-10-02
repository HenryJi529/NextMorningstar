<script lang="ts" setup>
import ExcelJS, {Workbook} from 'exceljs';
import * as XLSX from 'xlsx';
// @ts-ignore
import download from "downloadjs";
import {cloneDeep} from 'lodash';
import {setTheme} from "@/utils/handleTheme";

setTheme("light");

/**
 1. 规范的表格，不会出现一整行都被合并的情况
 2. 一般不会出现rowspan与colspan都大于1的情况
 */

interface Cell {
    value: string;
    rowspan: number;
    colspan: number;
}

function columnToLetter(column: number): string {
    // column 从 1 开始
    let temp, letter = '';
    while (column > 0) {
        temp = (column - 1) % 26;
        letter = String.fromCharCode(temp + 65) + letter;
        column = (column - temp - 1) / 26;
    }
    return letter;
}

const convertHtmlTableToExcelWorkbook = (tableElement: HTMLTableElement) => {
    const workbook = new ExcelJS.Workbook();
    const worksheet = workbook.addWorksheet('Sheet1');
    // 读取已合并的表格
    const mergedCells: Cell[][] = [];
    tableElement.querySelectorAll('tr').forEach((rowElement, rowIndex) => {
        mergedCells[rowIndex] = [];
        rowElement.querySelectorAll('td, th').forEach((cellElement) => {
            const cell: Cell = {
                value: cellElement.textContent?.trim() || '',
                rowspan: parseInt(cellElement.getAttribute('rowspan') || '1'),
                colspan: parseInt(cellElement.getAttribute('colspan') || '1'),
            }
            mergedCells[rowIndex].push(cell);
        })
    })
    console.log(mergedCells);
    // 获取未合并的表格
    const rawCells = cloneDeep(mergedCells);
    let row = 0;
    while (row < rawCells.length) {
        let col = 0;
        while (col < rawCells[row].length) {
            const currentCell = rawCells[row][col];
            if (currentCell.colspan > 1) {
                rawCells[row].splice(col + 1, 0, ...Array(currentCell.colspan - 1).fill({
                    value: '',
                    rowspan: 1,
                    colspan: 1
                }))
            }
            if (currentCell.rowspan > 1) {
                for (let i = 1; i < currentCell.rowspan; i++) {
                    rawCells[row + i].splice(col, 0, {
                        value: '',
                        colspan: 1,
                        rowspan: 1
                    })
                }
            }
            col += currentCell.colspan;
        }
        row++;
    }
    console.log(rawCells);
    // 将单元格的值填入工作簿
    rawCells.forEach((rowCells: Cell[]) => {
        worksheet.addRow(rowCells.map((cell: Cell) => cell.value));
    })
    // 合并单元格
    for (let row = 0; row < rawCells.length; row++) {
        let col = 0;
        while (col < rawCells[row].length) {
            const cell = rawCells[row][col];
            if (cell.colspan > 1 || cell.rowspan > 1) {
                // worksheet.mergeCells(row, col, row + cell.rowspan-1, col + cell.colspan-1); NOTE: 存在bug
                worksheet.mergeCells(columnToLetter(col + 1) + (row + 1), columnToLetter(col + cell.colspan) + (row + cell.rowspan))
            }
            col += cell.colspan;
        }
    }
    // 设置单元格样式
    worksheet.eachRow(row => {
        row.eachCell(cell => {
            cell.alignment = {
                horizontal: 'center',
                vertical: 'middle',
            };
            cell.border = {
                top: {style: 'thin'},
                bottom: {style: 'thin'},
                left: {style: 'thin'},
                right: {style: 'thin'},
            }
        })
    })

    return workbook;

}

const downloadExcelWorkbook = async (workbook: Workbook, filename: string) => {
    const buffer = await workbook.xlsx.writeBuffer();
    const mimeType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
    const blob = new Blob([buffer], {type: mimeType});

    download(blob, filename + '.xlsx', mimeType)
}


const exportTableMine = () => {
    const tableElement = document.getElementById('selectedTable') as HTMLTableElement;
    const workbook = convertHtmlTableToExcelWorkbook(tableElement);
    downloadExcelWorkbook(workbook, "exportedTable1");
}


const exportTableWithSheetJS = () => {
    const tableElement = document.getElementById('selectedTable') as HTMLTableElement;
    const wb = XLSX.utils.table_to_book(tableElement);
    // 导出为Excel文件
    XLSX.writeFile(wb, 'exportedTable2.xlsx');
}


</script>

<template>
    <div class="w-full">
        <div class="overflow-scroll">
            <table id="selectedTable" class="simple-table">
                <caption class="text-xl">公司2023年部门业绩报表</caption>
                <thead>
                <tr>
                    <th rowspan="2">部门</th>
                    <th colspan="3">第一季度</th>
                    <th colspan="3">第二季度</th>
                    <th colspan="3">第三季度</th>
                    <th colspan="3">第四季度</th>
                    <th rowspan="2">年度总计</th>
                </tr>
                <tr>
                    <th>收入</th>
                    <th>支出</th>
                    <th>利润</th>
                    <th>收入</th>
                    <th>支出</th>
                    <th>利润</th>
                    <th>收入</th>
                    <th>支出</th>
                    <th>利润</th>
                    <th>收入</th>
                    <th>支出</th>
                    <th>利润</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>销售部</td>
                    <td>1,200</td>
                    <td>800</td>
                    <td>400</td>
                    <td>1,500</td>
                    <td>900</td>
                    <td>600</td>
                    <td>1,800</td>
                    <td>1,000</td>
                    <td>800</td>
                    <td>2,000</td>
                    <td>1,100</td>
                    <td>900</td>
                    <td>2,700</td>
                </tr>
                <tr>
                    <td>市场部</td>
                    <td>800</td>
                    <td>1,000</td>
                    <td>-200</td>
                    <td>900</td>
                    <td>1,100</td>
                    <td>-200</td>
                    <td>1,000</td>
                    <td>1,200</td>
                    <td>-200</td>
                    <td>1,200</td>
                    <td>1,300</td>
                    <td>-100</td>
                    <td>-700</td>
                </tr>
                <tr>
                    <td>研发部</td>
                    <td>500</td>
                    <td>1,500</td>
                    <td>-1,000</td>
                    <td>600</td>
                    <td>1,600</td>
                    <td>-1,000</td>
                    <td>700</td>
                    <td>1,700</td>
                    <td>-1,000</td>
                    <td>800</td>
                    <td>1,800</td>
                    <td>-1,000</td>
                    <td>-4,000</td>
                </tr>
                <tr>
                    <td>行政部</td>
                    <td>300</td>
                    <td>1,200</td>
                    <td>-900</td>
                    <td>300</td>
                    <td>1,200</td>
                    <td>-900</td>
                    <td>300</td>
                    <td>1,200</td>
                    <td>-900</td>
                    <td>300</td>
                    <td>1,200</td>
                    <td>-900</td>
                    <td>-3,600</td>
                </tr>
                </tbody>
                <tfoot>
                <tr>
                    <td>公司总计</td>
                    <td>2,800</td>
                    <td>4,500</td>
                    <td>-1,700</td>
                    <td>3,300</td>
                    <td>4,800</td>
                    <td>-1,500</td>
                    <td>3,800</td>
                    <td>5,100</td>
                    <td>-1,300</td>
                    <td>4,300</td>
                    <td>5,400</td>
                    <td>-1,100</td>
                    <td>-5,600</td>
                </tr>
                <tr>
                    <td colspan="14" style="text-align: left; font-style: italic;">
                        注：所有金额单位为万元人民币；利润=收入-支出；研发部数据包含长期项目投入。
                    </td>
                </tr>
                </tfoot>
            </table>
        </div>
        <div class="flex justify-center items-center text-2xl">
            <div class="m-10 flex justify-center items-center">
                <span>
                    <font-awesome-icon :icon="['fas', 'download']"
                                       class="cursor-pointer mr-1"
                                       @click="exportTableMine"/>
                    手动
                </span>
            </div>
            <div class="m-10 flex justify-center items-center">
                <span>
                    <font-awesome-icon :icon="['fas', 'download']"
                                       class="cursor-pointer mr-1"
                                       @click="exportTableWithSheetJS"/>
                    调库
                </span>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.simple-table {
    text-align: center;
    width: 100%;

    td, th {
        padding: 2px 4px;
        border: 1px solid black;
    }

    th {
        @apply font-bold text-gray-600;
    }
}
</style>