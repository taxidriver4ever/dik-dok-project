<template>
    <div style="display: flex;justify-content: center;align-items: center; flex-direction: column;margin-bottom: 150px;"
        v-if="!isLiving">
        <h2 style="color: white;">填写以下内容(封面，标题，url)以开始直播</h2>
        <ElUpload action="#" list-type="picture-card" :auto-upload="false" :on-change="handleChange"
            :on-remove="handleRemove" :file-list="fileList" :limit="1" :before-upload="beforeUpload"
            accept=".jpg,.jpeg,.png,.gif,.webp,.bmp,.JPG,.JPEG,.PNG,.GIF,.WEBP,.BMP">
            <ElIcon v-if="fileList.length === 0">
                <Plus></Plus>
            </ElIcon>
            <template #file="{ file }">
                <div>
                    <img class="el-upload-list__item-thumbnail" :src="file.url" alt="" />
                    <span class="el-upload-list__item-actions">
                        <span class="el-upload-list__item-preview" @click="handlePictureCardPreview(file)">
                            <el-icon><zoom-in /></el-icon>
                        </span>
                        <span class="el-upload-list__item-delete" @click="handleRemoveFile(file)">
                            <el-icon>
                                <Delete />
                            </el-icon>
                        </span>
                    </span>
                </div>
            </template>

            <!-- 上传限制提示 -->
            <template #tip>
                <div style="color: #ccc; margin-top: 10px;">
                    只能上传图片文件（JPG/PNG/GIF等），且最多只能上传一张
                </div>
            </template>
        </ElUpload>
        <br></br>
        <ElInput style="width: 80vh;font-size: large;" placeholder="标题（选填）" v-model="title">
        </ElInput>
        <br></br>
        <ElInput v-model="liveContent" style="width: 80vh;" placeholder="请输入m3u8格式的url"></ElInput>
        <br></br>
        <ElButton type="warning" @click="startToLive">点击开启直播</ElButton>
    </div>
    <div style="display: flex; justify-content: center; align-items: center;flex-direction: column;margin-bottom: 100px;"
        v-else>
        <h1 style="color: white;">{{ videoTitle }}</h1>
        <div style="display: flex;">
            <video-player :src="videoUrl" controls preload="auto" autoplay="any" :width="1024" :height="520" :options="playerOptions"
                @ready="onPlayerReady" @error="onPlayerError" />
        </div>
        <br></br>
        <ElButton type="danger" style="font-size: large; height: 40px; width: 250px;border-radius: 10px;"
            @click="closeLive">关闭直播</ElButton>
    </div>
</template>

<script setup lang="ts">
import axios from 'axios';
import { ElButton, ElIcon, ElInput, ElMessage, ElUpload } from 'element-plus';
import { Delete, Download, Plus, ZoomIn } from '@element-plus/icons-vue'
import { onMounted, ref } from 'vue';
import type { UploadFile, UploadFiles, UploadProps } from 'element-plus'
import { VideoPlayer } from '@videojs-player/vue'
import 'video.js/dist/video-js.css'
import VideoJsPlayer from 'video.js';

const isLiving = ref(false);
const liveContent = ref("")
const title = ref("")
const videoUrl = ref("")
const videoTitle = ref("")
const dialogImageUrl = ref('')
const dialogVisible = ref(false)
const fileList = ref<UploadFile[]>([]) // 用于控制文件列表


// 文件状态改变时的回调
const handleChange: UploadProps['onChange'] = (file, files) => {
    fileList.value = files
}

// 删除文件
const handleRemove: UploadProps['onRemove'] = (file, files) => {
    fileList.value = files
}

// 手动触发删除（点击删除图标时）
const handleRemoveFile = (file: UploadFile) => {
    const index = fileList.value.indexOf(file)
    if (index !== -1) {
        fileList.value.splice(index, 1)
    }
}


const playerOptions = ref({
  html5: {
    vhs: {
      overrideNative: true
    }
  },
  liveui: true,
  responsive: true,
  controlBar: {
    pictureInPictureToggle: false
  }
})

