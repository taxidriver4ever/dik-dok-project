<template>
    <div class="tiktok-container" ref="container" @touchstart="handleTouchStart" @touchmove="handleTouchMove"
        @touchend="handleTouchEnd" @wheel="handleWheel">
        <div class="videos-wrapper" :style="{ transform: `translateY(-${currentIndex * 100}vh)` }">
            <div v-for="(video, index) in videos" :key="video.id" class="video-item">
                <video :width="VideoWidth" :height="VideoHeight" :src="video.url" playsinline ref="videoPlayers"
                    @loadeddata="onVideoLoaded" @canplay="onVideoCanPlay" loop controls></video>
                <div class="video-back">
                    <ElButton class="back-button" @click="backSearchPlace"><ElIcon size="20"><Back/></ElIcon></ElButton>
                </div>
                <div class="video-info">
                    <div style="color: gray;font-size: 14px;">{{ video.uploadDate }}</div>
                    <div class="video-title">{{ video.title }}</div>
                    <div class="video-author">
                        <div class="author-avatar"></div>
                        @{{ video.author }}
                    </div>
                </div>
            </div>
        </div>

        <div class="controls">
            <div class="control-btn">
                <div class="control-icon-div">
                    <img class="control-icon" v-if="isClickLike" @click="SendToLike"
                        src="/src/images/FillHeart.png"></img>
                    <img class="control-icon" v-else-if="!isClickLike" @click="SendToLike"
                        src="/src/images/BlankHeart.png"></img>
                </div>
                <div>{{ currentVideo?.likes }}</div>
            </div>
            <div class="control-btn">
                <div class="control-icon-div">
                    <img class="control-icon" src="/src/images/comment.png"></img>
                </div>
                <div>{{ currentVideo?.comments }}</div>
            </div>
            <div class="control-btn" style="margin-bottom: 70px;">
                <div class="control-icon-div">
                    <img class="control-icon" src="/src/images/share.png"></img>
                </div>
                <div>分享</div>
            </div>
        </div>

        <div class="loading-indicator" v-if="isLoading"></div>
    </div>
</template>

<script lang="ts" setup>
import axios from 'axios';
import { ElButton, ElMessage, ElIcon } from 'element-plus';
import { send } from 'vite';
import { ref, onMounted, computed, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router';
import { Back } from '@element-plus/icons-vue';

class Video {
    id: number;
    url: string;
    title: string;
    author: string;
    likes: number;
    comments: number;
    coverUrl: string;
    uploadDate: string;

    constructor(id: number, url: string, title: string, auther: string, likes: number, comments: number, coverUrl: string, uploadDate: string | number) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.author = auther;
        this.likes = likes;
        this.comments = comments;
        this.coverUrl = coverUrl;
        this.uploadDate = this.formatUploadDate(uploadDate);
    }

    // 添加日期格式化方法
    private formatUploadDate(timestamp: string | number): string {
        const date = new Date(Number(timestamp));
        
        // 返回更友好的格式
        return date.toLocaleString('zh-CN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        });
    }

    // 添加获取原始时间戳的方法（如果需要）
    getUploadTimestamp(): number {
        return new Date(this.uploadDate).getTime();
    }

    // 添加相对时间显示（如"2小时前"）
    getRelativeTime(): string {
        const now = new Date();
        const date = new Date(this.uploadDate);
        const diff = now.getTime() - date.getTime();
        
        const minutes = Math.floor(diff / 60000);
        const hours = Math.floor(diff / 3600000);
        const days = Math.floor(diff / 86400000);
        
        if (minutes < 1) return '刚刚';
        if (minutes < 60) return `${minutes}分钟前`;
        if (hours < 24) return `${hours}小时前`;
        if (days < 30) return `${days}天前`;
        
        return this.uploadDate; // 超过30天显示完整日期
    }
}  

// 视频尺寸
const VideoWidth = ref(1300);
const VideoHeight = ref(700);
const isClickLike = ref(false)
// 视频数据
const videos = ref<Video[]>([]);
const route = useRoute();
const currentIndex = ref(0);
const isSwiping = ref(false);
const startY = ref(0);
const currentY = ref(0);
const isLoading = ref(true);
const container = ref<HTMLElement | null>(null);
const videoPlayers = ref<HTMLVideoElement[]>([]);
const hasSwiped = ref(false); // 标记是否已经滑动过一次
const router = useRouter();

// 当前视频
const currentVideo = computed(() => {
    return videos.value.length > 0 ? videos.value[currentIndex.value] : null;
});

function backSearchPlace(){
    router.back();
}

function getLikeStatus() {
    axios({
        url: "http://localhost:8080/video/getLikeStatus",
        method: "POST",
        headers: {
            'name': localStorage.getItem("name"),
            "uuid": localStorage.getItem("uuid")
        },
        data: {
            name: localStorage.getItem("name"),
            url: videos.value[currentIndex.value].url
        }
    }).then(res => {
        if (res.data.code === 200) {
            isClickLike.value = res.data.data.status;
            videos.value[currentIndex.value].likes = res.data.data.likeCount;
        } else {
            ElMessage.warning("您尚未登录")
        }
    }).catch(e => {
        ElMessage.error(e)
    })
}

watch(currentIndex, (newValue) => {
    getLikeStatus();
    if (currentIndex.value + 1 === videos.value.length) {
    }
})

async function SendToLike() {
    if (isClickLike.value === false) {
        await axios({
            url: "http://localhost:8080/video/clickLike",
            method: "POST",
            headers: {
                'name': localStorage.getItem("name"),
                "uuid": localStorage.getItem("uuid")
            },
            data: {
                name: localStorage.getItem("name"),
                url: videos.value[currentIndex.value].url
            }
        }).then(res => {
            if (res.data.code === 200) {
                videos.value[currentIndex.value].likes++;
                isClickLike.value = true;
            } else {
                ElMessage.warning("您尚未登录")
            }
        }).catch(e => {
            ElMessage.error(e)
        })
    }
    else {
        await axios({
            url: "http://localhost:8080/video/cancelLike",
            method: "POST",
            headers: {
                'name': localStorage.getItem("name"),
                "uuid": localStorage.getItem("uuid")
            },
            data: {
                name: localStorage.getItem("name"),
                url: videos.value[currentIndex.value].url
            }
        }).then(res => {
            if (res.data.code === 200) {
                videos.value[currentIndex.value].likes--;
                isClickLike.value = false;
            } else {
                ElMessage.warning("您尚未登录")
            }
        }).catch(e => {
            ElMessage.error(e)
        })
    }
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
    getLikeStatus();


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