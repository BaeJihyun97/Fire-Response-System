<template>
    <div class="min-h-screen bg-gray-50">
      <!-- 헤더 -->
      <AppHeader title="회원가입" :showBackButton="true" />
      
      <!-- 메인 컨텐츠 -->
      <div class="container mx-auto px-4 py-8 max-w-lg">
        <!-- 로고 및 환영 메시지 -->
        <div class="flex flex-col items-center mb-8">
          <div class="flex items-center justify-center h-20 w-20 bg-gradient-to-r from-primary-600 to-primary-500 rounded-full mb-4 shadow-lg">
            <Flame class="h-10 w-10 text-white" />
          </div>
          <h2 class="text-2xl font-bold text-gray-900 mb-2">화재알리미 가입하기</h2>
          <p class="text-gray-600 text-center">안전한 지역사회를 위한 실시간 화재 정보 서비스에 가입하세요</p>
        </div>
        
        <!-- 회원가입 폼 -->
        <div class="bg-white rounded-lg shadow-md overflow-hidden mb-6">
          <div class="p-6">
            <form @submit.prevent="handleRegister">
              <div class="mb-4">
                <label for="username" class="block text-sm font-medium text-gray-700 mb-1">아이디</label>
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <User class="h-5 w-5 text-gray-400" />
                  </div>
                  <input 
                    v-model="username" 
                    type="text" 
                    id="username" 
                    class="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                    placeholder="아이디를 입력하세요"
                    required
                  />
                </div>
              </div>
              
              <div class="mb-4">
                <label for="email" class="block text-sm font-medium text-gray-700 mb-1">이메일</label>
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <Mail class="h-5 w-5 text-gray-400" />
                  </div>
                  <input 
                    v-model="email" 
                    type="email" 
                    id="email" 
                    class="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                    placeholder="이메일을 입력하세요"
                    required
                  />
                </div>
              </div>
              
              <div class="mb-4">
                <label for="password" class="block text-sm font-medium text-gray-700 mb-1">비밀번호</label>
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <Lock class="h-5 w-5 text-gray-400" />
                  </div>
                  <input 
                    v-model="password" 
                    :type="showPassword ? 'text' : 'password'" 
                    id="password" 
                    class="w-full pl-10 pr-10 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                    placeholder="비밀번호를 입력하세요"
                    required
                  />
                  <div class="absolute inset-y-0 right-0 pr-3 flex items-center">
                    <button 
                      type="button" 
                      @click="showPassword = !showPassword" 
                      class="text-gray-400 hover:text-gray-600 focus:outline-none"
                    >
                      <Eye v-if="showPassword" class="h-5 w-5" />
                      <EyeOff v-else class="h-5 w-5" />
                    </button>
                  </div>
                </div>
              </div>
              
              <div class="mb-6">
                <label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-1">비밀번호 확인</label>
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <Lock class="h-5 w-5 text-gray-400" />
                  </div>
                  <input 
                    v-model="confirmPassword" 
                    :type="showConfirmPassword ? 'text' : 'password'" 
                    id="confirmPassword" 
                    class="w-full pl-10 pr-10 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                    :class="{ 'border-red-500': passwordMismatch }"
                    placeholder="비밀번호를 다시 입력하세요"
                    required
                  />
                  <div class="absolute inset-y-0 right-0 pr-3 flex items-center">
                    <button 
                      type="button" 
                      @click="showConfirmPassword = !showConfirmPassword" 
                      class="text-gray-400 hover:text-gray-600 focus:outline-none"
                    >
                      <Eye v-if="showConfirmPassword" class="h-5 w-5" />
                      <EyeOff v-else class="h-5 w-5" />
                    </button>
                  </div>
                </div>
                <p v-if="passwordMismatch" class="text-xs text-red-500 mt-1">
                  비밀번호가 일치하지 않습니다.
                </p>
              </div>
              
              <div class="mb-6">
                <div class="flex items-center">
                  <input 
                    v-model="agreeTerms" 
                    id="agree-terms" 
                    type="checkbox" 
                    class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
                    required
                  />
                  <label for="agree-terms" class="ml-2 block text-sm text-gray-700">
                    <span>이용약관에 동의합니다</span>
                  </label>
                </div>
              </div>
              
              <button 
                type="submit" 
                class="w-full bg-gradient-to-r from-primary-600 to-primary-500 text-white py-2 px-4 rounded-md font-medium hover:from-primary-700 hover:to-primary-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 transition-colors"
                :disabled="isLoading || passwordMismatch"
              >
                <div class="flex items-center justify-center">
                  <Loader v-if="isLoading" class="animate-spin h-5 w-5 mr-2" />
                  <span>{{ isLoading ? '가입 중...' : '회원가입' }}</span>
                </div>
              </button>
            </form>
          </div>
        </div>
        
        <!-- 로그인 링크 -->
        <div class="text-center">
          <p class="text-sm text-gray-600">
            이미 계정이 있으신가요?
            <router-link to="/login" class="font-medium text-primary-600 hover:text-primary-500">
              로그인
            </router-link>
          </p>
        </div>
      </div>
      
      <!-- 알림 토스트 -->
      <div 
        v-if="showToast" 
        class="fixed bottom-24 left-1/2 transform -translate-x-1/2 bg-gray-800 text-white px-4 py-2 rounded-md shadow-lg text-sm"
      >
        {{ toastMessage }}
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, computed } from 'vue';
  import { useRouter } from 'vue-router';
  import { Eye, EyeOff, Flame, Loader, Lock, Mail, User } from 'lucide-vue-next';
  import AppHeader from '../components/AppHeader.vue';
  
  const router = useRouter();
  
  // 상태 변수
  const username = ref('');
  const email = ref('');
  const password = ref('');
  const confirmPassword = ref('');
  const showPassword = ref(false);
  const showConfirmPassword = ref(false);
  const agreeTerms = ref(false);
  const isLoading = ref(false);
  const showToast = ref(false);
  const toastMessage = ref('');
  
  // 비밀번호 일치 여부 확인
  const passwordMismatch = computed(() => {
    return confirmPassword.value && password.value !== confirmPassword.value;
  });
  
  // 토스트 메시지 표시 함수
  const showToastMessage = (message) => {
    toastMessage.value = message;
    showToast.value = true;
    setTimeout(() => {
      showToast.value = false;
    }, 3000);
  };
  
  // 회원가입 처리 함수
  const handleRegister = async () => {
    try {
      // 입력 검증
      if (!username.value || !email.value || !password.value || !confirmPassword.value) {
        showToastMessage('모든 필드를 입력해주세요.');
        return;
      }
      
      if (passwordMismatch.value) {
        showToastMessage('비밀번호가 일치하지 않습니다.');
        return;
      }
      
      if (!agreeTerms.value) {
        showToastMessage('이용약관에 동의해주세요.');
        return;
      }
      
      isLoading.value = true;
      
      // 회원가입 시뮬레이션 (실제 구현 시 제거)
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // 회원가입 성공 처리
      showToastMessage('회원가입이 완료되었습니다!');
      
      // 로그인 페이지로 리디렉션
      setTimeout(() => {
        router.push('/login');
      }, 1000);
      
    } catch (error) {
      console.error('회원가입 오류:', error);
      showToastMessage('회원가입에 실패했습니다. 다시 시도해주세요.');
    } finally {
      isLoading.value = false;
    }
  };
  </script>
  