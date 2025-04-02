<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 헤더 -->
    <div class="bg-white border-b border-gray-200 sticky top-0 z-10">
      <div class="container mx-auto px-4 py-3 max-w-lg">
        <div class="flex items-center justify-between">
          <div class="flex items-center">
            <button @click="$router.go(-1)" class="mr-3">
              <ChevronLeft class="h-6 w-6 text-gray-600" />
            </button>
            <h1 class="text-xl font-bold text-gray-800">알림</h1>
          </div>
          <button 
            v-if="notificationStore.unreadCount > 0"
            @click="notificationStore.markAllAsRead()" 
            class="text-sm text-primary-600 hover:text-primary-700"
          >
            모두 읽음 표시
          </button>
        </div>
      </div>
    </div>
    
    <!-- 알림 필터 -->
    <div class="container mx-auto px-4 py-3 max-w-lg">
      <div class="flex space-x-2 overflow-x-auto pb-2">
        <button 
          @click="activeFilter = 'all'" 
          :class="[
            'px-3 py-1 rounded-full text-sm whitespace-nowrap',
            activeFilter === 'all' 
              ? 'bg-primary-600 text-white' 
              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
          ]"
        >
          전체 알림
        </button>
        <button 
          @click="activeFilter = 'emergency'" 
          :class="[
            'px-3 py-1 rounded-full text-sm whitespace-nowrap',
            activeFilter === 'emergency' 
              ? 'bg-red-600 text-white' 
              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
          ]"
        >
          긴급 알림
        </button>
        <button 
          @click="activeFilter = 'verification'" 
          :class="[
            'px-3 py-1 rounded-full text-sm whitespace-nowrap',
            activeFilter === 'verification' 
              ? 'bg-green-600 text-white' 
              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
          ]"
        >
          확인 알림
        </button>
        <button 
          @click="activeFilter = 'social'" 
          :class="[
            'px-3 py-1 rounded-full text-sm whitespace-nowrap',
            activeFilter === 'social' 
              ? 'bg-blue-600 text-white' 
              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
          ]"
        >
          소셜 알림
        </button>
      </div>
    </div>
    
    <!-- 알림 목록 -->
    <div class="container mx-auto px-4 py-2 max-w-lg pb-24">
      <div v-if="filteredNotifications.length === 0" class="py-10 text-center">
        <BellOff class="h-12 w-12 mx-auto mb-3 text-gray-400" />
        <p class="text-gray-500">알림이 없습니다</p>
      </div>
      
      <div v-else class="divide-y divide-gray-100">
        <div 
          v-for="notification in filteredNotifications" 
          :key="notification.id"
          :class="[
            'py-4 px-2 hover:bg-gray-50 transition-colors duration-150',
            notification.read ? '' : 'bg-primary-50'
          ]"
        >
          <div class="flex">
            <!-- 알림 타입에 따른 아이콘 - 고정 크기 추가 -->
            <div :class="[
              'rounded-full p-2 mr-4 flex-shrink-0 h-10 w-10 flex items-center justify-center',
              getNotificationTypeClass(notification.type)
            ]">
              <component :is="getNotificationIcon(notification.type)" class="h-5 w-5" />
            </div>
            
            <div class="flex-1">
              <div class="flex justify-between items-start">
                <p class="font-medium text-gray-800">{{ notification.title }}</p>
                <span class="text-xs text-gray-500 ml-2 mt-1">{{ notification.time }}</span>
              </div>
              <p class="text-sm text-gray-600 mt-1">{{ notification.message }}</p>
              
              <!-- 알림 액션 버튼 -->
              <div v-if="notification.actionText" class="mt-3">
                <button 
                  @click="handleNotificationAction(notification)"
                  class="px-3 py-1 bg-primary-100 text-primary-700 rounded-full text-sm hover:bg-primary-200 transition-colors"
                >
                  {{ notification.actionText }}
                </button>
              </div>
              
              <!-- 읽음/안읽음 토글 -->
              <div class="mt-3 flex justify-end">
                <button 
                  @click="notificationStore.toggleReadStatus(notification.id)"
                  class="text-xs text-gray-500 hover:text-gray-700"
                >
                  {{ notification.read ? '안읽음으로 표시' : '읽음으로 표시' }}
                </button>
              </div>
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
          <router-link to="/map" class="flex flex-col items-center text-gray-500">
            <Map class="h-6 w-6" />
            <span class="text-xs mt-1">지도</span>
          </router-link>
          <router-link to="/report">
            <button class="rounded-full bg-gradient-to-r from-primary-600 to-primary-500 h-14 w-14 flex items-center justify-center text-white shadow-lg transform hover:scale-105 transition-transform duration-200">
              <Camera class="h-7 w-7" />
            </button>
          </router-link>
          <router-link to="/notifications" class="flex flex-col items-center text-primary-600">
            <Bell class="h-6 w-6" />
            <span class="text-xs mt-1 font-medium">알림</span>
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

