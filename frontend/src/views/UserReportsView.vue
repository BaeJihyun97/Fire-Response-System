<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 상단 네비게이션 바 -->
    <AppHeader title="내 리포트" :showBackButton="true">
      <template #actions>
        <button 
          @click="refreshData" 
          class="p-2 rounded-full hover:bg-gray-100"
          :class="{ 'animate-spin': isRefreshing }"
        >
          <RefreshCw class="h-5 w-5 text-gray-700" />
        </button>
      </template>
    </AppHeader>
    
    <!-- 메인 컨텐츠 -->
    <div class="container mx-auto px-4 py-4 max-w-lg pb-24">
      <!-- 탭 컴포넌트 -->
      <div class="mb-6">
        <div class="flex border-b border-gray-200 mb-4">
          <button 
            @click="activeTab = 'confirmed'" 
            :class="[
              'tab',
              activeTab === 'confirmed' ? 'tab-active' : 'tab-inactive'
            ]"
          >
            화재 확인
          </button>
          
        </div>

        <!-- 화재 확인 탭 -->
        <div v-if="activeTab === 'confirmed'">
          <div v-if="fireReports.length === 0" class="text-center py-8 text-gray-500">
            아직 작성한 화재 확인 리포트가 없습니다.
          </div>
          <div v-else class="space-y-6">
            <fire-report-card 
              v-for="fire in fireReports" 
              :key="fire.id" 
              :fire="fire"
            />
          </div>
        </div>

        <!-- 커뮤니티 제보 탭 -->
        <div v-if="activeTab === 'community'">
          <div v-if="communityReports.length === 0" class="text-center py-8 text-gray-500">
            아직 작성한 커뮤니티 제보가 없습니다.
          </div>
          <div v-else class="space-y-6 mt-4">
            <fire-report-card 
              v-for="fire in communityReports" 
              :key="fire.id" 
              :fire="fire"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { RefreshCw } from 'lucide-vue-next';
import FireReportCard from '../components/FireReportCard.vue';
import AppHeader from '../components/AppHeader.vue';
import { useRouter, useRoute } from 'vue-router';
import { reportApiService } from '../services/api';

const activeTab = ref('confirmed');
const isRefreshing = ref(false);
const router = useRouter();
const route = useRoute();

// 화재 제보 데이터
const fireReports = ref([]);
// 커뮤니티 제보 데이터
const communityReports = ref([]);

// 사용자 ID (라우트 파라미터에서 가져옴)
const userId = ref(route.params.userId);

// 데이터 새로고침 함수
const refreshData = async () => {
  isRefreshing.value = true;
  
  try {
    console.log('Fetching reports for userId:', userId.value);
    // 사용자 ID에 따른 리포트 가져오기
    const response = await reportApiService.getReportsByUserId(userId.value);
    console.log('Reports response:', response.data);
    
    if (response.data && Array.isArray(response.data)) {
      // API 응답 데이터를 현재 구조에 맞게 변환
      fireReports.value = response.data.map(report => ({
        id: report.reportId,
        coordinates: { lat: report.latitude, lng: report.longitude },
        timestamp: report.uploadedAt,
        status: report.status || '진행 중',
        isFire: true,
        riskLevel: report.riskLevel || '중간',
        verified: report.verified || false,
        metadata: {
          likes: report.likes || 0,
          comments: report.comments || 0,
          videoUrl: report.videoId ? `https://team05sa.blob.core.windows.net/videos/${report.videoId}/${report.videoId}.mp4` : '',
          imageUrl: '',
          description: report.description || ''
        }
      }));
    } else {
      console.error('Invalid response format:', response.data);
      fireReports.value = [];
    }

    // 커뮤니티 제보는 현재 없으므로 빈 배열로 설정
    communityReports.value = [];
  } catch (error) {
    console.error('데이터 새로고침 오류:', error);
    fireReports.value = [];
    communityReports.value = [];
  } finally {
    isRefreshing.value = false;
  }
};

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  refreshData();
});
</script>

<style scoped>
.tab {
  @apply px-4 py-2 text-sm font-medium;
}

.tab-active {
  @apply text-primary-600 border-b-2 border-primary-600;
}

.tab-inactive {
  @apply text-gray-500 hover:text-gray-700;
}

.shadow-soft {
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.btn {
  @apply px-3 py-1.5 rounded-lg font-medium text-sm;
}

.btn-primary {
  @apply bg-primary-600 text-white hover:bg-primary-700 transition-colors;
}
</style> 