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
            <!-- 위치 클릭 가능하도록 수정 -->
            <button 
              @click="showMap" 
              class="hover:text-primary-600 hover:underline"
              :disabled="isLoadingMap"
            >
              <span v-if="isLoadingAddress">
                <span class="inline-block w-4 h-4 border-2 border-t-transparent border-gray-500 rounded-full animate-spin mr-1"></span>
                위치 확인 중...
              </span>
              <span v-else>{{ displayLocation }}</span>
            </button>
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
    
    <!-- 지도 모달 -->
    <MapView
      v-if="showMapModal"
      :location-name="displayLocation"
      :location-address="formattedAddress?.fullAddress || `위도: ${post.coordinates.lat}, 경도: ${post.coordinates.lng}`"
      :location-coords="post.coordinates"
      @close="showMapModal = false"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { MapPin, AlertTriangle, MessageSquare, Share } from 'lucide-vue-next';
import CommentSection from './CommentSection.vue';
import MapView from '../views/MapView.vue';
import { reverseGeocode } from '../services/geocodingService';

const props = defineProps({
  post: {
    type: Object,
    required: true,
    default: () => ({
      // 기본값 설정
      id: '',
      username: '',
      userAvatar: '',
      coordinates: { lat: 37.5665, lng: 126.9780 }, // 기본값 (서울시청)
      time: '',
      description: '',
      imageUrl: '',
      distance: '',
      confirms: 0,
      comments: 0
    })
  }
});

// 필요한 상태 변수들
const showComments = ref(false);
const isConfirmed = ref(false);
const showMapModal = ref(false);
const isLoadingMap = ref(false);
const isLoadingAddress = ref(true); // 초기에는 주소 로딩 중
const formattedAddress = ref(null);

// 표시할 위치 정보 계산 - fullAddress로 변경
const displayLocation = computed(() => {
  if (formattedAddress.value && formattedAddress.value.fullAddress) {
    return formattedAddress.value.fullAddress;
  }
  
  // 역지오코딩 결과가 없으면 좌표 표시
  if (props.post.coordinates) {
    return `위도: ${props.post.coordinates.lat.toFixed(5)}, 경도: ${props.post.coordinates.lng.toFixed(5)}`;
  }
  
  return '위치 정보 없음';
});

// 컴포넌트 마운트 시 좌표로부터 주소 변환
onMounted(async () => {
  console.log('CommunityPostCard 마운트됨, 좌표:', props.post.coordinates);
  
  if (!props.post.coordinates) {
    console.error('좌표 정보가 없습니다');
    isLoadingAddress.value = false;
    return;
  }
  
  try {
    // 좌표를 주소로 변환 (역지오코딩)
    const address = await reverseGeocode(
      props.post.coordinates.lat, 
      props.post.coordinates.lng
    );
    
    formattedAddress.value = address;
    console.log('주소 변환 성공:', address);
  } catch (error) {
    console.error('주소 변환 실패:', error);
    // 역지오코딩 실패 시 좌표를 그대로 표시하도록 formattedAddress는 null로 유지
  } finally {
    isLoadingAddress.value = false;
  }
});

// 댓글 토글
const openComments = () => {
  showComments.value = !showComments.value;
};

// 위험해요 버튼 클릭
const confirmFire = () => {
  isConfirmed.value = !isConfirmed.value;
  // 실제로는 API 호출을 통해 서버에 업데이트
  if (isConfirmed.value) {
    props.post.confirms++;
  } else {
    props.post.confirms--;
  }
};

// 공유 기능
const shareReport = () => {
  // 공유 기능 구현
  if (navigator.share) {
    navigator.share({
      title: `제보: ${displayLocation.value}`,
      text: props.post.description,
      url: window.location.href
    }).catch(err => {
      console.error('공유 실패:', err);
    });
  } else {
    alert('공유 기능을 지원하지 않는 브라우저입니다.');
  }
};

// 지도 표시 함수 (간소화)
const showMap = () => {
  try {
    // 이미 로딩 중이면 중복 요청 방지
    if (isLoadingMap.value) return;
    isLoadingMap.value = true;
    
    // 좌표가 있는 경우에만 지도 표시
    if (props.post.coordinates) {
      showMapModal.value = true;
    } else {
      alert('위치 정보가 없습니다.');
    }
  } catch (error) {
    console.error('지도 표시 실패:', error);
    alert('지도를 표시하는데 실패했습니다: ' + (error.message || '알 수 없는 오류'));
  } finally {
    isLoadingMap.value = false;
  }
};
</script>