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
    
    <!-- 메인 컨텐츠 -->
    <div class="container mx-auto px-4 py-4 max-w-lg pb-24">
      <!-- 기존 내용 유지 -->
      <!-- ... -->
      
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
              v-for="fire in confirmedFires" 
              :key="fire.id" 
              :fire="fire"
            />
          </div>
        </div>

        <!-- 커뮤니티 제보 탭 -->
        <div v-if="activeTab === 'community'">
          <div class="space-y-6 mt-4">
            <community-post-card 
              v-for="post in communityPosts" 
              :key="post.id" 
              :post="post"
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
          <button class="flex flex-col items-center text-gray-500">
            <Search class="h-6 w-6" />
            <span class="text-xs mt-1">검색</span>
          </button>
          <router-link to="/report">
            <button class="rounded-full bg-gradient-to-r from-primary-600 to-primary-500 h-14 w-14 flex items-center justify-center text-white shadow-lg transform hover:scale-105 transition-transform duration-200">
              <Camera class="h-7 w-7" />
            </button>
          </router-link>
          <router-link to="/notifications" class="flex flex-col items-center text-gray-500">
            <Bell class="h-6 w-6" />
            <span class="text-xs mt-1">알림</span>
          </router-link>
          <button class="flex flex-col items-center text-gray-500">
            <User class="h-6 w-6" />
            <span class="text-xs mt-1">내정보</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { AlertTriangle, Bell, Camera, Home, Phone, Search, User } from 'lucide-vue-next';
import FireReportCard from '../components/FireReportCard.vue';
import CommunityPostCard from '../components/CommunityPostCard.vue';
import { useNotificationStore } from '../stores/notificationStore';

const notificationStore = useNotificationStore();
const activeTab = ref('confirmed');

const confirmedFires = ref([
  {
    id: "1",
    username: "소방지킴이",
    userAvatar: "https://placehold.co/40x40",
    location: "강남역, 서울",
    time: "5분 전",
    description: "상업 건물 3층에서 화재 발생. 연기가 심하게 나고 있습니다. 주변 도로 통제 중.",
    riskLevel: "높음",
    imageUrl: "https://placehold.co/400x200",
    distance: "1.2km",
    confirms: 128, // 좋아요에서 confirms로 변경
    comments: 24,
    verified: true
  },
  {
    id: "2",
    username: "안전제일",
    userAvatar: "https://placehold.co/40x40",
    location: "여의도 공원, 서울",
    time: "15분 전",
    description: "공원 동쪽 입구 근처에서 작은 산불 발생. 소방차 출동 중입니다.",
    riskLevel: "중간",
    imageUrl: "https://placehold.co/400x200",
    distance: "3.5km",
    confirms: 87, // 좋아요에서 confirms로 변경
    comments: 15,
    verified: true
  }
]);

const communityPosts = ref([
  {
    id: "3",
    username: "시민제보자",
    userAvatar: "https://placehold.co/40x40",
    location: "홍대 앞, 서울",
    time: "30분 전",
    description: "이 건물에서 연기가 나는 것 같은데 화재인지 확인 부탁드립니다.",
    imageUrl: "https://placehold.co/400x200",
    distance: "5.2km",
    confirms: 45, // 좋아요에서 confirms로 변경
    comments: 12,
    verified: false
  },
  {
    id: "4",
    username: "동네지킴이",
    userAvatar: "https://placehold.co/40x40",
    location: "강동구 천호동, 서울",
    time: "1시간 전",
    description: "아파트 단지 근처에서 연기가 보입니다. 화재인지 확인 필요합니다.",
    imageUrl: "https://placehold.co/400x200",
    distance: "8.7km",
    confirms: 67, // 좋아요에서 confirms로 변경
    comments: 23,
    verified: false
  }
]);
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