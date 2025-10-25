<template>
    <ElContainer style="background-color: rgb(22, 24, 35); height: 100vh;" class="custom-scroll-container">
        <ElHeader style="padding: 0; margin: 0;">
            <ElButton class="back-button" @click="backToHome">
                <ElIcon size="25">
                    <Back />
                </ElIcon>
            </ElButton>
        </ElHeader>
        <ElHeader style="padding: 0;margin: 0;height: 5vh;">
            <ElButton @click="showSearchVideo" :class="{
                'video-button': true,
                'active': currentSelected === 'video'
            }">
                视频
            </ElButton>
            <ElButton @click="showSearchUser" :class="{
                'user-button': true,
                'active': currentSelected === 'user'
            }">
                用户
            </ElButton>
            <ElButton @click="showSearchLive" :class="{
                'live-button': true,
                'active': currentSelected === 'live'
            }">
                直播
            </ElButton>
        </ElHeader>
        <ElMain style="display: flex; justify-content: center; padding: 0; margin: 0;"
            v-if="currentSelected === 'video'">
            <!-- 添加一个容器来管理布局 -->
            <div style="margin-left: auto; display: flex; align-items: center; justify-content: center;font-size: 40px;color: rgb(32, 34, 45);"
                v-if="videosAreEmpty">
                搜索内容为空
            </div>
            <div class="video-container custom-scroll-content">
                <div v-for="(video, index) in videos" :key="index" class="video-item" @click="goToWatch(video, index)">
                    <ElImage style="width: 36vh; height: auto;border-radius: 20px !important;" :src="video.coverUrl">
                    </ElImage>
                    <div style="color: white;">{{ video.title }}</div>
                    <div style="color: white;font-size: small;">@{{ video.author }}</div>
                </div>
            </div>
        </ElMain>
        <ElMain style="display: flex; padding: 0; margin: 0;flex-direction: column;"
            v-else-if="currentSelected === 'user'">
            <!-- 添加一个容器来管理布局 -->
            <div style="margin-left: auto; display: flex; align-items: center; justify-content: center;font-size: 40px;color: rgb(32, 34, 45);"
                v-if="videosAreEmpty">
                搜索内容为空
            </div>
            <div v-for="(user, index) in users" :key="index" style="color: white;display: flex;margin-right: auto;flex-direction: column;
            margin-bottom: 20px;">
                <div style="display: flex;flex-direction: column;margin-left: 20px;">
                    <div style="display: flex;margin-right: auto;">{{ user.userName }}</div>
                    <!-- <div style="display: flex;">{{ user.userEmail }}</div> -->
                </div>
                <div style="display: flex;margin-left: auto;">
                    <ElButton type="danger" @click="subscribeUser(user.userName, index)" v-if="!user.status">关注</ElButton>
                    <ElButton type="info" @click="cancelSubscribeUser(user.userName,index)" v-else>取消关注</ElButton>
                </div>
            </div>
        </ElMain>
        <!-- <ElMain style="display: flex; justify-content: center; padding: 0; margin: 0;" v-if="currentSelected === 'video'">
            <div style="margin-left: auto; display: flex; align-items: center; justify-content: center;font-size: 40px;color: rgb(32, 34, 45);"
                v-if="videosAreEmpty">
                搜索内容为空
            </div>
            <div class="video-container custom-scroll-content">
                <div v-for="(live, index) in lives" :key="index" class="video-item" @click="goToWatchLive(live, index)">
                    <ElImage style="width: 36vh; height: auto;border-radius: 20px !important;" :src="live.coverUrl">
                    </ElImage>
                    <div style="color: white;">{{ live.title }}</div>
                    <div style="color: white;font-size: small;">@{{ live.author }}</div>
                </div>
            </div>
        </ElMain> -->
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
const lives = ref<Live[]>([]);
const users = ref<User[]>([]);
const videosAreEmpty = ref(false);
const router = useRouter();
const currentSelected = ref("video");
interface Video {
    url: string;
    coverUrl: string;
    author: string;
    avatar: string;
    title: string;
    uploadDate: string;
    // view: number;
    likes: number;
    comments: number;
}
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
interface User {
    id: string;
    userName: string;
    userPassword: string;
    userEmail: string;
    avatar: string;
    createdTimeMilli: string;
    updatedTimeMilli: string;
    status: boolean;
}
function showSearchVideo() {
    currentSelected.value = 'video'
    // getSearchVideo();
}
function showSearchUser() {
    currentSelected.value = 'user'
    getSearchUser();
}
function showSearchLive() {
    currentSelected.value = 'live'
    // getSearchLive();
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
        url: "http://127.0.0.1:8080/video/data",
        method: "POST",
        data: {
            name: localStorage.getItem("name") || "",
            keyword: searchKeyword.value
        }
    }).then(res => {
        if (res.data.code === 200) {
            for (let i = 0; i < res.data.data.length; i++) {
                videos.value.push({
                    url: res.data.data[i].url,
                    coverUrl: res.data.data[i].coverUrl,
                    author: res.data.data[i].author,
                    avatar: res.data.data[i].avatar,
                    title: res.data.data[i].title,
                    uploadDate: res.data.data[i].uploadDateMilli,
                    likes: res.data.data[i].likeCount,
                    comments: res.data.data[i].commentCount,
                });
            }
            if (videos.value.length === 0) videosAreEmpty.value = true;
        }
        else ElMessage.warning("获取视频失败")
    }).catch(e => {
        console.log(e);
    })
}
async function getSearchUser() {
    await axios({
        url: "http://127.0.0.1:8080/user/data",
        method: "POST",
        headers: {
            'name': localStorage.getItem("name"),
            "uuid": localStorage.getItem("uuid")
        },
        data: {
            name: localStorage.getItem("name") || "",
            keyword: searchKeyword.value
        }
    }).then(res => {
        if (res.data.code === 200) {
            users.value = res.data.data
            if (users.value.length === 0) {
                videosAreEmpty.value = true;
            }
        }
    }).catch(e => {
        console.log(e);
    })
}
async function subscribeUser(subscribeUserName: string, index: number) {
    await axios({
        url: "http://127.0.0.1:8080/user/subscription",
        method: "POST",
        headers: {
            'name': localStorage.getItem("name"),
            "uuid": localStorage.getItem("uuid")
        },
        params: {
            userName: localStorage.getItem("name") || "",
            subscribeUserName: subscribeUserName
        }
    }).then(res => {
        if (res.data.code === 200) {
            users.value[index].status = true;
        }
    }).catch(e => {
        console.log(e);
    })
}
async function cancelSubscribeUser(subscribeUserName: string, index: number) {
    await axios({
        url: "http://127.0.0.1:8080/user/cancel-subscription",
        method: "POST",
        headers: {
            'name': localStorage.getItem("name"),
            "uuid": localStorage.getItem("uuid")
        },
        params: {
            userName: localStorage.getItem("name") || "",
            subscribeUserName: subscribeUserName
        }
    }).then(res => {
        if (res.data.code === 200) 
            users.value[index].status = false;
        else ElMessage.warning("删除失败")
    }).catch(e => {
        console.log(e);
    })
}
async function getSearchLive() {
    await axios({
        url: "http://127.0.0.1:8080/live/data",
        method: "POST",
        data: {
            name: localStorage.getItem("name") || "",
            keyword: searchKeyword.value
        }
    }).then(res => {
        if (res.data.code === 200) {
            lives.value = res.data.data;
            if (lives.value.length === 0) videosAreEmpty.value = true;
        }
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
        uploadDate: formatUploadDate(v.uploadDate),
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
    margin-bottom: auto;
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

.video-button {
    margin-left: 20px;
    background-color: rgba(0, 0, 0, 0);
    color: rgb(201, 201, 201);
    border: 0;
    font-size: large;
}

.video-button.active {
    color: red;
}

.video-button:hover {
    background-color: rgba(0, 0, 0, 0);
    color: white;
}

.user-button {
    background-color: rgba(0, 0, 0, 0);
    color: rgb(201, 201, 201);
    border: 0;
    font-size: large;
}

.user-button.active {
    color: red;
}

.user-button:hover {
    background-color: rgba(0, 0, 0, 0);
    color: white;
}

.live-button {
    background-color: rgba(0, 0, 0, 0);
    color: rgb(201, 201, 201);
    font-size: large;
    border: 0;
}

.live-button.active {
    color: red;
}

.live-button:hover {
    background-color: rgba(0, 0, 0, 0);
    color: white;
}
</style>