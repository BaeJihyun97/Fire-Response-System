<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 헤더 -->
    <AppHeader title="화재 지도" :showBackButton="true">
      <template #actions>
        <button 
          @click="getUserLocation" 
          class="p-2 rounded-full bg-gray-100 hover:bg-gray-200"
          :disabled="isLoadingUserLocation"
        >
          <Locate class="h-5 w-5 text-gray-700" />
        </button>
      </template>
    </AppHeader>
    
    <!-- 지도 컨테이너 -->
    <div class="relative h-[calc(100vh-120px)]">
      <div id="map" class="w-full h-full"></div>
      
      <!-- 로딩 인디케이터 -->
      <div v-if="isLoading" class="absolute inset-0 flex items-center justify-center bg-white bg-opacity-70">
        <div class="flex flex-col items-center">
          <div class="w-10 h-10 border-4 border-primary-600 border-t-transparent rounded-full animate-spin"></div>
          <p class="mt-2 text-sm text-gray-600">지도를 불러오는 중...</p>
        </div>
      </div>
      
      <!-- 에러 메시지 -->
      <div v-if="error" class="absolute inset-0 flex items-center justify-center bg-white bg-opacity-90">
        <div class="text-center p-4">
          <AlertTriangle class="h-10 w-10 text-red-500 mx-auto mb-2" />
          <p class="text-red-600 font-medium">지도를 불러오는데 실패했습니다</p>
          <p class="text-sm text-gray-600 mt-1">{{ error }}</p>
          <button 
            @click="initMap" 
            class="mt-3 px-4 py-2 bg-primary-600 text-white rounded-md text-sm font-medium hover:bg-primary-700"
          >
            다시 시도
          </button>
        </div>
      </div>
      
      <!-- 위치 정보 상태 -->
      <div class="absolute bottom-2 left-2 bg-white rounded-md shadow-md p-2 text-xs">
        <div v-if="isLoadingUserLocation" class="flex items-center text-gray-600">
          <div class="w-3 h-3 border-2 border-t-transparent border-blue-500 rounded-full animate-spin mr-1"></div>
          <span>내 위치 확인 중...</span>
        </div>
        <div v-else-if="userLocationError" class="flex items-center text-red-600">
          <AlertTriangle class="h-3 w-3 mr-1" />
          <span>{{ userLocationError }}</span>
        </div>
        <div v-else-if="userLocation" class="flex items-center text-green-600">
          <CheckCircle class="h-3 w-3 mr-1" />
          <span>내 위치 확인됨</span>
        </div>
      </div>
      
      <!-- 범례 -->
      <div class="absolute bottom-2 right-2 bg-white rounded-md shadow-md p-2">
        <div class="flex flex-col space-y-1 text-xs">
          <div class="flex items-center">
            <div class="w-3 h-3 bg-red-500 rounded-full mr-1"></div>
            <span>위험도: 높음</span>
          </div>
          <div class="flex items-center">
            <div class="w-3 h-3 bg-orange-500 rounded-full mr-1"></div>
            <span>위험도: 중간</span>
          </div>
          <div class="flex items-center">
            <div class="w-3 h-3 bg-green-500 rounded-full mr-1"></div>
            <span>위험도: 낮음</span>
          </div>
          <div class="flex items-center">
            <div class="w-3 h-3 bg-blue-500 rounded-full mr-1"></div>
            <span>내 위치</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { AlertTriangle, CheckCircle, Locate } from 'lucide-vue-next';
import AppHeader from '../components/AppHeader.vue';

