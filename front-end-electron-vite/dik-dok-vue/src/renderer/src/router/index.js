// src/renderer/router/index.js (Vue版本)
import { createMemoryHistory, createRouter, createWebHashHistory } from 'vue-router'
import FrameExtraction from '../components/FrameExtraction.vue'
import Home from '../components/Home.vue'
import Recommend from '../components/Recommend.vue'
import SearchVideo from '../components/SearchVideo.vue'
import SearchToWatch from '../components/SearchToWatch.vue'
import live from '../components/Live.vue'
import OpenLive from '../components/OpenLive.vue'
import LiveToWatch from '../components/LiveToWatch.vue'
import { ElMessage } from 'element-plus'
import component from 'element-plus/es/components/tree-select/src/tree-select-option.mjs'

const routes = [
    {
        path: '/',
        component: Home,
        children: [
            {
                path: 'frame-extraction',
                component: FrameExtraction
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
    },
    {
        name: "LiveToWatch",
        path: '/live-to-watch',
        component: LiveToWatch
    }
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})

export default router