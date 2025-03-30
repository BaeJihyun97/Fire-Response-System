<script>
export default {
  name: 'AllFireReportsView'
}
</script>

<script setup>
import { ref } from 'vue';
import { 
  AlertTriangle, 
  ArrowLeft,
  Bell, 
  Camera,
  CheckCircle,
  Clock, 
  Download,
  Filter,
  Flame,
  Home,
  MapPin, 
  Phone, 
  Search,
  Trash2,
  User,
  XCircle
} from 'lucide-vue-next';

// 화재 제보 데이터 (실제로는 API에서 가져옴)
const fireReports = ref([
  {
    id: "1",
    username: "소방지킴이",
    userAvatar: "https://placehold.co/40x40",
    location: "강남역, 서울",
    time: "5분 전",
    riskLevel: "높음",
    status: "진행 중",
    verified: true,
    confirmedByFireDept: true
  },
  {
    id: "2",
    username: "안전제일",
    userAvatar: "https://placehold.co/40x40",
    location: "여의도 공원, 서울",
    time: "15분 전",
    riskLevel: "중간",
    status: "진행 중",
    verified: true,
    confirmedByFireDept: false
  },
  {
    id: "8",
    username: "시민안전",
    userAvatar: "https://placehold.co/40x40",
    location: "강북구 수유동, 서울",
    time: "1시간 전",
    riskLevel: "심각",
    status: "진행 중",
    verified: true,
    confirmedByFireDept: true
  },
  {
    id: "9",
    username: "안전지킴이",
    userAvatar: "https://placehold.co/40x40",
    location: "중구 명동, 서울",
    time: "2시간 전",
    riskLevel: "높음",
    status: "진행 중",
    verified: true,
    confirmedByFireDept: false
  },
  {
    id: "10",
    username: "화재감시단",
    userAvatar: "https://placehold.co/40x40",
    location: "성동구 성수동, 서울",
    time: "3시간 전",
    riskLevel: "중간",
    status: "진행 중",
    verified: true,
    confirmedByFireDept: true
  }
]);

// 소방관 확인 토글
const toggleFirefighterConfirmation = (report) => {
  report.confirmedByFireDept = !report.confirmedByFireDept;
  if (report.confirmedByFireDept) {
    report.confirmedAt = new Date().toISOString();
    console.log(`소방관 확인: 화재 #${report.id}가 소방관에 의해 확인되었습니다.`);
  } else {
    report.confirmedAt = null;
    console.log(`소방관 확인 취소: 화재 #${report.id}의 소방관 확인이 취소되었습니다.`);
  }
};

// 화재 상태 변경 처리
const handleStatusChange = (report) => {
  console.log(`화재 #${report.id}의 상태가 "${report.status}"(으)로 변경되었습니다.`);
};

