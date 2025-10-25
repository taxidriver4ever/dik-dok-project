<template>
    <div class="video-container">
        <div v-for="(live, index) in lives" class="video-item" :key="index" @click="watchTheLiving(index)">
            <ElImage style="width: 295px; height: auto;border-radius: 20px !important;" :src="live.coverUrl">
            </ElImage>
            <div style="color: white;">{{ live.title }}</div>
            <div style="color: white;font-size: 12px;">@{{ live.author }}</div>
        </div>
    </div>
    <div style="display: flex;margin-right: auto;font-size: 50px;" v-if="searchNothing">
        尚无人开播
    </div>
</template>

<script setup lang="ts">
import axios from 'axios';
import { ElImage, ElMain, ElMessage } from 'element-plus';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useLiveDataStore } from '../store/LiveData';

const liveDataStore = useLiveDataStore();
const javaUrl = "http://localhost:8080"
const searchNothing = ref(false)
const router = useRouter();
interface Live {
    id: number;
    url: string;
    title: string;
    author: string;
    coverUrl: string;
    createdTime: string;
    updatedTime: string;
    type: string;
}
const lives = ref<Live[]>([])
function watchTheLiving(index: number) {
    liveDataStore.currentIndex = index
    router.push({name: "LiveToWatch"})
}
function getLivingData() {
    axios({
        url: javaUrl + "/live/living-all-data",
        method: "POST",
        data: {

        }
    }).then(res => {
        if (res.data.code === 200) {
            lives.value = res.data.data
            liveDataStore.lives = lives.value;
        }
        else {
            searchNothing.value = true;
        }
    }).catch(e => {
        ElMessage.error(e)
    })
}
onMounted(() => {
    getLivingData();
})
</script>

<style lang="css" scoped>
/* 响应式设计：在小屏幕上调整图片大小 */
@media (max-width: 768px) {
    .video-item .el-image {
        width: 250px !important;
    }
}

@media (max-width: 480px) {
    .video-item .el-image {
        width: 200px !important;
    }

    :deep(::-webkit-scrollbar) {
        width: 6px;
    }
}

.video-container {
    padding: 20px;
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    justify-content: flex-start;
    margin-right: auto;
    margin-bottom: auto;
    max-width: 200%;
    overflow-y: auto;
    padding-right: 8px;
    scroll-behavior: smooth;
}
</style>