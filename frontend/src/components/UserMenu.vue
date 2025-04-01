<template>
    <div class="relative">
      <!-- 사용자 아바타 버튼 -->
      <button 
        @click="toggleMenu" 
        class="flex items-center focus:outline-none"
        ref="menuButton"
      >
        <div class="h-8 w-8 rounded-full overflow-hidden border-2 border-white">
          <img 
            :src="avatarUrl" 
            alt="사용자" 
            class="h-full w-full object-cover"
          />
        </div>
      </button>
      
      <!-- 드롭다운 메뉴 -->
      <div 
        v-if="isMenuOpen" 
        class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-10"
        ref="menu"
      >
        <!-- 사용자 정보 -->
        <div class="px-4 py-2 border-b border-gray-100">
          <p class="text-sm font-medium text-gray-900">사용자</p>
          <p class="text-xs text-gray-500 truncate">user@example.com</p>
        </div>
        
        <!-- 메뉴 항목 -->
        <router-link 
          to="/profile" 
          class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
          @click="isMenuOpen = false"
        >
          <div class="flex items-center">
            <User class="h-4 w-4 mr-2" />
            <span>내 프로필</span>
          </div>
        </router-link>
        
        <router-link 
          to="/notifications" 
          class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
          @click="isMenuOpen = false"
        >
          <div class="flex items-center">
            <Bell class="h-4 w-4 mr-2" />
            <span>알림</span>
          </div>
        </router-link>
        
        <div class="border-t border-gray-100 mt-1 pt-1">
          <router-link 
            to="/login" 
            class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
            @click="isMenuOpen = false"
          >
            <div class="flex items-center">
              <LogIn class="h-4 w-4 mr-2" />
              <span>로그인</span>
            </div>
          </router-link>
          
          <router-link 
            to="/register" 
            class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
            @click="isMenuOpen = false"
          >
            <div class="flex items-center">
              <UserPlus class="h-4 w-4 mr-2" />
              <span>회원가입</span>
            </div>
          </router-link>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, onUnmounted } from 'vue';
  import { Bell, LogIn, User, UserPlus } from 'lucide-vue-next';
  import { useRouter, useRoute } from 'vue-router';
  
  const router = useRouter();
  const route = useRoute();
  
  const isMenuOpen = ref(false);
  const menuButton = ref(null);
  const menu = ref(null);
  
  // 아바타 URL 생성
  const avatarUrl = 'https://ui-avatars.com/api/?name=User&background=random';
  
  // 메뉴 토글 함수
  const toggleMenu = () => {
    isMenuOpen.value = !isMenuOpen.value;
  };
  
  // 메뉴 외부 클릭 시 닫기
  const handleClickOutside = (event) => {
    if (
      isMenuOpen.value && 
      menuButton.value && 
      menu.value && 
      !menuButton.value.contains(event.target) && 
      !menu.value.contains(event.target)
    ) {
      isMenuOpen.value = false;
    }
  };
  
  // 이벤트 리스너 등록 및 해제
  onMounted(() => {
    document.addEventListener('click', handleClickOutside);
  });
  
  onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside);
  });
  </script>
  