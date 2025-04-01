<script>
export default {
  name: 'AllClosedEventsView'
}
</script>

<script setup>
import { ref } from 'vue';
import { 
  AlertTriangle, 
  Archive,
  ArrowLeft,
  Bell, 
  Camera,
  CheckCircle,
  Clock, 
  Flame,
  Home,
  InfoIcon,
  MapPin, 
  Phone, 
  RefreshCw,
  Search,
  User
} from 'lucide-vue-next';

// 종결된 화재 제보
const closedReports = ref([
  {
    id: '5',
    coordinates: { lat: 37.566535, lng: 126.977969 },
    timestamp: '2024-03-31T13:00:00',
    status: '종결',
    isFire: true,
    riskLevel: '높음',
    verified: true,
    metadata: {
      likes: 45,
      comments: 12,
      videoUrl: 'https://team05sa.blob.core.windows.net/videos/38/38.mp4',
      imageUrl: 'https://example.com/image1.jpg'
    }
  },
  {
    id: '6',
    coordinates: { lat: 37.538617, lng: 127.094454 },
    timestamp: '2024-03-31T12:30:00',
    status: '종결',
    isFire: true,
    riskLevel: '중간',
    verified: true,
    metadata: {
      likes: 32,
      comments: 8,
      videoUrl: 'https://team05sa.blob.core.windows.net/videos/38/38.mp4',
      imageUrl: 'https://example.com/image2.jpg'
    }
  }
]);

// 이벤트 재개
const restoreEvent = (event) => {
  if (confirm(`${event.isDeleted ? '오보로 처리된 제보' : '종결된 화재'} #${event.id}를 재개하시겠습니까?`)) {
    closedReports.value = closedReports.value.filter(e => e.id !== event.id);
    console.log(`이벤트 #${event.id}가 재개되었습니다.`);
  }
};
</script>

