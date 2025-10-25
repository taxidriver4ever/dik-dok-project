<template>
  <div class="tiktok-container" ref="container" @touchstart="handleTouchStart" @touchmove="handleTouchMove"
    @touchend="handleTouchEnd" @wheel="handleWheel">

    <div class="videos-wrapper" :style="{ transform: `translateY(-${currentIndex * 100}vh)` }">
      <div v-for="(video, index) in videos" :key="video.id" class="video-item">
        <video :width="VideoWidth" :height="VideoHeight" :src="video.url" playsinline ref="videoPlayers"
          @loadeddata="onVideoLoaded" @canplay="onVideoCanPlay" loop controls></video>
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
      <div class="control-btn">
        <div class="control-icon-div">
          <img class="control-icon" v-if="isClickLike" @click="SendToLike" src="/src/images/FillHeart.png"></img>
          <img class="control-icon" v-else-if="!isClickLike" @click="SendToLike" src="/src/images/BlankHeart.png"></img>
        </div>
        <div>{{ currentVideo?.likes }}</div>
      </div>
      <div class="control-btn">
        <div class="control-icon-div">
          <img class="control-icon" src="/src/images/comment.png" @click="clickComment"></img>
        </div>
        <div>{{ currentVideo?.comments }}</div>
      </div>
      <div class="control-btn" style="margin-bottom: 70px;">
        <div class="control-icon-div">
          <img class="control-icon" src="/src/images/share.png"></img>
        </div>
        <div>分享</div>
      </div>
      <div
        style="height: 90vh; background-color: white; width: 400px; overflow: visible;border-radius: 10px;display: flex;flex-direction: column;"
        v-show="isClickComment">
        <ElHeader style="height: 5vh;margin-top: 20px;display: flex;">
          <el-button @click="isClickComment = false" type="text">
            <ElIcon size="20">
              <Back />
            </ElIcon>
          </el-button>
          <h1>&nbsp;&nbsp;全部评论</h1>
        </ElHeader>
        <ElMain style="padding: 0;margin: 0 0 0 20px;display: flex;flex-direction: column;">
          <div v-for="(comment, index) in comments" :key="index"
            style="margin-bottom: 40px;display: flex;flex-direction: column; max-width:100vh;overflow-x: hidden;">
            <div style="font-size: 12px;">{{ comment }}</div>
            <div style="display: flex;margin-left: auto;margin-right: 20px;font-size: 12px;">like</div>
          </div>
        </ElMain>
        <ElFooter style="height: 20vh; display: flex; margin: 0; padding: 0;">
          <div v-if="isWantComment"
            style="background-color: white;width: 100vh; ;display: flex;flex-direction: column;margin-top: auto; margin-bottom: 20px; padding: 20px 20px 0 20px;">
            <div>
              <ElButton type="text" @click="isWantComment = false">
                <ElIcon size="20">
                  <Back />
                </ElIcon>收起
              </ElButton>
            </div>
            <div style="display: flex;flex-direction: column;background-color: white;">
              <ElInput v-model="commentInputContent" resize="none" :autosize="{ minRows: 3, maxRows: 3 }"
                placeholder="请输入你的评论" type="textarea">
              </ElInput>
              <ElButton @click="sendComment" style="width: 100px;display: flex;margin-left: auto;" type="danger">发送
              </ElButton>
            </div>
          </div>
          <div v-else
            style="display: flex;margin-top: auto; margin-left: auto; margin-right: auto;margin-bottom: 20px;">
            <ElButton type="text" style="display: flex;font-size: large;" @click="isWantComment = true">评论</ElButton>
          </div>
        </ElFooter>
      </div>
    </div>



    <div class="loading-indicator" v-if="isLoading"></div>
  </div>
</template>

<script lang="ts" setup>
import axios from 'axios';
import { ElButton, ElFooter, ElHeader, ElIcon, ElInput, ElMain, ElMessage } from 'element-plus';
import { send } from 'vite';
import { ref, onMounted, computed, nextTick, watch, onUnmounted } from 'vue'
import { Back } from '@element-plus/icons-vue';

interface Video {
  id: number;
  url: string;
  title: string;
  author: string;
  likes: number;
  comments: number;
  coverUrl: string;
}

interface Comment {
  id: number;
  url: string;
  content: string;
  author: string;
  likes: number;
  comments: number;
}

// 视频尺寸
const VideoWidth = ref(1300);
const VideoHeight = ref(700);
const isClickLike = ref(false)
// 视频数据
const videos = ref<Video[]>([]);
const isClickComment = ref(false)
const currentIndex = ref(0);
const isSwiping = ref(false);
const startY = ref(0);
const currentY = ref(0);
const isLoading = ref(true);
const container = ref<HTMLElement | null>(null);
const videoPlayers = ref<HTMLVideoElement[]>([]);
const hasSwiped = ref(false); // 标记是否已经滑动过一次
const comments = ref<Comment[]>([]);
const commentInputContent = ref("")
const isWantComment = ref(true)