// 화재 제보 데이터 (실제로는 API에서 가져옴)
const fireReports = ref([
  {
    id: '1',
    username: '소방지킴이',
    coordinates: { lat: 37.498095, lng: 127.027610 }, // 강남역 좌표
    description: '상업 건물 3층에서 화재 발생. 연기가 심하게 나고 있습니다.',
    riskLevel: '높음',
    verified: true
  },
  {
    id: "2",
    username: "안전제일",
    coordinates: { lat: 37.526120, lng: 126.925771 }, // 여의도 공원 좌표
    description: "공원 동쪽 입구 근처에서 작은 산불 발생. 소방차 출동 중입니다.",
    riskLevel: "중간",
    verified: true
  },
  {
    id: "3",
    username: "시민제보자",
    coordinates: { lat: 37.566535, lng: 126.977969 }, // 서울시청 좌표
    description: "남산타워 근처에서 연기가 보입니다. 화재인지 확인 부탁드립니다.",
    riskLevel: "낮음",
    verified: true
  }
]);

// 상태 변수
const isLoading = ref(true);
const error = ref(null);
const userLocation = ref(null);
const isLoadingUserLocation = ref(false);
const userLocationError = ref(null);

// 지도 및 마커 객체
let map = null;
let markers = [];
let userMarker = null;
let infoWindows = [];

// 지도 초기화 함수
const initMap = async () => {
  isLoading.value = true;
  error.value = null;
  
  try {
    console.log('지도 초기화 시작');
    
    // 네이버 맵 객체가 로드되었는지 확인
    if (!window.naver || !window.naver.maps) {
      throw new Error('네이버 지도 API가 로드되지 않았습니다.');
    }
    
    // 지도 옵션 설정
    const mapOptions = {
      center: new window.naver.maps.LatLng(37.5665, 126.9780), // 서울시청
      zoom: 11,
      zoomControl: true,
      zoomControlOptions: {
        position: window.naver.maps.Position.TOP_RIGHT
      }
    };
    
    // 지도 생성
    map = new window.naver.maps.Map('map', mapOptions);
    
    // 지도 로드 완료 이벤트 리스너 추가
    window.naver.maps.Event.once(map, 'init_stylemap', () => {
      console.log('지도 스타일 초기화 완료');
      
      // 화재 제보 마커 생성
      createFireMarkers();
      
      // 사용자 위치가 있으면 사용자 위치 마커도 생성
      if (userLocation.value) {
        createUserMarker();
      }
      
      // 모든 마커가 보이도록 지도 영역 조정
      fitAllMarkersInView();
      
      isLoading.value = false;
    });
    
    // 지도 에러 이벤트 리스너
    window.naver.maps.Event.addListener(map, 'error', (e) => {
      console.error('지도 에러:', e);
      error.value = '지도를 불러오는 중 오류가 발생했습니다.';
      isLoading.value = false;
    });
    
  } catch (err) {
    console.error('지도 초기화 실패:', err);
    error.value = err.message || '지도를 불러오는데 실패했습니다.';
    isLoading.value = false;
  }
};

