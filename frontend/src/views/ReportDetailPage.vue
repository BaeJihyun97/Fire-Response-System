<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 상단 네비게이션 바 -->
    <AppHeader title="제보 상세" :showBackButton="true" @back="goBack">
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
      <!-- 로딩 상태 -->
      <div v-if="isLoading" class="flex items-center justify-center py-12">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
      </div>

      <!-- 에러 상태 -->
      <div v-else-if="error" class="text-center py-12">
        <AlertTriangle class="mx-auto h-12 w-12 text-red-500" />
        <h3 class="mt-2 text-sm font-medium text-gray-900">데이터를 불러오는데 실패했습니다</h3>
        <p class="mt-1 text-sm text-gray-500">{{ error }}</p>
        <button
          @click="refreshData"
          class="mt-4 inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
        >
          다시 시도
        </button>
      </div>

      <!-- 제보 상세 내용 -->
      <div v-else class="space-y-6">
        <!-- 상태 배지 -->
        <div class="flex items-center justify-between">
          <div class="flex items-center space-x-2">
            <span
              class="px-3 py-1 rounded-full text-sm font-medium"
              :class="{
                'bg-red-100 text-red-800': report.isFire,
                'bg-yellow-100 text-yellow-800': !report.isFire
              }"
            >
              {{ report.isFire ? '화재 발생' : '화재 의심' }}
            </span>
            <span
              v-if="report.verified"
              class="px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800 flex items-center"
            >
              <Shield class="h-3 w-3 mr-1" />
              확인됨
            </span>
          </div>
          <span
            class="px-3 py-1 rounded-full text-sm font-medium"
            :class="{
              'bg-red-100 text-red-800': report.riskLevel === '높음',
              'bg-orange-100 text-orange-800': report.riskLevel === '중간',
              'bg-green-100 text-green-800': report.riskLevel === '낮음',
              'bg-purple-100 text-purple-800': report.riskLevel === 'N/A'
            }"
          >
            위험도: {{ report.riskLevel }}
          </span>
        </div>

        <!-- 미디어 -->
        <div class="relative rounded-xl overflow-hidden">
          <!-- 비디오가 있으면 비디오 플레이어 표시 -->
          <div v-if="report.metadata.videoUrl" class="w-full aspect-video">
            <VideoPlayer
              :videoUrl="report.metadata.videoUrl"
              :posterUrl="report.metadata.imageUrl"
              :showControls="true"
              :muted="false"
              class="w-full h-full"
            />
          </div>

          <!-- 비디오가 없으면 이미지 표시 -->
          <img
            v-else
            :src="report.metadata.imageUrl"
            alt="화재 이미지"
            class="w-full aspect-video object-cover"
          />
        </div>

        <!-- 위치 정보 -->
        <div class="bg-white rounded-xl p-4 shadow-soft">
          <div class="flex items-center text-sm text-gray-600 mb-2">
            <MapPin class="h-4 w-4 mr-2" />
            <span>위치 정보</span>
          </div>
          <div class="flex items-center justify-between">
            <span class="text-sm font-medium">{{ displayLocation }}</span>
            <button
              @click="showMap"
              class="text-sm text-primary-600 hover:text-primary-700"
            >
              지도에서 보기
            </button>
          </div>
        </div>

        <!-- 시간 정보 -->
        <div class="bg-white rounded-xl p-4 shadow-soft">
          <div class="flex items-center text-sm text-gray-600 mb-2">
            <Clock class="h-4 w-4 mr-2" />
            <span>제보 시간</span>
          </div>
          <div class="text-sm font-medium">{{ formatTime(report.timestamp) }}</div>
        </div>

        <!-- 상태 정보 -->
        <div class="bg-white rounded-xl p-4 shadow-soft">
          <div class="flex items-center text-sm text-gray-600 mb-2">
            <Activity class="h-4 w-4 mr-2" />
            <span>현재 상태</span>
          </div>
          <div class="text-sm font-medium">{{ report.status }}</div>
        </div>

        <!-- 위험도별 대응방안 -->
        <div class="mt-6">
          <h3 class="text-lg font-semibold mb-3">대응방안</h3>
          <div class="bg-white rounded-lg shadow p-4">
            <div v-if="report.riskLevel === '심각'" class="space-y-3">
              <div class="flex items-start">
                <AlertTriangle class="h-5 w-5 text-red-500 mt-0.5 mr-2" />
                <div class="flex-1">
                  <h4 class="font-medium text-red-800">즉시 대응 필요</h4>
                  <div class="mt-2">
                    <div v-for="(section, index) in [
                      {
                        title: '즉시 신고 및 대피',
                        items: [
                          '즉시 119 신고 (위치, 상황 상세 설명)',
                          '모든 인원 즉시 대피 (가장 가까운 비상구로 이동)',
                          '대피 시 엘리베이터 사용 금지',
                          '대피 시 가스, 전기 차단'
                        ]
                      },
                      {
                        title: '소방력 동원',
                        items: [
                          '소방차 3대 이상 출동 필요',
                          '구급차 2대 이상 대기',
                          '고층 소방차 1대 이상 요청',
                          '특수장비 소방차 1대 이상 요청'
                        ]
                      },
                      {
                        title: '전문가 동원',
                        items: [
                          '위험물질 처리팀 출동',
                          '구조대 2팀 이상 출동',
                          '의료진 2팀 이상 대기',
                          '화재조사팀 출동'
                        ]
                      },
                      {
                        title: '주변 대응',
                        items: [
                          '주변 건물 대피 준비',
                          '도로 통제 및 우회로 안내',
                          '주차장 차량 대피',
                          '주변 상가 대피 준비'
                        ]
                      }
                    ]" :key="index" class="mb-3">
                      <button
                        @click="toggleSection(index)"
                        class="w-full flex items-center justify-between p-2 rounded-lg hover:bg-red-50 transition-colors"
                      >
                        <span class="text-sm font-medium text-red-700">{{ section.title }}</span>
                        <ChevronDown
                          class="h-4 w-4 text-red-500 transition-transform"
                          :class="{ 'rotate-180': openSections[index] }"
                        />
                      </button>
                      <div
                        v-show="openSections[index]"
                        class="mt-2 pl-4 space-y-1"
                      >
                        <div v-for="(item, itemIndex) in section.items" :key="itemIndex" class="text-sm text-gray-700">
                          • {{ item }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-else-if="report.riskLevel === '높음'" class="space-y-3">
              <div class="flex items-start">
                <AlertTriangle class="h-5 w-5 text-orange-500 mt-0.5 mr-2" />
                <div class="flex-1">
                  <h4 class="font-medium text-orange-800">신속한 대응 필요</h4>
                  <div class="mt-2">
                    <div v-for="(section, index) in [
                      {
                        title: '신고 및 대피 준비',
                        items: [
                          '119 신고 (위치, 상황 설명)',
                          '인근 인원 대피 준비',
                          '비상구 확인 및 정리',
                          '가스, 전기 차단 준비'
                        ]
                      },
                      {
                        title: '소방력 동원',
                        items: [
                          '소방차 2대 출동 필요',
                          '구급차 1대 대기',
                          '고층 소방차 1대 요청',
                          '특수장비 소방차 1대 요청'
                        ]
                      },
                      {
                        title: '전문가 동원',
                        items: [
                          '화재 진압팀 출동',
                          '구조대 1팀 출동',
                          '의료진 1팀 대기',
                          '화재조사팀 출동'
                        ]
                      },
                      {
                        title: '주변 대응',
                        items: [
                          '주변 건물 대피 준비',
                          '도로 통제 준비',
                          '주차장 차량 대피 준비',
                          '주변 상가 대피 준비'
                        ]
                      }
                    ]" :key="index" class="mb-3">
                      <button
                        @click="toggleSection(index)"
                        class="w-full flex items-center justify-between p-2 rounded-lg hover:bg-orange-50 transition-colors"
                      >
                        <span class="text-sm font-medium text-orange-700">{{ section.title }}</span>
                        <ChevronDown
                          class="h-4 w-4 text-orange-500 transition-transform"
                          :class="{ 'rotate-180': openSections[index] }"
                        />
                      </button>
                      <div
                        v-show="openSections[index]"
                        class="mt-2 pl-4 space-y-1"
                      >
                        <div v-for="(item, itemIndex) in section.items" :key="itemIndex" class="text-sm text-gray-700">
                          • {{ item }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-else-if="report.riskLevel === '중간'" class="space-y-3">
              <div class="flex items-start">
                <AlertTriangle class="h-5 w-5 text-yellow-500 mt-0.5 mr-2" />
                <div class="flex-1">
                  <h4 class="font-medium text-yellow-800">주의 필요</h4>
                  <div class="mt-2">
                    <div v-for="(section, index) in [
                      {
                        title: '신고 및 대피 준비',
                        items: [
                          '119 신고 (위치, 상황 설명)',
                          '인원 대피 준비',
                          '비상구 확인',
                          '가스, 전기 차단 준비'
                        ]
                      },
                      {
                        title: '소방력 동원',
                        items: [
                          '소방차 1대 출동 필요',
                          '구급차 대기',
                          '고층 소방차 대기',
                          '특수장비 소방차 대기'
                        ]
                      },
                      {
                        title: '전문가 동원',
                        items: [
                          '화재 진압팀 대기',
                          '구조대 대기',
                          '의료진 대기',
                          '화재조사팀 대기'
                        ]
                      },
                      {
                        title: '주변 대응',
                        items: [
                          '주변 건물 대피 준비',
                          '도로 통제 준비',
                          '주차장 차량 대피 준비',
                          '주변 상가 대피 준비'
                        ]
                      }
                    ]" :key="index" class="mb-3">
                      <button
                        @click="toggleSection(index)"
                        class="w-full flex items-center justify-between p-2 rounded-lg hover:bg-yellow-50 transition-colors"
                      >
                        <span class="text-sm font-medium text-yellow-700">{{ section.title }}</span>
                        <ChevronDown
                          class="h-4 w-4 text-yellow-500 transition-transform"
                          :class="{ 'rotate-180': openSections[index] }"
                        />
                      </button>
                      <div
                        v-show="openSections[index]"
                        class="mt-2 pl-4 space-y-1"
                      >
                        <div v-for="(item, itemIndex) in section.items" :key="itemIndex" class="text-sm text-gray-700">
                          • {{ item }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-else-if="report.riskLevel === '낮음'" class="space-y-3">
              <div class="flex items-start">
                <AlertTriangle class="h-5 w-5 text-green-500 mt-0.5 mr-2" />
                <div class="flex-1">
                  <h4 class="font-medium text-green-800">관찰 필요</h4>
                  <div class="mt-2">
                    <div v-for="(section, index) in [
                      {
                        title: '신고 및 대피 준비',
                        items: [
                          '119 신고 (위치, 상황 설명)',
                          '인원 대피 준비',
                          '비상구 확인',
                          '가스, 전기 차단 준비'
                        ]
                      },
                      {
                        title: '소방력 동원',
                        items: [
                          '소방차 대기',
                          '구급차 대기',
                          '고층 소방차 대기',
                          '특수장비 소방차 대기'
                        ]
                      },
                      {
                        title: '전문가 동원',
                        items: [
                          '화재 진압팀 대기',
                          '구조대 대기',
                          '의료진 대기',
                          '화재조사팀 대기'
                        ]
                      },
                      {
                        title: '주변 대응',
                        items: [
                          '주변 건물 대피 준비',
                          '도로 통제 준비',
                          '주차장 차량 대피 준비',
                          '주변 상가 대피 준비'
                        ]
                      }
                    ]" :key="index" class="mb-3">
                      <button
                        @click="toggleSection(index)"
                        class="w-full flex items-center justify-between p-2 rounded-lg hover:bg-green-50 transition-colors"
                      >
                        <span class="text-sm font-medium text-green-700">{{ section.title }}</span>
                        <ChevronDown
                          class="h-4 w-4 text-green-500 transition-transform"
                          :class="{ 'rotate-180': openSections[index] }"
                        />
                      </button>
                      <div
                        v-show="openSections[index]"
                        class="mt-2 pl-4 space-y-1"
                      >
                        <div v-for="(item, itemIndex) in section.items" :key="itemIndex" class="text-sm text-gray-700">
                          • {{ item }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-else-if="report.riskLevel === 'N/A'" class="space-y-3">
              <div class="flex items-start">
                <AlertTriangle class="h-5 w-5 text-purple-500 mt-0.5 mr-2" />
                <div class="flex-1">
                  <h4 class="font-medium text-purple-800">기본 안전 수칙 준수</h4>
                  <div class="mt-2">
                    <div v-for="(section, index) in [
                      {
                        title: '기본 안전 수칙',
                        items: [
                          '화재 감지기, 소화기 등 안전장비 점검',
                          '비상구 주변 물건 정리',
                          '가스, 전기 사용 시 주의',
                          '화기 취급 시 주의'
                        ]
                      },
                      {
                        title: '대비 사항',
                        items: [
                          '비상시 연락처 확인',
                          '대피 경로 숙지',
                          '소화기 사용법 확인',
                          '응급처치 방법 숙지'
                        ]
                      },
                      {
                        title: '일상적 점검',
                        items: [
                          '전기 콘센트 과부하 확인',
                          '가스 누수 여부 확인',
                          '화재 감지기 작동 확인',
                          '소화기 유효기간 확인'
                        ]
                      },
                      {
                        title: '교육 및 훈련',
                        items: [
                          '소방훈련 참여',
                          '화재 대응 매뉴얼 숙지',
                          '응급처치 교육 참여',
                          '안전교육 이수'
                        ]
                      }
                    ]" :key="index" class="mb-3">
                      <button
                        @click="toggleSection(index)"
                        class="w-full flex items-center justify-between p-2 rounded-lg hover:bg-purple-50 transition-colors"
                      >
                        <span class="text-sm font-medium text-purple-700">{{ section.title }}</span>
                        <ChevronDown
                          class="h-4 w-4 text-purple-500 transition-transform"
                          :class="{ 'rotate-180': openSections[index] }"
                        />
                      </button>
                      <div
                        v-show="openSections[index]"
                        class="mt-2 pl-4 space-y-1"
                      >
                        <div v-for="(item, itemIndex) in section.items" :key="itemIndex" class="text-sm text-gray-700">
                          • {{ item }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- AI 분석 결과 -->
        <div class="mt-6">
          <h3 class="text-lg font-semibold mb-3">AI 분석 결과</h3>
          <div class="bg-white rounded-lg shadow p-4 space-y-4">
            <!-- 화재 감지 결과 -->
            <div class="flex items-start">
              <div class="flex-1">
                <div class="flex items-center mb-2">
                  <AlertTriangle class="h-5 w-5 mr-2" :class="aiAnalysis.fire_detected ? 'text-red-500' : 'text-green-500'" />
                  <h4 class="font-medium">화재 감지 결과</h4>
                </div>
                <p class="text-sm text-gray-700">
                  {{ aiAnalysis.fire_detected ? '화재가 감지되었습니다.' : '화재가 감지되지 않았습니다.' }}
                </p>
              </div>
            </div>

            <!-- 연기 감지 결과 -->
            <div class="flex items-start">
              <div class="flex-1">
                <div class="flex items-center mb-2">
                  <Cloud class="h-5 w-5 mr-2" :class="aiAnalysis.smoke.present ? 'text-orange-500' : 'text-green-500'" />
                  <h4 class="font-medium">연기 감지 결과</h4>
                </div>
                <p class="text-sm text-gray-700">
                  {{ aiAnalysis.smoke.present ?
                    `연기가 감지되었습니다. (색상: ${aiAnalysis.smoke.color}, 농도: ${aiAnalysis.smoke.density})` :
                    '연기가 감지되지 않았습니다.' }}
                </p>
              </div>
            </div>

            <!-- 인물/동물 감지 결과 -->
            <div class="flex items-start">
              <div class="flex-1">
                <div class="flex items-center mb-2">
                  <Users class="h-5 w-5 mr-2" :class="aiAnalysis.people_or_animals.present ? 'text-blue-500' : 'text-gray-500'" />
                  <h4 class="font-medium">인물/동물 감지 결과</h4>
                </div>
                <p class="text-sm text-gray-700">
                  {{ aiAnalysis.people_or_animals.present ? aiAnalysis.people_or_animals.details : '인물이나 동물이 감지되지 않았습니다.' }}
                </p>
              </div>
            </div>

            <!-- 화재 확산 상태 -->
            <div class="flex items-start">
              <div class="flex-1">
                <div class="flex items-center mb-2">
                  <Flame class="h-5 w-5 mr-2" :class="aiAnalysis.fire_spread.spreading ? 'text-red-500' : 'text-green-500'" />
                  <h4 class="font-medium">화재 확산 상태</h4>
                </div>
                <p class="text-sm text-gray-700">
                  {{ aiAnalysis.fire_spread.spreading ?
                    '화재가 확산되고 있습니다.' :
                    '화재가 확산되고 있지 않습니다.' }}
                </p>
              </div>
            </div>

            <!-- 소방대원 감지 결과 -->
            <div class="flex items-start">
              <div class="flex-1">
                <div class="flex items-center mb-2">
                  <Shield class="h-5 w-5 mr-2" :class="aiAnalysis.firefighting_response.responders_present ? 'text-blue-500' : 'text-gray-500'" />
                  <h4 class="font-medium">소방대원 감지 결과</h4>
                </div>
                <p class="text-sm text-gray-700">
                  {{ aiAnalysis.firefighting_response.responders_present ?
                    '소방대원이 현장에 있습니다.' :
                    '소방대원이 아직 도착하지 않았습니다.' }}
                </p>
              </div>
            </div>

            <!-- 감지된 객체 목록 -->
            <div class="flex items-start">
              <div class="flex-1">
                <div class="flex items-center mb-2">
                  <List class="h-5 w-5 mr-2 text-gray-500" />
                  <h4 class="font-medium">감지된 객체</h4>
                </div>
                <div class="flex flex-wrap gap-2">
                  <span
                    v-for="(object, index) in aiAnalysis.objects"
                    :key="index"
                    class="px-2 py-1 bg-gray-100 text-gray-700 rounded-full text-xs"
                  >
                    {{ object }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 액션 버튼 -->
        <div class="grid grid-cols-3 gap-2">
          <button
            @click="confirmFire"
            class="flex items-center justify-center text-sm font-medium py-2 rounded-lg"
            :class="isConfirmed ? 'text-red-600 bg-red-50' : 'text-gray-600 bg-gray-50'"
          >
            <AlertTriangle :class="['h-5 w-5 mr-1.5', isConfirmed ? 'fill-red-100' : '']" />
            <span>위험해요 {{ report.metadata.likes }}</span>
          </button>

          <button
            @click="openComments"
            class="flex items-center justify-center text-gray-600 text-sm font-medium py-2 rounded-lg bg-gray-50"
          >
            <MessageSquare class="h-5 w-5 mr-1.5" />
            <span>댓글 {{ report.metadata.comments }}</span>
          </button>

          <button
            @click="shareReport"
            class="flex items-center justify-center text-gray-600 text-sm font-medium py-2 rounded-lg bg-gray-50"
          >
            <Share2 class="h-5 w-5 mr-1.5" />
            <span>공유</span>
          </button>
        </div>

        <!-- 댓글 섹션 -->
        <div v-if="showComments" class="bg-white rounded-xl p-4 shadow-soft">
          <comment-section :post-id="report.id" />
        </div>
      </div>
    </div>

    <!-- 지도 모달 -->
    <MapView
      v-if="showMapModal"
      :location-name="displayLocation"
      :location-address="formattedAddress?.fullAddress || `위도: ${report.coordinates.lat}, 경도: ${report.coordinates.lng}`"
      :location-coords="report.coordinates"
      @close="showMapModal = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { AlertTriangle, MapPin, Clock, Activity, MessageSquare, Share2, Shield, RefreshCw, ChevronDown, Cloud, Users, Flame, List } from 'lucide-vue-next';
import AppHeader from '../components/AppHeader.vue';
import VideoPlayer from '../components/VideoPlayer.vue';
import CommentSection from '../components/CommentSection.vue';
import MapView from './MapView.vue';
import { reverseGeocode } from '../services/geocodingService';
import { postApi, reportApiService, videoApiService } from '../services/api';

const router = useRouter();
const route = useRoute();
const isLoading = ref(true);
const isRefreshing = ref(false);
const error = ref(null);
const showComments = ref(false);
const showMapModal = ref(false);
const isConfirmed = ref(false);
const displayLocation = ref('위치 정보 로딩 중...');
const formattedAddress = ref(null);

// 더미 데이터 - 실제로는 API에서 가져올 데이터
const report = ref({
  id: '',
  coordinates: { lat: 37.5665, lng: 126.9780 },
  timestamp: '',
  status: '진행 중',
  isFire: false,
  riskLevel: '낮음',
  verified: false,
  metadata: {
    likes: 0,
    comments: 0,
    videoUrl: null,
    imageUrl: ''
  }
});

// AI 분석 결과 데이터
const aiAnalysis = ref({
  fire_detected: false,
  fire_size: 'N/A',
  flame_color: [],
  smoke: {
    present: false,
    color: 'N/A',
    density: 'N/A'
  },
  objects: [],
  people_or_animals: {
    present: false,
    details: ''
  },
  fire_spread: {
    spreading: false,
    indicators: []
  },
  firefighting_response: {
    responders_present: false
  },
  severity: 'N/A'
});

// 아코디언 섹션 상태 관리
const openSections = ref([false, false, false, false]);

// 섹션 토글 함수
const toggleSection = (index) => {
  openSections.value[index] = !openSections.value[index];
};

// 데이터 새로고침
const refreshData = async () => {
  isRefreshing.value = true;
  error.value = null;

  try {
    const postId = route.params.id;
    const response = await postApi.getPost(postId);
    const post = response.data;

    // API 응답 데이터를 현재 구조에 맞게 변환
    report.value = {
      id: post.postId,
      coordinates: { lat: post.latitude, lng: post.longitude },
      timestamp: new Date().toISOString(),
      status: '진행 중',
      isFire: true,
      riskLevel: '중간',
      verified: false,
      metadata: {
        likes: post.reactionCount || 0,
        comments: 0,
        videoUrl: post.blurredVideoUri || '',
        imageUrl: ''
      }
    };

    // 위치 정보 가져오기
    await loadLocationInfo();
    let videoId;
    try {
      const reportResponse = await reportApiService.getLatestReportByEventId(post.eventId);
      if (reportResponse.data && reportResponse.data.videoId) {
        videoId = reportResponse.data.videoId;
        report.value.metadata.videoUrl = `https://team05sa.blob.core.windows.net/videos/${reportResponse.data.videoId}/${reportResponse.data.videoId}.mp4`;
      }
    } catch (error) {
      console.error('비디오 정보 가져오기 실패:', error);
    }
    const videoInfo = await videoApiService.getVideo(videoId);
    const ai = videoInfo.data.latest_analysis;
    if (ai !== null) {
      aiAnalysis.value = ai;
      report.value.isFire = ai.fire_detected;
      report.value.riskLevel = ai.severity;
    }
  } catch (err) {
    error.value = '데이터를 불러오는데 실패했습니다. 다시 시도해주세요.';
    console.error('데이터 로딩 오류:', err);
  } finally {
    isLoading.value = false;
    isRefreshing.value = false;
  }
};

const formatAddress = (address) => {
  if (!address) return '';

  const parts = [];
  if (address.province) parts.push(address.province);
  if (address.city) parts.push(address.city);
  if (address.borough) parts.push(address.borough);
  if (address.quarter) parts.push(address.quarter);
  if (address.road) parts.push(address.road);
  if (address.house_number) parts.push(address.house_number);

  return parts.join(' ');
};

const getLocationInfo = async (coordinates) => {
  try {
    const response = await fetch(
      `https://nominatim.openstreetmap.org/reverse?format=json&lat=${coordinates.lat}&lon=${coordinates.lng}&zoom=18&addressdetails=1&accept-language=ko`
    );
    const data = await response.json();

    if (data && data.address) {
      return formatAddress(data.address);
    }
    return '위치 정보 없음';
  } catch (error) {
    console.error('위치 정보 로딩 실패:', error);
    return '위치 정보 없음';
  }
};

// 위치 정보 로드
const loadLocationInfo = async () => {
  try {
    const result = await getLocationInfo(report.value.coordinates);
    formattedAddress.value = result;
    displayLocation.value = result;
  } catch (err) {
    console.error('위치 정보 로딩 오류:', err);
    displayLocation.value = `위도: ${report.value.coordinates.lat}, 경도: ${report.value.coordinates.lng}`;
  }
};

// 지도 보기
const showMap = () => {
  showMapModal.value = true;
};

// 시간 포맷팅
const formatTime = (timestamp) => {
  if (!timestamp) return '';
  const date = new Date(timestamp);
  return date.toLocaleString();
};

// 뒤로 가기
const goBack = () => {
  router.back();
};

// 위험해요 버튼 클릭
const confirmFire = () => {
  isConfirmed.value = !isConfirmed.value;
  if (isConfirmed.value) {
    report.value.metadata.likes++;
  } else {
    report.value.metadata.likes--;
  }
};

// 댓글 섹션 토글
const openComments = () => {
  showComments.value = !showComments.value;
};

// 공유하기
const shareReport = async () => {
  try {
    await navigator.share({
      title: `화재 제보: ${displayLocation.value}`,
      text: report.value.isFire ? '화재가 발생했습니다.' : '화재 의심 상황입니다.',
      url: window.location.href
    });
  } catch (err) {
    console.error('공유하기 오류:', err);
  }
};

// 초기 데이터 로드
onMounted(() => {
  refreshData();
});
</script>

<style scoped>
.shadow-soft {
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}
</style>

