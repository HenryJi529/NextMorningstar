<script setup lang="ts">
import {GameMode, GameModeInfoMap} from "@/constants/GameConstant";
import {onMounted, ref} from "vue";
import axios from "axios";
import {API_KILL_RANK_SEASON} from "@/constants/ApiConstant";
import type {SeasonRank} from "@/types/kill";
import type {R} from "@/types/common";
import {TOKEN} from "@/constants/LocalStorageConstant";

const seasonRank = ref<SeasonRank>();

onMounted(async ()=> {
    const response: R<SeasonRank> = (
        await axios.get(
            API_KILL_RANK_SEASON,
            {
                headers: {
                    Authorization: localStorage.getItem(TOKEN),
                }
            }
        )
    ).data;
    seasonRank.value = response.data;
})

</script>

<template>
    <div class="w-[13rem] grid grid-flow-col grid-rows-6 lg:grid-rows-3 gap-4 border border-dashed rounded-lg p-2" style="font-family: monospace" v-if="seasonRank">
        <div class="flex justify-between items-center">
            <span>{{GameModeInfoMap[GameMode.IDENTITY].name}}:</span>
            <span>{{seasonRank.identity}}</span>
        </div>
        <div class="flex justify-between items-center">
            <span>{{GameModeInfoMap[GameMode.REVERT].name}}:</span>
            <span>{{seasonRank.revert}}</span>
        </div>
        <div class="flex justify-between items-center">
            <span>{{GameModeInfoMap[GameMode.NATION].name}}:</span>
            <span>{{seasonRank.nation}}</span>
        </div>
        <div class="flex justify-between items-center">
            <span>{{GameModeInfoMap[GameMode.SOLO].name}}:</span>
            <span>{{seasonRank.solo}}</span>
        </div>
        <div class="flex justify-between items-center">
            <span>{{GameModeInfoMap[GameMode.DOUBLES].name}}:</span>
            <span>{{seasonRank.doubles}}</span>
        </div>
        <div class="flex justify-between items-center">
            <span>{{GameModeInfoMap[GameMode.TRIPLES].name}}:</span>
            <span>{{seasonRank.triples}}</span>
        </div>
    </div>
</template>