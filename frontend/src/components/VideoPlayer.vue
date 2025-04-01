<template>
  <div class="video-player-container">
    <!-- 비디오 플레이어 -->
    <div class="relative rounded-lg overflow-hidden bg-black">
      <video
        ref="videoRef"
        class="w-full h-full object-contain"
        :class="{ 'cursor-pointer': !isPlaying }"
        @click="togglePlay"
        :src="videoUrl"
        :poster="posterUrl"
        :controls="showControls"
        :autoplay="autoplay"
        :loop="loop"
        :muted="muted"
        :playsinline="playsinline"
        @loadeddata="onVideoLoaded"
        @playing="isPlaying = true"
        @pause="isPlaying = false"
        @ended="isPlaying = false"
        @error="onVideoError"
      ></video>

      <!-- 로딩 인디케이터 -->
      <div v-if="isLoading" class="absolute inset-0 flex items-center justify-center bg-black bg-opacity-40">
        <div class="w-12 h-12 border-4 border-primary-600 border-t-transparent rounded-full animate-spin"></div>
      </div>

      <!-- 에러 메시지 -->
      <div v-if="error" class="absolute inset-0 flex items-center justify-center bg-black bg-opacity-70">
        <div class="text-center p-4">
          <AlertTriangle class="h-10 w-10 text-red-500 mx-auto mb-2" />
          <p class="text-white font-medium">{{ error }}</p>
          <button 
            @click="retryLoading" 
            class="mt-3 px-4 py-2 bg-primary-600 text-white rounded-md text-sm font-medium hover:bg-primary-700"
          >
            다시 시도
          </button>
        </div>
      </div>

      <!-- 커스텀 플레이 버튼 (컨트롤이 숨겨져 있을 때) -->
      <div 
        v-if="!showControls && !isPlaying && !isLoading && !error" 
        class="absolute inset-0 flex items-center justify-center bg-black bg-opacity-30 cursor-pointer"
        @click.stop="togglePlay"
      >
        <div class="w-16 h-16 rounded-full bg-white bg-opacity-80 flex items-center justify-center">
          <Play class="h-8 w-8 text-primary-600" />
        </div>
      </div>
    </div>

    <!-- 비디오 정보 (선택적) -->
    <div v-if="title || description" class="mt-2">
      <h3 v-if="title" class="font-medium text-gray-900">{{ title }}</h3>
      <p v-if="description" class="text-sm text-gray-600 mt-1">{{ description }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue';
import { AlertTriangle, Play } from 'lucide-vue-next';

const props = defineProps({
  videoUrl: {
    type: String,
    required: true
  },
  posterUrl: {
    type: String,
    default: ''
  },
  title: {
    type: String,
    default: ''
  },
  description: {
    type: String,
    default: ''
  },
  autoplay: {
    type: Boolean,
    default: false
  },
  loop: {
    type: Boolean,
    default: false
  },
  muted: {
    type: Boolean,
    default: false
  },
  playsinline: {
    type: Boolean,
    default: true
  },
  showControls: {
    type: Boolean,
    default: true
  }
});

const emit = defineEmits(['loaded', 'error', 'play', 'pause', 'ended']);

// 상태 변수
const videoRef = ref(null);
const isLoading = ref(true);
const isPlaying = ref(false);
const error = ref(null);

// 비디오 로드 완료 처리
const onVideoLoaded = () => {
  isLoading.value = false;
  emit('loaded', {
    duration: videoRef.value?.duration || 0,
    width: videoRef.value?.videoWidth || 0,
    height: videoRef.value?.videoHeight || 0
  });
};

// 비디오 에러 처리
const onVideoError = (e) => {
  isLoading.value = false;
  error.value = '비디오를 불러오는데 실패했습니다.';
  console.error('비디오 로드 에러:', e);
  emit('error', e);
};

// 재생/일시정지 토글
const togglePlay = () => {
  if (!videoRef.value) return;
  
  if (videoRef.value.paused) {
    videoRef.value.play()
      .then(() => {
        isPlaying.value = true;
        emit('play');
      })
      .catch(err => {
        console.error('비디오 재생 에러:', err);
        error.value = '비디오를 재생할 수 없습니다.';
        emit('error', err);
      });
  } else {
    videoRef.value.pause();
    isPlaying.value = false;
    emit('pause');
  }
};

// 다시 로드 시도
const retryLoading = () => {
  if (!videoRef.value) return;
  
  error.value = null;
  isLoading.value = true;
  videoRef.value.load();
};

// 외부에서 호출 가능한 메서드들
const play = () => {
  if (videoRef.value && videoRef.value.paused) {
    togglePlay();
  }
};

const pause = () => {
  if (videoRef.value && !videoRef.value.paused) {
    togglePlay();
  }
};

const stop = () => {
  if (videoRef.value) {
    videoRef.value.pause();
    videoRef.value.currentTime = 0;
    isPlaying.value = false;
  }
};

// videoUrl이 변경되면 비디오 다시 로드
watch(() => props.videoUrl, (newUrl) => {
  if (newUrl) {
    error.value = null;
    isLoading.value = true;
    if (videoRef.value) {
      videoRef.value.load();
    }
  }
});

// 컴포넌트 마운트 시 초기화
onMounted(() => {
  if (props.videoUrl) {
    isLoading.value = true;
  } else {
    isLoading.value = false;
    error.value = '비디오 URL이 제공되지 않았습니다.';
  }
});

// 컴포넌트 언마운트 시 정리
onBeforeUnmount(() => {
  if (videoRef.value) {
    videoRef.value.pause();
  }
});

// 외부에서 접근 가능한 메서드 노출
defineExpose({
  play,
  pause,
  stop,
  videoElement: videoRef
});
</script>

<style scoped>
.video-player-container {
  width: 100%;
}

/* 애니메이션 */
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.animate-spin {
  animation: spin 1s linear infinite;
}
</style>

