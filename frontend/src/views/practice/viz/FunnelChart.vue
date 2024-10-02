<script setup lang="ts">
import * as THREE from 'three';
// @ts-expect-error ThreeJS类型缺失
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls';
// @ts-expect-error ThreeJS类型缺失
import { CSS2DRenderer, CSS2DObject } from 'three/examples/jsm/renderers/CSS2DRenderer';
import { onMounted, ref, watch } from 'vue';

const containerRef = ref<HTMLDivElement>();

let scene: THREE.Scene;
let camera: THREE.PerspectiveCamera;
let renderer: THREE.WebGLRenderer;
let labelRenderer: CSS2DRenderer;
const data = ref<Datum[]>([]);

interface Datum {
    code: string;
    name: string;
    value: number;
}

const RISK_LEVEL_CODE_NAME_MAP: Record<string, string> = {
    '100101': '最低风险',
    '100201': '低风险',
    '100301': '中风险I',
    '100302': '中风险II',
    '100401': '高风险I',
    '100402': '高风险II',
    '100403': '高风险III',
    '100404': '高风险IV',
    '100501': '最高风险',
};

const createLabel = (content: string, size: number, color: string, position: THREE.Vector3) => {
    const div = document.createElement('div');
    div.textContent = content;
    div.style.color = color;
    div.style.fontSize = size + 'px';
    div.style.fontFamily = 'Times New Roman, Microsoft YaHei';
    div.style.pointerEvents = 'none';
    const label = new CSS2DObject(div);
    label.position.copy(position);
    label.userData.type = 'funnel-chart-label'; // 标记为漏斗图标签
    return label;
};

const createCylinder = (
    radiusTop: number,
    radiusBottom: number,
    height: number,
    color: string,
    positionY: number
) => {
    const cylinder = new THREE.Mesh(
        new THREE.CylinderGeometry(radiusTop, radiusBottom, height, 100, 100, true),
        new THREE.MeshBasicMaterial({
            color: color,
            side: THREE.DoubleSide,
            transparent: true,
            opacity: 0.9,
        })
    );
    cylinder.position.y = positionY;
    return cylinder;
};

const loadFunnelChart = (
    chartDom: HTMLDivElement,
    cameraZ: number = 2.5,
    cameraY: number = 0.2
) => {
    const animate = () => {
        requestAnimationFrame(animate);
        renderer.render(scene, camera);
        labelRenderer.render(scene, camera);
    };

    // 创建场景
    scene = new THREE.Scene();
    scene.background = null;
    // 创建相机
    camera = new THREE.PerspectiveCamera(
        45,
        chartDom.clientWidth / chartDom.clientHeight,
        0.1,
        1000
    );
    camera.position.z = cameraZ;
    camera.position.y = cameraY;
    camera.lookAt(0, 0, 0);
    // 创建渲染器
    renderer = new THREE.WebGLRenderer({
        alpha: true,
        antialias: true,
    });
    renderer.setSize(chartDom.clientWidth, chartDom.clientHeight);
    renderer.setClearColor(0x000000, 0);
    renderer.domElement.style.transform = 'translateY(10%)';
    if (chartDom.hasChildNodes()) {
        chartDom.innerHTML = '';
    }
    chartDom.appendChild(renderer.domElement);

    // 创建标签渲染器
    labelRenderer = new CSS2DRenderer();
    labelRenderer.setSize(chartDom.clientWidth, chartDom.clientHeight);
    labelRenderer.domElement.style.position = 'absolute';
    labelRenderer.domElement.style.top = '0px';
    labelRenderer.domElement.style.pointerEvents = 'none';
    labelRenderer.domElement.style.transform = 'translateY(10%)';
    chartDom.appendChild(labelRenderer.domElement);

    // 添加辅助坐标系与轨道控制器
    // const gridHelper = new THREE.GridHelper(10, 10,  0x0000ff, 0x000);
    // scene.add(gridHelper);
    new OrbitControls(camera, renderer.domElement);

    const colors = [
        'rgba(42, 90, 29, 1)',
        'rgba(81, 142, 69, 1)',
        'rgba(155, 161, 54, 1)',
        'rgba(165, 117, 41, 1)',
        'rgba(172, 79, 42, 1)',
        'rgba(176, 43, 32, 1)',
        'rgba(137, 21, 13, 1)',
        'rgba(92, 15, 10, 1)',
        'rgba(61, 10, 10, 1)',
    ];
    for (let i = 0; i < 9; i++) {
        // 添加物体
        const cylinder = createCylinder(
            1.3 - 0.1 * i,
            1.3 - 0.1 * (i + 1),
            0.1,
            colors[i],
            (4 - i) * 0.12
        );
        scene.add(cylinder);
    }
    animate();
    renderer.render(scene, camera);
    labelRenderer.render(scene, camera);
};

