<template>
  <div class="min-h-screen bg-gray-100">
    <!-- 긴급 헤더 -->
    <div class="bg-red-600 text-white py-2 px-4">
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
    
    <div class="container mx-auto px-4 py-6 max-w-lg">
      <div class="flex items-center mb-6">
        <button class="mr-2 p-2" @click="$router.back()">
          ←
        </button>
        <h1 class="text-xl font-bold">화재 제보 #{{ id }}</h1>
        <div class="ml-auto">
          <button class="p-2 border border-gray-300 rounded-md">
            <Share2 class="h-4 w-4" />
          </button>
        </div>
      </div>

      <div class="bg-white rounded-lg shadow overflow-hidden mb-4">
        <div class="p-3 pb-0">
          <div class="flex items-center">
            <div class="h-8 w-8 rounded-full bg-gray-200 overflow-hidden mr-2">
              <img :src="reportData.userAvatar" :alt="reportData.username" class="h-full w-full object-cover" />
            </div>
            <div>
              <div class="flex items-center">
                <span class="font-medium text-sm">{{ reportData.username }}</span>
                <span v-if="reportData.verified" class="ml-2 px-2 py-0.5 text-xs bg-blue-100 text-blue-800 border border-blue-200 rounded-full flex items-center">
                  <Shield class="h-3 w-3 mr-1" />
                  확인됨
                </span>
              </div>
              <div class="flex items-center text-xs text-gray-500">
                <MapPin class="h-3 w-3 mr-1" />
                {{ reportData.location }} · {{ reportData.distance }} 거리
              </div>
            </div>
          </div>
        </div>
        <div class="p-3">
          <div class="relative h-64 w-full mb-3">
            <img
              :src="reportData.imageUrl"
              :alt="`화재: ${reportData.location}`"
              class="h-full w-full object-cover rounded-md"
            />
            <div class="absolute top-2 right-2">
              <span class="px-2 py-0.5 rounded-full text-xs flex items-center bg-red-100 text-red-800 border border-red-200">
                <AlertTriangle class="h-3 w-3 mr-1" />
                위험도: {{ reportData.riskLevel }}
              </span>
            </div>
          </div>
          <p class="mb-2">{{ reportData.description }}</p>
          <div class="flex items-center text-xs text-gray-400">
            <Clock class="h-3 w-3 mr-1" />
            {{ reportData.time }}
          </div>
        </div>
        <div class="border-t border-gray-200"></div>
        <div class="p-0">
          <div class="w-full">
            <div class="flex justify-around p-1">
              <button 
                class="flex-1 flex items-center justify-center py-2"
              >
                <Heart class="h-4 w-4 mr-1" />
                <span class="text-sm">{{ reportData.likes }}</span>
              </button>
              <button 
                class="flex-1 flex items-center justify-center py-2"
              >
                <MessageSquare class="h-4 w-4 mr-1" />
                <span class="text-sm">{{ reportData.comments }}</span>
              </button>
              <button class="flex-1 flex items-center justify-center py-2">
                <Share2 class="h-4 w-4 mr-1" />
                <span class="text-sm">공유</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="mb-4">
        <div class="flex border-b border-gray-200">
          <button 
            @click="activeTab = 'comments'" 
            :class="[
              'py-2 px-4 font-medium text-sm flex-1 text-center',
              activeTab === 'comments' 
                ? 'border-b-2 border-red-500 text-red-600' 
                : 'text-gray-500 hover:text-gray-700'
            ]"
          >
            댓글
          </button>
          <button 
            @click="activeTab = 'analysis'" 
            :class="[
              'py-2 px-4 font-medium text-sm flex-1 text-center',
              activeTab === 'analysis' 
                ? 'border-b-2 border-red-500 text-red-600' 
                : 'text-gray-500 hover:text-gray-700'
            ]"
          >
            AI 분석
          </button>
          <button 
            @click="activeTab = 'emergency'" 
            :class="[
              'py-2 px-4 font-medium text-sm flex-1 text-center',
              activeTab === 'emergency' 
                ? 'border-b-2 border-red-500 text-red-600' 
                : 'text-gray-500 hover:text-gray-700'
            ]"
          >
            긴급 대응
          </button>
        </div>

        <!-- 댓글 탭 -->
        <div v-if="activeTab === 'comments'" class="p-0">
          <div class="bg-white rounded-lg shadow p-4 mt-4">
            <comment-section :post-id="id" />
          </div>
        </div>

        <!-- AI 분석 탭 -->
        <div v-if="activeTab === 'analysis'">
          <div class="bg-white rounded-lg shadow overflow-hidden mt-4">
            <div class="p-4 border-b border-gray-200">
              <div class="flex items-center">
                <Shield class="h-5 w-5 mr-2 text-blue-600" />
                <h2 class="text-base font-semibold">AI 분석</h2>
              </div>
              <p class="text-sm text-gray-500">
                자동 위험 평가 및 대응 권장사항
              </p>
            </div>
            <div class="p-4 space-y-4">
              <div>
                <h3 class="text-sm font-medium mb-2">화재 유형</h3>
                <span class="px-2 py-1 rounded-full text-xs border border-gray-300">
                  {{ reportData.analysis.type }}
                </span>
              </div>

              <div>
                <h3 class="text-sm font-medium mb-2">주변 위험 요소</h3>
                <div class="space-y-2">
                  <div 
                    v-for="(hazard, index) in reportData.analysis.hazards" 
                    :key="index" 
                    class="flex items-center justify-between p-2 bg-gray-50 rounded-md"
                  >
                    <div class="flex items-center">
                      <AlertTriangle 
                        class="h-4 w-4 mr-2" 
                        :class="{
                          'text-red-500': hazard.risk === '높음',
                          'text-orange-500': hazard.risk === '중간',
                          'text-yellow-500': hazard.risk === '낮음'
                        }" 
                      />
                      <span>{{ hazard.name }}</span>
                    </div>
                    <div class="flex items-center">
                      <span class="text-sm text-gray-500 mr-2">{{ hazard.distance }}</span>
                      <span 
                        class="px-2 py-0.5 rounded-full text-xs"
                        :class="{
                          'bg-red-100 text-red-800': hazard.risk === '높음',
                          'bg-orange-100 text-orange-800': hazard.risk === '중간',
                          'bg-yellow-100 text-yellow-800': hazard.risk === '낮음'
                        }"
                      >
                        {{ hazard.risk }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>

              <div>
                <h3 class="text-sm font-medium mb-2">AI 평가</h3>
                <p class="text-sm bg-blue-50 p-3 rounded-md border border-blue-100">
                  {{ reportData.analysis.aiAssessment }}
                </p>
              </div>
            </div>
          </div>
        </div>

        <!-- 긴급 대응 탭 -->
        <div v-if="activeTab === 'emergency'">
          <div class="bg-red-50 border border-red-200 rounded-lg shadow overflow-hidden mt-4">
            <div class="p-4 border-b border-red-200">
              <h2 class="text-base font-semibold text-red-800">긴급 대응</h2>
              <p class="text-sm text-red-600">
                소방서에 신고가 접수되었습니다
              </p>
            </div>
            <div class="p-4">
              <div class="space-y-2">
                <div class="flex items-center justify-between">
                  <span class="text-sm">상태:</span>
                  <span class="px-2 py-0.5 rounded-full text-xs bg-yellow-100 text-yellow-800">
                    출동 중
                  </span>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-sm">도착 예정:</span>
                  <span class="font-medium">3분 후</span>
                </div>
              </div>
            </div>
            <div class="p-4 border-t border-red-200">
              <button class="w-full border border-red-300 text-red-800 rounded-md py-2 hover:bg-red-100">
                119 긴급 신고
              </button>
            </div>
          </div>

          <div class="bg-white rounded-lg shadow overflow-hidden mt-4">
            <div class="p-4 border-b border-gray-200">
              <h2 class="text-base font-semibold">권장 자원</h2>
              <p class="text-sm text-gray-500">
                화재 AI 분석 기반
              </p>
            </div>
            <div class="p-4">
              <ul class="space-y-2">
                <li v-for="(resource, index) in reportData.analysis.recommendedResources" :key="index" class="flex items-center">
                  <ArrowUpRight class="h-4 w-4 mr-2 text-blue-500" />
                  {{ resource }}
                </li>
              </ul>
            </div>
          </div>

          <div class="bg-white rounded-lg shadow overflow-hidden mt-4">
            <div class="p-4 border-b border-gray-200">
              <h2 class="text-base font-semibold">대피 구역</h2>
            </div>
            <div class="p-4">
              <div class="relative h-48 w-full bg-gray-100 rounded-md flex items-center justify-center">
                <MapPin class="h-8 w-8 text-red-500" />
                <div class="absolute inset-0 rounded-md border-2 border-red-400 opacity-50"></div>
                <div class="absolute inset-0 rounded-md border-2 border-red-300 opacity-30" style="margin: 20px"></div>
                <div class="absolute inset-0 rounded-md border-2 border-red-200 opacity-20" style="margin: 40px"></div>
              </div>
              <div class="mt-2 text-sm text-center">
                권장 대피 반경: 200m
              </div>
            </div>
            <div class="p-4 border-t border-gray-200">
              <button class="w-full border border-gray-300 rounded-md py-2">
                상세 지도 보기
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { AlertTriangle, ArrowUpRight, Clock, Heart, MapPin, MessageSquare, Phone, Share2, Shield } from 'lucide-vue-next';
import CommentSection from '../components/CommentSection.vue';

const props = defineProps({
  id: {
    type: String,
    required: true
  }
});

const activeTab = ref('comments');

// 실제 앱에서는 ID를 기반으로 데이터를 가져옵니다
const reportData = ref({
  id: props.id,
  username: "소방지킴이",
  userAvatar: "/placeholder.svg?height=40&width=40",
  location: "강남역, 서울",
  time: "5분 전",
  description: "상업 건물 3층에서 화재 발생. 연기가 심하게 나고 있습니다. 여러 창문에서 연기가 보이며, 외부에서는 화염이 보이지 않습니다.",
  riskLevel: "높음",
  imageUrl: "/placeholder.svg?height=300&width=600",
  distance: "1.2km",
  likes: 128,
  comments: 24,
  verified: true,
  analysis: {
    type: "상업 건물 화재",
    hazards: [
      { name: "주유소", distance: "150m", risk: "높음" },
      { name: "주거 건물", distance: "50m", risk: "중간" },
    ],
    aiAssessment: "화재는 상업 건물 3층에 국한된 것으로 보입니다. 인근 주유소가 화재 확산의 위험을 높이고 있습니다. 200m 반경 내 주변 건물의 즉각적인 대피를 권장합니다.",
    recommendedResources: [
      "소방차 3대",
      "사다리차 1대",
      "구급차 2대",
      "위험물 처리팀"
    ]
  }
});
</script>