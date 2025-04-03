<template>
  <div
    class="bg-white rounded-xl shadow-soft overflow-hidden mb-4 cursor-pointer hover:shadow-md transition-shadow duration-200"
    @click="goToDetail"
  >
    <!-- 헤더 -->
    <div class="p-4 flex items-center justify-between">
      <div class="flex items-center">
        <div>
          <div class="flex items-center">
            <div class="h-8 w-8 rounded-full bg-gray-200 overflow-hidden mr-2">
              <img src="https://ui-avatars.com/api/?name=🔥&background=DC2626&color=fff&size=128" alt="화재" class="h-full w-full object-cover" />
            </div>
            <h3 class="font-medium text-gray-900">{{ fire.isFire ? '화재' : '화재 의심' }} #{{ fire.id }}</h3>
            <span v-if="fire.verified" class="ml-2 px-2 py-0.5 text-xs bg-blue-100 text-blue-800 border border-blue-200 rounded-full flex items-center">
              <Shield class="h-3 w-3 mr-1" />
              확인됨
            </span>
          </div>
          <div class="flex items-center text-xs text-gray-500 mt-0.5">
            <MapPin class="h-3 w-3 mr-1" />
            <!-- 위치 클릭 가능하도록 수정 -->
            <button
              @click.stop="showMap"
              class="hover:text-primary-600 hover:underline"
              :disabled="isLoadingMap"
            >
              <span v-if="isLoadingAddress">
                <span class="inline-block w-4 h-4 border-2 border-t-transparent border-gray-500 rounded-full animate-spin mr-1"></span>
                위치 확인 중...
              </span>
              <span v-else>{{ displayLocation }}</span>
            </button>
            <span class="mx-1">•</span>
            <span>{{ formatTime(fire.timestamp) }}</span>
          </div>
        </div>
      </div>

      <div class="flex items-center">
        <span
          class="px-3 py-1 rounded-full text-sm font-medium"
          :class="{
            'bg-red-100 text-red-800': fire.isFire,
            'bg-yellow-100 text-yellow-800': !fire.isFire
          }"
        >
          {{ fire.isFire ? '화재 발생' : '화재 의심' }}
        </span>
      </div>
    </div>

    <!-- 미디어 (비디오 또는 이미지) -->
    <div class="relative">
      <!-- 비디오가 있으면 비디오 플레이어 표시 -->
      <div v-if="fire.metadata.videoUrl" class="relative aspect-video mb-4">
        <video
          v-if="!videoError"
          :src="fire.metadata.videoUrl"
          class="w-full h-full object-contain cursor-pointer"
          @click="toggleVideo"
          @error="handleVideoError"
          controls
          preload="metadata"
        ></video>
        <div v-else class="w-full h-full flex items-center justify-center bg-gray-100">
          <div class="text-center">
            <AlertCircle class="h-8 w-8 text-gray-400 mx-auto mb-2" />
            <p class="text-sm text-gray-500">비디오를 로드할 수 없습니다</p>
          </div>
        </div>
      </div>

      <!-- 비디오가 없으면 이미지 표시 -->
      <img
        v-else-if="fire.metadata.imageUrl"
        :src="fire.metadata.imageUrl"
        alt="화재 이미지"
        class="w-full h-96 object-cover"
        @click.stop="viewDetail"
      />

      <!-- 위험도 표시 - 위험도에 따라 색상 차별화 -->
      <div
        class="absolute top-3 right-3 px-3 py-1 rounded-full text-xs font-medium flex items-center"
        :class="riskLevelClass"
      >
        <AlertTriangle class="h-3 w-3 mr-1" />
        위험도: {{ fire.riskLevel }}
      </div>

      <!-- 비디오 표시 아이콘 -->
      <div
        v-if="fire.metadata.videoUrl"
        class="absolute bottom-3 left-3 bg-black bg-opacity-60 rounded-full p-1.5"
      >
        <Video class="h-4 w-4 text-white" />
      </div>
    </div>

    <!-- 내용 -->
    <div class="p-4">
      <p class="text-gray-800 text-sm">{{ fire.isFire ? '화재가 발생했습니다.' : '화재 의심 상황입니다.' }}</p>

      <!-- 액션 버튼 -->
      <div class="grid grid-cols-3 gap-2 mt-4 pt-3 border-t border-gray-100">
        <button
          @click.stop="confirmFire"
          class="flex items-center justify-center text-sm font-medium py-1"
          :class="isConfirmed ? 'text-red-600' : 'text-gray-600'"
        >
          <AlertTriangle :class="['h-5 w-5 mr-1.5', isConfirmed ? 'fill-red-100' : '']" />
          <span>위험해요 {{ fire.metadata.likes }}</span>
        </button>

        <button @click.stop="openComments" class="flex items-center justify-center text-gray-600 text-sm font-medium py-1">
          <MessageSquare class="h-5 w-5 mr-1.5" />
          <span>댓글 {{ fire.metadata.comments }}</span>
        </button>

        <button @click.stop="shareReport" class="flex items-center justify-center text-gray-600 text-sm font-medium py-1">
          <Share2 class="h-5 w-5 mr-1.5" />
          <span>공유</span>
        </button>
      </div>
    </div>

    <!-- 댓글 섹션 (토글) -->
    <div
      v-if="showComments"
      class="px-4 pb-4 pt-2 bg-gray-50 border-t border-gray-100"
      @click.stop
    >
      <comment-section :post-id="parseInt(fire.id)" />
    </div>

    <!-- 지도 모달 -->
    <MapView
      v-if="showMapModal"
      :location-name="displayLocation"
      :location-address="formattedAddress?.fullAddress || `위도: ${fire.coordinates.lat}, 경도: ${fire.coordinates.lng}`"
      :location-coords="fire.coordinates"
      @close="showMapModal = false"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { MapPin, AlertTriangle, MessageSquare, Share2, Shield, Video, AlertCircle } from 'lucide-vue-next';
