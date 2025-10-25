// types/shims-video.d.ts 或 src/shims-video.d.ts
declare module 'video.js/dist/video-js.min.css' {
  const content: any;
  export default content;
}

declare module 'video.js/dist/video-js.css' {
  const content: any;
  export default content;
}

declare module 'video.js' {
  import videojs from 'video.js';
  export default videojs;
}