<script>
export default {
  name: 'NotificationsPage'
}
</script>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { 
  Bell, 
  BellOff, 
  Flame, 
  AlertTriangle, 
  ShieldCheck, 
  Info, 
  MessageSquare,
  ChevronLeft,
  Home,
  Search,
  Camera,
  User,
  Map
} from 'lucide-vue-next';
import { useRouter } from 'vue-router';
import { useNotificationStore } from '../stores/notificationStore';
import { getAllNotifications } from '../services/notificationService';

const router = useRouter();
const notificationStore = useNotificationStore();
const activeFilter = ref('all');
const notifications = ref([]);
const isLoading = ref(true);

// 토큰에서 userId 추출 함수
const getUserIdFromToken = () => {
  const token = localStorage.getItem('token');
  if (token) {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));
      
      const payload = JSON.parse(jsonPayload);
      return payload.sub || payload.userId;
    } catch (error) {
      console.error('토큰 디코딩 실패:', error);
      return null;
    }
  }
  return null;
};

// 알림 목록 가져오기
const fetchNotifications = async () => {
  try {
    isLoading.value = true;
    const userId = getUserIdFromToken();
    if (!userId) {
      console.error('사용자 ID를 찾을 수 없습니다.');
      return;
    }
    
    notifications.value = await getAllNotifications(userId);
  } catch (error) {
    console.error('알림 목록을 가져오는데 실패했습니다:', error);
  } finally {
    isLoading.value = false;
  }
};

// 필터링된 알림
const filteredNotifications = computed(() => {
  if (activeFilter.value === 'all') {
    return notifications.value;
  }
  return notifications.value.filter(notification => 
    notification.type === activeFilter.value
  );
});

// 알림 액션 처리
const handleNotificationAction = (notification) => {
  router.push(notification.actionLink);
};

// 컴포넌트 마운트 시 알림 목록 가져오기
onMounted(() => {
  fetchNotifications();
});

// 알림 타입에 따른 아이콘 반환
const getNotificationIcon = (type) => {
  switch (type) {
    case 'emergency':
      return Flame;
    case 'verification':
      return ShieldCheck;
    case 'social':
      return MessageSquare;
    case 'danger':
      return AlertTriangle;
    case 'info':
      return Info;
    default:
      return Bell;
  }
};

// 알림 타입에 따른 스타일 클래스 반환
const getNotificationTypeClass = (type) => {
  switch (type) {
    case 'emergency':
      return 'bg-red-100 text-red-600';
    case 'verification':
      return 'bg-green-100 text-green-600';
    case 'social':
      return 'bg-blue-100 text-blue-600';
    case 'danger':
      return 'bg-orange-100 text-orange-600';
    case 'info':
      return 'bg-yellow-100 text-yellow-600';
    default:
      return 'bg-gray-100 text-gray-600';
  }
};
</script>