import { useRouter } from 'vue-router';
import CommentSection from './CommentSection.vue';
import MapView from '../views/MapView.vue';
import VideoPlayer from './VideoPlayer.vue';
import { reverseGeocode } from '../services/geocodingService';
import { postApi, reportApiService, videoApiService } from '../services/api';

const props = defineProps({
  fire: {
    type: Object,
    required: true,
    default: () => ({
      id: '',
      coordinates: { lat: 37.5665, lng: 126.9780 },
      location: '',
      timestamp: '',
      status: '진행 중',
      isFire: false,
      riskLevel: '낮음',
      verified: false,
      metadata: {
        likes: 0,
        comments: 0,
        videoUrl: null,
        imageUrl: ''
      }
    })
  },
  disableClick: {
    type: Boolean,
    default: false
  }
});

const router = useRouter();

// 필요한 상태 변수들
const showComments = ref(false);
const isConfirmed = ref(false);
const showMapModal = ref(false);
const isLoadingMap = ref(false);
const isLoadingAddress = ref(true); // 초기에는 주소 로딩 중
const formattedAddress = ref(null);
const isPlaying = ref(false);
const videoError = ref(false);

// 위험도에 따른 클래스 계산
const riskLevelClass = computed(() => {
  switch (props.fire.riskLevel) {
    case '높음':
      return 'bg-red-100 text-red-800 border border-red-200';
    case '중간':
      return 'bg-orange-100 text-orange-800 border border-orange-200';
    case '낮음':
      return 'bg-green-100 text-green-800 border border-green-200';
    default:
      return 'bg-gray-100 text-gray-800 border border-gray-200';
  }
});