<template>
  <div class="min-h-screen bg-gray-100">
    <!-- 긴급 헤더 -->
    <div class="bg-red-600 text-white py-2 px-4">
      <div class="container mx-auto flex items-center justify-between max-w-4xl">
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
                3
              </span>
            </router-link>
            
            <div class="h-8 w-8 rounded-full bg-gray-200 overflow-hidden">
              <img src="https://placehold.co/32x32" alt="프로필" class="h-full w-full object-cover" />
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="container mx-auto px-4 py-6 max-w-4xl">
      <div class="flex items-center justify-between mb-6">
        <div class="flex items-center">
          <router-link to="/admin" class="mr-2">
            <button class="p-2 border border-gray-300 rounded-md">
              <ArrowLeft class="h-5 w-5" />
            </button>
          </router-link>
          <h1 class="text-2xl font-bold">종결된 이벤트 목록</h1>
        </div>
        <div class="flex items-center gap-2">
          <div class="relative">
            <input 
              type="text" 
              placeholder="검색..." 
              class="pl-9 pr-4 py-2 border border-gray-300 rounded-md w-64"
            />
            <Search class="h-4 w-4 text-gray-400 absolute left-3 top-1/2 transform -translate-y-1/2" />
          </div>
        </div>
      </div>
      
      <div class="bg-white rounded-lg shadow overflow-hidden mb-6">
        <div class="p-4 border-b border-gray-200">
          <div class="flex justify-between items-center">
            <h2 class="font-semibold">종결된 이벤트</h2>
            <div class="flex items-center text-sm text-gray-500">
              <span>총 {{ closedReports.length }}개 이벤트</span>
            </div>
          </div>
        </div>
        
        <div class="p-4">
          <div v-if="closedReports.length === 0" class="text-center py-8 text-gray-500">
            <Archive class="h-12 w-12 mx-auto mb-2 text-gray-400" />
            <p>종결된 이벤트가 없습니다.</p>
          </div>
          <div v-else class="space-y-4">
            <div v-for="event in closedReports" :key="event.id" class="flex flex-col p-3 bg-white rounded-lg border">
              <div class="flex items-center justify-between">
                <div class="flex items-center">
                  <div class="h-10 w-10 rounded-full bg-gray-200 overflow-hidden mr-3">
                    <img :src="event.metadata.imageUrl" :alt="event.id" class="h-full w-full object-cover" />
                  </div>
                  <div>
                    <div class="flex items-center">
                      <span 
                        class="mr-2 px-2 py-0.5 rounded-full text-xs"
                        :class="{
                          'bg-red-100 text-red-800': event.riskLevel === '심각',
                          'bg-orange-100 text-orange-800': event.riskLevel === '높음',
                          'bg-yellow-100 text-yellow-800': event.riskLevel === '중간',
                          'bg-gray-100 text-gray-800': !event.riskLevel
                        }"
                      >
                        {{ event.riskLevel || '분류 없음' }}
                      </span>
                      <span class="font-medium">{{ event.isDeleted ? '오보' : '화재' }} #{{ event.id }}</span>
                      <!-- 소방관 확인 표시 -->
                      <span 
                        v-if="event.confirmedByFireDept" 
                        class="ml-2 px-2 py-0.5 rounded-full text-xs bg-green-100 text-green-800 border border-green-200 flex items-center"
                      >
                        <CheckCircle class="h-3 w-3 mr-1" />
                        소방관 확인
                      </span>
                    </div>
                    <div class="flex items-center text-sm text-gray-500 mt-1">
                      <MapPin class="h-3 w-3 mr-1" />
                      {{ event.location }}
                    </div>
                    <div class="flex items-center text-xs text-gray-400 mt-1">
                      <Clock class="h-3 w-3 mr-1" />
                      종결: {{ event.closedTime || event.time }}
                    </div>
                  </div>
                </div>
                <div class="flex items-center">
                  <span 
                    class="mr-2 px-2 py-0.5 rounded-full text-xs border"
                    :class="{
                      'border-gray-300 bg-gray-50 text-gray-800': event.status === '종결',
                      'border-red-300 bg-red-50 text-red-800': event.isDeleted
                    }"
                  >
                    {{ event.isDeleted ? '오보 처리됨' : '종결' }}
                  </span>
                  <router-link :to="`/report/${event.id}`">
                    <button class="px-3 py-1 bg-blue-500 text-white rounded-md text-sm">보기</button>
                  </router-link>
                </div>
              </div>
              
              <!-- 종결 사유 표시 -->
              <div class="mt-3 pt-3 border-t border-gray-100">
                <div class="flex flex-col">
                  <div class="flex items-start">
                    <InfoIcon class="h-4 w-4 text-gray-500 mr-2 mt-0.5" />
                    <div>
                      <span class="text-xs text-gray-500">종결 사유:</span>
                      <p class="text-sm text-gray-700">{{ event.closeReason || '사유가 입력되지 않았습니다.' }}</p>
                    </div>
                  </div>
                  <div class="flex justify-end mt-2">
                    <button 
                      @click="restoreEvent(event)" 
                      class="px-3 py-1 bg-gray-100 text-gray-700 border border-gray-300 rounded-md text-xs hover:bg-gray-200 transition-colors flex items-center"
                    >
                      <RefreshCw class="h-3 w-3 mr-1" />
                      재개
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="p-4 border-t border-gray-200">
          <div class="flex justify-center">
            <div class="flex gap-1">
              <button class="px-3 py-1.5 border border-gray-300 rounded-md text-sm">이전</button>
              <button class="px-3 py-1.5 bg-primary-600 text-white rounded-md text-sm">1</button>
              <button class="px-3 py-1.5 border border-gray-300 rounded-md text-sm">2</button>
              <button class="px-3 py-1.5 border border-gray-300 rounded-md text-sm">3</button>
              <button class="px-3 py-1.5 border border-gray-300 rounded-md text-sm">다음</button>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 하단 네비게이션 바 -->
    <div class="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 py-2 shadow-lg">
      <div class="container mx-auto max-w-lg">
        <div class="flex justify-around items-center">
          <router-link to="/" class="flex flex-col items-center text-gray-500">
            <Home class="h-6 w-6" />
            <span class="text-xs mt-1">홈</span>
          </router-link>
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

<style scoped>
/* 추가 스타일 */
.shadow-soft {
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}
</style>