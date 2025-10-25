<template>
    <div style="background-color: rgb(22, 24, 35);height: 100vh;">
        <ElContainer>
            <ElHeader style="margin: 0;padding: 0; display: flex; flex-direction: row;">
                <div style="display: flex; height: 50px; margin-left: 15px;">
                    <ElImage src="/src/images/Logo.png" style="width: 60px;"></ElImage>
                </div>
                <div style="display: flex; font-size: 18px;margin-top: 15px; font-weight: 700;color: white;">
                    抖阳
                </div>
                <div style="display: flex; margin: auto;">
                    <div class="search-wrapper" ref="searchWrapper">
                        <ElInput class="search-input" placeholder="搜索你感兴趣的内容" v-model="searchText"
                            @input="handleSearchInput" @keyup.enter="performSearch" @focus="showSuggestions = true"
                            @blur="handleInputBlur" clearable ref="searchInput">
                            <template #suffix>
                                <ElIcon v-if="searchLoading" class="is-loading">
                                    <Loading />
                                </ElIcon>
                            </template>
                        </ElInput>
                        <ElButton class="search-button" @click="performSearch">
                            <ElIcon>
                                <Search />
                            </ElIcon>
                            搜索
                        </ElButton>

                        <!-- 搜索建议下拉框 -->
                        <div v-if="showSuggestions && searchResults.length > 0" class="suggestions-dropdown">
                            <div v-for="(result, index) in searchResults" :key="index" class="suggestion-item"
                                :class="{ 'suggestion-active': activeSuggestionIndex === index }"
                                @mousedown="selectSuggestion(result)" @mouseenter="activeSuggestionIndex = index">
                                <ElIcon style="margin-right: 8px; color: #666;">
                                    <Search />
                                </ElIcon>
                                <span>{{ result }}</span>
                            </div>
                        </div>
                    </div>
                </div>
                <div style="display: flex; margin-left: auto; align-items: center; margin-right: 20px;">
                    <el-popover width="320" title="互动消息" placement="top-end" v-if="isLogged">
                        <template #reference>
                            <ElButton type="text" class="contribution-button" :icon="AlarmClock">
                                通知
                            </ElButton>
                        </template>
                        <div
                            style="display: flex;width: 300px;padding: 0;;max-height: 500px;overflow-y: auto;flex-direction: column;">
                            <div>

                            </div>
                        </div>
                    </el-popover>
                    <ElButton type="text" class="contribution-button" :icon="Message" v-if="isLogged">
                        私信
                    </ElButton>
                    <ElButton type="text" class="contribution-button" @click="ToOpenLive" :icon="VideoCameraFilled">
                        开直播
                    </ElButton>
                    <ElButton type="text" class="contribution-button" @click="ToFrameExtraction" :icon="Plus">
                        投稿
                    </ElButton>
                    <ElButton type="danger" @click="loginVisible = true" :circle="true" v-if="!isLogged"
                        style="margin-left: 20px;">
                        登录
                    </ElButton>
                    <ElButton type="text" class="contribution-button" @click="UserLogout" v-else-if="isLogged"
                        :icon="Back">
                        退出登录
                    </ElButton>
                    <ElDialog v-model="loginVisible"
                        style="background-image: url('/src/images/bg.png');background-size: 500px;background-repeat: no-repeat; display: flex; justify-content: center; height: 50vh; border-radius: 30px; width: 34%;">
                        <ElContainer style="display: flex; justify-content: center;">
                            <ElHeader style="display: flex; justify-content: center;margin-right: 10px;">
                                <h2 style="color: black;">登录后可享更多权益</h2>
                            </ElHeader>
                            <ElMain style="display: flex;flex-direction: column;margin-right: 25px;">
                                <ElHeader style="display: flex; flex-direction: row; justify-content: center;">
                                    <ElButton @click="codeLogin = true; passwordLogin = false" v-model="codeLogin"
                                        type="text">验证码登录
                                    </ElButton>
                                    <ElDivider direction="vertical"
                                        style="height: 30px;border-color: black;margin:0 40px;"></ElDivider>
                                    <ElButton @click="passwordLogin = true; codeLogin = false" v-model="passwordLogin"
                                        type="text">密码登录
                                    </ElButton>
                                </ElHeader>
                                <ElMain style="display: flex; justify-content:center; margin: 0; padding: 0;">
                                    <div v-if="codeLogin" style="justify-content: center;">
                                        <ElInput class="login-input" placeholder="请输入你的邮箱号码" v-model="userEmailByCode">
                                        </ElInput>
                                        <div style="display: flex; margin-top: 30px;">
                                            <ElInput class="login-input" v-model="userCodeByCode" placeholder="请输入验证码">
                                            </ElInput>
                                            <ElButton style="margin-left: 20px; height: 40px; border-radius: 10px;"
                                                @click="SendOTP" :disabled="!ableToSendOTP || countdown > 0"
                                                type="warning">
                                                {{ countdown > 0 ? `${countdown}秒后重试` : '发送验证码' }}
                                            </ElButton>
                                        </div>
                                        <div style="display: flex; justify-content: center; margin-top: 40px;"
                                            @click="LoginOrRegister">
                                            <ElButton type="danger"
                                                style="width: 200px;height: 35px;border-radius: 10px; font-size: 15px;"
                                                :disabled="!ableToLoginByCode">登录/注册</ElButton>
                                        </div>
                                    </div>
                                    <div v-else-if="passwordLogin" style="justify-content: center;">
                                        <ElInput class="login-input" placeholder="请输入你的邮箱号码"
                                            style="display: flex; width: 300px;" v-model="userEmailByPassword">

                                        </ElInput>
                                        <ElInput type="password" class="login-input" placeholder="请输入密码"
                                            style="display: flex; margin-top: 30px;" v-model="userPasswordByPassword"
                                            show-password>

                                        </ElInput>
                                        <div style="display: flex; justify-content: center; margin-top: 40px;">
                                            <ElButton type="danger" @click="LoginByPassword"
                                                :disabled="!ableToLoginByPassword"
                                                style="width: 200px;height: 35px;border-radius: 10px; font-size: 15px;">
                                                登录</ElButton>
                                        </div>
                                    </div>
                                </ElMain>
                            </ElMain>
                        </ElContainer>
                    </ElDialog>
                </div>
            </ElHeader>
            <ElContainer style="padding: 0;margin: 0;">
                <ElAside style="height: 100vh;width: 17vh;display: flex; flex-direction: column;">
                    <div style="margin-top: 15px; display: flex; justify-content: center;">
                        <ElButton type="primary" class="tool-button">
                            <ElIcon size="20" style="margin-right: 10px;">
                                <Pointer />
                            </ElIcon>精选
                        </ElButton>
                    </div>
                    <div style="display: flex; justify-content: center;" @click="ClickToRecommend">
                        <ElButton type="primary" class="tool-button">
                            <ElIcon size="20" style="margin-right: 10px;">
                                <StarFilled />
                            </ElIcon>推荐
                            <div @click="RefreshMyWindow" class="refresh-button"
                                style="display: flex; margin-left: 10px;">
                                <ElIcon size="20">
                                    <Refresh />
                                </ElIcon>
                            </div>
                        </ElButton>
                    </div>
                    <div style="display: flex; justify-content: center;">
                        <ElButton type="primary" class="tool-button">
                            <ElIcon size="20" style="margin-right: 10px;">
                                <Histogram />
                            </ElIcon>AI抖阳
                        </ElButton>
                    </div>
                    <ElDivider style="padding: 0;margin: 12px; width: 80%; border-color: rgb(32, 34, 45);"></ElDivider>
                    <div style="display: flex; justify-content: center;">
                        <ElButton type="primary" class="tool-button">
                            <ElIcon size="20" style="margin-right: 10px;">
                                <Avatar />
                            </ElIcon>关注
                        </ElButton>
                    </div>
                    <div style="display: flex; justify-content: center;">
                        <ElButton type="primary" class="tool-button">
                            <ElIcon size="20" style="margin-right: 10px;">
                                <Promotion />
                            </ElIcon>朋友
                        </ElButton>
                    </div>
                    <div style="display: flex; justify-content: center;">
                        <ElButton type="primary" class="tool-button">
                            <ElIcon size="20" style="margin-right: 10px;">
                                <User />
                            </ElIcon>我的
                        </ElButton>
                    </div>
                    <ElDivider style="padding: 0;margin: 12px; width: 80%; border-color: rgb(32, 34, 45);"></ElDivider>
                    <div style="display: flex; justify-content: center;">
                        <ElButton type="primary" class="tool-button">
                            <ElIcon size="20" style="margin-right: 10px;">
                                <DataBoard />
                            </ElIcon>AI开学季
                        </ElButton>
                    </div>
                    <div style="display: flex; justify-content: center;" @click="watchLive">
                        <ElButton type="primary" class="tool-button">
                            <ElIcon size="20" style="margin-right: 10px;">
                                <VideoPlay />
                            </ElIcon>直播
                        </ElButton>
                    </div>
                    <div style="display: flex; justify-content: center;">
                        <ElButton type="primary" class="tool-button">
                            <ElIcon size="20" style="margin-right: 10px;">
                                <Monitor />
                            </ElIcon>放映厅
                        </ElButton>
                    </div>
                    <div style="display: flex; justify-content: center;">
                        <ElButton type="primary" class="tool-button">
                            <ElIcon size="20" style="margin-right: 10px;">
                                <VideoCamera />
                            </ElIcon>短剧
                        </ElButton>
                    </div>
                </ElAside>
                <ElMain
                    style="display: flex;align-items: center; justify-content: center; height: 100vh; margin: 0; padding: 0;">
                    <RouterView></RouterView>
                </ElMain>
            </ElContainer>
        </ElContainer>
    </div>
