<template>
    <ElContainer style="background-color: rgb(22, 24, 35); height: 100vh;" class="custom-scroll-container">
        <ElHeader style="padding: 0; margin: 0;">
            <ElButton class="back-button" @click="backToHome">
                <ElIcon size="25">
                    <Back />
                </ElIcon>
            </ElButton>
        </ElHeader>
        <ElMain style="display: flex; justify-content: center; padding: 0; margin: 0;">
            <!-- 添加一个容器来管理布局 -->
            <div style="margin-left: auto; display: flex; align-items: center; justify-content: center;font-size: 40px;color: rgb(32, 34, 45);"
                v-if="videosAreEmpty">
                搜索内容为空
            </div>
            <div class="video-container custom-scroll-content">
                <div v-for="(video, index) in videos" :key="index" class="video-item" @click="goToWatch(video, index)">
                    <ElImage style="width: 295px; height: auto;border-radius: 20px !important;" :src="video.coverUrl">
                    </ElImage>
                    <div style="color: white;">{{ video.title }}</div>
                    <div style="color: white;font-size: small;">@{{ video.author }}</div>
                </div>
            </div>
        </ElMain>
    </ElContainer>
</template>

<script lang="ts" setup>
import { ElButton, ElContainer, ElHeader, ElIcon, ElImage, ElMain, ElMessage } from 'element-plus';
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { Back } from '@element-plus/icons-vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const route = useRoute();
const searchKeyword = ref("");
const videos = ref<Video[]>([]);
const videosAreEmpty = ref(false);
const router = useRouter();
class Video {
    url: string;
    coverUrl: string;
    author: string;
    avatar: string;
    title: string;
    uploadDate: string;
    // view: number;
    likes: number;
    comments: number;
    constructor(url: string, coverUrl: string, author: string, avatar: string, title: string, uploadDate: string,
        likes: number, comments: number
    ) {
        this.url = url;
        this.coverUrl = coverUrl;
        this.author = author;
        this.avatar = avatar;
        this.title = title;
        this.uploadDate = formatUploadDate(uploadDate);
        // this.view = view;
        this.likes = likes;
        this.comments = comments;
    }

}
function formatUploadDate(timestamp: string | number): string {
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
function backToHome() { window.location.href = "/" }
async function getSearchVideo() {
    await axios({
        url: "http://127.0.0.1:8080/video/searchVideo",
        method: "POST",
        data: {
            name: localStorage.getItem("name") || "",
            keyword: searchKeyword.value
        }
    }).then(res => {
        if (res.data.code === 200) {
            for (let i = 0; i < res.data.data.length; i++) {
                videos.value.push(new Video(
                    res.data.data[i].url,
                    res.data.data[i].coverUrl,
                    res.data.data[i].author,
                    res.data.data[i].avatar,
                    res.data.data[i].title,
                    res.data.data[i].uploadDateMilli,
                    res.data.data[i].likeCount,
                    res.data.data[i].commentCount,
                ));
            }
            if (videos.value.length === 0) videosAreEmpty.value = true;
        }
        else ElMessage.warning("获取视频失败")
    }).catch(e => {
        console.log(e);
    })
}
// 点击视频跳转到观看页面
function goToWatch(video: Video, index: number) {
    // 只传递原始视频数据（去除类实例方法）
    const rawVideos = videos.value.map(v => ({
        url: v.url,
        coverUrl: v.coverUrl,
        author: v.author,
        avatar: v.avatar,
        title: v.title,
        uploadDate: v.uploadDate,
        likes: v.likes,
        comments: v.comments
    }));
    router.push({
        path: `/search-to-watch/${encodeURIComponent(searchKeyword.value)}`,
        query: {
            currentIndex: index.toString(),
            videos: JSON.stringify(rawVideos)
        }
    });
}
onMounted(() => {
    searchKeyword.value = decodeURIComponent(route.params.q.toString());
    getSearchVideo();
})
</script>

<style lang="css" scoped>
/* 全局滚动条样式 */
:deep(::-webkit-scrollbar) {
    width: 10px;
    height: 10px;
}

:deep(::-webkit-scrollbar-track) {
    background: rgba(0, 0, 0, 0.1);
    border-radius: 10px;
}

:deep(::-webkit-scrollbar-thumb) {
    background: rgba(255, 255, 255, 0.2);
    border-radius: 10px;
    border: 2px solid transparent;
    background-clip: padding-box;
}

:deep(::-webkit-scrollbar-thumb:hover) {
    background: rgba(255, 255, 255, 0.3);
    border: 2px solid transparent;
    background-clip: padding-box;
}

:deep(::-webkit-scrollbar-corner) {
    background: rgba(0, 0, 0, 0.1);
}

/* 火狐浏览器滚动条样式 */
:deep(html) {
    scrollbar-width: thin;
    scrollbar-color: rgba(255, 255, 255, 0.2) rgba(0, 0, 0, 0.1);
}

/* 视频容器滚动条样式 */
.video-container {
    padding: 20px;
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    justify-content: flex-start;
    margin-right: auto;
    max-width: 200%;
    overflow-y: auto;
    padding-right: 8px;
    scroll-behavior: smooth;
}

/* 自定义滚动条容器 */
.custom-scroll-container {
    overflow: auto;
}

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

.back-button {
    margin-top: 15px;
    color: white;
    background-color: rgba(0, 0, 0, 0);
    border: 0;
}

.back-button:hover {
    color: rgb(190, 190, 190);
    background-color: rgba(0, 0, 0, 0);
    border: 0;
}
</style>