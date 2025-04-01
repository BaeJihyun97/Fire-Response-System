<template>
  <div class="min-h-screen bg-gray-100 pb-20">
    <!-- 상단 네비게이션 바 -->
    <div class="bg-white border-b border-gray-200 py-2 shadow-sm">
      <div class="container mx-auto max-w-4xl px-4">
        <div class="flex justify-between items-center">
          <router-link to="/" class="flex items-center">
            <Flame class="h-6 w-6 text-primary-600 mr-2" />
            <span class="font-bold text-xl text-gray-900">화재알리미</span>
          </router-link>
          
          <div class="flex items-center space-x-4">
            <router-link to="/report" class="flex items-center text-gray-700 hover:text-primary-600">
              <Camera class="h-5 w-5 mr-1" />
              <span class="text-sm font-medium">제보하기</span>
            </router-link>
            
            <router-link to="/notifications" class="flex items-center text-gray-700 hover:text-primary-600 relative">
              <Bell class="h-5 w-5 mr-1" />
              <span class="text-sm font-medium">알림</span>
              <span class="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-4 w-4 flex items-center justify-center">
                2
              </span>
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <div class="container mx-auto px-4 py-6 max-w-4xl">
      <div class="mb-6">
        <router-link to="/" class="flex items-center text-gray-600 hover:text-gray-900">
          <ChevronLeft class="h-5 w-5 mr-1" />
          <span>뒤로 가기</span>
        </router-link>
      </div>

      <div class="bg-white rounded-lg shadow-md overflow-hidden">
        <div class="p-4 border-b border-gray-200">
          <div class="flex justify-between items-center">
            <h1 class="text-xl font-bold text-gray-900">화재 제보 #{{ id }}</h1>
            <div class="flex items-center">
              <span 
                class="px-2 py-0.5 rounded-full text-xs"
                :class="{
                  'bg-red-100 text-red-800': report.riskLevel === '심각',
                  'bg-orange-100 text-orange-800': report.riskLevel === '높음',
                  'bg-yellow-100 text-yellow-800': report.riskLevel === '중간',
                  'bg-blue-100 text-blue-800': !report.riskLevel
                }"
              >
                {{ report.riskLevel || '확인 중' }}
              </span>
            </div>
          </div>
        </div>

        <div class="p-4">
          <div class="flex items-start mb-6">
            <div class="h-12 w-12 rounded-full bg-gray-200 overflow-hidden mr-3">
              <img :src="report.userAvatar" :alt="report.username" class="h-full w-full object-cover" />
            </div>
            <div>
              <div class="flex items-center">
                <span class="font-medium">{{ report.username }}</span>
                <span 
                  v-if="report.verified" 
                  class="ml-2 px-2 py-0.5 rounded-full text-xs bg-green-100 text-green-800 border border-green-200 flex items-center"
                >
                  <CheckCircle class="h-3 w-3 mr-1" />
                  인증된 사용자
                </span>
              </div>
              <div class="flex items-center text-sm text-gray-500 mt-1">
                <MapPin class="h-3 w-3 mr-1" />
                {{ report.location }}
              </div>
              <div class="flex items-center text-xs text-gray-400 mt-1">
                <Clock class="h-3 w-3 mr-1" />
                {{ report.time }}
              </div>
            </div>
          </div>

          <!-- 영상 플레이어 추가 -->
          <div v-if="report.videoUrl" class="mb-6">
            <h2 class="text-lg font-medium text-gray-900 mb-2">화재 영상</h2>
            <VideoPlayer 
              :video-url="report.videoUrl" 
              :thumbnail-url="report.thumbnailUrl"
            />
          </div>

          <!-- 제보 사진 -->
          <div v-if="report.images && report.images.length > 0" class="mb-6">
            <h2 class="text-lg font-medium text-gray-900 mb-2">제보 사진</h2>
            <div class="grid grid-cols-2 gap-2">
              <div v-for="(image, index) in report.images" :key="index" class="rounded-lg overflow-hidden">
                <img :src="image" alt="제보 사진" class="w-full h-48 object-cover" />
              </div>
            </div>
          </div>

          <!-- 제보 내용 -->
          <div class="mb-6">
            <h2 class="text-lg font-medium text-gray-900 mb-2">제보 내용</h2>
            <p class="text-gray-700">
              {{ report.description || '제보 내용이 없습니다.' }}
            </p>
          </div>

          <!-- 위치 정보 -->
          <div class="mb-6">
            <h2 class="text-lg font-medium text-gray-900 mb-2">위치 정보</h2>
            <div class="bg-gray-100 rounded-lg h-64 flex items-center justify-center">
              <MapPin class="h-8 w-8 text-gray-400" />
              <span class="ml-2 text-gray-500">지도 위치</span>
            </div>
          </div>

          <!-- 소방서 확인 정보 -->
          <div class="mb-6">
            <h2 class="text-lg font-medium text-gray-900 mb-2">소방서 확인 정보</h2>
            <div v-if="report.confirmedByFireDept" class="bg-green-50 border border-green-200 rounded-lg p-4">
              <div class="flex items-center">
                <CheckCircle class="h-5 w-5 text-green-600 mr-2" />
                <div>
                  <p class="text-sm font-medium text-green-800">소방서에서 확인한 화재입니다</p>
                  <p class="text-xs text-green-700">확인 시간: {{ report.confirmedAt || '정보 없음' }}</p>
                </div>
              </div>
            </div>
            <div v-else class="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
              <div class="flex items-center">
                <AlertTriangle class="h-5 w-5 text-yellow-600 mr-2" />
                <div>
                  <p class="text-sm font-medium text-yellow-800">소방서 확인 대기 중</p>
                  <p class="text-xs text-yellow-700">소방서에서 아직 확인하지 않은 제보입니다</p>
                </div>
              </div>
            </div>
          </div>

          <!-- 사용자 반응 -->
          <div class="border-t border-gray-200 pt-4">
            <div class="flex justify-between items-center">
              <div class="flex items-center">
                <button class="flex items-center text-gray-600 hover:text-red-600 mr-4">
                  <AlertTriangle class="h-5 w-5 mr-1" />
                  <span>위험해요 ({{ report.dangers || 0 }})</span>
                </button>
                <button class="flex items-center text-gray-600 hover:text-blue-600">
                  <Share2 class="h-5 w-5 mr-1" />
                  <span>공유하기</span>
                </button>
              </div>
              <button class="px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700">
                119 신고하기
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { 
  AlertTriangle, 
  Bell, 
  Camera, 
  CheckCircle, 
  ChevronLeft, 
  Clock, 
  Flame, 
  MapPin, 
  Share2 
} from 'lucide-vue-next';
import VideoPlayer from '../components/VideoPlayer.vue';

