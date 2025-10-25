import { defineStore } from "pinia";
import { ref } from "vue";

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
export const useLiveDataStore = defineStore('LiveData',{

    state(){
        return {
            lives: <Live[]>[],
            currentIndex: 0
        }
    }
})