<template>
  <div class="bg-white rounded-lg shadow overflow-hidden">
    <div class="p-4 border-b border-gray-200 flex justify-between items-center">
      <div>
        <h2 class="text-lg font-medium text-gray-900">화재 제보 상세</h2>
        <p class="text-sm text-gray-500">제보 ID: {{ report.id }}</p>
      </div>
      <div class="flex items-center">
        <span 
          class="mr-2 px-2 py-0.5 rounded-full text-xs"
          :class="{
            'bg-red-100 text-red-800': report.riskLevel === '심각',
            'bg-orange-100 text-orange-800': report.riskLevel === '높음',
            'bg-yellow-100 text-yellow-800': report.riskLevel === '중간'
          }"
        >
          {{ report.riskLevel }}
        </span>
        <span 
          class="px-2 py-0.5 rounded-full text-xs border"
          :class="{
            'border-blue-300 bg-blue-50 text-blue-800': report.status === '진행 중',
            'border-gray-300 bg-gray-50 text-gray-800': report.status === '종결'
          }"
        >
          {{ report.status }}
        </span>
      </div>
    </div>

    <div class="p-4">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <!-- 제보 정보 -->
        <div>
          <div class="flex items-start mb-4">
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

          <div class="mb-4">
            <h3 class="text-sm font-medium text-gray-700 mb-2">제보 내용</h3>
            <p class="text-sm text-gray-600">
              {{ report.description || '제보 내용이 없습니다.' }}
            </p>
          </div>

          <div class="mb-4">
            <h3 class="text-sm font-medium text-gray-700 mb-2">위치 정보</h3>
            <div class="bg-gray-100 rounded-lg h-40 flex items-center justify-center">
              <MapPin class="h-6 w-6 text-gray-400" />
              <span class="ml-2 text-sm text-gray-500">지도 위치</span>
            </div>
          </div>
        </div>

        <!-- 미디어 정보 -->
        <div>
          <div class="mb-4">
            <h3 class="text-sm font-medium text-gray-700 mb-2">제보 영상</h3>
            <VideoPlayer 
              :video-url="report.videoUrl" 
              :thumbnail-url="report.thumbnailUrl"
            />
          </div>

          <div class="mb-4">
            <h3 class="text-sm font-medium text-gray-700 mb-2">제보 사진</h3>
            <div class="grid grid-cols-2 gap-2">
              <div v-for="(image, index) in report.images" :key="index" class="rounded-lg overflow-hidden">
                <img :src="image" alt="제보 사진" class="w-full h-32 object-cover" />
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="mt-6 border-t border-gray-200 pt-4">
        <h3 class="text-sm font-medium text-gray-700 mb-2">소방관 확인 정보</h3>
        <div v-if="report.confirmedByFireDept" class="bg-green-50 border border-green-200 rounded-lg p-3">
          <div class="flex items-center">
            <CheckCircle class="h-5 w-5 text-green-600 mr-2" />
            <div>
              <p class="text-sm font-medium text-green-800">소방관에 의해 확인됨</p>
              <p class="text-xs text-green-700">확인 시간: {{ report.confirmedAt || '정보 없음' }}</p>
            </div>
          </div>
        </div>
        <div v-else class="bg-yellow-50 border border-yellow-200 rounded-lg p-3">
          <div class="flex items-center">
            <AlertTriangle class="h-5 w-5 text-yellow-600 mr-2" />
            <div>
              <p class="text-sm font-medium text-yellow-800">소방관 확인 대기 중</p>
              <p class="text-xs text-yellow-700">현장 확인이 필요합니다</p>
            </div>
          </div>
        </div>
      </div>

      <div class="mt-6 flex justify-end space-x-3">
        <button @click="goBack" class="px-4 py-2 border border-gray-300 rounded-md text-sm">
          뒤로 가기
        </button>
        <button class="px-4 py-2 bg-blue-600 text-white rounded-md text-sm hover:bg-blue-700">
          상태 변경
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { AlertTriangle, CheckCircle, Clock, MapPin } from 'lucide-vue-next';
import VideoPlayer from '../../components/VideoPlayer.vue';

export default {
  name: 'FireReportDetail',
  components: {
    AlertTriangle,
    CheckCircle,
    Clock,
    MapPin,
    VideoPlayer
  },
  props: {
    id: {
      type: String,
      required: true
    }
  },
  setup(props) {
    const router = useRouter();
    const route = useRoute();
    
    // 실제로는 API에서 데이터를 가져와야 함
    const report = ref({
      id: props.id || route.params.id,
      username: "소방지킴이",
      userAvatar: "https://placehold.co/40x40",
      location: "강남역, 서울",
      time: "5분 전",
      riskLevel: "높음",
      status: "진행 중",
      verified: true,
      confirmedByFireDept: true,
      confirmedAt: "2023-05-15 14:30:00",
      description: "강남역 인근 건물에서 연기가 발생하고 있습니다. 2층 창문에서 불꽃이 보이며 사람들이 대피하고 있습니다.",
      videoUrl: "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4", // 샘플 비디오 URL
      thumbnailUrl: "https://placehold.co/600x400",
      images: [
        "https://placehold.co/600x400?text=Fire+Image+1",
        "https://placehold.co/600x400?text=Fire+Image+2",
        "https://placehold.co/600x400?text=Fire+Image+3",
        "https://placehold.co/600x400?text=Fire+Image+4"
      ]
    });

    const goBack = () => {
      router.back();
    };

    return {
      report,
      goBack
    };
  }
};
</script>
