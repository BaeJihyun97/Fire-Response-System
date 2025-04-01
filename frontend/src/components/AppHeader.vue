<template>
    <header class="bg-white border-b border-gray-200 py-2 shadow-sm">
      <div class="container mx-auto max-w-lg px-4">
        <div class="flex justify-between items-center">
          <div class="flex items-center">
            <button 
              v-if="showBackButton"
              @click="goBack" 
              class="p-2 mr-2 rounded-full hover:bg-gray-100"
            >
              <ChevronLeft class="h-5 w-5 text-gray-700" />
            </button>
            <router-link to="/" class="flex items-center" v-else>
              <Flame class="h-5 w-5 text-primary-600 mr-2" />
            </router-link>
            <h1 class="font-bold text-xl text-gray-900">{{ title }}</h1>
          </div>
          <div class="flex items-center space-x-2">
            <slot name="actions"></slot>
            <UserMenu />
          </div>
        </div>
      </div>
    </header>
  </template>
  
  <script setup>
  import { useRouter } from 'vue-router';
  import { ChevronLeft, Flame } from 'lucide-vue-next';
  import UserMenu from './UserMenu.vue';
  
  const router = useRouter();
  
  // Props
  const props = defineProps({
    title: {
      type: String,
      default: '화재알리미'
    },
    showBackButton: {
      type: Boolean,
      default: false
    }
  });
  
  // 뒤로 가기 함수
  const goBack = () => {
    router.back();
  };
  </script>
  
  