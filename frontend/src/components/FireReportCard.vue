<template>
  <div class="bg-white rounded-xl shadow-soft overflow-hidden mb-4">
    <!-- 헤더 -->
    <div class="p-4 flex items-center justify-between">
      <div class="flex items-center">
        <img :src="fire.userAvatar" alt="프로필" class="w-10 h-10 rounded-full mr-3" />
        <div>
          <div class="flex items-center">
            <h3 class="font-medium text-gray-900">{{ fire.username }}</h3>
            <div v-if="fire.verified" class="ml-2 bg-blue-100 text-blue-800 text-xs px-2 py-0.5 rounded-full flex items-center">
              <CheckCircle class="h-3 w-3 mr-1" />
              <span>소방서 확인</span>
            </div>
          </div>
          <div class="flex items-center text-xs text-gray-500 mt-0.5">
            <MapPin class="h-3 w-3 mr-1" />
            <span>{{ fire.location }}</span>
            <span class="mx-1">•</span>
            <span>{{ fire.time }}</span>
          </div>
        </div>
      </div>
      
      <div class="flex items-center">
        <span class="text-xs bg-gray-100 text-gray-800 px-2 py-1 rounded-full">{{ fire.distance }}</span>
      </div>
    </div>
    
    <!-- 이미지 -->
    <div class="relative">
      <img :src="fire.imageUrl" alt="화재 이미지" class="w-full h-48 object-cover" />
      
      <!-- 위험도 표시 -->
      <div class="absolute top-3 right-3 px-3 py-1 rounded-full text-xs font-medium"
        :class="{
          'bg-red-500 text-white': fire.riskLevel === '높음',
          'bg-yellow-500 text-white': fire.riskLevel === '중간',
          'bg-green-500 text-white': fire.riskLevel === '낮음'
        }"
      >
        위험도: {{ fire.riskLevel }}
      </div>
    </div>
    
    <!-- 내용 -->
    <div class="p-4">
      <p class="text-gray-800 text-sm">{{ fire.description }}</p>
      
      <!-- 액션 버튼 -->
      <div class="grid grid-cols-3 gap-2 mt-4 pt-3 border-t border-gray-100">
        <button 
          @click="confirmFire" 
          class="flex items-center justify-center text-sm font-medium py-1"
          :class="isConfirmed ? 'text-red-600' : 'text-gray-600'"
        >
          <AlertTriangle :class="['h-5 w-5 mr-1.5', isConfirmed ? 'fill-red-100' : '']" />
          <span>위험해요 {{ fire.confirms }}</span>
        </button>
        
        <button @click="openComments" class="flex items-center justify-center text-gray-600 text-sm font-medium py-1">
          <MessageSquare class="h-5 w-5 mr-1.5" />
          <span>댓글 {{ fire.comments }}</span>
        </button>
        
        <button @click="shareReport" class="flex items-center justify-center text-gray-600 text-sm font-medium py-1">
          <Share class="h-5 w-5 mr-1.5" />
          <span>공유</span>
        </button>
      </div>
    </div>
    
    <!-- 댓글 섹션 (토글) -->
    <div v-if="showComments" class="px-4 pb-4 pt-2 bg-gray-50 border-t border-gray-100">
      <comment-section :post-id="fire.id" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { MapPin, CheckCircle, AlertTriangle, MessageSquare, Share } from 'lucide-vue-next';
import CommentSection from './CommentSection.vue';

const props = defineProps({
  fire: {
    type: Object,
    required: true
  }
});

const showComments = ref(false);
const isConfirmed = ref(false);

const openComments = () => {
  showComments.value = !showComments.value;
};

const confirmFire = () => {
  isConfirmed.value = !isConfirmed.value;
  // 실제로는 API 호출을 통해 서버에 업데이트
  if (isConfirmed.value) {
    props.fire.confirms++;
  } else {
    props.fire.confirms--;
  }
};

const shareReport = () => {
  // 공유 기능 구현
  if (navigator.share) {
    navigator.share({
      title: `화재 제보: ${props.fire.location}`,
      text: props.fire.description,
      url: window.location.href
    }).catch(err => {
      console.error('공유 실패:', err);
    });
  } else {
    alert('공유 기능을 지원하지 않는 브라우저입니다.');
  }
};
</script>