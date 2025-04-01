<template>
    <div class="fixed inset-0 z-50 bg-black bg-opacity-50 flex items-center justify-center p-4">
      <div class="bg-white w-full max-w-lg rounded-xl overflow-hidden shadow-xl">
        <!-- 헤더 -->
        <div class="p-4 border-b border-gray-200 flex items-center justify-between">
          <h2 class="text-lg font-semibold">{{ locationName }}</h2>
          <button @click="$emit('close')" class="p-1 rounded-full hover:bg-gray-100">
            <X class="h-5 w-5" />
          </button>
        </div>
        
        <!-- 주소 정보 -->
        <div class="px-4 py-2 bg-gray-50 border-b border-gray-200">
          <div class="flex items-start">
            <MapPin class="h-4 w-4 text-gray-500 mt-0.5 mr-2 flex-shrink-0" />
            <p class="text-sm text-gray-700">{{ locationAddress }}</p>
          </div>
        </div>
        
        <!-- 지도 컨테이너 -->
        <div class="relative">
          <div id="map" class="w-full h-80"></div>
          
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
                <span>제보 위치</span>
              </div>
              <div class="flex items-center">
                <div class="w-3 h-3 bg-blue-500 rounded-full mr-1"></div>
                <span>내 위치</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 하단 버튼 -->
        <div class="p-4 flex justify-between">
          <button 
            @click="getUserLocation" 
            class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 flex items-center"
            :disabled="isLoadingUserLocation"
          >
            <Locate class="h-4 w-4 mr-1" />
            {{ userLocation ? '내 위치 새로고침' : '내 위치 확인' }}
          </button>
          
          <button 
            @click="openNaverMap" 
            class="px-4 py-2 bg-primary-600 text-white rounded-md text-sm font-medium hover:bg-primary-700 flex items-center"
          >
            <ExternalLink class="h-4 w-4 mr-1" />
            네이버 지도에서 보기
          </button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, onUnmounted, watch } from 'vue';
  import { AlertTriangle, CheckCircle, ExternalLink, Locate, MapPin, X } from 'lucide-vue-next';
  
  const props = defineProps({
    locationName: {
      type: String,
      default: '위치 정보'
    },
    locationAddress: {
      type: String,
      default: '주소 정보 없음'
    },
    locationCoords: {
      type: Object,
      required: true,
      default: () => ({ lat: 37.5665, lng: 126.9780 }) // 기본값: 서울시청
    }
  });
  
  const emit = defineEmits(['close']);
  
  const isLoading = ref(true);
  const error = ref(null);
  let map = null;
  let reportMarker = null;
  let userMarker = null;
  
  // 사용자 위치 관련 상태
  const userLocation = ref(null);
  const isLoadingUserLocation = ref(false);
  const userLocationError = ref(null);
  
  // 지도 초기화 함수
  const initMap = async () => {
    isLoading.value = true;
    error.value = null;
    
    try {
      console.log('지도 초기화 시작, 좌표:', props.locationCoords);
      
      // 네이버 맵 객체가 로드되었는지 확인
      if (!window.naver || !window.naver.maps) {
        throw new Error('네이버 지도 API가 로드되지 않았습니다.');
      }
      
      // 지도 옵션 설정
      const mapOptions = {
        center: new window.naver.maps.LatLng(
          props.locationCoords.lat,
          props.locationCoords.lng
        ),
        zoom: 15,
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
        
        // 제보 위치 마커 생성
        createReportMarker();
        
        // 사용자 위치가 있으면 사용자 위치 마커도 생성
        if (userLocation.value) {
          createUserMarker();
          fitBothMarkersInView();
        }
        
        // 지도 중심 및 줌 레벨 재설정 (지도가 완전히 로드된 후)
        setTimeout(() => {
          const position = new window.naver.maps.LatLng(
            props.locationCoords.lat,
            props.locationCoords.lng
          );
          map.setCenter(position);
          map.setZoom(15);
          isLoading.value = false;
        }, 500);
        
        // 자동으로 사용자 위치 가져오기
        getUserLocation();
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
  
  // 제보 위치 마커 생성 함수
  const createReportMarker = () => {
    if (!map || !window.naver || !window.naver.maps) return;
    
    try {
      // 기존 마커가 있으면 제거
      if (reportMarker) {
        reportMarker.setMap(null);
      }
      
      // 마커 위치 설정
      const position = new window.naver.maps.LatLng(
        props.locationCoords.lat,
        props.locationCoords.lng
      );
      
      // 마커 생성 (빨간색 마커)
      reportMarker = new window.naver.maps.Marker({
        position: position,
        map: map,
        animation: window.naver.maps.Animation.DROP,
        icon: {
          content: `
            <div style="
              width: 24px;
              height: 24px;
              background-color: #ff3b30;
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
            <h4 style="margin: 0 0 5px 0; font-weight: 600;">${props.locationName}</h4>
            <p style="margin: 0; font-size: 12px; color: #666;">${props.locationAddress}</p>
          </div>
        `,
        borderWidth: 0,
        disableAnchor: true,
        backgroundColor: 'white',
        borderColor: '#ddd',
        anchorSize: new window.naver.maps.Size(0, 0)
      });
      
      // 마커 클릭 시 정보창 표시
      window.naver.maps.Event.addListener(reportMarker, 'click', () => {
        if (infoWindow.getMap()) {
          infoWindow.close();
        } else {
          infoWindow.open(map, reportMarker);
        }
      });
      
      // 초기에 정보창 표시
      infoWindow.open(map, reportMarker);
      
    } catch (err) {
      console.error('제보 위치 마커 생성 실패:', err);
      error.value = '위치 표시에 실패했습니다.';
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
        if (infoWindow.getMap()) {
          infoWindow.close();
        } else {
          infoWindow.open(map, userMarker);
        }
      });
      
    } catch (err) {
      console.error('사용자 위치 마커 생성 실패:', err);
    }
  };
  
  // 두 마커가 모두 보이도록 지도 영역 조정
  const fitBothMarkersInView = () => {
    if (!map || !window.naver || !window.naver.maps || !reportMarker || !userMarker) return;
    
    try {
      const bounds = new window.naver.maps.LatLngBounds();
      bounds.extend(reportMarker.getPosition());
      bounds.extend(userMarker.getPosition());
      
      // 약간의 여백 추가
      bounds.extend(new window.naver.maps.LatLng(
        reportMarker.getPosition().lat() + 0.005,
        reportMarker.getPosition().lng() + 0.005
      ));
      bounds.extend(new window.naver.maps.LatLng(
        reportMarker.getPosition().lat() - 0.005,
        reportMarker.getPosition().lng() - 0.005
      ));
      
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
          fitBothMarkersInView();
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
  
  // 네이버 지도 앱/웹 열기
  const openNaverMap = () => {
    const { lat, lng } = props.locationCoords;
    const name = encodeURIComponent(props.locationName);
    
    // 모바일 기기 확인
    const isMobile = /iPhone|iPad|iPod|Android/i.test(navigator.userAgent);
    
    if (isMobile) {
      // 모바일 앱 URL 스킴
      window.location.href = `nmap://place?lat=${lat}&lng=${lng}&name=${name}&appname=화재알리미`;
      
      // 앱이 열리지 않으면 웹으로 리다이렉트
      setTimeout(() => {
        window.location.href = `https://map.naver.com/v5/search/${name}?c=${lng},${lat},15,0,0,0,dh`;
      }, 2000);
    } else {
      // 데스크톱 웹 URL
      window.open(`https://map.naver.com/v5/search/${name}?c=${lng},${lat},15,0,0,0,dh`, '_blank');
    }
  };
  
  // 좌표 변경 감지
  watch(() => props.locationCoords, (newCoords) => {
    console.log('좌표 변경 감지:', newCoords);
    if (map && window.naver && window.naver.maps) {
      const position = new window.naver.maps.LatLng(newCoords.lat, newCoords.lng);
      map.setCenter(position);
      createReportMarker();
      
      // 사용자 위치가 있으면 두 마커가 모두 보이도록 지도 영역 조정
      if (userLocation.value) {
        fitBothMarkersInView();
      }
    }
  }, { deep: true });
  
  // 컴포넌트 마운트 시 지도 초기화
  onMounted(() => {
    console.log('MapView 마운트됨');
    initMap();
  });
  
  // 컴포넌트 언마운트 시 지도 정리
  onUnmounted(() => {
    console.log('MapView 언마운트됨');
    if (reportMarker) {
      reportMarker.setMap(null);
    }
    if (userMarker) {
      userMarker.setMap(null);
    }
    map = null;
  });
  </script>