</template>

<script setup>
import { ElAside, ElButton, ElContainer, ElDialog, ElMessageBox, ElDivider, ElHeader, ElIcon, ElImage, ElInput, ElMain, ElMessage, ElPopover } from 'element-plus';
import { Avatar, VideoCameraFilled, Plus, Back, DataBoard, Refresh, Histogram, Loading, Lock, Monitor, Pointer, Promotion, Search, StarFilled, User, VideoCamera, VideoPlay, AlarmClock, Message } from '@element-plus/icons-vue';
import { ref, watch, toRefs, computed, onUnmounted, onMounted, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { debounce } from 'lodash-es';

const isLogged = ref(false)
const router = useRouter();
const loginVisible = ref(false);
const codeLogin = ref(true)
const passwordLogin = ref(false)
const userCodeByCode = ref("")
const userEmailByCode = ref("")
const userEmailByPassword = ref("")
const userPasswordByPassword = ref("")
const countdown = ref(0)
let countdownTimer = null
const searchText = ref('')
const searchLoading = ref(false)
const searchResults = ref([]) // 搜索结果列表
const showSuggestions = ref(false) // 是否显示建议框
const activeSuggestionIndex = ref(-1) // 当前激活的建议项索引
const searchWrapper = ref(null) // 搜索容器引用
const searchInput = ref(null) // 输入框引用


const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));