const updateFunnelChart = (chartDom: HTMLElement) => {
    removeLabelsByType('funnel-chart-label');

    const sizes = [0.04, 0.037, 0.035, 0.032, 0.03, 0.027, 0.025, 0.023, 0.02];
    const locations = [0.75, 0.5, 0.27, 0.09, -0.08, -0.24, -0.385, -0.51, -0.62];
    const sum = data.value.map(item => item.value).reduce((acc, cur) => acc + cur, 0);
    for (let i = 0; i < 9; i++) {
        // 添加标签
        const ratio =
            i < 4 ? (data.value[i].value / sum) * 100 : (data.value[i].value / sum) * 10000;
        const firstSignificantDigitPlace = Math.floor(Math.log10(ratio));
        let fraction;
        if (firstSignificantDigitPlace >= -2) {
            fraction = 2;
        } else if (firstSignificantDigitPlace < -2 && firstSignificantDigitPlace != -Infinity) {
            fraction = firstSignificantDigitPlace * -1;
        } else {
            fraction = 0;
        }
        const percent = (
            Math.round(ratio * Math.pow(10, fraction)) / Math.pow(10, fraction)
        ).toFixed(fraction);
        const name = RISK_LEVEL_CODE_NAME_MAP[Object.keys(RISK_LEVEL_CODE_NAME_MAP)[i]];
        const textContent = name + ' ' + percent + (i < 4 ? '%' : '‱');
        const label = createLabel(
            textContent,
            chartDom.offsetWidth * sizes[i],
            '#fff',
            new THREE.Vector3(0, locations[i], 0)
        );
        scene.add(label);
    }

    renderer.render(scene, camera);
    labelRenderer.render(scene, camera);
};

const removeLabelsByType = (type: string) => {
    const labelsToRemove: CSS2DObject[] = [];
    // 收集要删除的标签
    scene.traverse(object => {
        if (object instanceof CSS2DObject && object.userData.type === type) {
            labelsToRemove.push(object);
        }
    });
    // 执行删除
    labelsToRemove.forEach(label => {
        // 从父级移除
        if (label.parent) {
            label.parent.remove(label);
        }
        // 清理DOM元素
        if (label.element && label.element.parentNode) {
            label.element.parentNode.removeChild(label.element);
        }
    });
};

const fetchData = () => {
    setTimeout(() => {
        const data1: Datum[] = [];
        for (let i = 0; i < Object.keys(RISK_LEVEL_CODE_NAME_MAP).length; i++) {
            data1.push({
                name: RISK_LEVEL_CODE_NAME_MAP[Object.keys(RISK_LEVEL_CODE_NAME_MAP)[i]],
                code: Object.keys(RISK_LEVEL_CODE_NAME_MAP)[i],
                value: Math.floor(Math.random() * 1000),
            });
        }
        data.value = data1;
    }, 500);
};

watch(
    data,
    () => {
        updateFunnelChart(containerRef.value as HTMLDivElement);
    },
    {
        immediate: false,
    }
);

onMounted(() => {
    fetchData();

    const container = containerRef.value as HTMLDivElement;
    loadFunnelChart(container);
    new ResizeObserver(() => {
        const width = container.clientWidth;
        const height = container.clientHeight;

        renderer.setSize(width, height);
        labelRenderer.setSize(width, height);
        camera.aspect = width / height;
        camera.updateProjectionMatrix();
        if (data.value.length > 0) {
            updateFunnelChart(container);
        }
    }).observe(container);
});
</script>

<template>
    <div ref="containerRef" class="h-full w-full relative"></div>
</template>

<style scoped lang="scss"></style>
