<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 긴급 헤더 -->
    <div class="bg-gradient-to-r from-primary-600 to-primary-500 text-white py-2 px-4 shadow-md">
      <div class="container mx-auto flex items-center justify-between max-w-lg">
        <div class="flex items-center">
          <AlertTriangle class="h-4 w-4 mr-2" />
          <span class="text-sm font-medium">긴급 신고: 119</span>
        </div>
        <div class="flex items-center">
          <Phone class="h-4 w-4 mr-1" />
          <span class="text-xs">화재 시 즉시 119로 신고하세요</span>
        </div>
      </div>
    </div>
    
    <!-- 상단 네비게이션 바 -->
    <AppHeader title="화재알리미" :showBackButton="false">
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
          <button 
            @click="activeTab = 'community'" 
            :class="[
              'tab',
              activeTab === 'community' ? 'tab-active' : 'tab-inactive'
            ]"
          >
            커뮤니티 제보
          </button>
        </div>

        <!-- 화재 확인 탭 -->
        <div v-if="activeTab === 'confirmed'">
          <div class="mb-4 border border-primary-200 bg-primary-50 rounded-xl p-4 shadow-soft">
            <p class="text-sm text-primary-800">
              <span class="font-bold">긴급 알림:</span> 현재 강남역 부근에 화재가 발생했습니다. 해당 지역 방문을 자제해 주시기 바랍니다.
            </p>
          </div>

          <div class="space-y-6">
            <fire-report-card 
              v-for="fire in fireReports" 
              :key="fire.id" 
              :fire="fire"
            />
          </div>
        </div>

        <!-- 커뮤니티 제보 탭 -->
        <div v-if="activeTab === 'community'">
          <div class="space-y-6 mt-4">
            <fire-report-card 
              v-for="fire in communityReports" 
              :key="fire.id" 
              :fire="fire"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 하단 네비게이션 바 -->
    <div class="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 py-2 shadow-lg">
      <div class="container mx-auto max-w-lg">
        <div class="flex justify-around items-center">
          <button class="flex flex-col items-center text-primary-600">
            <Home class="h-6 w-6" />
            <span class="text-xs mt-1 font-medium">홈</span>
          </button>
          <router-link to="/map" class="flex flex-col items-center text-gray-500">
            <Map class="h-6 w-6" />
            <span class="text-xs mt-1">지도</span>
          </router-link>
          <router-link to="/report">
            <button class="rounded-full bg-gradient-to-r from-primary-600 to-primary-500 h-14 w-14 flex items-center justify-center text-white shadow-lg transform hover:scale-105 transition-transform duration-200">
              <Camera class="h-7 w-7" />
            </button>
          </router-link>
          <router-link to="/notifications" class="flex flex-col items-center text-gray-500">
            <Bell class="h-6 w-6" />
            <span class="text-xs mt-1">알림</span>
          </router-link>
          <router-link to="/profile" class="flex flex-col items-center text-gray-500">
            <User class="h-6 w-6" />
            <span class="text-xs mt-1">내정보</span>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { AlertTriangle, Bell, Camera, Home, Map, Phone, RefreshCw, User } from 'lucide-vue-next';
import FireReportCard from '../components/FireReportCard.vue';
import AppHeader from '../components/AppHeader.vue';
import { useRouter } from 'vue-router';

const activeTab = ref('confirmed');
const isRefreshing = ref(false);
const router = useRouter();

// 화재 제보 데이터
const fireReports = ref([
  {
    id: '1',
    coordinates: { lat: 37.498095, lng: 127.027610 },
    timestamp: '2025-03-31T23:30:00',
    status: '진행 중',
    isFire: true,
    riskLevel: '높음',
    verified: true,
    metadata: {
      likes: 45,
      comments: 12,
      videoUrl: '',
      imageUrl: 'https://example.com/image1.jpg'
    }
  },
  {
    id: '2',
    coordinates: { lat: 37.526120, lng: 126.925771 },
    timestamp: '2024-03-31T15:00:00',
    status: '진행 중',
    isFire: true,
    riskLevel: '중간',
    verified: true,
    metadata: {
      likes: 32,
      comments: 8,
      videoUrl: '',
      imageUrl: 'https://example.com/image2.jpg'
    }
  }
]);

// 커뮤니티 제보 데이터
const communityReports = ref([
  {
    id: '3',
    coordinates: { lat: 37.566535, lng: 126.977969 },
    timestamp: new Date(Date.now() - 600000).toISOString(), // 10분 전
    status: '진행 중',
    isFire: false,
    riskLevel: '낮음',
    verified: false,
    metadata: {
      likes: 67,
      comments: 12,
      videoUrl: '',
      imageUrl: 'https://placehold.co/600x400'
    }
  },
  {
    id: '4',
    coordinates: { lat: 37.538617, lng: 127.094454 },
    timestamp: new Date(Date.now() - 3600000).toISOString(), // 1시간 전
    status: '진행 중',
    isFire: false,
    riskLevel: '낮음',
    verified: false,
    metadata: {
      likes: 89,
      comments: 23,
      videoUrl: '',
      imageUrl: 'https://placehold.co/400x200'
    }
  }
]);

// 데이터 새로고침 함수
const refreshData = async () => {
  isRefreshing.value = true;
  
  try {
    // 여기에 데이터 새로고침 로직 추가
    await new Promise(resolve => setTimeout(resolve, 1000)); // 시뮬레이션
  } catch (error) {
    console.error('데이터 새로고침 오류:', error);
  } finally {
    isRefreshing.value = false;
  }
};

// 디버깅을 위한 로그
onMounted(() => {
  console.log('HomePage 마운트됨');
  console.log('fireReports:', fireReports.value);
  console.log('communityReports:', communityReports.value);
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

