<template>
    <div class="min-h-screen bg-gray-100 p-4">
      <h1 class="text-2xl font-bold mb-4">알림 테스트 페이지</h1>
      
      <div class="bg-white rounded-lg shadow p-4 mb-4">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-lg font-medium">알림 버튼 테스트</h2>
          
          <!-- 알림 버튼 -->
          <div class="relative">
            <button 
              class="p-2 rounded-full bg-red-100 relative"
              @click="showNotifications = !showNotifications"
            >
              <Bell class="h-5 w-5 text-red-600" />
              <span 
                v-if="unreadCount > 0" 
                class="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center"
              >
                {{ unreadCount }}
              </span>
            </button>
            
            <!-- 알림 목록 -->
            <div 
              v-if="showNotifications" 
              class="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-lg z-50 overflow-hidden"
            >
              <div class="p-3 border-b">
                <h3 class="font-medium">알림</h3>
              </div>
              
              <div class="max-h-60 overflow-y-auto">
                <div 
                  v-for="notification in notifications" 
                  :key="notification.id"
                  class="p-3 border-b hover:bg-gray-50"
                >
                  <div class="flex">
                    <div class="bg-red-100 rounded-full p-2 mr-3">
                      <Flame class="h-4 w-4 text-red-600" />
                    </div>
                    <div>
                      <p class="text-sm font-medium">{{ notification.title }}</p>
                      <p class="text-xs text-gray-600">{{ notification.message }}</p>
                      <p class="text-xs text-gray-500 mt-1">{{ notification.time }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <p>이 페이지는 알림 기능을 테스트하기 위한 간단한 페이지입니다.</p>
      </div>
      
      <div class="bg-white rounded-lg shadow p-4">
        <h2 class="text-lg font-medium mb-2">알림 목록</h2>
        <ul class="space-y-2">
          <li 
            v-for="notification in notifications" 
            :key="notification.id"
            class="p-2 border rounded"
          >
            {{ notification.title }} - {{ notification.time }}
          </li>
        </ul>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, computed } from 'vue';
  import { Bell, Flame } from 'lucide-vue-next'; // Fire 대신 Flame 사용
  
  const showNotifications = ref(false);
  const notifications = ref([
    {
      id: 1,
      title: '긴급 화재 알림',
      message: '강남역 부근에 화재가 발생했습니다.',
      time: '5분 전',
      read: false
    },
    {
      id: 2,
      title: '화재 제보 확인',
      message: '귀하의 화재 제보가 확인되었습니다.',
      time: '30분 전',
      read: false
    },
    {
      id: 3,
      title: '댓글 알림',
      message: '안전지킴이님이 댓글을 남겼습니다.',
      time: '1시간 전',
      read: true
    }
  ]);
  
  const unreadCount = computed(() => {
    return notifications.value.filter(n => !n.read).length;
  });
  </script>