// 提取常量
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

// 使用计算属性
const ableToSendOTP = computed(() =>
    emailRegex.test(userEmailByCode.value)
);

const ableToLoginByCode = computed(() =>
    emailRegex.test(userEmailByCode.value) && userCodeByCode.value !== ""
);

const ableToLoginByPassword = computed(() =>
    emailRegex.test(userEmailByPassword.value) && userPasswordByPassword.value !== ""
);

// 防抖搜索函数
const debouncedSearch = debounce(async (searchValue) => {
    if (searchValue.trim() === '') {
        resetSearch();
        return;
    }

    searchLoading.value = true;
    try {
        const results = await performSearchRequest(searchValue);
        searchResults.value = results;
        showSuggestions.value = true;
        activeSuggestionIndex.value = -1;
    } catch (error) {
        console.error('搜索失败:', error);
        ElMessage.error('搜索失败，请重试');
        searchResults.value = [];
    } finally {
        searchLoading.value = false;
    }
}, 500);

async function watchLive() {
    router.push("/live")
}
// 搜索请求函数
async function performSearchRequest(searchValue) {
    try {
        const response = await axios({
            url: "http://127.0.0.1:8080/video/title",
            method: "POST",
            data: {
                name: localStorage.getItem("name") || "",
                keyword: searchValue
            }
        });

        if (response.data.code === 200) {
            const res = [];
            if (response.data.data.length > 9) {
                for (let i = 0; i < 9; i++)
                    res.push(response.data.data[i].title);
                return res;
            }
            else return response.data.data.map(item => item.title);
        } else {
            return [];
        }
    } catch (error) {
        console.error('搜索请求失败:', error);
        ElMessage.error('搜索请求失败');
        return [];
    }
}

// 选择建议项
function selectSuggestion(suggestion) {
    searchText.value = suggestion;
    showSuggestions.value = false;
    performSearch();
}

// 处理输入框失去焦点
function handleInputBlur() {
    // 延迟隐藏，以便点击建议项时有时间处理
    setTimeout(() => {
        showSuggestions.value = false;
    }, 200);
}

// 处理键盘导航
function handleKeyNavigation(event) {
    if (!showSuggestions.value || searchResults.value.length === 0) return;

    switch (event.key) {
        case 'ArrowDown':
            event.preventDefault();
            activeSuggestionIndex.value =
                (activeSuggestionIndex.value + 1) % searchResults.value.length;
            break;
        case 'ArrowUp':
            event.preventDefault();
            activeSuggestionIndex.value =
                activeSuggestionIndex.value <= 0 ?
                    searchResults.value.length - 1 :
                    activeSuggestionIndex.value - 1;
            break;
        case 'Enter':
            if (activeSuggestionIndex.value >= 0) {
                event.preventDefault();
                selectSuggestion(searchResults.value[activeSuggestionIndex.value]);
            }
            break;
        case 'Escape':
            showSuggestions.value = false;
            break;
    }
}

