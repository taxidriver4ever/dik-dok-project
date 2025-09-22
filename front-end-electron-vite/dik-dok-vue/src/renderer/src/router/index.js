// src/renderer/router/index.js (Vue版本)
import { createMemoryHistory, createRouter, createWebHashHistory } from 'vue-router'
import FrameExtraction from '../components/FrameExtraction.vue'
import Home from '../components/Home.vue'
import Recommend from '../components/Recommend.vue'
import SearchVideo from '../components/SearchVideo.vue'
import SearchToWatch from '../components/SearchToWatch.vue'
import live from '../components/live.vue'
import OpenLive from '../components/OpenLive.vue'
import { ElMessage } from 'element-plus'
import component from 'element-plus/es/components/tree-select/src/tree-select-option.mjs'

const routes = [
    {
        path: '/',
        component: Home,
        children: [
            {
                path: 'frame-extraction',
                component: FrameExtraction,
                // beforeEnter: (to, from, next) => {
                //     console.log('独享守卫：即将进入帧提取页面', to, from);
                //     axios({
                //         url: "http://127.0.0.1:8080/video/nothing",
                //         method: "POST",
                //         headers: {
                //             "name": localStorage.getItem("name"),
                //             "uuid": localStorage.getItem("uuid")
                //         }
                //     }).then(res => {
                //         if (res.data !== 'Not logged in') next("/frame-extraction");
                //     }).catch(e => {
                //         ElMessage.error(e);
                //     })
                // }
            },
            {
                path: "open-live",
                component: OpenLive
            },
            {
                path: 'recommend',
                component: Recommend
            },
            {
                path: "",
                component: Recommend
            },
            {
                path: "live",
                component: live
            }
        ]
    },
    {
        path: '/search-video/:q',
        component: SearchVideo
    },
    {
        path: '/search-to-watch/:q',
        component: SearchToWatch
    }
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})

export default router