const onPlayerReady = (player: VideoJsPlayer) => {
  console.log('播放器已准备好', player)
  
  player.on('loadeddata', () => {
    console.log('视频数据已加载')
  })
}

const onPlayerError = (error: Error) => {
  console.error('播放错误', error)
}

// 上传前的校验
const beforeUpload: UploadProps['beforeUpload'] = (file) => {
    // 检查文件类型
    const isImage = /\.(jpg|jpeg|png|gif|webp|bmp)$/i.test(file.name)
    if (!isImage) {
        ElMessage.warning('只能上传图片文件！')
        return false
    }

    // 检查文件大小（可选，比如限制5MB）
    const isLt5M = file.size / 1024 / 1024 < 5
    if (!isLt5M) {
        ElMessage.warning('图片大小不能超过5MB！')
        return false
    }

    return false // 返回false阻止自动上传
}

const handlePictureCardPreview = (file: UploadFile) => {
    dialogImageUrl.value = file.url!
    dialogVisible.value = true
}

async function startToLive() {
    if (!/\.flv(\?.*)?$/i.test(liveContent.value) && !/\.m3u8(\?.*)?$/i.test(liveContent.value)) {
        ElMessage.warning("url格式错误")
        return
    }
    else if (title.value.length > 44) {
        ElMessage.warning("标题名称过长");
        return
    }
    else if(fileList.value.length === 0){
        ElMessage.warning("未选择直播封面");
        return
    }
    // 这里可以添加处理上传图片的逻辑
    if (fileList.value.length > 0) {
        const file = fileList.value[0]
        console.log('上传的图片文件:', file)
        // 你可以在这里将图片上传到服务器，或者进行其他处理
    }

    // 创建FormData对象
    const formData = new FormData();
    formData.append("author", localStorage.getItem("name") || "");
    formData.append("url", liveContent.value);
    formData.append("title", title.value);

    // 如果有封面图片，添加到FormData
    if (fileList.value.length > 0) {
        formData.append("coverImage", fileList.value[0].raw || "");
    }

    await axios({
        url: "http://localhost:8080/live/live-data",
        method: "POST",
        headers: {
            "name": localStorage.getItem("name"),
            "uuid": localStorage.getItem("uuid")
        },
        data: formData
    }).then(res => {
        if (res.data.code === 200) {
            liveContent.value = ""
            title.value = ""
            fileList.value = [] // 清空文件列表
            videoUrl.value = res.data.data.url;
            videoTitle.value = res.data.data.title;
            localStorage.setItem("live", "yes")
            isLiving.value = true
        }
    }).catch(e => {
        console.log(e)
    })
}

async function closeLive() {
    await axios({
        url: "http://localhost:8080/live/live-data",
        method: "DELETE",
        headers: {
            "name": localStorage.getItem("name"),
            "uuid": localStorage.getItem("uuid")
        },
        params: {
            author: localStorage.getItem("name")
        }
    }).then(res => {
        if (res.data.code === 200) {
            localStorage.removeItem("live")
            videoUrl.value = ""
            videoTitle.value = ""
            isLiving.value = false;
        }
    }).catch(e => {
        console.log(e)
    })
}

function getLiveStatus() {
    axios({
        url: "http://localhost:8080/live/initial-data",
        method: "POST",
        headers: {
            "name": localStorage.getItem("name"),
            "uuid": localStorage.getItem("uuid")
        },
        params: {
            author: localStorage.getItem("name")
        }
    }).then(res => {
        if (res.data.code === 200) {
            isLiving.value = true
            videoUrl.value = res.data.data.url
            videoTitle.value = res.data.data.title
        }
    }).catch(e => {
        ElMessage.error(e)
    })
}

onMounted(() => {
    getLiveStatus();
})
</script>

<style lang="css" scoped>
:deep(.el-upload-list--picture-card) {
    display: flex;
    justify-content: center;
}

:deep(.el-upload--picture-card) {
    width: 148px;
    height: 148px;
    line-height: 148px;
}
</style>