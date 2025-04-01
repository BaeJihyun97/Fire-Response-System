<template>
    <div class="min-h-screen flex flex-col">
      <!-- 상단 네비게이션 바 -->
      <div class="bg-white border-b border-gray-200 py-2 shadow-sm">
        <div class="container mx-auto max-w-6xl px-4">
          <div class="flex justify-between items-center">
            <div class="flex items-center">
              <router-link to="/admin" class="flex items-center text-gray-700 hover:text-primary-600">
                <ArrowLeft class="h-5 w-5 mr-2" />
                <span class="font-medium">관리자 대시보드로 돌아가기</span>
              </router-link>
            </div>
            
            <div class="flex items-center">
              <Flame class="h-6 w-6 text-primary-600 mr-2" />
              <span class="font-bold text-xl text-gray-900">화재알리미</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="container mx-auto px-4 py-6 max-w-6xl flex-1 flex flex-col">
        <div class="flex justify-between items-center mb-6">
          <div class="flex items-center">
            <Map class="h-6 w-6 text-blue-600 mr-2" />
            <h1 class="text-2xl font-bold">전체 위험 분석 지도</h1>
          </div>
        </div>
        
        <div class="flex-1 flex bg-white rounded-lg shadow overflow-hidden">
          <!-- 좌측 필터 패널 -->
          <div class="w-64 border-r border-gray-200 p-4 overflow-y-auto">
            <div class="mb-4">
              <label class="block text-sm font-medium text-gray-700 mb-2">기간 선택</label>
              <div class="space-y-2">
                <button 
                  v-for="period in periods" 
                  :key="period.value"
                  @click="selectedPeriod = period.value; updateMapVisualization()"
                  :class="[
                    'w-full px-3 py-1.5 text-sm rounded-md text-left',
                    selectedPeriod === period.value 
                      ? 'bg-primary-100 text-primary-800 border border-primary-300' 
                      : 'bg-gray-100 text-gray-700 border border-gray-200 hover:bg-gray-200'
                  ]"
                >
                  {{ period.label }}
                </button>
              </div>
            </div>
            
            <div class="mb-4">
              <label class="block text-sm font-medium text-gray-700 mb-2">위험도 필터</label>
              <div class="space-y-2">
                <div 
                  v-for="risk in riskLevels" 
                  :key="risk.value"
                  class="flex items-center"
                >
                  <input 
                    type="checkbox" 
                    :id="`risk-${risk.value}`" 
                    v-model="risk.selected"
                    class="h-4 w-4 text-primary-600 rounded border-gray-300"
                    @change="updateMapVisualization"
                  />
                  <label :for="`risk-${risk.value}`" class="ml-2 text-sm text-gray-700 flex items-center">
                    <div :class="`w-3 h-3 rounded-full mr-1.5 ${risk.colorClass}`"></div>
                    {{ risk.label }}
                  </label>
                </div>
              </div>
            </div>
            
            <div class="mb-4">
              <label class="block text-sm font-medium text-gray-700 mb-2">지역 선택</label>
              <select 
                v-model="selectedRegion" 
                class="w-full px-3 py-2 border border-gray-300 rounded-md text-sm"
                @change="updateMapVisualization"
              >
                <option value="all">전체 지역</option>
                <option value="seoul">서울특별시</option>
                <option value="gyeonggi">경기도</option>
                <option value="busan">부산광역시</option>
                <option value="incheon">인천광역시</option>
                <option value="daegu">대구광역시</option>
              </select>
            </div>
            
            <div class="mb-4">
              <label class="block text-sm font-medium text-gray-700 mb-2">표시 모드</label>
              <div class="grid grid-cols-3 gap-2">
                <button 
                  @click="displayMode = 'heatmap'; updateMapVisualization()"
                  :class="[
                    'px-3 py-1.5 text-sm rounded-md',
                    displayMode === 'heatmap' 
                      ? 'bg-primary-100 text-primary-800 border border-primary-300' 
                      : 'bg-gray-100 text-gray-700 border border-gray-200 hover:bg-gray-200'
                  ]"
                >
                  히트맵
                </button>
                <button 
                  @click="displayMode = 'markers'; updateMapVisualization()"
                  :class="[
                    'px-3 py-1.5 text-sm rounded-md',
                    displayMode === 'markers' 
                      ? 'bg-primary-100 text-primary-800 border border-primary-300' 
                      : 'bg-gray-100 text-gray-700 border border-gray-200 hover:bg-gray-200'
                  ]"
                >
                  마커
                </button>
                <button 
                  @click="displayMode = 'clusters'; updateMapVisualization()"
                  :class="[
                    'px-3 py-1.5 text-sm rounded-md',
                    displayMode === 'clusters' 
                      ? 'bg-primary-100 text-primary-800 border border-primary-300' 
                      : 'bg-gray-100 text-gray-700 border border-gray-200 hover:bg-gray-200'
                  ]"
                >
                  클러스터
                </button>
              </div>
            </div>
            
            <div v-if="displayMode === 'heatmap'" class="mb-4">
              <label class="block text-sm font-medium text-gray-700 mb-2">히트맵 투명도</label>
              <div class="flex items-center justify-between mb-1">
                <span class="text-xs text-gray-500">0%</span>
                <span class="text-xs text-gray-500">{{ heatmapOpacity }}%</span>
                <span class="text-xs text-gray-500">100%</span>
              </div>
              <input 
                type="range" 
                v-model="heatmapOpacity" 
                min="0" 
                max="100" 
                step="5"
                class="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer"
                @input="updateMapVisualization"
              />
            </div>
          </div>
          
          <!-- 우측 지도 영역 -->
          <div class="flex-1 relative">
            <div id="full-risk-map" class="w-full h-full"></div>
            
            <!-- 로딩 인디케이터 -->
            <div v-if="isLoading" class="absolute inset-0 flex items-center justify-center bg-white bg-opacity-70 z-10">
              <div class="flex flex-col items-center">
                <div class="w-10 h-10 border-4 border-primary-600 border-t-transparent rounded-full animate-spin"></div>
                <p class="mt-2 text-sm text-gray-600">데이터를 불러오는 중...</p>
              </div>
            </div>
            
            <!-- 범례 -->
            <div class="absolute bottom-4 right-4 bg-white rounded-md shadow-md p-3 z-10">
              <h3 class="text-sm font-medium text-gray-700 mb-2">위험도 범례</h3>
              <div class="flex flex-col space-y-2 text-xs">
                <div class="flex items-center">
                  <div class="w-4 h-4 bg-red-500 rounded-sm mr-2"></div>
                  <span>심각 (위험도 80-100%)</span>
                </div>
                <div class="flex items-center">
                  <div class="w-4 h-4 bg-orange-500 rounded-sm mr-2"></div>
                  <span>높음 (위험도 60-80%)</span>
                </div>
                <div class="flex items-center">
                  <div class="w-4 h-4 bg-yellow-500 rounded-sm mr-2"></div>
                  <span>중간 (위험도 40-60%)</span>
                </div>
                <div class="flex items-center">
                  <div class="w-4 h-4 bg-green-500 rounded-sm mr-2"></div>
                  <span>낮음 (위험도 20-40%)</span>
                </div>
                <div class="flex items-center">
                  <div class="w-4 h-4 bg-blue-500 rounded-sm mr-2"></div>
                  <span>매우 낮음 (위험도 0-20%)</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 하단 타임라인 -->
        <div class="bg-white border-t border-gray-200 p-4 rounded-b-lg shadow">
          <div class="flex items-center justify-between mb-1">
            <label class="text-sm font-medium text-gray-700">시간대별 데이터</label>
            <span class="text-sm text-gray-500">{{ selectedTimeRange }}</span>
          </div>
          <input 
            type="range" 
            v-model="timelinePosition" 
            min="0" 
            max="23" 
            step="1"
            class="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer"
            @input="updateMapVisualization"
          />
          <div class="flex justify-between mt-1 text-xs text-gray-500">
            <span>00:00</span>
            <span>06:00</span>
            <span>12:00</span>
            <span>18:00</span>
            <span>23:59</span>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
  import { ArrowLeft, Flame, Map } from 'lucide-vue-next';
  
  export default {
    name: 'FullRiskMapPage',
    components: {
      ArrowLeft,
      Flame,
      Map
    },
    setup() {
      // 위험 분석 지도 관련 상태 변수들
      const isLoading = ref(false);
      const selectedPeriod = ref('week');
      const selectedRegion = ref('all');
      const displayMode = ref('heatmap');
      const heatmapOpacity = ref(65);
      const timelinePosition = ref(12);
  
      // 기간 옵션
      const periods = [
        { label: '오늘', value: 'today' },
        { label: '이번 주', value: 'week' },
        { label: '이번 달', value: 'month' },
        { label: '올해', value: 'year' }
      ];
  
      // 위험도 레벨
      const riskLevels = ref([
        { label: '심각', value: 'critical', selected: true, colorClass: 'bg-red-500' },
        { label: '높음', value: 'high', selected: true, colorClass: 'bg-orange-500' },
        { label: '중간', value: 'medium', selected: true, colorClass: 'bg-yellow-500' },
        { label: '낮음', value: 'low', selected: true, colorClass: 'bg-green-500' },
        { label: '매우 낮음', value: 'veryLow', selected: true, colorClass: 'bg-blue-500' }
      ]);
  
      // 선택된 시간대 표시
      const selectedTimeRange = computed(() => {
        const hour = timelinePosition.value;
        const nextHour = (hour + 1) % 24;
        return `${hour.toString().padStart(2, '0')}:00 - ${nextHour.toString().padStart(2, '0')}:00`;
      });
  
      // 지도 및 히트맵 객체
      let map = null;
      let mapOverlays = [];
      let mapMarkers = [];
  
      // 화재 데이터 (실제로는 API에서 가져와야 함)
      const fireReports = ref([
        {
          id: "1",
          username: "소방지킴이",
          userAvatar: "https://placehold.co/40x40",
          location: "강남역, 서울",
          time: "5분 전",
          riskLevel: "높음",
          status: "진행 중",
          verified: true,
          confirmedByFireDept: true
        },
        {
          id: "2",
          username: "안전제일",
          userAvatar: "https://placehold.co/40x40",
          location: "여의도 공원, 서울",
          time: "15분 전",
          riskLevel: "중간",
          status: "진행 중",
          verified: true,
          confirmedByFireDept: false
        }
      ]);
  
      const pendingReports = ref([
        {
          id: "3",
          username: "시민제보자",
          userAvatar: "https://placehold.co/40x40",
          location: "홍대 앞, 서울",
          time: "30분 전",
          dangers: 67,
          verified: false
        },
        {
          id: "4",
          username: "동네지킴이",
          userAvatar: "https://placehold.co/40x40",
          location: "강동구 천호동, 서울",
          time: "1시간 전",
          dangers: 92,
          verified: false
        }
      ]);
  
      const closedReports = ref([
        {
          id: "5",
          username: "안전지킴이",
          userAvatar: "https://placehold.co/40x40",
          location: "서초구 서초동, 서울",
          time: "2시간 전",
          closedTime: "30분 전",
          riskLevel: "중간",
          status: "종결",
          verified: true,
          confirmedByFireDept: true,
          closeReason: "화재 완전 진화 완료. 현장 안전 확보됨."
        },
        {
          id: "6",
          username: "시민제보자",
          userAvatar: "https://placehold.co/40x40",
          location: "마포구 합정동, 서울",
          time: "3시간 전",
          closedTime: "1시간 전",
          isDeleted: true,
          closeReason: "현장 확인 결과 화재가 아닌 것으로 판명. 요리 과정에서 발생한 연기로 확인됨."
        }
      ]);
  
      // 지도 초기화 함수
      const initMap = async () => {
        isLoading.value = true;
        
        try {
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
          map = new window.naver.maps.Map('full-risk-map', mapOptions);
          
          // 지도 로드 완료 이벤트 리스너 추가
          window.naver.maps.Event.once(map, 'init_stylemap', () => {
            console.log('지도 스타일 초기화 완료');
            
            // 선택된 표시 모드에 따라 데이터 시각화
            updateMapVisualization();
            
            isLoading.value = false;
          });
          
          // 지도 에러 이벤트 리스너
          window.naver.maps.Event.addListener(map, 'error', (e) => {
            console.error('지도 에러:', e);
            isLoading.value = false;
          });
          
        } catch (err) {
          console.error('지도 초기화 실패:', err);
          isLoading.value = false;
          
          // 네이버 지도 API가 로드되지 않은 경우, 로드 후 초기화
          console.log('네이버 지도 API를 로드합니다.');
          
          // 실제 구현에서는 네이버 지도 API를 동적으로 로드하는 코드 필요
          // 여기서는 간단히 시뮬레이션
          setTimeout(() => {
            console.log('네이버 지도 API 로드 시뮬레이션');
            
            // 네이버 지도 API 시뮬레이션
            window.naver = {
              maps: {
                Map: function(elementId, options) {
                  console.log(`지도 생성: ${elementId}`);
                  return {
                    setCenter: function() {},
                    setZoom: function() {}
                  };
                },
                LatLng: function(lat, lng) {
                  return { lat, lng };
                },
                Event: {
                  addListener: function(target, eventName, callback) {},
                  once: function(target, eventName, callback) {
                    // 즉시 콜백 호출하여 지도 초기화 완료 시뮬레이션
                    if (eventName === 'init_stylemap') {
                      setTimeout(callback, 100);
                    }
                  }
                },
                Circle: function(options) {
                  return {
                    setMap: function() {}
                  };
                },
                Marker: function(options) {
                  return {
                    setMap: function() {}
                  };
                },
                Size: function(width, height) {
                  return { width, height };
                },
                Point: function(x, y) {
                  return { x, y };
                },
                Position: {
                  TOP_RIGHT: 'TOP_RIGHT'
                }
              }
            };
            
            // 지도 초기화
            initMap();
          }, 500);
        }
      };
  
      // 지도 시각화 업데이트 함수
      const updateMapVisualization = () => {
        if (!map || !window.naver || !window.naver.maps) return;
        
        try {
          // 기존 오버레이 및 마커 제거
          clearMapOverlays();
          
          // 선택된 표시 모드에 따라 다른 시각화 적용
          switch (displayMode.value) {
            case 'heatmap':
              createHeatmap();
              break;
            case 'markers':
              createRiskMarkers();
              break;
            case 'clusters':
              createMarkerClusters();
              break;
          }
        } catch (err) {
          console.error('지도 시각화 업데이트 실패:', err);
        }
      };
  
      // 기존 오버레이 및 마커 제거 함수
      const clearMapOverlays = () => {
        // 오버레이 제거
        mapOverlays.forEach(overlay => overlay.setMap(null));
        mapOverlays = [];
        
        // 마커 제거
        mapMarkers.forEach(marker => marker.setMap(null));
        mapMarkers = [];
      };
  
      // 히트맵 생성 함수
      const createHeatmap = () => {
        // 활성 화재 및 검토 필요 제보 위치에 원형 오버레이 추가
        const allReports = [...fireReports.value, ...pendingReports.value, ...closedReports.value];
        
        // 각 제보 위치에 원형 오버레이 추가
        allReports.forEach(report => {
          // 필터링된 위험도 레벨만 표시
          const riskLevel = report.riskLevel || '미분류';
          const riskFilter = riskLevels.value.find(r => r.label === riskLevel);
          
          if (riskFilter && !riskFilter.selected) {
            return; // 선택되지 않은 위험도는 표시하지 않음
          }
          
          // 실제 구현에서는 제보의 위도/경도 정보를 사용해야 함
          // 여기서는 임의의 위치를 사용
          const lat = 37.5 + (Math.random() * 0.1);
          const lng = 126.9 + (Math.random() * 0.1);
          
          // 위험도에 따른 색상 설정
          let color = '#3B82F6'; // 기본 파란색
          let radius = 300; // 기본 반경 (미터)
          
          if (report.riskLevel === '심각') {
            color = '#EF4444'; // 빨간색
            radius = 500;
          } else if (report.riskLevel === '높음') {
            color = '#F97316'; // 주황색
            radius = 400;
          } else if (report.riskLevel === '중간') {
            color = '#FBBF24'; // 노란색
            radius = 300;
          }
          
          // 종결된 이벤트는 투명도 낮게 설정
          const opacity = (report.status === '종결' || report.isDeleted) ? 
            (heatmapOpacity.value / 200) : (heatmapOpacity.value / 100);
          
          // 원형 오버레이 생성
          const circleOverlay = new window.naver.maps.Circle({
            map: map,
            center: new window.naver.maps.LatLng(lat, lng),
            radius: radius,
            fillColor: color,
            fillOpacity: opacity,
            strokeColor: color,
            strokeOpacity: 0.5,
            strokeWeight: 2
          });
          
          // 오버레이 클릭 이벤트 추가
          window.naver.maps.Event.addListener(circleOverlay, 'click', () => {
            alert(`화재 제보 #${report.id}
  위치: ${report.location}
  위험도: ${report.riskLevel || '미분류'}
  상태: ${report.status || (report.isDeleted ? '오보' : '미확인')}`);
          });
          
          // 오버레이 저장
          mapOverlays.push(circleOverlay);
        });
      };
  
      // 위험도 마커 생성 함수
      const createRiskMarkers = () => {
        // 모든 제보 위치에 마커 추가
        const allReports = [...fireReports.value, ...pendingReports.value, ...closedReports.value];
        
        allReports.forEach(report => {
          // 필터링된 위험도 레벨만 표시
          const riskLevel = report.riskLevel || '미분류';
          const riskFilter = riskLevels.value.find(r => r.label === riskLevel);
          
          if (riskFilter && !riskFilter.selected) {
            return; // 선택되지 않은 위험도는 표시하지 않음
          }
          
          // 실제 구현에서는 제보의 위도/경도 정보를 사용해야 함
          // 여기서는 임의의 위치를 사용
          const lat = 37.5 + (Math.random() * 0.1);
          const lng = 126.9 + (Math.random() * 0.1);
          
          // 위험도에 따른 아이콘 설정
          let iconUrl = '';
          
          if (report.status === '종결' || report.isDeleted) {
            iconUrl = 'https://maps.google.com/mapfiles/ms/icons/gray-dot.png';
          } else if (report.riskLevel === '심각') {
            iconUrl = 'https://maps.google.com/mapfiles/ms/icons/red-dot.png';
          } else if (report.riskLevel === '높음') {
            iconUrl = 'https://maps.google.com/mapfiles/ms/icons/orange-dot.png';
          } else if (report.riskLevel === '중간') {
            iconUrl = 'https://maps.google.com/mapfiles/ms/icons/yellow-dot.png';
          } else {
            iconUrl = 'https://maps.google.com/mapfiles/ms/icons/blue-dot.png';
          }
          
          // 마커 생성
          const marker = new window.naver.maps.Marker({
            position: new window.naver.maps.LatLng(lat, lng),
            map: map,
            title: `화재 제보 #${report.id}`,
            icon: {
              url: iconUrl,
              size: new window.naver.maps.Size(32, 32),
              origin: new window.naver.maps.Point(0, 0),
              anchor: new window.naver.maps.Point(16, 32)
            }
          });
          
          // 마커 클릭 이벤트 추가
          window.naver.maps.Event.addListener(marker, 'click', () => {
            alert(`화재 제보 #${report.id}
  위치: ${report.location}
  위험도: ${report.riskLevel || '미분류'}
  상태: ${report.status || (report.isDeleted ? '오보' : '미확인')}`);
          });
          
          // 마커 저장
          mapMarkers.push(marker);
        });
      };
  
      // 마커 클러스터 생성 함수
      const createMarkerClusters = () => {
        // 모든 제보 위치에 마커 추가 (활성, 검토 필요, 종결된 이벤트)
        const allReports = [...fireReports.value, ...pendingReports.value, ...closedReports.value];
        
        // 마커 생성
        const markers = allReports.map(report => {
          // 필터링된 위험도 레벨만 표시
          const riskLevel = report.riskLevel || '미분류';
          const riskFilter = riskLevels.value.find(r => r.label === riskLevel);
          
          if (riskFilter && !riskFilter.selected) {
            return null; // 선택되지 않은 위험도는 표시하지 않음
          }
          
          // 실제 구현에서는 제보의 위도/경도 정보를 사용해야 함
          // 여기서는 임의의 위치를 사용
          const lat = 37.5 + (Math.random() * 0.1);
          const lng = 126.9 + (Math.random() * 0.1);
          
          // 상태에 따른 아이콘 설정
          let iconUrl = '';
          
          if (report.status === '종결' || report.isDeleted) {
            iconUrl = 'https://maps.google.com/mapfiles/ms/icons/gray-dot.png';
          } else if (report.riskLevel === '심각') {
            iconUrl = 'https://maps.google.com/mapfiles/ms/icons/red-dot.png';
          } else if (report.riskLevel === '높음') {
            iconUrl = 'https://maps.google.com/mapfiles/ms/icons/orange-dot.png';
          } else if (report.riskLevel === '중간') {
            iconUrl = 'https://maps.google.com/mapfiles/ms/icons/yellow-dot.png';
          } else {
            iconUrl = 'https://maps.google.com/mapfiles/ms/icons/blue-dot.png';
          }
          
          // 마커 생성
          const marker = new window.naver.maps.Marker({
            position: new window.naver.maps.LatLng(lat, lng),
            map: map,
            title: `화재 제보 #${report.id}`,
            icon: {
              url: iconUrl,
              size: new window.naver.maps.Size(32, 32),
              origin: new window.naver.maps.Point(0, 0),
              anchor: new window.naver.maps.Point(16, 32)
            }
          });
          
          // 마커 클릭 이벤트 추가
          window.naver.maps.Event.addListener(marker, 'click', () => {
            alert(`화재 제보 #${report.id}
  위치: ${report.location}
  위험도: ${report.riskLevel || '미분류'}
  상태: ${report.status || (report.isDeleted ? '오보' : '미확인')}`);
          });
          
          // 마커 저장
          mapMarkers.push(marker);
          
          return marker;
        }).filter(marker => marker !== null);
        
        // 실제 구현에서는 네이버 지도 API의 MarkerClustering 기능 사용
        // 여기서는 시뮬레이션만 수행
        console.log(`${markers.length}개의 마커가 클러스터링되었습니다.`);
      };
  
      // 컴포넌트 마운트 시 지도 초기화
      onMounted(() => {
        console.log('FullRiskMapPage 마운트됨');
        // 지도 초기화
        initMap();
      });
  
      // 컴포넌트 언마운트 시 지도 정리
      onUnmounted(() => {
        console.log('FullRiskMapPage 언마운트됨');
        
        // 오버레이 및 마커 제거
        clearMapOverlays();
        
        // 지도 변수 초기화
        map = null;
      });
  
      return {
        isLoading,
        selectedPeriod,
        selectedRegion,
        displayMode,
        heatmapOpacity,
        timelinePosition,
        periods,
        riskLevels,
        selectedTimeRange,
        updateMapVisualization
      };
    }
  };
  </script>
  
  <style scoped>
  /* 커스텀 스타일링 */
  input[type="range"]::-webkit-slider-thumb {
    -webkit-appearance: none;
    appearance: none;
    width: 16px;
    height: 16px;
    border-radius: 50%;
    background: #3b82f6;
    cursor: pointer;
  }
  
  input[type="range"]::-moz-range-thumb {
    width: 16px;
    height: 16px;
    border-radius: 50%;
    background: #3b82f6;
    cursor: pointer;
  }
  </style>