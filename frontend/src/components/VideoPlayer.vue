<template>
    <div class="video-player">
      <div v-if="videoUrl" class="relative rounded-lg overflow-hidden">
        <video 
          ref="videoRef"
          class="w-full rounded-lg"
          controls
          :poster="thumbnailUrl || ''"
        >
          <source :src="videoUrl" :type="videoType">
          브라우저가 비디오 태그를 지원하지 않습니다.
        </video>
        
        <div v-if="loading" class="absolute inset-0 flex items-center justify-center bg-black bg-opacity-50">
          <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-white"></div>
        </div>
      </div>
      <div v-else class="bg-gray-100 rounded-lg p-4 flex items-center justify-center">
        <div class="text-gray-500 text-center">
          <VideoOff class="h-8 w-8 mx-auto mb-2" />
          <p>비디오를 찾을 수 없습니다</p>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, onMounted, watch } from 'vue';
  import { VideoOff } from 'lucide-vue-next';
  
  export default {
    name: 'VideoPlayer',
    components: {
      VideoOff
    },
    props: {
      videoUrl: {
        type: String,
        default: ''
      },
      thumbnailUrl: {
        type: String,
        default: ''
      },
      autoplay: {
        type: Boolean,
        default: false
      }
    },
    setup(props) {
      const videoRef = ref(null);
      const loading = ref(true);
      const videoType = ref('video/mp4');
  
      // 비디오 URL에 따라 비디오 타입 결정
      watch(() => props.videoUrl, (newUrl) => {
        if (newUrl) {
          if (newUrl.endsWith('.mp4')) {
            videoType.value = 'video/mp4';
          } else if (newUrl.endsWith('.webm')) {
            videoType.value = 'video/webm';
          } else if (newUrl.endsWith('.ogg')) {
            videoType.value = 'video/ogg';
          }
        }
      }, { immediate: true });
  
      onMounted(() => {
        if (videoRef.value) {
          // 비디오 로딩 이벤트 처리
          videoRef.value.addEventListener('loadeddata', () => {
            loading.value = false;
            if (props.autoplay) {
              videoRef.value.play();
            }
          });
  
          videoRef.value.addEventListener('error', () => {
            loading.value = false;
            console.error('비디오 로딩 실패');
          });
        }
      });
  
      return {
        videoRef,
        loading,
        videoType
      };
    }
  };
  </script>
  