// 重置搜索
function resetSearch() {
    searchResults.value = [];
    showSuggestions.value = false;
    activeSuggestionIndex.value = -1;
    debouncedSearch.cancel(); // 取消未执行的防抖任务
}

// 处理搜索输入
function handleSearchInput(value) {
    if (value.trim() === '') {
        resetSearch();
    } else {
        debouncedSearch(value);
    }
}

// 手动触发搜索
function performSearch() {
    if (searchText.value.trim()) {
        showSuggestions.value = false;
        // 这里可以执行实际的搜索跳转逻辑
        console.log('执行最终搜索:', searchText.value);
        // ElMessage.success(`搜索: ${searchText.value}`);
        router.replace(`/search-video/${encodeURIComponent(searchText.value)}`);
    }
}

// 组件卸载时清理
onUnmounted(() => {
    if (countdownTimer) {
        clearInterval(countdownTimer)
    }
    debouncedSearch.cancel();
})

// 添加键盘事件监听
onMounted(() => {
    isLoggedIn();
    if (searchInput.value) {
        searchInput.value.$el.querySelector('input').addEventListener('keydown', handleKeyNavigation);
    }
});

function RefreshMyWindow() {
    window.location.href = '/'
}

function ClickToRecommend() {
    router.push("/recommend");
}

function ToFrameExtraction() {
    axios({
        url: "http://127.0.0.1:8080/video/nothing",
        method: "POST",
        headers: {
            "name": localStorage.getItem("name"),
            "uuid": localStorage.getItem("uuid")
        }
    }).then(res => {
        if (res.data === 'Not logged in') {
            loginVisible.value = true;
        } else {
            router.push("/frame-extraction")
        }
    }).catch(e => {
        ElMessage.error(e);
    })
}

function ToOpenLive() {
    axios({
        url: "http://127.0.0.1:8080/video/nothing",
        method: "POST",
        headers: {
            "name": localStorage.getItem("name"),
            "uuid": localStorage.getItem("uuid")
        }
    }).then(res => {
        if (res.data === 'Not logged in') {
            loginVisible.value = true;
        } else {
            router.push("/open-live")
        }
    }).catch(e => {
        ElMessage.error(e);
    })
}

function isLoggedIn() {
    axios({
        url: "http://127.0.0.1:8080/video/nothing",
        method: "POST",
        headers: {
            "name": localStorage.getItem("name"),
            "uuid": localStorage.getItem("uuid")
        }
    }).then(res => {
        if (res.data !== 'Not logged in') isLogged.value = true;
    }).catch(e => {
        console.log(e);
    })
}

async function SendOTP() {
    await axios({
        url: "http://127.0.0.1:8080/user/OTP",
        method: "post",
        data: {
            userEmail: userEmailByCode.value,
        }
    }).then(res => {
        if (res.data.code === 200) {
            ElMessage.success("验证码发送成功")
            countdown.value = 60
            countdownTimer = setInterval(() => {
                if (countdown.value > 0) {
                    countdown.value--
                } else {
                    clearInterval(countdownTimer)
                }
            }, 1000)
        }
        else ElMessage.error("发送失败")
    }).catch(e => {
        ElMessage.error(e);
        console.log(e);
    })
}

