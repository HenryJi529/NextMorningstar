<script setup lang="ts">
import {onMounted, ref, watch} from 'vue';
import {Graph, type GraphData} from '@antv/g6';
import axios from "@/axios";
import {setTheme} from "@/utils/handleTheme";
import {message} from "ant-design-vue";
import { Renderer as SVGRenderer } from '@antv/g-svg';

setTheme('light');

interface Person {
    id: string;
    born: number;
    name: string;
}

interface Movie {
    id: string;
    title: string;
    released: number;
    tagline: string;
}

interface Pair {
    personId: string;
    movieId: string;
    type: string;
}

interface RawGraphData {
    personList: Person[];
    movieList: Movie[];
    pairList: Pair[];
}

const person1Name = ref('');
const person2Name = ref('');
const select1Visible = ref(false);
const select2Visible = ref(false);
const person1 = ref<Person>();
const person2 = ref<Person>();
const rawGraphData = ref<RawGraphData>();
const processedGraphData = ref<GraphData>();
let graph: Graph;

const personList1 = ref<Person[]>([]);
const personList2 = ref<Person[]>([]);


const fetchPersonList = (name: string) => {
    return new Promise(resolve => {
        axios.get(`/api/practice/i1?name=${name}`).then(res=>{
            console.log(res.data.data);
            resolve(res.data.data);
        })
    })
}

const fetchPersonList1 = () => {
    fetchPersonList(person1Name.value).then(res=>{
        personList1.value = res as Person[];
    })
}

const fetchPersonList2 = () => {
    fetchPersonList(person2Name.value).then(res=>{
        personList2.value = res as Person[];
    })
}

const choosePerson1 = (person: Person) => {
    person1.value = person;
    person1Name.value = person.name;
    select1Visible.value = false;
}

const choosePerson2 = (person: Person) => {
    person2.value = person;
    person2Name.value = person.name;
    select2Visible.value = false;
}

const search = () => {
    if(!(person1.value && person2.value)){
        message.warn('请选择两位演员...');
        return;
    }
    if(person1.value?.id === person2.value?.id){
        message.warn('不能选择同一个人...');
        return;
    }
    axios({
        method: 'get',
        url: '/api/practice/i2',
        params: {
            person1Id: person1.value?.id,
            person2Id: person2.value?.id
        }
    }).then(res=> {
        if(res.data.data===null){
            message.warn('这两位没有交集...');
            return;
        }
        rawGraphData.value = res.data.data as RawGraphData;
        processedGraphData.value = processData(rawGraphData.value);
        console.log(processedGraphData.value)
        graph.setData(processedGraphData.value);
        graph.render();
    })
}

watch(person1Name, () => {
    fetchPersonList1();
});
watch(person2Name, () => {
    fetchPersonList2();
});

const processData = (rawGraphData: RawGraphData): GraphData => {
    const personNodes = rawGraphData.personList.map(person => {
        return {
            id: 'P' + person.id,
            data: {
                name: person.name,
            }
        }
    });
    const movieNodes = rawGraphData.movieList.map(movie => {
        return {
            id: 'M' + movie.id,
            data: {
                name: movie.title,
            }
        }
    });
    const edges = rawGraphData.pairList.map(pair => {
        return {
            id: 'P' + pair.personId + '-' + 'M' + pair.movieId,
            source: 'P' + pair.personId,
            target: 'M' + pair.movieId,
            data: {
                name: pair.type,
            }
        }
    });
    return {
        nodes: [...personNodes, ...movieNodes],
        edges: edges
    };
}

onMounted(() => {
    graph = new Graph({
        container: 'container',
        renderer: () => new SVGRenderer(),
        autoResize: true,
        autoFit: 'view',
        padding: 1,
        node: {
            type: 'circle',
            style: {
                size: [60, 60],
                radius: 6,
                lineWidth: 0,
                fill: datum => datum.id.startsWith('P') ? '#1890FF' : '#2FC25B',
                labelText: datum => datum?.data?.name as string,
                labelFontWeight: datum => datum.id === ('P' + person1.value?.id) || datum.id === ('P' + person2.value?.id) ? 'bold' : 'normal' ,
                labelFontStyle: datum => datum.id.startsWith('P') ? 'normal' : 'italic'
            },
        },
        edge: {
            style: {
                endArrow: true,
                labelText: (datum) => datum?.data?.name as string,

            },
        },
        behaviors: ['zoom-canvas', 'drag-element'],
        layout: {
            type: 'force',
            preventOverlap: true,
            link: {
                distance: 100
            },
        },
    });

});

</script>

<template>
    <div class="flex items-center justify-center space-x-10 mb-4 px-3">
        <div class="flex-1 flex items-center">
            <span class="person-field">演员1: </span>
            <div class="relative flex-1">
                <input type="text" v-model="person1Name" class="person-input" @change="fetchPersonList1" @focus="select1Visible=true">
                <div class="absolute w-full mt-1 z-10" v-show="select1Visible">
                    <template v-for="person in personList1.slice(0, 10)">
                        <div class="person-item" @click="choosePerson1(person)">{{ person.name }} ({{ person.born }})</div>
                    </template>
                </div>
            </div>
        </div>
        <div class="flex-1 flex items-center">
            <span class="person-field">演员2: </span>
            <div class="relative flex-1">
                <input type="text" v-model="person2Name" class="person-input" @change="fetchPersonList2" @focus="select2Visible=true">
                <div class="absolute w-full mt-1 z-10" v-show="select2Visible">
                    <template v-for="person in personList2.slice(0, 10)">
                        <div class="person-item" @click="choosePerson2(person)">{{ person.name }} ({{ person.born }})</div>
                    </template>
                </div>
            </div>
        </div>
        <div class="btn" @click="search">搜索</div>
    </div>
    <div class="flex-1 px-4 py-6 w-full flex">
        <div class="flex-1" id="container">
        </div>
    </div>

</template>

<style scoped lang="scss">
.person-item {
    @apply p-2 hover:bg-gray-200 cursor-pointer rounded-xl w-full indent-1;
}
.person-field {
    @apply text-lg font-bold mr-4;
}
.person-input {
    @apply bg-transparent px-3 p-2 rounded-lg w-full;
}
</style>



