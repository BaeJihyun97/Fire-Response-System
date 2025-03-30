<template>
  <div class="bg-white rounded-xl shadow-soft overflow-hidden mb-4">
    <!-- 헤더 -->
    <div class="p-4 flex items-center justify-between">
      <div class="flex items-center">
        <img :src="post.userAvatar" alt="프로필" class="w-10 h-10 rounded-full mr-3" />
        <div>
          <h3 class="font-medium text-gray-900">{{ post.username }}</h3>
          <div class="flex items-center text-xs text-gray-500 mt-0.5">
            <MapPin class="h-3 w-3 mr-1" />
            <span>{{ post.location }}</span>
            <span class="mx-1">•</span>
            <span>{{ post.time }}</span>
          </div>
        </div>
      </div>
      
      <div class="flex items-center">
        <span class="text-xs bg-gray-100 text-gray-800 px-2 py-1 rounded-full">{{ post.distance }}</span>
      </div>
    </div>
    
    <!-- 이미지 -->
    <div class="relative">
      <img :src="post.imageUrl" alt="제보 이미지" class="w-full h-48 object-cover" />
      
      <!-- 미확인 제보 표시 -->
      <div class="absolute top-3 right-3 bg-gray-800 bg-opacity-70 text-white px-3 py-1 rounded-full text-xs font-medium">
        확인 필요
      </div>
    </div>
    
    <!-- 내용 -->
    <div class="p-4">
      <p class="text-gray-800 text-sm">{{ post.description }}</p>
      
      <!-- 액션 버튼 -->
      <div class="grid grid-cols-3 gap-2 mt-4 pt-3 border-t border-gray-100">
        <button 
          @click="confirmFire" 
          class="flex items-center justify-center text-sm font-medium py-1"
          :class="isConfirmed ? 'text-red-600' : 'text-gray-600'"
        >
          <AlertTriangle :class="['h-5 w-5 mr-1.5', isConfirmed ? 'fill-red-100' : '']" />
          <span>위험해요 {{ post.confirms }}</span>
        </button>
        
        <button @click="openComments" class="flex items-center justify-center text-gray-600 text-sm font-medium py-1">
          <MessageSquare class="h-5 w-5 mr-1.5" />
          <span>댓글 {{ post.comments }}</span>
        </button>
        
        <button @click="shareReport" class="flex items-center justify-center text-gray-600 text-sm font-medium py-1">
          <Share class="h-5 w-5 mr-1.5" />
          <span>공유</span>
        </button>
      </div>
    </div>
    
    <!-- 댓글 섹션 (토글) -->
    <div v-if="showComments" class="px-4 pb-4 pt-2 bg-gray-50 border-t border-gray-100">
      <comment-section :post-id="post.id" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { MapPin, AlertTriangle, MessageSquare, Share } from 'lucide-vue-next';
import CommentSection from './CommentSection.vue';

const props = defineProps({
  post: {
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
    props.post.confirms++;
  } else {
    props.post.confirms--;
  }
};

const shareReport = () => {
  // 공유 기능 구현
  if (navigator.share) {
    navigator.share({
      title: `화재 제보: ${props.post.location}`,
      text: props.post.description,
      url: window.location.href
    }).catch(err => {
      console.error('공유 실패:', err);
    });
  } else {
    alert('공유 기능을 지원하지 않는 브라우저입니다.');
  }
};
</script>