// 화재 제보 마커 생성 함수
const createFireMarkers = () => {
  if (!map || !window.naver || !window.naver.maps) return;
  
  try {
    // 기존 마커 제거
    markers.forEach(marker => marker.setMap(null));
    markers = [];
    
    // 기존 정보창 제거
    infoWindows.forEach(infoWindow => infoWindow.close());
    infoWindows = [];
    
    // 각 화재 제보에 대한 마커 생성
    fireReports.value.forEach(fire => {
      // 마커 위치 설정
      const position = new window.naver.maps.LatLng(
        fire.coordinates.lat,
        fire.coordinates.lng
      );
      
      // 위험도에 따른 마커 색상 설정
      let markerColor;
      switch (fire.riskLevel) {
        case '높음':
          markerColor = '#ff3b30'; // 빨간색
          break;
        case '중간':
          markerColor = '#ff9500'; // 주황색
          break;
        case '낮음':
          markerColor = '#34c759'; // 초록색
          break;
        default:
          markerColor = '#8e8e93'; // 회색
      }
      
      // 마커 생성
      const marker = new window.naver.maps.Marker({
        position: position,
        map: map,
        animation: window.naver.maps.Animation.DROP,
        icon: {
          content: `
            <div style="
              width: 24px;
              height: 24px;
              background-color: ${markerColor};
              border: 2px solid white;
              border-radius: 50%;
              box-shadow: 0 2px 6px rgba(0,0,0,0.3);
              transform: translate(-50%, -50%);
            "></div>
          `,
          anchor: new window.naver.maps.Point(12, 12)
        }
      });
      
      // 정보창 생성
      const infoWindow = new window.naver.maps.InfoWindow({
        content: `
          <div style="padding: 10px; min-width: 200px;">
            <h4 style="margin: 0 0 5px 0; font-weight: 600;">${fire.username}의 제보</h4>
            <p style="margin: 0 0 5px 0; font-size: 12px; color: #666;">
              위험도: <span style="color: ${markerColor}; font-weight: bold;">${fire.riskLevel}</span>
            </p>
            <p style="margin: 0; font-size: 12px;">${fire.description}</p>
          </div>
        `,
        borderWidth: 0,
        disableAnchor: true,
        backgroundColor: 'white',
        borderColor: '#ddd',
        anchorSize: new window.naver.maps.Size(0, 0)
      });
      
      // 마커 클릭 시 정보창 표시
      window.naver.maps.Event.addListener(marker, 'click', () => {
        // 다른 정보창 모두 닫기
        infoWindows.forEach(iw => iw.close());
        
        // 현재 정보창 열기
        infoWindow.open(map, marker);
      });
      
      // 마커와 정보창 배열에 추가
      markers.push(marker);
      infoWindows.push(infoWindow);
    });
    
  } catch (err) {
    console.error('화재 제보 마커 생성 실패:', err);
    error.value = '화재 위치 표시에 실패했습니다.';
  }
};

// 사용자 위치 마커 생성 함수
const createUserMarker = () => {
  if (!map || !window.naver || !window.naver.maps || !userLocation.value) return;
  
  try {
    // 기존 마커가 있으면 제거
    if (userMarker) {
      userMarker.setMap(null);
    }
    
    // 마커 위치 설정
    const position = new window.naver.maps.LatLng(
      userLocation.value.lat,
      userLocation.value.lng
    );
    
    // 마커 생성 (파란색 마커)
    userMarker = new window.naver.maps.Marker({
      position: position,
      map: map,
      animation: window.naver.maps.Animation.DROP,
      icon: {
        content: `
          <div style="
            width: 24px;
            height: 24px;
            background-color: #007aff;
            border: 2px solid white;
            border-radius: 50%;
            box-shadow: 0 2px 6px rgba(0,0,0,0.3);
            transform: translate(-50%, -50%);
            display: flex;
            align-items: center;
            justify-content: center;
          ">
            <div style="
              width: 8px;
              height: 8px;
              background-color: white;
              border-radius: 50%;
            "></div>
          </div>
        `,
        anchor: new window.naver.maps.Point(12, 12)
      }
    });
    
    // 정보창 생성
    const infoWindow = new window.naver.maps.InfoWindow({
      content: `
        <div style="padding: 10px; min-width: 150px;">
          <h4 style="margin: 0 0 5px 0; font-weight: 600;">내 위치</h4>
          <p style="margin: 0; font-size: 12px; color: #666;">
            위도: ${userLocation.value.lat.toFixed(6)}<br>
            경도: ${userLocation.value.lng.toFixed(6)}
          </p>
        </div>
      `,
      borderWidth: 0,
      disableAnchor: true,
      backgroundColor: 'white',
      borderColor: '#ddd',
      anchorSize: new window.naver.maps.Size(0, 0)
    });
    
    // 마커 클릭 시 정보창 표시
    window.naver.maps.Event.addListener(userMarker, 'click', () => {
      // 다른 정보창 모두 닫기
      infoWindows.forEach(iw => iw.close());
      
      // 현재 정보창 열기
      infoWindow.open(map, userMarker);
    });
    
    // 정보창 배열에 추가
    infoWindows.push(infoWindow);
    
  } catch (err) {
    console.error('사용자 위치 마커 생성 실패:', err);
  }
};

