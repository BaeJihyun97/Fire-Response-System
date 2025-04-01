<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 헤더 -->
    <div class="bg-white border-b border-gray-200 py-2 shadow-sm">
      <div class="container mx-auto max-w-lg px-4">
        <div class="flex justify-between items-center">
          <div class="flex items-center">
            <button 
              @click="goBack" 
              class="p-2 mr-2 rounded-full hover:bg-gray-100"
            >
              <ChevronLeft class="h-5 w-5 text-gray-700" />
            </button>
            <h1 class="font-bold text-xl text-gray-900">화재 제보 상세</h1>
          </div>
        </div>
      </div>
    </div>
    
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
                <span>{{ report.timestamp }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 비디오 플레이어 -->
        <div class="bg-white rounded-lg shadow-sm overflow-hidden">
          <VideoPlayer 
            :videoUrl="report.videoUrl" 
            :posterUrl="report.imageUrl"
            @error="handleVideoError"
          />
        </div>
        
        <!-- 위치 정보 -->
        <div class="bg-white rounded-lg shadow-sm p-4">
          <h3 class="font-medium text-gray-900 mb-2 flex items-center">
            <MapPin class="h-5 w-5 mr-2 text-primary-600" />
            위치 정보
          </h3>
          <p class="text-gray-700">{{ report.location }}</p>
          
          <!-- 지도 미리보기 (클릭 시 전체 지도 보기) -->
          <div class="mt-3 h-40 bg-gray-100 rounded-lg overflow-hidden relative">
            <img 
              :src="`https://maps.googleapis.com/maps/api/staticmap?center=${report.coordinates.lat},${report.coordinates.lng}&zoom=15&size=600x200&markers=color:red%7C${report.coordinates.lat},${report.coordinates.lng}&key=YOUR_API_KEY`" 
              alt="지도" 
              class="w-full h-full object-cover"
            />
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
        
        <!-- 관리자 액션 버튼 -->
        <div class="bg-white rounded-lg shadow-sm p-4">
          <h3 class="font-medium text-gray-900 mb-3">관리자 액션</h3>
          
          <div class="grid grid-cols-2 gap-3">
            <button 
              @click="confirmReport" 
              class="px-4 py-2 bg-green-600 text-white rounded-lg text-sm font-medium hover:bg-green-700 flex items-center justify-center"
              :disabled="isProcessing"
            >
              <CheckCircle class="h-5 w-5 mr-2" />
              제보 확인
            </button>
            
            <button 
              @click="markAsFalse" 
              class="px-4 py-2 bg-red-600 text-white rounded-lg text-sm font-medium hover:bg-red-700 flex items-center justify-center"
              :disabled="isProcessing"
            >
              <XCircle class="h-5 w-5 mr-2" />
              허위 제보
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { 
  ChevronLeft, 
  AlertTriangle, 
  Shield, 
  Clock, 
  MapPin, 
  FileText, 
  CheckCircle, 
  XCircle 
} from 'lucide-vue-next';
import VideoPlayer from '../../components/VideoPlayer.vue';

const route = useRoute();
const router = useRouter();
const reportId = computed(() => route.params.id);

// 상태 변수
const isLoading = ref(true);
const error = ref(null);
const report = ref(null);
const isProcessing = ref(false);

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

// 뒤로 가기
const goBack = () => {
  router.back();
};

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
      timestamp: '2023-08-15 14:30:45',
      videoUrl: 'https://team05sa.blob.core.windows.net/videos/38/38.mp4', // 샘플 비디오 URL
      imageUrl: 'https://placehold.co/600x400',
      location: '서울특별시 강남구 테헤란로 152',
      coordinates: { lat: 37.498095, lng: 127.027610 }, // 강남역 좌표
      description: '상업 건물 3층에서 화재 발생. 연기가 심하게 나고 있습니다. 주변 건물로 확산 우려가 있어 긴급 대응이 필요합니다.',
      riskLevel: '높음',
      emergency: true
    };
    
  } catch (err) {
    console.error('제보 데이터 로드 실패:', err);
    error.value = '제보 정보를 불러오는데 실패했습니다.';
  } finally {
    isLoading.value = false;
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
  
  // 지도 페이지로 이동하거나 모달 표시
  router.push({
    name: 'Map',
    query: {
      lat: report.value.coordinates.lat,
      lng: report.value.coordinates.lng,
      title: `화재 제보: ${report.value.location}`
    }
  });
};

// 제보 확인 처리
const confirmReport = async () => {
  if (!report.value) return;
  
  isProcessing.value = true;
  
  try {
    // 실제로는 API 호출
    // await fetch(`/api/fire-reports/${reportId.value}/confirm`, { method: 'POST' });
    
    // 확인 처리 시뮬레이션
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    alert('제보가 확인되었습니다.');
    router.push('/admin');
  } catch (err) {
    console.error('제보 확인 처리 실패:', err);
    alert('제보 확인 처리에 실패했습니다.');
  } finally {
    isProcessing.value = false;
  }
};

// 허위 제보 처리
const markAsFalse = async () => {
  if (!report.value) return;
  
  const reason = prompt('허위 제보로 판단한 이유를 입력해주세요:');
  if (!reason) return; // 취소한 경우
  
  isProcessing.value = true;
  
  try {
    // 실제로는 API 호출
    // await fetch(`/api/fire-reports/${reportId.value}/false`, { 
    //   method: 'POST',
    //   headers: { 'Content-Type': 'application/json' },
    //   body: JSON.stringify({ reason })
    // });
    
    // 허위 제보 처리 시뮬레이션
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    alert('허위 제보로 처리되었습니다.');
    router.push('/admin');
  } catch (err) {
    console.error('허위 제보 처리 실패:', err);
    alert('허위 제보 처리에 실패했습니다.');
  } finally {
    isProcessing.value = false;
  }
};

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  loadReportData();
});
</script>

