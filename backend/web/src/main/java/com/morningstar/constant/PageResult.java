package com.morningstar.constant;

import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页工具类
 */
@Schema(description = "分页工具类")
@Data
public class PageResult<T> implements Serializable {
    /**
     * 总记录数
     */
    @Schema(description = "总记录数")
    private Integer totalRecordNum;

    /**
     * 总页数
     */
    @Schema(description = "总页数")
    private Integer totalPageNum;

    /**
     * 页记录数
     */
    @Schema(description = "页记录数")
    private Integer pageSize;

    /**
     * 当前页记录数
     */
    @Schema(description = "当前页记录数")
    private Integer currentPageSize;

    /**
     * 当前第几页(1+)
     */
    @Schema(description = "当前页码")
    private Integer pageNum;

    /**
     * 记录集合
     */
    @Schema(description = "记录集合")
    private List<T> records;

    /**
     * 分页数据组装
     */
    public PageResult(PageInfo<T> pageInfo) {
        loadPageInfo(pageInfo);
        records = pageInfo.getList();
    }

    public PageResult(List<T> records, int pageNum, int pageSize, long totalRecordNum) {
        this.totalRecordNum = (int) totalRecordNum;
        this.totalPageNum = (int) ((totalRecordNum % pageSize == 0) ? totalRecordNum / pageSize : totalRecordNum / pageSize + 1);
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.currentPageSize = records.size();
        this.records = records;
    }

    private void loadPageInfo(PageInfo<?> pageInfo) {
        totalRecordNum = (int) pageInfo.getTotal();
        totalPageNum = pageInfo.getPages();
        pageNum = pageInfo.getPageNum();
        pageSize = pageInfo.getPageSize();
        currentPageSize = pageInfo.getSize();
    }

    /**
     * 修正分页信息
     */
    public void fixPageInfo(PageInfo<Object> otherPageInfo) {
        loadPageInfo(otherPageInfo);
    }
}