export default {
  name: 'ReportDetailPage',
  components: {
    AlertTriangle,
    Bell,
    Camera,
    CheckCircle,
    ChevronLeft,
    Clock,
    Flame,
    MapPin,
    Share2,
    VideoPlayer
  },
  props: {
    id: {
      type: String,
      required: false
    }
  },
  setup(props) {
    const route = useRoute();
    const reportId = props.id || route.params.id;
    
    // 실제로는 API에서 데이터를 가져와야 함
    const report = ref({
      id: reportId,
      username: "시민제보자",
      userAvatar: "https://placehold.co/40x40",
      location: "강남역, 서울",
      time: "10분 전",
      riskLevel: "높음",
      verified: true,
      confirmedByFireDept: true,
      confirmedAt: "2023-05-15 14:30:00",
      description: "강남역 인근 건물에서 연기가 발생하고 있습니다. 2층 창문에서 불꽃이 보이며 사람들이 대피하고 있습니다.",
      videoUrl: "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4", // 샘플 비디오 URL
      thumbnailUrl: "https://placehold.co/600x400",
      dangers: 42,
      images: [
        "https://placehold.co/600x400?text=Fire+Image+1",
        "https://placehold.co/600x400?text=Fire+Image+2",
        "https://placehold.co/600x400?text=Fire+Image+3",
        "https://placehold.co/600x400?text=Fire+Image+4"
      ]
    });

    onMounted(() => {
      // 실제로는 여기서 API 호출을 통해 데이터를 가져옴
      console.log(`화재 제보 ID: ${reportId} 상세 정보 로드`);
    });

    return {
      id: reportId,
      report
    };
  }
};
</script>