async function UserLogout() {
    ElMessageBox.confirm(
        '确定要退出登录吗？',
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(() => {
        localStorage.removeItem("name");
        localStorage.removeItem("uuid");
        window.location.href = "/";
    }).catch(() => {
        // 用户取消删除
    });
}

async function LoginOrRegister() {
    await axios({
        url: "http://127.0.0.1:8080/user/email-OTP",
        method: "post",
        data: {
            userEmail: userEmailByCode.value,
            code: userCodeByCode.value
        }
    }).then(res => {
        if (res.data.code === 200) {
            localStorage.setItem("name", res.data.data.name)
            localStorage.setItem("uuid", res.data.data.uuid)
            isLogged.value = true;
            window.location.href = "/";
        } else {
            ElMessage.warning(res.data.message)
        }
    }).catch(e => {
        ElMessage.error(e)
    })
}

async function LoginByPassword() {
    await axios({
        url: "http://127.0.0.1:8080/user/email-password",
        method: "post",
        data: {
            userEmail: userEmailByPassword.value,
            userPassword: userPasswordByPassword.value
        }
    }).then(res => {
        if (res.data.code === 200) {
            localStorage.setItem("name", res.data.data.name)
            localStorage.setItem("uuid", res.data.data.uuid)
            isLogged.value = true;
            window.location.href = "/";
        } else {
            ElMessage.warning(res.data.message)
        }
    }).catch(e => {
        ElMessage.error(e)
    })
}
</script>

<style scoped>
.tool-button {
    width: 120px;
    height: 40px;
    background-color: rgb(22, 24, 35);
    color: rgb(201, 201, 201);
    border: none;
    font-size: 16px;
    border-radius: 10px;
    display: flex;
    justify-content: flex-start;
}

.tool-button:hover {
    background-color: rgba(201, 201, 201, 0.1);
    color: white
}

.search-input {
    margin-left: 220px;
    color: white;
    height: 42px;
    width: 370px;
    border-radius: 0 !important;
    --el-input-border-radius: 10px 0 0 10px;
    --el-input-bg-color: rgb(57, 58, 68);
    --el-border-color: rgb(57, 58, 68);
}

.search-wrapper {
    position: relative;
}

.search-wrapper:hover .search-button {
    font-size: 16px;
    font-weight: 600;
    border: 0;
    background-color: white;
    color: black;
}

.search-wrapper:hover .search-input {
    color: white;
    height: 42px;
    width: 370px;
    border-radius: 0 !important;
    --el-input-border-radius: 10px 0 0 10px;
    --el-input-bg-color: rgb(19, 19, 23);
    --el-border-color: white;
    --el-input-font-size: 16px;
    --el-input-text-color: #ffffff;
    border-right: 0;
}

.search-input :deep(.el-input__wrapper.is-focus) {
    color: white;
    box-shadow: 0 0 0 2px white inset !important;
    --el-input-bg-color: rgb(19, 19, 23);
    --el-input-font-size: 16px;
    --el-input-text-color: #ffffff;
}

.search-input :deep(.el-input__wrapper:hover) {
    color: white;
    box-shadow: 0 0 0 2px white inset !important;
    --el-input-bg-color: rgb(19, 19, 23);
    --el-input-font-size: 16px;
    --el-input-text-color: #ffffff;
}

.search-button {
    width: 80px;
    font-size: 16px;
    font-weight: 600;
    height: 42px;
    border-radius: 0 10px 10px 0;
    border-color: rgb(22, 24, 35);
    border: 0;
    color: white;
    background-color: rgb(57, 58, 68);
}

.search-button:hover {
    font-size: 16px;
    font-weight: 600;
    border: 0;
    background-color: white;
    color: rgb(50, 50, 50);
}

/* 搜索建议下拉框样式 */
.suggestions-dropdown {
    position: absolute;
    top: 100%;
    left: 220px;
    width: 370px;
    max-height: 400px;
    overflow-y: auto;
    background: white;
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    z-index: 9999;
    margin-top: 4px;
}

.suggestion-item {
    height: 20px;
    padding: 12px 16px;
    cursor: pointer;
    display: flex;
    align-items: center;
    transition: background-color 0.2s;
    color: #606266;
}

.suggestion-item:hover,
.suggestion-active {
    background-color: #f5f7fa;
    color: #409eff;
}

/* 使用 :focus-within 实现联动效果 */
.search-wrapper:focus-within .search-input {
    --el-input-bg-color: rgb(19, 19, 23);
    --el-border-color: white;
    border-right: 0;
}

.search-wrapper:focus-within .search-button {
    background-color: white !important;
    color: black !important;
}

/* 确保 hover 和 focus 状态一致 */
.search-wrapper:hover,
.search-wrapper:focus-within {
    .search-input {
        --el-input-bg-color: rgb(19, 19, 23);
        --el-border-color: white;
        border-right: 0;
    }

    .search-button {
        background-color: white !important;
        color: black !important;
    }

    .search-button:hover {
        background-color: white !important;
        color: rgb(91, 91, 91) !important;
    }
}

.contribution-button {
    font-size: 12px;
    color: rgb(157, 157, 157);
}

.contribution-button:hover {
    font-size: 12px;
    color: white !important;
}

.refresh-button {
    margin-left: 50px;
    color: rgba(255, 255, 255, 0);
}

.refresh-button:hover {
    color: rgba(255, 255, 255, 1);
}

.login-input {
    height: 40px;
    --el-input-border-radius: 10px;
}

/* 加载动画样式 */
:deep(.el-icon.is-loading) {
    animation: rotating 2s linear infinite;
}

@keyframes rotating {
    0% {
        transform: rotate(0deg);
    }

    100% {
        transform: rotate(360deg);
    }
}
</style>