// 화재 이벤트 삭제 처리
const deleteFireEvent = (report) => {
  if (confirm(`화재 #${report.id}를 오보로 처리하시겠습니까?`)) {
    fireReports.value = fireReports.value.filter(r => r.id !== report.id);
    console.log(`화재 #${report.id}가 오보로 처리되었습니다.`);
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
          <h1 class="text-2xl font-bold">모든 화재 제보</h1>
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
          <button class="p-2 border border-gray-300 rounded-md">
            <Filter class="h-5 w-5" />
          </button>
        </div>
      </div>
      
      <div class="bg-white rounded-lg shadow overflow-hidden mb-6">
        <div class="p-4 border-b border-gray-200">
          <div class="flex justify-between items-center">
            <h2 class="font-semibold">화재 제보 목록</h2>
            <div class="flex items-center text-sm text-gray-500">
              <span>총 {{ fireReports.length }}개 제보</span>
            </div>
          </div>
        </div>
        
        <div class="p-4">
          <div v-if="fireReports.length === 0" class="text-center py-8 text-gray-500">
            <XCircle class="h-12 w-12 mx-auto mb-2 text-gray-400" />
            <p>화재 제보가 없습니다.</p>
          </div>
          <div v-else class="space-y-4">
            <div v-for="report in fireReports" :key="report.id" class="flex flex-col p-3 bg-white rounded-lg border">
              <div class="flex items-center justify-between">
                <div class="flex items-center">
                  <div class="h-10 w-10 rounded-full bg-gray-200 overflow-hidden mr-3">
                    <img :src="report.userAvatar" :alt="report.username" class="h-full w-full object-cover" />
                  </div>
                  <div>
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
                      <span class="font-medium">화재 #{{ report.id }}</span>
                      <!-- 소방관 확인 표시 -->
                      <span 
                        v-if="report.confirmedByFireDept" 
                        class="ml-2 px-2 py-0.5 rounded-full text-xs bg-green-100 text-green-800 border border-green-200 flex items-center"
                      >
                        <CheckCircle class="h-3 w-3 mr-1" />
                        소방관 확인
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
                <div class="flex items-center">
                  <span 
                    class="mr-2 px-2 py-0.5 rounded-full text-xs border"
                    :class="{
                      'border-blue-300 bg-blue-50 text-blue-800': report.status === '진행 중',
                      'border-gray-300 bg-gray-50 text-gray-800': report.status === '종결'
                    }"
                  >
                    {{ report.status }}
                  </span>
                  <router-link :to="`/report/${report.id}`">
                    <button class="px-3 py-1 bg-blue-500 text-white rounded-md text-sm">보기</button>
                  </router-link>
                </div>
              </div>
              
              <!-- 상태 관리 컨트롤 추가 -->
              <div class="mt-3 pt-3 border-t border-gray-100 grid grid-cols-3 gap-3">
                <div>
                  <label class="block text-xs text-gray-500 mb-1">화재 상태 변경</label>
                  <select 
                    v-model="report.status" 
                    class="w-full text-sm border border-gray-300 rounded-md p-1.5"
                    @change="handleStatusChange(report)"
                  >
                    <option value="진행 중">진행 중</option>
                    <option value="종결">종결</option>
                  </select>
                </div>
                <div>
                  <label class="block text-xs text-gray-500 mb-1">소방관 확인</label>
                  <button 
                    @click="toggleFirefighterConfirmation(report)" 
                    class="w-full py-1.5 border rounded-md text-sm transition-colors flex items-center justify-center"
                    :class="report.confirmedByFireDept 
                      ? 'bg-green-100 text-green-700 border-green-300 hover:bg-green-200' 
                      : 'bg-blue-100 text-blue-700 border-blue-300 hover:bg-blue-200'"
                  >
                    <CheckCircle class="h-4 w-4 mr-1" />
                    {{ report.confirmedByFireDept ? '확인됨' : '확인하기' }}
                  </button>
                </div>
                <div>
                  <label class="block text-xs text-gray-500 mb-1">이벤트 관리</label>
                  <button 
                    @click="deleteFireEvent(report)" 
                    class="w-full py-1.5 bg-red-100 text-red-700 border border-red-300 rounded-md text-sm hover:bg-red-200 transition-colors flex items-center justify-center"
                  >
                    <Trash2 class="h-4 w-4 mr-1" />
                    화재 아님 (삭제)
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="p-4 border-t border-gray-200">
          <div class="flex justify-between items-center">
            <div class="flex gap-1">
              <button class="px-3 py-1.5 border border-gray-300 rounded-md text-sm">이전</button>
              <button class="px-3 py-1.5 bg-primary-600 text-white rounded-md text-sm">1</button>
              <button class="px-3 py-1.5 border border-gray-300 rounded-md text-sm">2</button>
              <button class="px-3 py-1.5 border border-gray-300 rounded-md text-sm">3</button>
              <button class="px-3 py-1.5 border border-gray-300 rounded-md text-sm">다음</button>
            </div>
            <button class="px-3 py-1.5 border border-gray-300 rounded-md text-sm flex items-center">
              <Download class="h-4 w-4 mr-1" />
              내보내기
            </button>
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