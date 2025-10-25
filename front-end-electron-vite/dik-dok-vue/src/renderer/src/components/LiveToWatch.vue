<template>
    <div class="tiktok-container" ref="container" @touchstart="handleTouchStart" @touchmove="handleTouchMove"
        @touchend="handleTouchEnd" @wheel="handleWheel">
        <div class="videos-wrapper" :style="{ transform: `translateY(-${currentIndex * 100}vh)` }">
            <div v-for="(video, index) in videos" :key="video.id" class="video-item">
                <video-player autoplay="any" :width="VideoWidth" :height="VideoHeight" :src="video.url" playsinline ref="videoPlayers"
                    @loadeddata="onVideoLoaded"  @canplay="onVideoCanPlay" loop controls></video-player>
                <div class="video-back">
                    <ElButton class="back-button" @click="backSearchPlace"><ElIcon size="20"><Back/></ElIcon></ElButton>
                </div>
                <div class="video-info">
                    <div class="video-title">{{ video.title }}</div>
                    <div class="video-author">
                        <div class="author-avatar"></div>
                        @{{ video.author }}
                    </div>
                </div>
            </div>
        </div>

        <div class="controls">
        </div>
    </div>
</template>

<script lang="ts" setup>
import axios from 'axios';
import { ElButton, ElMessage, ElIcon } from 'element-plus';
import { send } from 'vite';
import { ref, onMounted, computed, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router';
import { Back } from '@element-plus/icons-vue';
import { useLiveDataStore } from '../store/LiveData';
import { VideoPlayer } from '@videojs-player/vue'
import 'video.js/dist/video-js.css'
import VideoJsPlayer from 'video.js';
import videojs from 'video.js';

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

// 视频尺寸
const VideoWidth = ref(1500);
const VideoHeight = ref(750);
// 视频数据
const route = useRoute();
const isSwiping = ref(false);
const startY = ref(0);
const currentY = ref(0);
const isLoading = ref(true);
const container = ref<HTMLElement | null>(null);
const videoPlayers = ref<HTMLVideoElement[]>([]);
const hasSwiped = ref(false); // 标记是否已经滑动过一次
const router = useRouter();
const liveDataStore = useLiveDataStore();
const videos = ref<Live[]>(liveDataStore.lives);
const currentIndex = ref(liveDataStore.currentIndex);

function backSearchPlace(){
    router.back();
}
// 处理触摸开始事件
const handleTouchStart = (e: TouchEvent) => {
    isSwiping.value = true;
    startY.value = e.touches[0].clientY;
    hasSwiped.value = false; // 重置滑动标记
};

// 处理触摸移动事件
const handleTouchMove = (e: TouchEvent) => {
    if (!isSwiping.value || hasSwiped.value) return;

    currentY.value = e.touches[0].clientY;
    const diff = currentY.value - startY.value;

    // 限制最大滑动距离
    if (Math.abs(diff) > 80) {
        isSwiping.value = false;
        hasSwiped.value = true; // 标记已经滑动过一次
        handleSwipe(diff > 0 ? 'down' : 'up');

        // 添加一个短暂延迟后重置滑动标记，允许再次滑动
        setTimeout(() => {
            hasSwiped.value = false;
        }, 300);
    }
};

// 处理触摸结束事件
const handleTouchEnd = () => {
    isSwiping.value = false;
    // 触摸结束后立即重置滑动标记，允许再次滑动
    hasSwiped.value = false;
};

// 处理滚轮事件
const handleWheel = (e: WheelEvent) => {
    if (hasSwiped.value) return; // 如果已经滑动过，不再处理

    if (e.deltaY > 50) {
        hasSwiped.value = true; // 标记已经滑动过一次
        handleSwipe('up');

        // 添加一个短暂延迟后重置滑动标记，允许再次滑动
        setTimeout(() => {
            hasSwiped.value = false;
        }, 300);
    } else if (e.deltaY < -50) {
        hasSwiped.value = true; // 标记已经滑动过一次
        handleSwipe('down');

        // 添加一个短暂延迟后重置滑动标记，允许再次滑动
        setTimeout(() => {
            hasSwiped.value = false;
        }, 300);
    }
};

// 处理滑动
const handleSwipe = (direction: string) => {
    if (direction === 'up') {
        // 向上滑动，播放下一个视频
        if (currentIndex.value < videos.value.length - 1) {
            pauseCurrentVideo();
            currentIndex.value++;
            playCurrentVideo();
        }
    } else if (direction === 'down') {
        // 向下滑动，播放上一个视频
        if (currentIndex.value > 0) {
            pauseCurrentVideo();
            currentIndex.value--;
            playCurrentVideo();
        }
    }
};

// 播放当前视频
const playCurrentVideo = () => {
    nextTick(() => {
        if (videoPlayers.value[currentIndex.value]) {
            videoPlayers.value[currentIndex.value].play().catch(e => {
                console.log("Autoplay prevented:", e);
            });
        }
    });
};

// 暂停当前视频
const pauseCurrentVideo = () => {
    if (videoPlayers.value[currentIndex.value]) {
        videoPlayers.value[currentIndex.value].pause();
    }
};

// 视频加载完成
const onVideoLoaded = () => {
    console.log("Video loaded");
};

// 视频可以播放
const onVideoCanPlay = () => {
    isLoading.value = false;
    // 自动播放当前视频
    playCurrentVideo();
};

onMounted(() => {
    // 设置容器高度
    if (container.value) {
        container.value.style.height = `${window.innerHeight}px`;
    }
    const routeVideos = route.query.videos;
    const currentIdx = route.query.currentIndex;

    if (routeVideos && currentIdx) {
        try {
            videos.value = JSON.parse(routeVideos as string);
            currentIndex.value = parseInt(currentIdx as string);
        } catch (e) {
            console.error('解析视频数据失败', e);
        }
    }


    // 监听窗口大小变化
    window.addEventListener('resize', () => {
        if (container.value) {
            container.value.style.height = `${window.innerHeight}px`;
        }
    });
});
</script>

<style scoped>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

.tiktok-container {
    position: relative;
    width: 100%;
    height: 120vh;
    overflow: hidden;
    background: rgb(22, 24, 35);
    /* border-radius: 20px; */
    /* margin-right: 65px; */
    /* margin-bottom: 100px; */
}

.videos-wrapper {
    position: relative;
    width: 100%;
    height: 100vh;
    transition: transform 0.3s ease-out;
}

.video-item {
    position: relative;
    width: 100%;
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background: rgb(22, 24, 35);
    margin-top: auto;
}

.video-item video {
    background-color: rgb(0, 0, 0);
    width: 200vh;
    height: 100%;
    margin-bottom: auto;
}

.video-info {
    position: absolute;
    bottom: 10px;
    left: 16px;
    right: 16px;
    z-index: 10;
}

.video-back {
    position: absolute;
    top: 10px;
    left: 16px;
    right: 16px;
    z-index: 10;
}

.video-title {
    font-size: 16px;
    font-weight: 500;
    margin-bottom: 8px;
    color: #fff;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

.video-author {
    display: flex;
    align-items: center;
    font-size: 14px;
    color: #fff;
    opacity: 0.9;
}

.author-avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    margin-right: 10px;
    background: linear-gradient(45deg, #ff0050, #ffd900);
}

.loading-indicator {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 20;
    width: 50px;
    height: 50px;
    border: 3px solid rgba(255, 255, 255, 0.3);
    border-radius: 50%;
    border-top-color: #fff;
    animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
    to {
        transform: translate(-50%, -50%) rotate(360deg);
    }
}

.controls {
    position: absolute;
    right: 16px;
    bottom: 10vh;
    display: flex;
    flex-direction: column;
    gap: 40px;
    z-index: 15;
}

.control-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    color: #fff;
    font-size: 12px;
}

.control-icon {
    width: 55px;
    height: 55px;
}

.control-icon-div {
    width: 60px;
    height: 60px;
}

.control-icon:hover {
    width: 60px;
    height: 60px;
}

.back-button {
    background-color: rgba(90, 90, 90, 0.3);
    border: 0;
    color: #fff;

}

.back-button:hover {
    background-color: rgba(90, 90, 90, 0.5);
    color: #fff;
}

/* 更通用的方法，针对所有浏览器 */
video::-webkit-media-controls,
video::-webkit-media-controls-enclosure {
    opacity: 1 !important;
    display: flex !important;
    width: inherit/2;
    height: 100%;
}
</style>