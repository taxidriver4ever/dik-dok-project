<template>
    <div style="display: flex;justify-content: center;align-items: center; flex-direction: column;margin-bottom: 150px;"
        v-if="!isLiving">
        <h2 style="color: white;">填写以下内容以开始直播</h2>
        <ElInput style="width: 80vh;font-size: large;" placeholder="标题（选填）" v-model="title">

        </ElInput>
        <br></br>
        <ElInput v-model="liveContent" style="width: 80vh;" placeholder="请输入m3u8/flv格式的url"></ElInput>
        <br></br>
        <ElButton type="warning" @click="startToLive">点击开启直播</ElButton>
    </div>
    <div style="display: flex; justify-content: center; align-items: center;flex-direction: column;margin-bottom: 100px;"
        v-else>
        <h1 style="color: white;">{{ videoTitle }}</h1>
        <div style="display: flex;">
            <video class="video" controls preload="auto" width="900" height="500">
                <source :src="videoUrl" type="application/x-mpegURL">
            </video>
        </div>
        <br></br>
        <ElButton type="danger" style="font-size: large; height: 40px; width: 250px;border-radius: 10px;"
            @click="closeLive">关闭直播</ElButton>
    </div>
</template>

<script setup lang="ts">
import axios from 'axios';
import { ElButton, ElInput, ElMessage } from 'element-plus';
import { ref } from 'vue';
const isLiving = ref(localStorage.getItem("live") === 'yes');
const liveContent = ref("")
const title = ref("")
const videoUrl = ref("")
const videoTitle = ref("")
async function startToLive() {
    if (!/\.flv(\?.*)?$/i.test(liveContent.value) && !/\.m3u8(\?.*)?$/i.test(liveContent.value)) {
        ElMessage.warning("url格式错误")
        return
    }
    else if(title.value.length > 44) {
        ElMessage.warning("标题名称过长");
        return
    }
    await axios({
        url: "http://localhost:8080/live/live-data",
        method: "POST",
        headers: {
            "name": localStorage.getItem("name"),
            "uuid": localStorage.getItem("uuid")
        },
        params: {
            author: localStorage.getItem("name"),
            url: liveContent.value,
            title: title.value
        }
    }).then(res => {
        if (res.data.code === 200) {
            liveContent.value = ""
            title.value = ""
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
</script>

<style lang="css" scoped></style>