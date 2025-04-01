<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 헤더 -->
    <AppHeader title="화재 제보 상세" :showBackButton="true" />
    
    <!-- 메인 컨텐츠 -->
    <div class="container mx-auto px-4 py-6 max-w-lg">
      <!-- 로딩 상태 -->
      <div v-if="isLoading" class="flex justify-center items-center py-12">
        <div class="w-12 h-12 border-4 border-primary-600 border-t-transparent rounded-full animate-spin"></div>
      </div>
      
      <!-- 에러 상태 -->
      <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-lg p-4 text-center">
        <AlertTriangle class="h-8 w-8 text-red-500 mx-auto mb-2" />
        <p class="text-red-700 font-medium">{{ error }}</p>
        <button 
          @click="loadReportData" 
          class="mt-3 px-4 py-2 bg-primary-600 text-white rounded-md text-sm font-medium hover:bg-primary-700"
        >
          다시 시도
        </button>
      </div>
      
      <!-- 제보 상세 정보 -->
      <div v-else-if="report" class="space-y-6">
        <!-- 제보자 정보 -->
        <div class="bg-white rounded-lg shadow-sm p-4">
          <div class="flex items-center">
            <img :src="report.userAvatar" alt="프로필" class="w-12 h-12 rounded-full mr-4" />
            <div>
              <div class="flex items-center">
                <h3 class="font-medium text-gray-900">{{ report.username }}</h3>
                <span v-if="report.verified" class="ml-2 px-2 py-0.5 text-xs bg-blue-100 text-blue-800 border border-blue-200 rounded-full flex items-center">
                  <Shield class="h-3 w-3 mr-1" />
                  확인됨
                </span>
              </div>
              <div class="flex items-center text-xs text-gray-500 mt-1">
                <Clock class="h-3 w-3 mr-1" />
                <span>{{ report.time }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 비디오 플레이어 또는 이미지 -->
        <div class="bg-white rounded-lg shadow-sm overflow-hidden">
          <div v-if="report.videoUrl" class="w-full aspect-video">
            <VideoPlayer 
              :videoUrl="report.videoUrl" 
              :posterUrl="report.imageUrl"
              @error="handleVideoError"
            />
          </div>
          <img 
            v-else 
            :src="report.imageUrl" 
            alt="화재 이미지" 
            class="w-full object-cover"
          />
        </div>
        
        <!-- 위치 정보 -->
        <div class="bg-white rounded-lg shadow-sm p-4">
          <h3 class="font-medium text-gray-900 mb-2 flex items-center">
            <MapPin class="h-5 w-5 mr-2 text-primary-600" />
            위치 정보
          </h3>
          <p class="text-gray-700">{{ report.location || displayLocation }}</p>
          
          <!-- 지도 미리보기 (클릭 시 전체 지도 보기) -->
          <div class="mt-3 h-40 bg-gray-100 rounded-lg overflow-hidden relative">
            <div id="mini-map" class="w-full h-full"></div>
            <button 
              @click="openMap" 
              class="absolute bottom-2 right-2 bg-white rounded-lg shadow-md px-3 py-1.5 text-sm font-medium text-gray-700 hover:bg-gray-50"
            >
              지도 보기
            </button>
          </div>
        </div>
        
        <!-- 제보 내용 -->
        <div class="bg-white rounded-lg shadow-sm p-4">
          <h3 class="font-medium text-gray-900 mb-2 flex items-center">
            <FileText class="h-5 w-5 mr-2 text-primary-600" />
            제보 내용
          </h3>
          <p class="text-gray-700">{{ report.description }}</p>
          
          <!-- 위험도 표시 -->
          <div class="mt-4 flex items-center">
            <span class="text-sm text-gray-600 mr-2">위험도:</span>
            <span 
              class="px-3 py-1 rounded-full text-xs font-medium"
              :class="riskLevelClass"
            >
              {{ report.riskLevel }}
            </span>
          </div>
        </div>
        
        <!-- 액션 버튼 -->
        <div class="bg-white rounded-lg shadow-sm p-4">
          <div class="grid grid-cols-3 gap-2">
            <button 
              @click="confirmFire" 
              class="flex flex-col items-center justify-center text-sm font-medium py-2"
              :class="isConfirmed ? 'text-red-600' : 'text-gray-600'"
            >
              <AlertTriangle :class="['h-6 w-6 mb-1', isConfirmed ? 'fill-red-100' : '']" />
              <span>위험해요 {{ report.confirms }}</span>
            </button>
            
            <button @click="openComments" class="flex flex-col items-center justify-center text-gray-600 text-sm font-medium py-2">
              <MessageSquare class="h-6 w-6 mb-1" />
              <span>댓글 {{ report.comments }}</span>
            </button>
            
            <button @click="shareReport" class="flex flex-col items-center justify-center text-gray-600 text-sm font-medium py-2">
              <Share2 class="h-6 w-6 mb-1" />
              <span>공유</span>
            </button>
          </div>
        </div>
        
        <!-- 댓글 섹션 (토글) -->
        <div v-if="showComments" class="bg-white rounded-lg shadow-sm p-4">
          <h3 class="font-medium text-gray-900 mb-3">댓글</h3>
          <comment-section :post-id="report.id" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { 
  AlertTriangle, 
  Shield, 
  Clock, 
  MapPin, 
  FileText, 
  MessageSquare, 
  Share2 
} from 'lucide-vue-next';
import AppHeader from '../components/AppHeader.vue';
import VideoPlayer from '../components/VideoPlayer.vue';
import CommentSection from '../components/CommentSection.vue';
import { reverseGeocode } from '../services/geocodingService';

const route = useRoute();
const router = useRouter();
const reportId = computed(() => route.params.id);

// 상태 변수
const isLoading = ref(true);
const error = ref(null);
const report = ref(null);
const showComments = ref(false);
const isConfirmed = ref(false);
const formattedAddress = ref(null);
const map = ref(null);
const marker = ref(null);

// 위험도에 따른 클래스 계산
const riskLevelClass = computed(() => {
  if (!report.value) return '';
  
  switch (report.value.riskLevel) {
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

// 표시할 위치 정보 계산
const displayLocation = computed(() => {
  if (formattedAddress.value && formattedAddress.value.fullAddress) {
    return formattedAddress.value.fullAddress;
  }
  
  // 역지오코딩 결과가 없으면 좌표 표시
  if (report.value?.coordinates) {
    return `위도: ${report.value.coordinates.lat.toFixed(5)}, 경도: ${report.value.coordinates.lng.toFixed(5)}`;
  }
  
  return '위치 정보 없음';
});

// 제보 데이터 로드
const loadReportData = async () => {
  isLoading.value = true;
  error.value = null;
  
  try {
    // 실제로는 API 호출을 통해 데이터를 가져옵니다
    // const response = await fetch(`/api/fire-reports/${reportId.value}`);
    // const data = await response.json();
    
    // 테스트용 데이터
    await new Promise(resolve => setTimeout(resolve, 1000)); // 로딩 시뮬레이션
    
    // 샘플 데이터 (실제로는 API 응답으로 대체)
    report.value = {
      id: reportId.value,
      username: '소방지킴이',
      userAvatar: 'https://placehold.co/40x40',
      verified: true,
      time: '5분 전',
      videoUrl: 'https://team05sa.blob.core.windows.net/videos/38/38.mp4', // 샘플 비디오 URL
      imageUrl: 'https://placehold.co/600x400',
      location: '',
      coordinates: { lat: 37.498095, lng: 127.027610 }, // 강남역 좌표
      description: '상업 건물 3층에서 화재 발생. 연기가 심하게 나고 있습니다. 주변 건물로 확산 우려가 있어 긴급 대응이 필요합니다.',
      riskLevel: '높음',
      confirms: 128,
      comments: 24
    };
    
    // 좌표로부터 주소 변환
    if (report.value.coordinates) {
      try {
        const address = await reverseGeocode(
          report.value.coordinates.lat, 
          report.value.coordinates.lng
        );
        formattedAddress.value = address;
      } catch (err) {
        console.error('주소 변환 실패:', err);
      }
    }
    
    // 지도 초기화
    setTimeout(() => {
      initMap();
    }, 500);
    
  } catch (err) {
    console.error('제보 데이터 로드 실패:', err);
    error.value = '제보 정보를 불러오는데 실패했습니다.';
  } finally {
    isLoading.value = false;
  }
};

// 지도 초기화
const initMap = () => {
  if (!report.value?.coordinates || !window.naver || !window.naver.maps) return;
  
  try {
    const position = new window.naver.maps.LatLng(
      report.value.coordinates.lat,
      report.value.coordinates.lng
    );
    
    // 지도 옵션
    const mapOptions = {
      center: position,
      zoom: 15,
      zoomControl: false,
      scrollWheel: false,
      draggable: false
    };
    
    // 지도 생성
    map.value = new window.naver.maps.Map('mini-map', mapOptions);
    
    // 마커 생성
    marker.value = new window.naver.maps.Marker({
      position: position,
      map: map.value,
      icon: {
        content: `
          <div style="
            width: 24px;
            height: 24px;
            background-color: #ff3b30;
            border: 2px solid white;
            border-radius: 50%;
            box-shadow: 0 2px 6px rgba(0,0,0,0.3);
            transform: translate(-50%, -50%);
          "></div>
        `,
        anchor: new window.naver.maps.Point(12, 12)
      }
    });
  } catch (err) {
    console.error('지도 초기화 실패:', err);
  }
};

// 비디오 에러 처리
const handleVideoError = (err) => {
  console.error('비디오 로드 에러:', err);
  // 필요한 경우 추가 처리
};

// 지도 보기
const openMap = () => {
  if (!report.value?.coordinates) return;
  
  // 지도 페이지로 이동
  router.push({
    name: 'Map',
    query: {
      lat: report.value.coordinates.lat,
      lng: report.value.coordinates.lng,
      title: `화재 제보: ${report.value.location || displayLocation.value}`
    }
  });
};

// 댓글 토글
const openComments = () => {
  showComments.value = !showComments.value;
};

// 위험해요 버튼 클릭
const confirmFire = () => {
  isConfirmed.value = !isConfirmed.value;
  // 실제로는 API 호출을 통해 서버에 업데이트
  if (isConfirmed.value) {
    report.value.confirms++;
  } else {
    report.value.confirms--;
  }
};

// 공유 기능
const shareReport = () => {
  // 공유 기능 구현
  if (navigator.share) {
    navigator.share({
      title: `화재 제보: ${report.value.location || displayLocation.value}`,
      text: report.value.description,
      url: window.location.href
    }).catch(err => {
      console.error('공유 실패:', err);
    });
  } else {
    alert('공유 기능을 지원하지 않는 브라우저입니다.');
  }
};

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  loadReportData();
});

// 컴포넌트 언마운트 시 정리
onBeforeUnmount(() => {
  if (map.value) {
    map.value = null;
  }
  if (marker.value) {
    marker.value = null;
  }
});
</script>

