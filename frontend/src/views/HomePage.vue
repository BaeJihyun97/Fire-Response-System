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
    <AppHeader title="화재알리미" :showBackButton="false">
      <template #actions>
        <button
          @click="refreshData"
          class="p-2 rounded-full hover:bg-gray-100"
          :class="{ 'animate-spin': isRefreshing }"
        >
          <RefreshCw class="h-5 w-5 text-gray-700" />
        </button>
      </template>
    </AppHeader>

    <!-- 메인 컨텐츠 -->
    <div class="container mx-auto px-4 py-4 max-w-lg pb-24">
      <!-- 긴급 알림 -->
      <div v-if="alarms.length > 0" class="mb-4 border border-red-200 bg-red-50 rounded-xl p-4 shadow-soft">
        <p class="text-sm text-red-800">
          <span class="font-bold">긴급 알림:</span> {{ warnLocation }} 부근에 화재가 발생했습니다. 해당 지역 방문을 자제해 주시기 바랍니다.
        </p>
      </div>

      <!-- 탭 컴포넌트 -->
      <div class="mb-6">
        <div class="flex border-b border-gray-200 mb-4">
          <button
            @click="activeTab = 'confirmed'"
            :class="[
              'tab',
              activeTab === 'confirmed' ? 'tab-active' : 'tab-inactive'
            ]"
          >
            화재 확인
          </button>
          <button
            @click="activeTab = 'community'"
            :class="[
              'tab',
              activeTab === 'community' ? 'tab-active' : 'tab-inactive'
            ]"
          >
            커뮤니티 제보
          </button>
        </div>

        <!-- 화재 확인 탭 -->
        <div v-if="activeTab === 'confirmed'">
          <div class="space-y-6">
            <fire-report-card
              v-for="fire in fireReports"
              :key="fire.id"
              :fire="fire"
            />
          </div>
        </div>

        <!-- 커뮤니티 제보 탭 -->
        <div v-if="activeTab === 'community'">
          <div class="space-y-6 mt-4">
            <fire-report-card
              v-for="fire in communityReports"
              :key="fire.id"
              :fire="fire"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 하단 네비게이션 바 -->
    <div class="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 py-2 shadow-lg">
      <div class="container mx-auto max-w-lg">
        <div class="flex justify-around items-center">
          <button class="flex flex-col items-center text-primary-600">
            <Home class="h-6 w-6" />
            <span class="text-xs mt-1 font-medium">홈</span>
          </button>
          <router-link to="/map" class="flex flex-col items-center text-gray-500">
            <Map class="h-6 w-6" />
            <span class="text-xs mt-1">지도</span>
          </router-link>
          <router-link to="/report">
            <button class="rounded-full bg-gradient-to-r from-primary-600 to-primary-500 h-14 w-14 flex items-center justify-center text-white shadow-lg transform hover:scale-105 transition-transform duration-200">
              <Camera class="h-7 w-7" />
            </button>
          </router-link>
          <router-link to="/notifications" class="flex flex-col items-center text-gray-500 relative">
            <Bell class="h-6 w-6" />
            <span class="text-xs mt-1">알림</span>
            <span v-if="unreadCount > 0" class="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
              {{ unreadCount }}
            </span>
          </router-link>
          <router-link to="/profile" class="flex flex-col items-center text-gray-500">
            <User class="h-6 w-6" />
            <span class="text-xs mt-1">내정보</span>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { AlertTriangle, Bell, Camera, Home, Map, Phone, RefreshCw, User } from 'lucide-vue-next';
import FireReportCard from '../components/FireReportCard.vue';
import AppHeader from '../components/AppHeader.vue';
import { useRouter } from 'vue-router';
import { postApi, reportApiService } from '../services/api';
import { reverseGeocode } from '../services/geocodingService';
import { getUnreadNotifications } from '../services/notificationService';
import { getCurrentLocation } from '../services/locationService';

const activeTab = ref('confirmed');
const isRefreshing = ref(false);
const router = useRouter();
const unreadCount = ref(0);
const isLoading = ref(true);
const error = ref(null);
const warnLocation = ref(null);
const alarms = ref([]);

// 화재 제보 데이터
const fireReports = ref([]);
// 커뮤니티 제보 데이터
const communityReports = ref([]);

// 토큰에서 userId 추출 함수
const getUserIdFromToken = () => {
  const token = localStorage.getItem('token');
  if (token) {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));

      const payload = JSON.parse(jsonPayload);
      return payload.sub || payload.userId; // sub 또는 userId 필드 사용
    } catch (error) {
      console.error('토큰 디코딩 실패:', error);
      return null;
    }
  }
  return null;
};

