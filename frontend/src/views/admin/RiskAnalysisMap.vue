<template>
    <div class="risk-analysis-map">
      <!-- 지도 필터 컨트롤 -->
      <div class="p-4 border-b border-gray-200">
        <div class="grid grid-cols-2 gap-3">
          <div>
            <label class="block text-xs text-gray-500 mb-1">표시 모드</label>
            <select 
              v-model="displayMode" 
              class="w-full text-sm border border-gray-300 rounded-md p-1.5"
            >
              <option value="heatmap">히트맵</option>
              <option value="markers">마커</option>
              <option value="clusters">클러스터</option>
            </select>
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">기간</label>
            <select 
              v-model="selectedPeriod" 
              class="w-full text-sm border border-gray-300 rounded-md p-1.5"
            >
              <option value="today">오늘</option>
              <option value="week">이번 주</option>
              <option value="month">이번 달</option>
              <option value="year">올해</option>
            </select>
          </div>
        </div>
        
        <!-- 위험도 필터 -->
        <div class="mt-3">
          <label class="block text-xs text-gray-500 mb-1">위험도 필터</label>
          <div class="flex flex-wrap gap-2">
            <div 
              v-for="risk in riskLevels" 
              :key="risk.value"
              class="flex items-center"
            >
              <input 
                type="checkbox" 
                :id="`risk-${risk.value}`" 
                v-model="risk.selected"
                class="h-3 w-3 text-primary-600 rounded border-gray-300"
                @change="updateMapVisualization"
              />
              <label :for="`risk-${risk.value}`" class="ml-1 text-xs text-gray-700 flex items-center">
                <div :class="`w-2 h-2 rounded-full mr-1 ${risk.colorClass}`"></div>
                {{ risk.label }}
              </label>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 지도 컨테이너 -->
      <div class="relative h-64">
        <div :id="mapId" class="w-full h-full"></div>
        
        <!-- 로딩 인디케이터 -->
        <div v-if="isLoading" class="absolute inset-0 flex items-center justify-center bg-white bg-opacity-70 z-10">
          <div class="flex flex-col items-center">
            <div class="w-8 h-8 border-3 border-primary-600 border-t-transparent rounded-full animate-spin"></div>
            <p class="mt-2 text-xs text-gray-600">데이터를 불러오는 중...</p>
          </div>
        </div>
        
        <!-- 범례 -->
        <div class="absolute bottom-2 right-2 bg-white rounded-md shadow-sm p-2 text-xs z-10">
          <div class="flex flex-col space-y-1">
            <div class="flex items-center">
              <div class="w-3 h-3 bg-red-500 rounded-full mr-1"></div>
              <span>심각</span>
            </div>
            <div class="flex items-center">
              <div class="w-3 h-3 bg-orange-500 rounded-full mr-1"></div>
              <span>높음</span>
            </div>
            <div class="flex items-center">
              <div class="w-3 h-3 bg-yellow-500 rounded-full mr-1"></div>
              <span>중간</span>
            </div>
            <div class="flex items-center">
              <div class="w-3 h-3 bg-green-500 rounded-full mr-1"></div>
              <span>낮음</span>
            </div>
          </div>
        </div>
        
        <!-- 히트맵 투명도 조절 -->
        <div v-if="displayMode === 'heatmap'" class="absolute bottom-2 left-2 bg-white rounded-md shadow-sm p-2 w-32 z-10">
          <div class="flex items-center justify-between mb-1">
            <label class="text-xs text-gray-700">투명도</label>
            <span class="text-xs text-gray-500">{{ heatmapOpacity }}%</span>
          </div>
          <input 
            type="range" 
            v-model="heatmapOpacity" 
            min="0" 
            max="100" 
            step="5"
            class="w-full h-1.5 bg-gray-200 rounded-lg appearance-none cursor-pointer"
            @input="updateMapVisualization"
          />
        </div>
      </div>
      
      <!-- 시간대별 데이터 슬라이더 -->
      <div class="p-3 border-t border-gray-200">
        <div class="flex items-center justify-between mb-1">
          <label class="text-xs text-gray-700">시간대별 데이터</label>
          <span class="text-xs text-gray-500">{{ selectedTimeRange }}</span>
        </div>
        <input 
          type="range" 
          v-model="timelinePosition" 
          min="0" 
          max="23" 
          step="1"
          class="w-full h-1.5 bg-gray-200 rounded-lg appearance-none cursor-pointer"
          @input="updateMapVisualization"
        />
        <div class="flex justify-between mt-1 text-xs text-gray-500">
          <span>00:00</span>
          <span>12:00</span>
          <span>23:59</span>
        </div>
      </div>
      
      
      
      <!-- 전체 위험 분석 지도 모달 -->
      <div v-if="showFullMap" class="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50">
        <div class="bg-white rounded-lg shadow-lg w-full max-w-4xl mx-4 h-[80vh] flex flex-col">
          <div class="p-4 border-b border-gray-200 flex justify-between items-center">
            <h3 class="text-lg font-bold">위험 분석 지도</h3>
            <button @click="closeFullMap" class="p-1 rounded-full hover:bg-gray-100">
              <X class="h-5 w-5" />
            </button>
          </div>
          
          <div class="flex-1 flex">
            <!-- 좌측 필터 패널 -->
            <div class="w-64 border-r border-gray-200 p-4 overflow-y-auto">
              <div class="mb-4">
                <label class="block text-sm font-medium text-gray-700 mb-2">기간 선택</label>
                <div class="space-y-2">
                  <button 
                    v-for="period in periods" 
                    :key="period.value"
                    @click="selectedPeriod = period.value; updateFullMapVisualization()"
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
                      :id="`modal-risk-${risk.value}`" 
                      v-model="risk.selected"
                      class="h-4 w-4 text-primary-600 rounded border-gray-300"
                      @change="updateFullMapVisualization"
                    />
                    <label :for="`modal-risk-${risk.value}`" class="ml-2 text-sm text-gray-700 flex items-center">
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
                  @change="updateFullMapVisualization"
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
                    @click="displayMode = 'heatmap'; updateFullMapVisualization()"
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
                    @click="displayMode = 'markers'; updateFullMapVisualization()"
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
                    @click="displayMode = 'clusters'; updateFullMapVisualization()"
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
                  @input="updateFullMapVisualization"
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
          <div class="border-t border-gray-200 p-4">
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
              @input="updateFullMapVisualization"
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
    </div>
  </template>
  
  <script>
  import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
  import { X } from 'lucide-vue-next';
  
  export default {
    name: 'RiskAnalysisMap',
    components: {
      X
    },
    props: {
      fireReports: {
        type: Array,
        default: () => []
      },
      pendingReports: {
        type: Array,
        default: () => []
      },
      closedReports: {
        type: Array,
        default: () => []
      },
      showFullMap: {
        type: Boolean,
        default: false
      },
      mapId: {
        type: String,
        default: 'admin-map'
      }
    },
    emits: ['open-full-map', 'close-full-map'],
    setup(props, { emit }) {
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
      let fullMap = null;
      let mapOverlays = [];
      let mapMarkers = [];
      let fullMapOverlays = [];
      let fullMapMarkers = [];
  
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
          map = new window.naver.maps.Map(props.mapId, mapOptions);
          
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
  
      // 전체 지도 초기화 함수
      const initFullMap = async () => {
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
          fullMap = new window.naver.maps.Map('full-risk-map', mapOptions);
          
          // 지도 로드 완료 이벤트 리스너 추가
          window.naver.maps.Event.once(fullMap, 'init_stylemap', () => {
            console.log('전체 지도 스타일 초기화 완료');
            
            // 선택된 표시 모드에 따라 데이터 시각화
            updateFullMapVisualization();
            
            isLoading.value = false;
          });
          
          // 지도 에러 이벤트 리스너
          window.naver.maps.Event.addListener(fullMap, 'error', (e) => {
            console.error('지도 에러:', e);
            isLoading.value = false;
          });
          
        } catch (err) {
          console.error('전체 지도 초기화 실패:', err);
          isLoading.value = false;
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
  
      // 전체 지도 시각화 업데이트 함수
      const updateFullMapVisualization = () => {
        if (!fullMap || !window.naver || !window.naver.maps) return;
        
        try {
          // 기존 오버레이 및 마커 제거
          clearFullMapOverlays();
          
          // 선택된 표시 모드에 따라 다른 시각화 적용
          switch (displayMode.value) {
            case 'heatmap':
              createFullHeatmap();
              break;
            case 'markers':
              createFullRiskMarkers();
              break;
            case 'clusters':
              createFullMarkerClusters();
              break;
          }
        } catch (err) {
          console.error('전체 지도 시각화 업데이트 실패:', err);
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
  
      // 기존 전체 지도 오버레이 및 마커 제거 함수
      const clearFullMapOverlays = () => {
        // 오버레이 제거
        fullMapOverlays.forEach(overlay => overlay.setMap(null));
        fullMapOverlays = [];
        
        // 마커 제거
        fullMapMarkers.forEach(marker => marker.setMap(null));
        fullMapMarkers = [];
      };
  
      // 히트맵 생성 함수
      const createHeatmap = () => {
        // 활성 화재 및 검토 필요 제보 위치에 원형 오버레이 추가
        const allReports = [...props.fireReports, ...props.pendingReports];
        
        // 각 제보 위치에 원형 오버레이 추가
        allReports.forEach(report => {
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
          
          // 원형 오버레이 생성
          const circleOverlay = new window.naver.maps.Circle({
            map: map,
            center: new window.naver.maps.LatLng(lat, lng),
            radius: radius,
            fillColor: color,
            fillOpacity: heatmapOpacity.value / 100,
            strokeColor: color,
            strokeOpacity: 0.5,
            strokeWeight: 2
          });
          
          // 오버레이 클릭 이벤트 추가
          window.naver.maps.Event.addListener(circleOverlay, 'click', () => {
            alert(`화재 제보 #${report.id}\n위치: ${report.location}\n위험도: ${report.riskLevel || '미분류'}`);
          });
          
          // 오버레이 저장
          mapOverlays.push(circleOverlay);
        });
      };
  
      // 위험도 마커 생성 함수
      const createRiskMarkers = () => {
        // 활성 화재 및 검토 필요 제보 위치에 마커 추가
        const allReports = [...props.fireReports, ...props.pendingReports];
        
        allReports.forEach(report => {
          // 실제 구현에서는 제보의 위도/경도 정보를 사용해야 함
          // 여기서는 임의의 위치를 사용
          const lat = 37.5 + (Math.random() * 0.1);
          const lng = 126.9 + (Math.random() * 0.1);
          
          // 위험도에 따른 아이콘 설정
          let iconUrl = '';
          
          if (report.riskLevel === '심각') {
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
            alert(`화재 제보 #${report.id}\n위치: ${report.location}\n위험도: ${report.riskLevel || '미분류'}`);
          });
          
          // 마커 저장
          mapMarkers.push(marker);
        });
      };
  
      // 마커 클러스터 생성 함수
      const createMarkerClusters = () => {
        // 모든 제보 위치에 마커 추가 (활성, 검토 필요, 종결된 이벤트)
        const allReports = [...props.fireReports, ...props.pendingReports, ...props.closedReports];
        
        // 마커 생성
        const markers = allReports.map(report => {
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
            alert(`화재 제보 #${report.id}\n위치: ${report.location}\n위험도: ${report.riskLevel || '미분류'}\n상태: ${report.status || (report.isDeleted ? '오보' : '미확인')}`);
          });
          
          // 마커 저장
          mapMarkers.push(marker);
          
          return marker;
        });
        
        // 실제 구현에서는 네이버 지도 API의 MarkerClustering 기능 사용
        // 여기서는 시뮬레이션만 수행
        console.log(`${markers.length}개의 마커가 클러스터링되었습니다.`);
      };
  
      // 전체 히트맵 생성 함수
      const createFullHeatmap = () => {
        // 모든 제보 위치에 원형 오버레이 추가
        const allReports = [...props.fireReports, ...props.pendingReports, ...props.closedReports];
        
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
            map: fullMap,
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
            alert(`화재 제보 #${report.id}\n위치: ${report.location}\n위험도: ${report.riskLevel || '미분류'}\n상태: ${report.status || (report.isDeleted ? '오보' : '미확인')}`);
          });
          
          // 오버레이 저장
          fullMapOverlays.push(circleOverlay);
        });
      };
  
      // 전체 위험도 마커 생성 함수
      const createFullRiskMarkers = () => {
        // 모든 제보 위치에 마커 추가
        const allReports = [...props.fireReports, ...props.pendingReports, ...props.closedReports];
        
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
            map: fullMap,
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
            alert(`화재 제보 #${report.id}\n위치: ${report.location}\n위험도: ${report.riskLevel || '미분류'}\n상태: ${report.status || (report.isDeleted ? '오보' : '미확인')}`);
          });
          
          // 마커 저장
          fullMapMarkers.push(marker);
        });
      };
  
      // 전체 마커 클러스터 생성 함수
      const createFullMarkerClusters = () => {
        // 모든 제보 위치에 마커 추가 (활성, 검토 필요, 종결된 이벤트)
        const allReports = [...props.fireReports, ...props.pendingReports, ...props.closedReports];
        
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
            map: fullMap,
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
            alert(`화재 제보 #${report.id}\n위치: ${report.location}\n위험도: ${report.riskLevel || '미분류'}\n상태: ${report.status || (report.isDeleted ? '오보' : '미확인')}`);
          });
          
          // 마커 저장
          fullMapMarkers.push(marker);
          
          return marker;
        }).filter(marker => marker !== null);
        
        // 실제 구현에서는 네이버 지도 API의 MarkerClustering 기능 사용
        // 여기서는 시뮬레이션만 수행
        console.log(`${markers.length}개의 마커가 클러스터링되었습니다.`);
      };
  
      // 전체 지도 모달 닫기
      const closeFullMap = () => {
        emit('close-full-map');
      };
  
      // 감시자 설정
      watch([displayMode, selectedPeriod, selectedRegion, heatmapOpacity, timelinePosition], () => {
        updateMapVisualization();
      });
  
      // 전체 지도 모달 감시자
      watch(() => props.showFullMap, (newVal) => {
        if (newVal) {
          // 다음 틱에서 전체 지도 초기화 (DOM이 업데이트된 후)
          setTimeout(() => {
            initFullMap();
          }, 100);
        } else {
          // 전체 지도 모달이 닫힐 때 정리
          if (fullMap) {
            clearFullMapOverlays();
          }
        }
      });
  
      // 컴포넌트 마운트 시 지도 초기화
      onMounted(() => {
        console.log('RiskAnalysisMap 마운트됨');
        // 지도 초기화
        initMap();
      });
  
      // 컴포넌트 언마운트 시 지도 정리
      onUnmounted(() => {
        console.log('RiskAnalysisMap 언마운트됨');
        
        // 오버레이 및 마커 제거
        clearMapOverlays();
        clearFullMapOverlays();
        
        // 지도 변수 초기화
        map = null;
        fullMap = null;
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
        updateMapVisualization,
        updateFullMapVisualization,
        closeFullMap
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
  
  .risk-analysis-map {
    position: relative;
    z-index: 1;
  }
  </style>