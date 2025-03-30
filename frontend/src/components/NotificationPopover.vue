<template>
    <div class="relative">
      <button 
        class="p-2 rounded-full bg-white shadow-soft border border-gray-100 relative"
        @click="toggleNotifications"
      >
        <Bell class="h-5 w-5 text-secondary-500" />
        <!-- 읽지 않은 알림이 있을 경우 표시할 배지 -->
        <span 
          v-if="unreadCount > 0" 
          class="absolute -top-1 -right-1 bg-primary-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center"
        >
          {{ unreadCount > 9 ? '9+' : unreadCount }}
        </span>
      </button>
      
      <!-- 알림 팝오버 -->
      <div 
        v-if="isOpen" 
        class="absolute right-0 mt-2 w-80 bg-white rounded-xl shadow-lg z-50 overflow-hidden"
      >
        <div class="p-3 border-b border-gray-100 flex justify-between items-center">
          <h3 class="font-medium text-gray-800">알림</h3>
          <button 
            v-if="unreadCount > 0"
            @click="markAllAsRead" 
            class="text-xs text-primary-600 hover:text-primary-700"
          >
            모두 읽음 표시
          </button>
        </div>
        
        <div class="overflow-y-auto max-h-[60vh]">
          <div v-if="notifications.length === 0" class="p-4 text-center text-gray-500">
            <BellOff class="h-6 w-6 mx-auto mb-2 opacity-50" />
            <p class="text-sm">알림이 없습니다</p>
          </div>
          
          <div v-else>
            <div 
              v-for="notification in notifications" 
              :key="notification.id"
              :class="[
                'p-3 border-b border-gray-100 hover:bg-gray-50 transition-colors duration-150',
                notification.read ? 'bg-white' : 'bg-primary-50'
              ]"
            >
              <div class="flex items-start">
                <!-- 알림 타입에 따른 아이콘 -->
                <div :class="[
                  'rounded-full p-2 mr-3 flex-shrink-0',
                  getNotificationTypeClass(notification.type)
                ]">
                  <component :is="getNotificationIcon(notification.type)" class="h-4 w-4" />
                </div>
                
                <div class="flex-1">
                  <div class="flex justify-between items-start">
                    <p class="text-sm font-medium text-gray-800">{{ notification.title }}</p>
                    <span class="text-xs text-gray-500 ml-2">{{ notification.time }}</span>
                  </div>
                  <p class="text-xs text-gray-600 mt-1">{{ notification.message }}</p>
                  
                  <!-- 알림 액션 버튼 -->
                  <div v-if="notification.actionText" class="mt-2">
                    <button 
                      @click="handleAction(notification)"
                      class="text-xs font-medium text-primary-600 hover:text-primary-700"
                    >
                      {{ notification.actionText }}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="p-3 border-t border-gray-100 text-center">
          <router-link to="/notifications" class="text-xs text-secondary-600 hover:text-secondary-700">
            모든 알림 보기
          </router-link>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, computed } from 'vue';
  import { 
    Bell, 
    BellOff, 
    Flame, // Fire 대신 Flame 사용
    Heart, 
    ShieldCheck, // Shield 대신 ShieldCheck 사용
    Info, 
    MessageSquare 
  } from 'lucide-vue-next';
  
  const props = defineProps({
    isOpen: {
      type: Boolean,
      default: false
    },
    notifications: {
      type: Array,
      default: () => []
    }
  });
  
  const emit = defineEmits(['toggle', 'mark-all-read', 'action']);
  
  // 읽지 않은 알림 개수 계산
  const unreadCount = computed(() => {
    return props.notifications.filter(notification => !notification.read).length;
  });
  
  // 알림 팝오버 토글
  const toggleNotifications = () => {
    emit('toggle');
  };
  
  // 모든 알림 읽음 표시
  const markAllAsRead = () => {
    emit('mark-all-read');
  };
  
  // 알림 액션 처리
  const handleAction = (notification) => {
    emit('action', notification);
  };
  
  // 알림 타입에 따른 아이콘 반환
  const getNotificationIcon = (type) => {
    switch (type) {
      case 'emergency':
        return Flame; // Fire 대신 Flame 사용
      case 'verification':
        return ShieldCheck; // Shield 대신 ShieldCheck 사용
      case 'social':
        return MessageSquare;
      case 'like':
        return Heart;
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
      case 'like':
        return 'bg-pink-100 text-pink-600';
      case 'info':
        return 'bg-yellow-100 text-yellow-600';
      default:
        return 'bg-gray-100 text-gray-600';
    }
  };
  </script>
  
  <style scoped>
  /* 알림 팝오버 애니메이션 */
  @keyframes slideDown {
    from {
      opacity: 0;
      transform: translateY(-10px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
  
  .absolute {
    animation: slideDown 0.2s ease-out;
  }
  </style>