// 데이터 새로고침 함수
const refreshData = async () => {
  isRefreshing.value = true;

  try {
    const response = await postApi.getPosts();
    const events = response.data;
    console.log('API 응답 데이터 구조:', events);

    // API 응답 데이터를 현재 구조에 맞게 변환
    const transformedEvents = await Promise.all(events.map(async event => {
      console.log('이벤트 데이터:', event);

      // 위도/경도를 이용해 주소 가져오기
      let location = '';
      try {
        const address = await reverseGeocode(event.latitude, event.longitude);
        location = address.fullAddress || `위도: ${event.latitude.toFixed(5)}, 경도: ${event.longitude.toFixed(5)}`;
      } catch (error) {
        console.error('주소 변환 실패:', error);
        location = `위도: ${event.latitude.toFixed(5)}, 경도: ${event.longitude.toFixed(5)}`;
      }

      // 이벤트 ID로 최신 리포트 가져오기
      let videoUrl = '';
      let isFire = false;
      try {
        const reportResponse = await reportApiService.getLatestReportByEventId(event.eventId);
        if (reportResponse.data && reportResponse.data.videoId) {
          videoUrl = `https://team05sa.blob.core.windows.net/videos/${reportResponse.data.videoId}/${reportResponse.data.videoId}.mp4`;
          const videoInfo = await videoApiService.getVideo(reportResponse.data.videoId);
          isFire = videoInfo.data.latest_analysis.fire_detected;
        }
      } catch (error) {
        console.error('비디오 정보 가져오기 실패:', error);
      }

      return {
        id: event.postId,
        coordinates: { lat: event.latitude, lng: event.longitude },
        location: '',
        timestamp: new Date().toISOString(),
        status: '진행 중',
        isFire: isFire,
        riskLevel: '중간',
        verified: false,
        metadata: {
          likes: event.reactionCount || 0,
          comments: 0,
          videoUrl: videoUrl,
          imageUrl: ''
        }
      };
    }));

    // 화재 제보와 커뮤니티 제보 분리
    fireReports.value = transformedEvents.filter(event => event.isFire);
    communityReports.value = transformedEvents.filter(event => !event.isFire);
    console.log('화재 제보 데이터:', fireReports.value);
    console.log('커뮤니티 제보 데이터:', communityReports.value);
  } catch (error) {
    console.error('데이터 새로고침 오류:', error);
  } finally {
    isRefreshing.value = false;
  }
};

const formatAddress = (address) => {
  if (!address) return '';

  const parts = [];
  if (address.borough) parts.push(address.borough);
  if (address.quarter) parts.push(address.quarter);
  // if (address.road) parts.push(address.road);

  return parts.join(' ');
};

const getLocationInfo = async (lat, lng) => {
  try {
    const response = await fetch(
      `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&zoom=18&addressdetails=1&accept-language=ko`
    );
    const data = await response.json();

    if (data && data.address) {
      return formatAddress(data.address);
    }
    return `위도: ${lat}, 경도: ${lng}`;
  } catch (error) {
    console.error('위치 정보 로딩 실패:', error);
    return `위도: ${lat}, 경도: ${lng}`;
  }
};

// 위치 정보 로드
const loadLocationInfo = async (lat, lng) => {
  try {
    const result = await getLocationInfo(lat, lng);
    warnLocation.value = result;
  } catch (err) {
    console.error('위치 정보 로딩 오류:', err);
    displayLocation.value = `위도: ${lat}, 경도: ${lng}`;
  }
};

// 알림 수 업데이트 함수
const updateNotificationCount = async () => {
  try {
    const userId = getUserIdFromToken();
    if (!userId) {
      console.error('사용자 ID를 찾을 수 없습니다.');
      return;
    }

    const currLocation = await getCurrentLocation();
    const response = await getUnreadNotifications(userId, currLocation.latitude, currLocation.longitude);
    unreadCount.value = response.count;
    alarms.value = response.alarms || [];
    console.log(response.alarms);
    warnLocation.value = loadLocationInfo(response.alarms[0].latitude, response.alarms[0].longitude);
  } catch (error) {
    console.error('알림 수 업데이트 실패:', error);
  }
};


// 위치 정보 가져오기
// const getLocation = async () => {
//   try {
//     const position = await new Promise((resolve, reject) => {
//       navigator.geolocation.getCurrentPosition(resolve, reject);
//     });
//     location.value = {
//       latitude: position.coords.latitude,
//       longitude: position.coords.longitude
//     };
//     await fetchAlarms();
//   } catch (err) {
//     console.error('위치 정보 로딩 오류:', err);
//     error.value = '위치 정보를 가져오는데 실패했습니다.';
//   } finally {
//     isLoading.value = false;
//   }
// };

// 초기 데이터 로드
onMounted(() => {
  refreshData();
  updateNotificationCount();
});
</script>

<style scoped>
.tab {
  @apply px-4 py-2 text-sm font-medium;
}

.tab-active {
  @apply text-primary-600 border-b-2 border-primary-600;
}

.tab-inactive {
  @apply text-gray-500 hover:text-gray-700;
}

.shadow-soft {
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.btn {
  @apply px-3 py-1.5 rounded-lg font-medium text-sm;
}

.btn-primary {
  @apply bg-primary-600 text-white hover:bg-primary-700 transition-colors;
}
</style>

