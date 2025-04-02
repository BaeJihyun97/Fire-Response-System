<template>
  <div class="bg-white rounded-lg shadow-sm">
    <div class="p-4 border-b border-gray-100">
      <h3 class="font-medium text-gray-900">댓글 {{ comments.length }}개</h3>
    </div>
    
    <!-- 댓글 목록 -->
    <div class="divide-y divide-gray-100">
      <div v-for="comment in comments" :key="comment.id" class="p-4">
        <div class="flex items-start">
          <div class="h-8 w-8 rounded-full bg-gray-200 overflow-hidden mr-3">
            <img :src="comment.userAvatar" :alt="comment.username" class="h-full w-full object-cover" />
          </div>
          <div class="flex-1">
            <div class="flex items-center">
              <span class="font-medium text-gray-900">{{ comment.username }}</span>
              <span class="text-xs text-gray-500 ml-2">{{ comment.time }}</span>
            </div>
            <p class="text-gray-700 text-sm mt-1">{{ comment.text }}</p>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 댓글 작성 -->
    <div class="p-4 border-t border-gray-100">
      <div class="flex items-start">
        <div class="h-8 w-8 rounded-full bg-gray-200 overflow-hidden mr-3">
          <img src="https://placehold.co/32x32" alt="내 프로필" class="h-full w-full object-cover" />
        </div>
        <div class="flex-1">
          <textarea 
            v-model="newComment" 
            class="w-full border border-gray-300 rounded-md p-2 text-sm"
            rows="2"
            placeholder="댓글을 입력하세요..."
          ></textarea>
          <div class="flex justify-end mt-2">
            <button 
              @click="addComment" 
              class="px-3 py-1 bg-primary-600 text-white rounded-md text-sm hover:bg-primary-700 transition-colors"
              :disabled="!newComment.trim()"
              :class="{ 'opacity-50 cursor-not-allowed': !newComment.trim() }"
            >
              댓글 작성
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({
  postId: {
    type: Number,
    required: true
  }
});

// 댓글 데이터
const comments = ref([
  {
    id: 1,
    username: "시민제보자",
    userAvatar: "https://placehold.co/32x32",
    text: "저도 이 화재를 목격했습니다. 정말 위험해 보였어요.",
    time: "10분 전"
  },
  {
    id: 2,
    username: "안전지킴이",
    userAvatar: "https://placehold.co/32x32",
    text: "소방차가 빠르게 도착했네요. 다행입니다.",
    time: "5분 전"
  }
]);

// 새 댓글 입력
const newComment = ref('');

// 댓글 추가
const addComment = () => {
  if (!newComment.value.trim()) return;
  
  comments.value.push({
    id: comments.value.length + 1,
    username: "사용자",
    userAvatar: "https://placehold.co/32x32",
    text: newComment.value,
    time: "방금 전"
  });
  
  newComment.value = '';
};
</script>