// 표시할 위치 정보 계산 - location 속성 사용
const displayLocation = computed(() => {
  // formattedAddress가 있으면 그것을 사용
  if (formattedAddress.value?.fullAddress) {
    return formattedAddress.value.fullAddress;
  }

  // location이 있으면 그것을 사용
  if (props.fire.location) {
    return props.fire.location;
  }

  // location이 없으면 좌표 표시
  if (props.fire.coordinates) {
    return `위도: ${props.fire.coordinates.lat.toFixed(5)}, 경도: ${props.fire.coordinates.lng.toFixed(5)}`;
  }

  return '위치 정보 없음';
});

const formatAddress = (address) => {
  if (!address) return '';

  const parts = [];
  if (address.province) parts.push(address.province);
  if (address.city) parts.push(address.city);
  if (address.borough) parts.push(address.borough);
  if (address.quarter) parts.push(address.quarter);
  if (address.road) parts.push(address.road);
  if (address.house_number) parts.push(address.house_number);

  return parts.join(' ');
};

const getLocationInfo = async (coordinates) => {
  try {
    const response = await fetch(
      `https://nominatim.openstreetmap.org/reverse?format=json&lat=${coordinates.lat}&lon=${coordinates.lng}&zoom=18&addressdetails=1&accept-language=ko`
    );
    const data = await response.json();

    if (data && data.address) {
      return formatAddress(data.address);
    }
    return '위치 정보 없음';
  } catch (error) {
    console.error('위치 정보 로딩 실패:', error);
    return '위치 정보 없음';
  }
};

// 컴포넌트 마운트 시 좌표로부터 주소 변환
onMounted(async () => {
  console.log('FireReportCard 마운트됨, 좌표:', props.fire.coordinates);

  if (!props.fire.coordinates) {
    console.error('좌표 정보가 없습니다');
    isLoadingAddress.value = false;
    return;
  }

  try {
    // 좌표를 주소로 변환 (역지오코딩)
    const address = await getLocationInfo(props.fire.coordinates);
    formattedAddress.value = { fullAddress: address };
    console.log('주소 변환 성공:', address);
  } catch (error) {
    console.error('주소 변환 실패:', error);
  } finally {
    isLoadingAddress.value = false;
  }
});

// 상세 보기로 이동
const viewDetail = (event) => {
  // 비디오 플레이어 내부 클릭은 무시 (비디오 재생/일시정지 기능을 위해)
  if (event && event.target && event.target.tagName === 'VIDEO') {
    return;
  }

  if (props.fire.id) {
    router.push(`/posts/${props.fire.id}`);
  }
};

const getVideoData = async () => {
  try {
    const postId = props.fire.id;
    const response = await postApi.getPost(postId);
    const post = response.data;
    const reportResponse = await reportApiService.getLatestReportByEventId(post.eventId);
    if (reportResponse.data && reportResponse.data.videoId) {
      const videoId = reportResponse.data.videoId;
      const videoInfo = await videoApiService.getVideo(videoId);
      const ai = videoInfo.data.latest_analysis;
      if (ai !== null) {
        props.fire.isFire = ai.fire_detected;
        props.fire.riskLevel = ai.severity;
      }
    }
  } catch (error) {
    console.log(error);
  }
}

// 댓글 토글
const openComments = () => {
  console.log('댓글 토글 전:', showComments.value);
  showComments.value = !showComments.value;
  console.log('댓글 토글 후:', showComments.value);
};

// 위험해요 버튼 클릭
const confirmFire = async () => {
  try {
    // 토큰 가져오기
    const token = localStorage.getItem('token');
    if (!token) {
      alert('로그인이 필요합니다.');
      return;
    }

    // 토큰 디코딩
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    const decodedToken = JSON.parse(jsonPayload);
    const userId = decodedToken.preferred_username;

    // API 요청 보내기
    await postApi.reactToPost(props.fire.id, userId);

    // UI 업데이트
    isConfirmed.value = !isConfirmed.value;
    if (isConfirmed.value) {
      props.fire.metadata.likes++;
    } else {
      props.fire.metadata.likes--;
    }
  } catch (error) {
    console.error('위험해요 처리 실패:', error);
    alert('위험해요 처리를 실패했습니다.');
  }
};