// 当前视频
const currentVideo = computed(() => {
  return videos.value.length > 0 ? videos.value[currentIndex.value] : null;
});

// 用于存储定时器ID
let timerId: string | number | NodeJS.Timeout | null | undefined = null

watch(currentIndex, (newValue) => {
  // 清除之前的定时器
  if (timerId) {
    clearInterval(timerId)
    timerId = null
  }
  
  // 立即发送一次请求
  sendVideoUrlRequest()
  
  // 设置新的定时器，每5秒发送请求
  timerId = setInterval(() => {
    sendVideoUrlRequest()
  }, 5000)
})

// 提取请求函数
const sendVideoUrlRequest = () => {

  if (videos.value.length === 0) return
  
  const videoUrl = videos.value[currentIndex.value]?.url
  if (!videoUrl) return
  
  if(localStorage.getItem("name") === null || localStorage.getItem("uuid") === null){
    return
  }
  axios({
    url: "http://localhost:8080/video/url-tag",
    method: "POST",
    headers: {
      'name': localStorage.getItem("name"),
      "uuid": localStorage.getItem("uuid")
    },
    params: {
      userName: localStorage.getItem("name"),
      url: videoUrl
    }
  }).then(res => {
    if (res.data.code === 200) {
      console.log("URL check passed for index:", currentIndex.value)
    }
    else {
      alert(res.data);
    }
  }).catch(e => {
    ElMessage.error(e.message || "Request failed")
  })
}

// 组件卸载时清除定时器
onUnmounted(() => {
  if (timerId) {
    clearInterval(timerId)
    timerId = null
  }
})

function clickComment() {
  if (isClickComment.value === false) {
    isClickComment.value = true;
  }
  else isClickComment.value = false;
}

function sendComment() {
  if (commentInputContent.value !== "") {
    comments.value.push(
      {
        id: 0,
        url: videos.value[currentIndex.value].url,
        content: commentInputContent.value,
        author: videos.value[currentIndex.value].author,
        likes: 0,
        comments: 0
      }
    )
  }
}

function getLikeStatus() {
  axios({
    url: "http://localhost:8080/video/status",
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
    GetVideoFromOSS();
  }
})

async function SendToLike() {
  if (isClickLike.value === false) {
    await axios({
      url: "http://localhost:8080/video/click-like",
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
      url: "http://localhost:8080/video/cancel-like",
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

function GetVideoFromOSS() {
  axios({
    url: "http://127.0.0.1:8080/video/random",
    method: "POST",
    data: {

    }
  }).then(res => {
    console.log(res.data.data)
    const randomA = Math.floor(Math.random() * res.data.data.length);
    videos.value.push({
      id: 0,
      url: res.data.data[randomA].url,
      title: res.data.data[randomA].title,
      author: res.data.data[randomA].author,
      likes: res.data.data[randomA].likeCount,
      comments: res.data.data[randomA].commentCount,
      coverUrl: res.data.data[randomA].coverUrl
    })
    const randomB = Math.floor(Math.random() * res.data.data.length);
    videos.value.push({
      id: 1,
      url: res.data.data[randomB].url,
      title: res.data.data[randomB].title,
      author: res.data.data[randomB].author,
      likes: res.data.data[randomB].likeCount,
      comments: res.data.data[randomB].commentCount,
      coverUrl: res.data.data[randomB].coverUrl
    })
    const randomC = Math.floor(Math.random() * res.data.data.length);
    videos.value.push({
      id: 2,
      url: res.data.data[randomC].url,
      title: res.data.data[randomC].title,
      author: res.data.data[randomC].author,
      likes: res.data.data[randomC].likeCount,
      comments: res.data.data[randomC].commentCount,
      coverUrl: res.data.data[randomC].coverUrl
    })
    getLikeStatus();
  }).catch(e => {
    console.log(e);
  })
}

onMounted(() => {
  GetVideoFromOSS();
  // 设置容器高度
  if (container.value) {
    container.value.style.height = `${window.innerHeight}px`;
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
  height: 100vh;
  overflow: hidden;
  background: rgb(22, 24, 35);
  border-radius: 20px;
}

.videos-wrapper {
  position: relative;
  width: 100%;
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
  height: 91%;
  margin-bottom: auto;
  object-fit: contain;
  /* 确保视频比例正确 */
}

.video-info {
  position: absolute;
  bottom: 10px;
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
  margin-bottom: 10vh;
  /* position: relative; */
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

/* 更通用的方法，针对所有浏览器 */
video::-webkit-media-controls,
video::-webkit-media-controls-enclosure {
  opacity: 1 !important;
  display: flex !important;
  width: inherit/2;
  height: 100%;
}
</style>