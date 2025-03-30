<template>
    <div class="min-h-screen bg-gray-50 flex flex-col">
      <!-- 헤더 -->
      <div class="bg-white border-b border-gray-200 sticky top-0 z-10">
        <div class="container mx-auto px-4 py-3 max-w-lg">
          <div class="flex items-center justify-between">
            <h1 class="text-xl font-bold text-gray-800">제보 완료</h1>
          </div>
        </div>
      </div>
      
      <!-- 메인 컨텐츠 -->
      <div class="container mx-auto px-4 py-8 max-w-lg flex-1 flex flex-col items-center justify-center">
        <div class="bg-white rounded-xl shadow-lg p-8 w-full max-w-md text-center">
          <div class="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
            <CheckCircle class="h-12 w-12 text-green-600" />
          </div>
          
          <h2 class="text-2xl font-bold text-gray-800 mb-2">제보가 접수되었습니다</h2>
          <p class="text-gray-600 mb-6">
            소중한 제보에 감사드립니다. 제보하신 내용은 소방서에서 확인 후 처리될 예정입니다.
          </p>
          
          <div class="bg-primary-50 border border-primary-200 rounded-lg p-4 mb-6 text-left">
            <p class="text-sm text-primary-800">
              <span class="font-medium">중요:</span> 긴급한 화재 상황에서는 반드시 119로 신고해주세요.
            </p>
          </div>
          
          <div class="space-y-3">
            <router-link to="/">
              <button class="w-full py-3 bg-primary-600 text-white rounded-lg font-medium hover:bg-primary-700">
                홈으로 돌아가기
              </button>
            </router-link>
            
            <router-link to="/notifications">
              <button class="w-full py-3 border border-gray-300 text-gray-700 rounded-lg font-medium hover:bg-gray-50">
                알림 확인하기
              </button>
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { CheckCircle } from 'lucide-vue-next';
  </script>