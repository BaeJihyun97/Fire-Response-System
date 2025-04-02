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
      
      <!-- 상단 네비게이션 바 -->
      <div class="bg-white border-b border-gray-200 py-2 shadow-sm">
        <div class="container mx-auto max-w-lg px-4">
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
                <img :src="userProfile.avatar || 'https://placehold.co/32x32'" alt="프로필" class="h-full w-full object-cover" />
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 메인 컨텐츠 -->
      <div class="container mx-auto px-4 py-4 max-w-lg pb-24">
        <div class="bg-white rounded-lg shadow-md overflow-hidden mb-6">
          <!-- 프로필 헤더 -->
          <div class="relative">
            <div class="h-32 bg-gradient-to-r from-primary-500 to-primary-400"></div>
            <div class="absolute bottom-0 left-1/2 transform -translate-x-1/2 translate-y-1/2">
              <div class="relative">
                <div class="h-24 w-24 rounded-full border-4 border-white bg-gray-200 overflow-hidden">
                  <img :src="userProfile.avatar || 'https://placehold.co/96x96'" alt="프로필" class="h-full w-full object-cover" />
                </div>
                <button @click="openImagePicker" class="absolute bottom-0 right-0 bg-primary-600 text-white rounded-full p-1.5 shadow-md">
                  <Camera class="h-4 w-4" />
                </button>
                <input 
                  type="file" 
                  ref="fileInput" 
                  accept="image/*" 
                  class="hidden" 
                  @change="handleImageChange"
                />
              </div>
            </div>
          </div>
          
          <!-- 프로필 정보 -->
          <div class="pt-16 px-6 pb-6">
            <h1 class="text-xl font-bold text-center text-gray-900 mb-1">{{ userProfile.family_name }} {{ userProfile.given_name }}</h1>
            <p class="text-sm text-gray-500 text-center mb-4">{{ userProfile.email }}</p>
            
            <div class="flex justify-center space-x-4 mb-6">
              <div class="text-center">
                <p class="text-lg font-bold text-primary-600">{{ userProfile.reportCount }}</p>
                <p class="text-xs text-gray-500">제보</p>
              </div>
              <div class="text-center">
                <p class="text-lg font-bold text-primary-600">{{ userProfile.confirmCount }}</p>
                <p class="text-xs text-gray-500">확인</p>
              </div>
              <div class="text-center">
                <p class="text-lg font-bold text-primary-600">{{ userProfile.level }}</p>
                <p class="text-xs text-gray-500">레벨</p>
              </div>
            </div>
            
            <div v-if="userProfile.verified" class="flex items-center justify-center mb-6">
              <span class="px-3 py-1 rounded-full text-xs bg-green-100 text-green-800 border border-green-200 flex items-center">
                <CheckCircle class="h-3 w-3 mr-1" />
                인증된 사용자
              </span>
            </div>
          </div>
        </div>
        
        <!-- 프로필 설정 -->
        <div class="bg-white rounded-lg shadow-md overflow-hidden mb-6">
          <div class="p-4 border-b border-gray-200">
            <h2 class="font-semibold">프로필 설정</h2>
          </div>
          
          <div class="p-4 space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">이름</label>
              <input 
                v-model="userProfile.name" 
                type="text" 
                class="w-full border border-gray-300 rounded-md p-2 text-sm"
                placeholder="이름을 입력하세요"
              />
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">이메일</label>
              <input 
                v-model="userProfile.email" 
                type="email" 
                class="w-full border border-gray-300 rounded-md p-2 text-sm"
                placeholder="이메일을 입력하세요"
              />
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">전화번호</label>
              <input 
                v-model="userProfile.phone" 
                type="tel" 
                class="w-full border border-gray-300 rounded-md p-2 text-sm"
                placeholder="전화번호를 입력하세요"
              />
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">주소</label>
              <input 
                v-model="userProfile.address" 
                type="text" 
                class="w-full border border-gray-300 rounded-md p-2 text-sm"
                placeholder="주소를 입력하세요"
              />
            </div>
            
            <button 
              @click="saveProfile" 
              class="w-full bg-primary-600 text-white rounded-md py-2 text-sm font-medium hover:bg-primary-700 transition-colors"
            >
              프로필 저장
            </button>
          </div>
        </div>
        
        <!-- 계정 설정 -->
        <div class="bg-white rounded-lg shadow-md overflow-hidden mb-6">
          <div class="p-4 border-b border-gray-200">
            <h2 class="font-semibold">계정 설정</h2>
          </div>
          
          <div class="p-4 space-y-4">
            <button 
              @click="changePassword" 
              class="w-full border border-gray-300 rounded-md py-2 text-sm font-medium hover:bg-gray-50 transition-colors flex items-center justify-center"
            >
              <Lock class="h-4 w-4 mr-2" />
              비밀번호 변경
            </button>
            
            <button 
              @click="verifyAccount" 
              class="w-full border border-gray-300 rounded-md py-2 text-sm font-medium hover:bg-gray-50 transition-colors flex items-center justify-center"
              :disabled="userProfile.verified"
            >
              <CheckCircle class="h-4 w-4 mr-2" :class="userProfile.verified ? 'text-green-500' : ''" />
              {{ userProfile.verified ? '인증 완료' : '계정 인증하기' }}
            </button>
            
            <button 
              @click="logout" 
              class="w-full border border-gray-300 rounded-md py-2 text-sm font-medium text-red-600 hover:bg-red-50 transition-colors flex items-center justify-center"
            >
              <LogOut class="h-4 w-4 mr-2" />
              로그아웃
            </button>
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
            <router-link to="/notifications" class="flex flex-col items-center text-gray-500">
              <Bell class="h-6 w-6" />
              <span class="text-xs mt-1">알림</span>
            </router-link>
            <router-link to="/profile" class="flex flex-col items-center text-primary-600">
              <User class="h-6 w-6" />
              <span class="text-xs mt-1 font-medium">내정보</span>
            </router-link>
          </div>
        </div>
      </div>
      
      <!-- 비밀번호 변경 모달 -->
      <div v-if="showPasswordModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div class="bg-white rounded-lg shadow-lg p-6 w-full max-w-md mx-4">
          <h3 class="text-lg font-bold mb-4">비밀번호 변경</h3>
          
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-1">현재 비밀번호</label>
            <input 
              v-model="passwordForm.current" 
              type="password" 
              class="w-full border border-gray-300 rounded-md p-2 text-sm"
              placeholder="현재 비밀번호를 입력하세요"
            />
          </div>
          
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-1">새 비밀번호</label>
            <input 
              v-model="passwordForm.new" 
              type="password" 
              class="w-full border border-gray-300 rounded-md p-2 text-sm"
              placeholder="새 비밀번호를 입력하세요"
            />
          </div>
          
          <div class="mb-6">
            <label class="block text-sm font-medium text-gray-700 mb-1">새 비밀번호 확인</label>
            <input 
              v-model="passwordForm.confirm" 
              type="password" 
              class="w-full border border-gray-300 rounded-md p-2 text-sm"
              placeholder="새 비밀번호를 다시 입력하세요"
            />
          </div>
          
          <div class="flex justify-end space-x-3">
            <button 
              @click="showPasswordModal = false" 
              class="px-4 py-2 border border-gray-300 rounded-md text-sm"
            >
              취소
            </button>
            <button 
              @click="updatePassword" 
              class="px-4 py-2 bg-primary-600 text-white rounded-md text-sm hover:bg-primary-700"
            >
              변경하기
            </button>
          </div>
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
  import { ref, onMounted } from 'vue';
  import { AlertTriangle, Bell, Camera, CheckCircle, Flame, Home, Lock, LogOut, Map, Phone, User } from 'lucide-vue-next';
  
  // 사용자 프로필 데이터
  const userProfile = ref({
    name: '',
    email: '',
    phone: '',
    address: '',
    avatar: '',
    reportCount: 0,
    confirmCount: 0,
    level: 1,
    verified: false
  });
  
  // 토큰 디코딩 함수
  const decodeToken = () => {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
          return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        
        const payload = JSON.parse(jsonPayload);
        userProfile.value = {
          name: payload.name,
          given_name: payload.given_name,
          family_name: payload.family_name,
          email: payload.email,
          phone: '', // 토큰에 없는 정보는 기본값 유지
          address: '', // 토큰에 없는 정보는 기본값 유지
          avatar: `https://ui-avatars.com/api/?name=${encodeURIComponent(payload.family_name + payload.given_name)}&background=random`,
          reportCount: 0, // API에서 가져와야 함
          confirmCount: 0, // API에서 가져와야 함
          level: 1, // API에서 가져와야 함
          verified: payload.email_verified || false
        };
      } catch (error) {
        console.error('토큰 디코딩 실패:', error);
        localStorage.removeItem('token');
      }
    }
  };
  
  // 비밀번호 변경 폼
  const passwordForm = ref({
    current: '',
    new: '',
    confirm: ''
  });
  
  // 상태 변수
  const showPasswordModal = ref(false);
  const showToast = ref(false);
  const toastMessage = ref('');
  const fileInput = ref(null);
  
  // 토스트 메시지 표시 함수
  const showToastMessage = (message) => {
    toastMessage.value = message;
    showToast.value = true;
    setTimeout(() => {
      showToast.value = false;
    }, 3000);
  };
  
  // 프로필 저장
  const saveProfile = () => {
    // 실제로는 API 호출하여 서버에 저장
    console.log('프로필 저장:', userProfile.value);
    showToastMessage('프로필이 저장되었습니다.');
  };
  
  // 비밀번호 변경 모달 열기
  const changePassword = () => {
    showPasswordModal.value = true;
  };
  
  // 비밀번호 업데이트
  const updatePassword = () => {
    // 비밀번호 유효성 검사
    if (!passwordForm.value.current) {
      showToastMessage('현재 비밀번호를 입력해주세요.');
      return;
    }
    
    if (!passwordForm.value.new) {
      showToastMessage('새 비밀번호를 입력해주세요.');
      return;
    }
    
    if (passwordForm.value.new !== passwordForm.value.confirm) {
      showToastMessage('새 비밀번호가 일치하지 않습니다.');
      return;
    }
    
    // 실제로는 API 호출하여 비밀번호 변경
    console.log('비밀번호 변경:', passwordForm.value);
    
    // 폼 초기화 및 모달 닫기
    passwordForm.value = {
      current: '',
      new: '',
      confirm: ''
    };
    showPasswordModal.value = false;
    showToastMessage('비밀번호가 변경되었습니다.');
  };
  
  // 계정 인증
  const verifyAccount = () => {
    if (userProfile.value.verified) {
      return;
    }
    
    // 실제로는 인증 프로세스 시작
    showToastMessage('인증 이메일이 발송되었습니다. 이메일을 확인해주세요.');
  };
  
  // 로그아웃
  const logout = () => {
    localStorage.removeItem('token');
    showToastMessage('로그아웃 되었습니다.');
    router.push('/login');
  };
  
  // 이미지 선택기 열기
  const openImagePicker = () => {
    fileInput.value.click();
  };
  
  // 이미지 변경 처리
  const handleImageChange = (event) => {
    const file = event.target.files[0];
    if (!file) return;
    
    // 이미지 파일 유효성 검사
    if (!file.type.startsWith('image/')) {
      showToastMessage('이미지 파일만 업로드 가능합니다.');
      return;
    }
    
    // 파일 크기 제한 (5MB)
    if (file.size > 5 * 1024 * 1024) {
      showToastMessage('파일 크기는 5MB 이하여야 합니다.');
      return;
    }
    
    // 이미지 미리보기 생성
    const reader = new FileReader();
    reader.onload = (e) => {
      userProfile.value.avatar = e.target.result;
    };
    reader.readAsDataURL(file);
    
    // 실제로는 API를 통해 이미지 업로드
    console.log('프로필 이미지 변경:', file);
  };
  
  // 컴포넌트 마운트 시 초기화
  onMounted(() => {
    decodeToken();
  });
  </script>
  
  <style scoped>
  .shadow-soft {
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  }
  </style>