// 공유 기능
const shareReport = () => {
  // 공유 기능 구현
  if (navigator.share) {
    navigator.share({
      title: `화재 제보: ${displayLocation.value}`,
      text: props.fire.isFire ? '화재가 발생했습니다.' : '화재 의심 상황입니다.',
      url: window.location.href + `/posts/${props.fire.id}`
    }).catch(err => {
      console.error('공유 실패:', err);
    });
  } else {
    alert('공유 기능을 지원하지 않는 브라우저입니다.');
  }
};

// 지도 표시 함수 (간소화)
const showMap = () => {
  try {
    // 이미 로딩 중이면 중복 요청 방지
    if (isLoadingMap.value) return;
    isLoadingMap.value = true;

    // 좌표가 있는 경우에만 지도 표시
    if (props.fire.coordinates) {
      showMapModal.value = true;
    } else {
      alert('위치 정보가 없습니다.');
    }
  } catch (error) {
    console.error('지도 표시 실패:', error);
    alert('지도를 표시하는데 실패했습니다: ' + (error.message || '알 수 없는 오류'));
  } finally {
    isLoadingMap.value = false;
  }
};

// 시간 포맷팅 함수
const formatTime = (timestamp) => {
  if (!timestamp) return '';
  const date = new Date(timestamp);
  const now = new Date();
  const diff = now - date;

  // 1분 미만
  if (diff < 60000) return '방금 전';
  // 1시간 미만
  if (diff < 3600000) return `${Math.floor(diff / 60000)}분 전`;
  // 24시간 미만
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}시간 전`;
  // 그 이상
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
};

// 상세 페이지로 이동
const goToDetail = () => {
  if (props.disableClick) return;
  router.push(`/posts/${props.fire.id}`);
};

const handleVideoError = (error) => {
  console.error('비디오 로드 에러:', {
    error,
    videoUrl: props.fire.metadata.videoUrl,
    videoElement: error.target,
    networkState: error.target?.networkState,
    errorState: error.target?.error
  });
  videoError.value = true;
};

// 비디오 URL 유효성 검사 함수 추가
const validateVideoUrl = (url) => {
  try {
    const videoUrl = new URL(url);
    return videoUrl.protocol === 'https:';
  } catch (e) {
    console.error('비디오 URL 유효성 검사 실패:', e);
    return false;
  }
};

// 비디오 로드 전에 URL 검증
const loadVideo = () => {
  if (!props.fire.metadata.videoUrl) {
    videoError.value = true;
    return;
  }

  if (!validateVideoUrl(props.fire.metadata.videoUrl)) {
    console.error('유효하지 않은 비디오 URL:', props.fire.metadata.videoUrl);
    videoError.value = true;
    return;
  }

  videoError.value = false;
};

// 컴포넌트 마운트 시 비디오 URL 검증
onMounted(() => {
  if (props.fire.metadata.videoUrl) {
    loadVideo();
    getVideoData();
  }
});

// 비디오 URL이 변경될 때마다 검증
watch(() => props.fire.metadata.videoUrl, () => {
  if (props.fire.metadata.videoUrl) {
    loadVideo();
  }
});

const toggleVideo = () => {
  const video = document.querySelector('video');
  if (video) {
    if (isPlaying.value) {
      video.pause();
    } else {
      video.play();
    }
    isPlaying.value = !isPlaying.value;
  }
};

// props가 변경될 때마다 로그 출력
watch(() => props.fire, (newValue) => {
  console.log('FireReportCard props 변경:', newValue);
}, { deep: true });

// 컴포넌트 마운트 시 props 로그 출력
onMounted(() => {
  console.log('FireReportCard 마운트됨, props:', props.fire);
  // ... existing code ...
});
</script>