// 모든 마커가 보이도록 지도 영역 조정
const fitAllMarkersInView = () => {
  if (!map || !window.naver || !window.naver.maps) return;
  
  try {
    const bounds = new window.naver.maps.LatLngBounds();
    
    // 화재 제보 마커 위치 추가
    markers.forEach(marker => {
      bounds.extend(marker.getPosition());
    });
    
    // 사용자 위치 마커가 있으면 추가
    if (userMarker) {
      bounds.extend(userMarker.getPosition());
    }
    
    // 마커가 하나도 없는 경우 (bounds가 비어있는 경우)
    if (markers.length === 0 && !userMarker) {
      // 서울 중심으로 기본 영역 설정
      const seoulCenter = new window.naver.maps.LatLng(37.5665, 126.9780); // 서울시청
      bounds.extend(seoulCenter);
      bounds.extend(new window.naver.maps.LatLng(37.5665 + 0.1, 126.9780 + 0.1));
    }
    
    // 약간의 여백 추가
    const ne = bounds.getNE();
    const sw = bounds.getSW();
    const latPadding = (ne.lat() - sw.lat()) * 0.1;
    const lngPadding = (ne.lng() - sw.lng()) * 0.1;
    
    bounds.extend(new window.naver.maps.LatLng(ne.lat() + latPadding, ne.lng() + lngPadding));
    bounds.extend(new window.naver.maps.LatLng(sw.lat() - latPadding, sw.lng() - lngPadding));
    
    // 지도 영역 설정
    map.fitBounds(bounds);
  } catch (err) {
    console.error('지도 영역 조정 실패:', err);
  }
};

// 사용자 위치 가져오기
const getUserLocation = () => {
  if (!navigator.geolocation) {
    userLocationError.value = '브라우저가 위치 정보를 지원하지 않습니다.';
    return;
  }
  
  isLoadingUserLocation.value = true;
  userLocationError.value = null;
  
  navigator.geolocation.getCurrentPosition(
    (position) => {
      userLocation.value = {
        lat: position.coords.latitude,
        lng: position.coords.longitude
      };
      
      console.log('사용자 위치 확인:', userLocation.value);
      
      // 지도가 초기화되었으면 사용자 위치 마커 생성
      if (map && window.naver && window.naver.maps) {
        createUserMarker();
        fitAllMarkersInView();
      }
      
      isLoadingUserLocation.value = false;
    },
    (err) => {
      console.error('사용자 위치 확인 실패:', err);
      
      switch (err.code) {
        case 1:
          userLocationError.value = '위치 접근 권한이 거부되었습니다.';
          break;
        case 2:
          userLocationError.value = '위치를 확인할 수 없습니다.';
          break;
        case 3:
          userLocationError.value = '위치 확인 시간이 초과되었습니다.';
          break;
        default:
          userLocationError.value = '위치 확인에 실패했습니다.';
      }
      
      isLoadingUserLocation.value = false;
    },
    {
      enableHighAccuracy: true,
      timeout: 10000,
      maximumAge: 0
    }
  );
};

// 컴포넌트 마운트 시 지도 초기화 및 사용자 위치 확인
onMounted(() => {
  console.log('MapPage 마운트됨');
  initMap();
  getUserLocation();
});

// 컴포넌트 언마운트 시 지도 정리
onUnmounted(() => {
  console.log('MapPage 언마운트됨');
  
  // 마커 제거
  markers.forEach(marker => marker.setMap(null));
  if (userMarker) {
    userMarker.setMap(null);
  }
  
  // 정보창 닫기
  infoWindows.forEach(infoWindow => infoWindow.close());
  
  // 변수 초기화
  map = null;
  markers = [];
  userMarker = null;
  infoWindows = [];
});
</script>

