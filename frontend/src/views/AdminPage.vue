<template>
  <div class="min-h-screen bg-gray-100">
    <!-- 긴급 헤더 -->
    <div class="bg-red-600 text-white py-2 px-4">
      <div class="container mx-auto flex items-center justify-between max-w-4xl">
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
    
    <!-- 상단 네비게이션 바 추가 -->
    <div class="bg-white border-b border-gray-200 py-2 shadow-sm">
      <div class="container mx-auto max-w-4xl px-4">
        <div class="flex justify-between items-center">
          <router-link to="/" class="flex items-center">
            <Flame class="h-6 w-6 text-primary-600 mr-2" />
            <span class="font-bold text-xl text-gray-900">화재알리미</span>
          </router-link>
          
          <div class="flex items-center space-x-4">
            <router-link to="/report" class="flex items-center text-gray-700 hover:text-primary-600">
              <Camera class="h-5 w-5 mr-1" />
              <span class="text-sm font-medium">제보하기</span>
            </router-link>
            
            <router-link to="/notifications" class="flex items-center text-gray-700 hover:text-primary-600 relative">
              <Bell class="h-5 w-5 mr-1" />
              <span class="text-sm font-medium">알림</span>
              <span class="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-4 w-4 flex items-center justify-center">
                3
              </span>
            </router-link>
            
            <div class="h-8 w-8 rounded-full bg-gray-200 overflow-hidden">
              <img src="https://placehold.co/32x32" alt="프로필" class="h-full w-full object-cover" />
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="container mx-auto px-4 py-6 max-w-4xl">
      <div class="flex justify-between items-center mb-6">
        <div class="flex items-center">
          <Shield class="h-6 w-6 text-blue-600 mr-2" />
          <h1 class="text-2xl font-bold">소방서 관리자 포털</h1>
        </div>
        <div class="flex gap-2">
          <button class="p-2 border border-gray-300 rounded-md">
            <Bell class="h-5 w-5" />
          </button>
          <button class="px-3 py-2 border border-gray-300 rounded-md flex items-center">
            <FileText class="h-4 w-4 mr-2" /> 보고서
          </button>
        </div>
      </div>
  
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <div class="bg-white rounded-lg shadow p-4">
          <h2 class="text-2xl font-bold text-red-600">{{ activeReports.length }}</h2>
          <p class="text-sm text-gray-500">활성 화재</p>
        </div>
        <div class="bg-white rounded-lg shadow p-4">
          <h2 class="text-2xl font-bold text-yellow-600">5</h2>
          <p class="text-sm text-gray-500">배치된 자원</p>
        </div>
        <div class="bg-white rounded-lg shadow p-4">
          <h2 class="text-2xl font-bold text-green-600">12</h2>
          <p class="text-sm text-gray-500">가용 자원</p>
        </div>
        <div class="bg-white rounded-lg shadow p-4">
          <h2 class="text-2xl font-bold text-blue-600">{{ closedReports.length }}</h2>
          <p class="text-sm text-gray-500">종결된 이벤트</p>
        </div>
      </div>
  
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div class="lg:col-span-2">
          <div class="mb-4">
            <div class="flex border-b border-gray-200">
              <button 
                @click="activeTab = 'active'" 
                :class="[
                  'py-2 px-4 font-medium text-sm flex-1 text-center',
                  activeTab === 'active' 
                    ? 'border-b-2 border-red-500 text-red-600' 
                    : 'text-gray-500 hover:text-gray-700'
                ]"
              >
                확인된 화재
              </button>
              <button 
                @click="activeTab = 'pending'" 
                :class="[
                  'py-2 px-4 font-medium text-sm flex-1 text-center',
                  activeTab === 'pending' 
                    ? 'border-b-2 border-red-500 text-red-600' 
                    : 'text-gray-500 hover:text-gray-700'
                ]"
              >
                검토 필요 제보
              </button>
              <button 
                @click="activeTab = 'closed'" 
                :class="[
                  'py-2 px-4 font-medium text-sm flex-1 text-center',
                  activeTab === 'closed' 
                    ? 'border-b-2 border-red-500 text-red-600' 
                    : 'text-gray-500 hover:text-gray-700'
                ]"
              >
                종결된 이벤트
              </button>
            </div>
          </div>
          
          <!-- 확인된 화재 탭 -->
          <div v-if="activeTab === 'active'">
            <div class="bg-white rounded-lg shadow overflow-hidden mb-6">
              <div class="p-4 border-b border-gray-200">
                <h2 class="font-semibold">활성 화재 제보</h2>
                <p class="text-sm text-gray-500">
                  실시간 대응이 필요한 화재 제보
                </p>
              </div>
              <div class="p-4">
                <div v-if="activeReports.length === 0" class="text-center py-8 text-gray-500">
                  <XCircle class="h-12 w-12 mx-auto mb-2 text-gray-400" />
                  <p>현재 활성화된 화재 이벤트가 없습니다.</p>
                </div>
                <div v-else class="space-y-4">
                  <div v-for="report in activeReports" :key="report.id" class="flex flex-col p-3 bg-white rounded-lg border">
                    <div class="flex items-center justify-between">
                      <div class="flex items-center">
                        <div class="h-10 w-10 rounded-full bg-gray-200 overflow-hidden mr-3">
                          <img :src="report.userAvatar" :alt="report.username" class="h-full w-full object-cover" />
                        </div>
                        <div>
                          <div class="flex items-center">
                            <span 
                              class="mr-2 px-2 py-0.5 rounded-full text-xs"
                              :class="{
                                'bg-red-100 text-red-800': report.riskLevel === '심각',
                                'bg-orange-100 text-orange-800': report.riskLevel === '높음',
                                'bg-yellow-100 text-yellow-800': report.riskLevel === '중간'
                              }"
                            >
                              {{ report.riskLevel }}
                            </span>
                            <span class="font-medium">화재 #{{ report.id }}</span>
                            <!-- 소방관 확인 표시 -->
                            <span 
                              v-if="report.confirmedByFireDept" 
                              class="ml-2 px-2 py-0.5 rounded-full text-xs bg-green-100 text-green-800 border border-green-200 flex items-center"
                            >
                              <CheckCircle class="h-3 w-3 mr-1" />
                              소방관 확인
                            </span>
                          </div>
                          <div class="flex items-center text-sm text-gray-500 mt-1">
                            <MapPin class="h-3 w-3 mr-1" />
                            {{ report.location }}
                          </div>
                          <div class="flex items-center text-xs text-gray-400 mt-1">
                            <Clock class="h-3 w-3 mr-1" />
                            {{ report.time }}
                          </div>
                        </div>
                      </div>
                      <div class="flex items-center">
                        <span 
                          class="mr-2 px-2 py-0.5 rounded-full text-xs border"
                          :class="{
                            'border-blue-300 bg-blue-50 text-blue-800': report.status === '진행 중',
                            'border-gray-300 bg-gray-50 text-gray-800': report.status === '종결'
                          }"
                        >
                          {{ report.status }}
                        </span>
                        <router-link :to="`/admin/report/${report.id}`">
                          <button class="px-3 py-1 bg-blue-500 text-white rounded-md text-sm">보기</button>
                        </router-link>
                      </div>
                    </div>
                    
                    <!-- 비디오 링크 추가 -->
                    <div v-if="report.videoUrl" class="mt-2 pt-2 border-t border-gray-100">
                      <router-link :to="`/admin/report/${report.id}`" class="flex items-center text-blue-600 hover:text-blue-800">
                        <Video class="h-4 w-4 mr-1" />
                        <span class="text-sm">화재 영상 보기</span>
                      </router-link>
                    </div>
                    
                    <!-- 상태 관리 컨트롤 추가 -->
                    <div class="mt-3 pt-3 border-t border-gray-100 grid grid-cols-3 gap-3">
                      <div>
                        <label class="block text-xs text-gray-500 mb-1">화재 상태 변경</label>
                        <select 
                          v-model="report.status" 
                          class="w-full text-sm border border-gray-300 rounded-md p-1.5"
                          @change="handleStatusChange(report)"
                        >
                          <option value="진행 중">진행 중</option>
                          <option value="종결">종결</option>
                        </select>
                      </div>
                      <div>
                        <label class="block text-xs text-gray-500 mb-1">소방관 확인</label>
                        <button 
                          @click="toggleFirefighterConfirmation(report)" 
                          class="w-full py-1.5 border rounded-md text-sm transition-colors flex items-center justify-center"
                          :class="report.confirmedByFireDept 
                            ? 'bg-green-100 text-green-700 border-green-300 hover:bg-green-200' 
                            : 'bg-blue-100 text-blue-700 border-blue-300 hover:bg-blue-200'"
                        >
                          <CheckCircle class="h-4 w-4 mr-1" />
                          {{ report.confirmedByFireDept ? '확인됨' : '확인하기' }}
                        </button>
                      </div>
                      <div>
                        <label class="block text-xs text-gray-500 mb-1">이벤트 관리</label>
                        <button 
                          @click="deleteFireEvent(report)" 
                          class="w-full py-1.5 bg-red-100 text-red-700 border border-red-300 rounded-md text-sm hover:bg-red-200 transition-colors flex items-center justify-center"
                        >
                          <Trash2 class="h-4 w-4 mr-1" />
                          화재 아님 (삭제)
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="p-4 border-t border-gray-200">
                <router-link to="/admin/all-fire-reports">
                  <button class="w-full border border-gray-300 rounded-md py-2 text-sm">
                    모든 제보 보기
                  </button>
                </router-link>
              </div>
            </div>
          </div>
          
          <!-- 검토 필요 제보 탭 -->
          <div v-if="activeTab === 'pending'">
            <div class="bg-white rounded-lg shadow overflow-hidden mb-6">
              <div class="p-4 border-b border-gray-200">
                <h2 class="font-semibold">검토 필요 제보</h2>
                <p class="text-sm text-gray-500">
                  사용자 위험 표시가 많은 미확인 제보
                </p>
              </div>
              <div class="p-4">
                <div v-if="pendingReports.length === 0" class="text-center py-8 text-gray-500">
                  <CheckCircle class="h-12 w-12 mx-auto mb-2 text-gray-400" />
                  <p>검토가 필요한 제보가 없습니다.</p>
                </div>
                <div v-else class="space-y-4">
                  <div v-for="report in pendingReports" :key="report.id" class="flex flex-col p-3 bg-white rounded-lg border">
                    <div class="flex items-center justify-between">
                      <div class="flex items-center">
                        <div class="h-10 w-10 rounded-full bg-gray-200 overflow-hidden mr-3">
                          <img :src="report.userAvatar" :alt="report.username" class="h-full w-full object-cover" />
                        </div>
                        <div>
                          <div class="flex items-center">
                            <span class="font-medium">제보 #{{ report.id }}</span>
                            <span class="ml-2 px-2 py-0.5 rounded-full text-xs bg-yellow-100 text-yellow-800 border border-yellow-200 flex items-center">
                              <AlertTriangle class="h-3 w-3 mr-1" />
                              미확인
                            </span>
                          </div>
                          <div class="flex items-center text-sm text-gray-500 mt-1">
                            <MapPin class="h-3 w-3 mr-1" />
                            {{ report.location }}
                          </div>
                          <div class="flex items-center text-xs text-gray-400 mt-1">
                            <Clock class="h-3 w-3 mr-1" />
                            {{ report.time }}
                          </div>
                        </div>
                      </div>
                      <div class="flex items-center">
                        <div class="flex items-center mr-3">
                          <AlertTriangle class="h-4 w-4 text-red-500 mr-1" />
                          <span class="text-sm font-medium">{{ report.dangers }} 위험해요</span>
                        </div>
                        <router-link :to="`/admin/report/${report.id}`">
                          <button class="px-3 py-1 bg-blue-500 text-white rounded-md text-sm">보기</button>
                        </router-link>
                      </div>
                    </div>
                    
                    <!-- 제보 검증 컨트롤 추가 -->
                    <div class="mt-3 pt-3 border-t border-gray-100">
                      <div class="flex flex-col">
                        <label class="block text-xs text-gray-500 mb-1">제보 검증</label>
                        <div class="flex gap-2">
                          <button 
                            @click="confirmAsFireReport(report)" 
                            class="flex-1 px-3 py-2 bg-red-600 text-white rounded-md text-sm hover:bg-red-700 transition-colors"
                          >
                            화재 확인
                          </button>
                          <button 
                            @click="markAsFalseReport(report)" 
                            class="flex-1 px-3 py-2 bg-gray-600 text-white rounded-md text-sm hover:bg-gray-700 transition-colors"
                          >
                            오보 처리
                          </button>
                          <button 
                            @click="requestMoreInfo(report)" 
                            class="flex-1 px-3 py-2 bg-blue-600 text-white rounded-md text-sm hover:bg-blue-700 transition-colors"
                          >
                            추가 정보 요청
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 종결된 이벤트 탭 -->
          <div v-if="activeTab === 'closed'">
            <div class="bg-white rounded-lg shadow overflow-hidden mb-6">
              <div class="p-4 border-b border-gray-200">
                <h2 class="font-semibold">종결된 이벤트</h2>
                <p class="text-sm text-gray-500">
                  종결 처리된 화재 및 오보 이벤트
                </p>
              </div>
              <div class="p-4">
                <div v-if="closedReports.length === 0" class="text-center py-8 text-gray-500">
                  <Archive class="h-12 w-12 mx-auto mb-2 text-gray-400" />
                  <p>종결된 이벤트가 없습니다.</p>
                </div>
                <div v-else class="space-y-4">
                  <div v-for="report in closedReports" :key="report.id" class="flex flex-col p-3 bg-white rounded-lg border">
                    <div class="flex items-center justify-between">
                      <div class="flex items-center">
                        <div class="h-10 w-10 rounded-full bg-gray-200 overflow-hidden mr-3">
                          <img :src="report.userAvatar" :alt="report.username" class="h-full w-full object-cover" />
                        </div>
                        <div>
                          <div class="flex items-center">
                            <span 
                              class="mr-2 px-2 py-0.5 rounded-full text-xs"
                              :class="{
                                'bg-red-100 text-red-800': report.riskLevel === '심각',
                                'bg-orange-100 text-orange-800': report.riskLevel === '높음',
                                'bg-yellow-100 text-yellow-800': report.riskLevel === '중간',
                                'bg-gray-100 text-gray-800': !report.riskLevel
                              }"
                            >
                              {{ report.riskLevel || '분류 없음' }}
                            </span>
                            <span class="font-medium">{{ report.isDeleted ? '오보' : '화재' }} #{{ report.id }}</span>
                            <!-- 소방관 확인 표시 -->
                            <span 
                              v-if="report.confirmedByFireDept" 
                              class="ml-2 px-2 py-0.5 rounded-full text-xs bg-green-100 text-green-800 border border-green-200 flex items-center"
                            >
                              <CheckCircle class="h-3 w-3 mr-1" />
                              소방관 확인
                            </span>
                          </div>
                          <div class="flex items-center text-sm text-gray-500 mt-1">
                            <MapPin class="h-3 w-3 mr-1" />
                            {{ report.location }}
                          </div>
                          <div class="flex items-center text-xs text-gray-400 mt-1">
                            <Clock class="h-3 w-3 mr-1" />
                            {{ report.closedTime || report.time }}
                          </div>
                        </div>
                      </div>
                      <div class="flex items-center">
                        <span 
                          class="mr-2 px-2 py-0.5 rounded-full text-xs border"
                          :class="{
                            'border-gray-300 bg-gray-50 text-gray-800': report.status === '종결',
                            'border-red-300 bg-red-50 text-red-800': report.isDeleted
                          }"
                        >
                          {{ report.isDeleted ? '오보 처리됨' : '종결' }}
                        </span>
                        <router-link :to="`/admin/report/${report.id}`">
                          <button class="px-3 py-1 bg-blue-500 text-white rounded-md text-sm">보기</button>
                        </router-link>
                      </div>
                    </div>
                    
                    <!-- 종결 사유 표시 -->
                    <div class="mt-3 pt-3 border-t border-gray-100">
                      <div class="flex flex-col">
                        <div class="flex items-start">
                          <InfoIcon class="h-4 w-4 text-gray-500 mr-2 mt-0.5" />
                          <div>
                            <span class="text-xs text-gray-500">종결 사유:</span>
                            <p class="text-sm text-gray-700">{{ report.closeReason || '사유가 입력되지 않았습니다.' }}</p>
                          </div>
                        </div>
                        <div class="flex justify-end mt-2">
                          <button 
                            @click="restoreReport(report)" 
                            class="px-3 py-1 bg-gray-100 text-gray-700 border border-gray-300 rounded-md text-xs hover:bg-gray-200 transition-colors flex items-center"
                          >
                            <RefreshCw class="h-3 w-3 mr-1" />
                            재개
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="p-4 border-t border-gray-200">
                <router-link to="/admin/all-closed-events">
                  <button class="w-full border border-gray-300 rounded-md py-2 text-sm">
                    종결 이벤트 모두보기
                  </button>
                </router-link>
              </div>
            </div>
          </div>
  
          <div class="bg-white rounded-lg shadow overflow-hidden">
            <div class="p-4 border-b border-gray-200">
              <h2 class="font-semibold">자원 배치</h2>
              <p class="text-sm text-gray-500">
                현재 긴급 자원 배치 현황
              </p>
            </div>
            <div class="p-4">
              <div class="space-y-4">
                <div>
                  <div class="flex justify-between mb-1">
                    <span class="text-sm font-medium">소방차</span>
                    <span class="text-sm text-gray-500">5/10 배치됨</span>
                  </div>
                  <div class="w-full bg-gray-200 rounded-full h-2.5">
                    <div class="bg-blue-600 h-2.5 rounded-full" style="width: 50%"></div>
                  </div>
                </div>
                <div>
                  <div class="flex justify-between mb-1">
                    <span class="text-sm font-medium">사다리차</span>
                    <span class="text-sm text-gray-500">2/4 배치됨</span>
                  </div>
                  <div class="w-full bg-gray-200 rounded-full h-2.5">
                    <div class="bg-blue-600 h-2.5 rounded-full" style="width: 50%"></div>
                  </div>
                </div>
                <div>
                  <div class="flex justify-between mb-1">
                    <span class="text-sm font-medium">구급차</span>
                    <span class="text-sm text-gray-500">3/8 배치됨</span>
                  </div>
                  <div class="w-full bg-gray-200 rounded-full h-2.5">
                    <div class="bg-blue-600 h-2.5 rounded-full" style="width: 37.5%"></div>
                  </div>
                </div>
                <div>
                  <div class="flex justify-between mb-1">
                    <span class="text-sm font-medium">위험물 처리팀</span>
                    <span class="text-sm text-gray-500">1/2 배치됨</span>
                  </div>
                  <div class="w-full bg-gray-200 rounded-full h-2.5">
                    <div class="bg-blue-600 h-2.5 rounded-full" style="width: 50%"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
  
        <div>
          <!-- 위험 분석 지도 컴포넌트 -->
    <div class="bg-white rounded-lg shadow overflow-hidden mb-6">
      <div class="p-4 border-b border-gray-200">
        <h2 class="text-lg font-medium text-gray-900">위험 분석 지도</h2>
        <p class="text-sm text-gray-500">
          화재 위험의 지리적 분포
        </p>
      </div>
      <RiskAnalysisMap 
        :fire-reports="activeReports"
        :pending-reports="pendingReports"
        :closed-reports="closedReports"
        map-id="sidebar-map"
      />
      <div class="p-4 border-t border-gray-200">
        <button 
          @click="navigateToFullRiskMap" 
          class="w-full border border-gray-300 rounded-md py-2 text-sm hover:bg-gray-50 transition-colors"
        >
          전체 위험 분석 지도 보기
        </button>
      </div>
    </div>
  
          <div class="bg-white rounded-lg shadow overflow-hidden">
            <div class="p-4 border-b border-gray-200">
              <div class="flex items-center">
                <BarChart3 class="h-5 w-5 mr-2 text-blue-600" />
                <h2 class="font-semibold">AI 인사이트</h2>
              </div>
              <p class="text-sm text-gray-500">
                자동 분석 및 권장사항
              </p>
            </div>
            <div class="p-4 space-y-4">
              <div class="p-3 bg-yellow-50 border border-yellow-200 rounded-md">
                <div class="flex items-center mb-2">
                  <AlertTriangle class="h-4 w-4 text-yellow-600 mr-2" />
                  <h3 class="font-medium text-yellow-800">자원 알림</h3>
                </div>
                <p class="text-sm text-yellow-700">
                  현재 화재 패턴에 따라 더 빠른 대응 시간을 위해 강남 지역에 소방차 2대를 재배치하는 것이 좋습니다.
                </p>
              </div>
  
              <div class="p-3 bg-blue-50 border border-blue-200 rounded-md">
                <h3 class="font-medium text-blue-800 mb-2">화재 위험 예측</h3>
                <p class="text-sm text-blue-700">
                  AI 분석에 따르면 현재 날씨 조건으로 인해 산업 지역의 화재 위험이 증가했습니다. 예방 점검을 권장합니다.
                </p>
              </div>
            </div>
            <div class="p-4 border-t border-gray-200">
              <button class="w-full border border-gray-300 rounded-md py-2 text-sm">
                상세 분석 보기
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 하단 네비게이션 바 -->
    <div class="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 py-2 shadow-lg">
      <div class="container mx-auto max-w-lg">
        <div class="flex justify-around items-center">
          <router-link to="/" class="flex flex-col items-center text-gray-500">
            <Home class="h-6 w-6" />
            <span class="text-xs mt-1">홈</span>
          </router-link>
          <router-link to="/map" class="flex flex-col items-center text-gray-500">
            <Map class="h-6 w-6" />
            <span class="text-xs mt-1">지도</span>
          </router-link>
          <router-link to="/report">
            <button class="rounded-full bg-gradient-to-r from-primary-600 to-primary-500 h-14 w-14 flex items-center justify-center text-white shadow-lg transform hover:scale-105 transition-transform duration-200">
              <Camera class="h-7 w-7" />
            </button>
          </router-link>
          <router-link to="/notifications" class="flex flex-col items-center text-gray-500">
            <Bell class="h-6 w-6" />
            <span class="text-xs mt-1">알림</span>
          </router-link>
          <router-link to="/profile" class="flex flex-col items-center text-gray-500">
            <User class="h-6 w-6" />
            <span class="text-xs mt-1">내정보</span>
          </router-link>
        </div>
      </div>
    </div>
    
    <!-- 상태 변경 모달 -->
    <div v-if="showStatusModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg shadow-lg p-6 w-full max-w-md mx-4">
        <h3 class="text-lg font-bold mb-4">{{ statusModalTitle }}</h3>
        
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">변경 사유</label>
          <textarea 
            v-model="statusChangeReason" 
            class="w-full border border-gray-300 rounded-md p-2 text-sm"
            rows="3"
            placeholder="상태 변경 사유를 입력하세요..."
          ></textarea>
        </div>
        
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">알림 메시지 (선택사항)</label>
          <input 
            v-model="statusChangeNotification" 
            class="w-full border border-gray-300 rounded-md p-2 text-sm"
            placeholder="사용자에게 전송할 알림 메시지"
          />
        </div>
        
        <div class="flex justify-end space-x-3">
          <button 
            @click="cancelStatusChange" 
            class="px-4 py-2 border border-gray-300 rounded-md text-sm"
          >
            취소
          </button>
          <button 
            @click="confirmStatusChange" 
            class="px-4 py-2 bg-blue-600 text-white rounded-md text-sm hover:bg-blue-700"
          >
            확인
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { 
  AlertTriangle, 
  Archive,
  BarChart3, 
  Bell, 
  Camera,
  CheckCircle,
  Clock, 
  FileText, 
  Flame,
  Home,
  InfoIcon,
  Map,
  MapPin, 
  Phone, 
  RefreshCw,
  Search,
  Shield,
  Trash2,
  User,
  Video,
  XCircle
} from 'lucide-vue-next';
import RiskAnalysisMap from './admin/RiskAnalysisMap.vue';

export default {
  name: 'AdminPage',
  components: {
    AlertTriangle, 
    Archive,
    BarChart3, 
    Bell, 
    Camera,
    CheckCircle,
    Clock, 
    FileText, 
    Flame,
    Home,
    InfoIcon,
    Map,
    MapPin, 
    Phone, 
    RefreshCw,
    Search,
    Shield,
    Trash2,
    User,
    Video,
    XCircle,
    RiskAnalysisMap
  },
  setup() {
    const activeTab = ref('active');
    const showFullRiskMap = ref(false);
  
    // 확인된 화재 데이터
    const activeReports = ref([
      {
        id: "1",
        username: "소방지킴이",
        userAvatar: "https://placehold.co/40x40",
        location: "강남역, 서울",
        time: "5분 전",
        riskLevel: "높음",
        status: "진행 중",
        verified: true,
        confirmedByFireDept: true,
        confirmedAt: new Date().toISOString(),
        videoUrl: "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4" // 샘플 비디오 URL 추가
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
        confirmedByFireDept: false,
        videoUrl: null // 비디오 없음
      }
    ]);
  
    // 검토 필요 제보 데이터
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
  
    // 종결된 이벤트 데이터
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
        confirmedByFireDept: true, // 소방관 확인 여부
        confirmedAt: new Date(Date.now() - 3600000).toISOString(), // 소방관 확인 시간 (1시간 전)
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
      },
      {
        id: "7",
        username: "동네지킴이",
        userAvatar: "https://placehold.co/40x40",
        location: "송파구 잠실동, 서울",
        time: "어제",
        closedTime: "어제",
        riskLevel: "높음",
        status: "종결",
        verified: true,
        confirmedByFireDept: false, // 소방관 확인 여부
        closeReason: "화재 진화 완료. 인명 피해 없음. 재산 피해 조사 중."
      }
    ]);
  
    // 상태 변경 모달 관련 상태
    const showStatusModal = ref(false);
    const statusModalTitle = ref('');
    const statusChangeReason = ref('');
    const statusChangeNotification = ref('');
    const currentReport = ref(null);
    const changeType = ref(''); // 'status', 'delete', 'confirm', 'false', 'moreInfo', 'restore', 'firefighter'
  
    // setup() 함수 내에 추가
    const router = useRouter();
  
    const navigateToFullRiskMap = () => {
      router.push('/admin/full-risk-map');
    };
  
    // 화재 상태 변경 처리
    const handleStatusChange = (report) => {
      currentReport.value = report;
      changeType.value = 'status';
      statusModalTitle.value = '화재 상태 변경';
      showStatusModal.value = true;
      
      // 상태에 따른 기본 알림 메시지 설정
      if (report.status === '종결') {
        statusChangeNotification.value = `화재 #${report.id}가 종결 처리되었습니다.`;
      } else {
        statusChangeNotification.value = `화재 #${report.id}의 상태가 "${report.status}"(으)로 변경되었습니다.`;
      }
    };
  
    // 소방관 확인 토글
    const toggleFirefighterConfirmation = (report) => {
      if (report.confirmedByFireDept) {
        // 이미 확인된 경우, 확인 취소 모달 표시
        currentReport.value = report;
        changeType.value = 'firefighter';
        statusModalTitle.value = '소방관 확인 취소';
        statusChangeReason.value = '';
        statusChangeNotification.value = `화재 #${report.id}의 소방관 확인이 취소되었습니다.`;
        showStatusModal.value = true;
      } else {
        // 확인되지 않은 경우, 확인 처리
        report.confirmedByFireDept = true;
        report.confirmedAt = new Date().toISOString();
        
        // 알림 메시지 표시 (실제로는 서버에 요청)
        console.log(`소방관 확인: 화재 #${report.id}가 소방관에 의해 확인되었습니다.`);
      }
    };
  
    // 화재 이벤트 삭제 처리
    const deleteFireEvent = (report) => {
      currentReport.value = report;
      changeType.value = 'delete';
      statusModalTitle.value = '화재 이벤트 삭제';
      showStatusModal.value = true;
      statusChangeNotification.value = `화재 #${report.id}가 삭제되었습니다.`;
    };
  
    // 제보를 화재로 확인
    const confirmAsFireReport = (report) => {
      currentReport.value = report;
      changeType.value = 'confirm';
      statusModalTitle.value = '화재 확인';
      showStatusModal.value = true;
      statusChangeNotification.value = `제보 #${report.id}가 화재로 확인되었습니다. 소방대가 출동 중입니다.`;
    };
  
    // 제보를 오보로 처리
    const markAsFalseReport = (report) => {
      currentReport.value = report;
      changeType.value = 'false';
      statusModalTitle.value = '오보 처리';
      showStatusModal.value = true;
      statusChangeNotification.value = `제보 #${report.id}가 오보로 확인되었습니다. 감사합니다.`;
    };
  
    // 추가 정보 요청
    const requestMoreInfo = (report) => {
      currentReport.value = report;
      changeType.value = 'moreInfo';
      statusModalTitle.value = '추가 정보 요청';
      showStatusModal.value = true;
      statusChangeNotification.value = `제보 #${report.id}에 대한 추가 정보가 필요합니다. 자세한 위치나 사진을 제공해주세요.`;
    };
  
    // 종결된 이벤트 재개
    const restoreReport = (report) => {
      currentReport.value = report;
      changeType.value = 'restore';
      statusModalTitle.value = '이벤트 재개';
      showStatusModal.value = true;
      statusChangeNotification.value = `${report.isDeleted ? '오보로 처리된 제보' : '종결된 화재'} #${report.id}가 재개되었습니다.`;
    };
  
    // 상태 변경 취소
    const cancelStatusChange = () => {
      // 상태 변경 전으로 되돌리기
      if (changeType.value === 'status' && currentReport.value) {
        // 이전 상태로 복원 (여기서는 간단히 처리)
        // 실제로는 이전 상태를 저장해두고 복원해야 함
      }
      
      // 모달 닫기
      showStatusModal.value = false;
      statusChangeReason.value = '';
      statusChangeNotification.value = '';
      currentReport.value = null;
    };
  
    // 상태 변경 확인
    const confirmStatusChange = () => {
      if (!currentReport.value) return;
      
      const now = new Date();
      const formattedTime = '방금 전'; // 실제로는 시간 포맷팅 로직 필요
      
      // 상태 변경 타입에 따른 처리
      if (changeType.value === 'confirm') {
        // 제보를 확인된 화재로 변경
        const newFireReport = {
          id: currentReport.value.id,
          username: currentReport.value.username,
          userAvatar: currentReport.value.userAvatar,
          location: currentReport.value.location,
          time: currentReport.value.time,
          riskLevel: "중간", // 기본값
          status: "진행 중",
          verified: true,
          confirmedByFireDept: false // 소방관 확인은 기본적으로 false
        };
        
        // 확인된 화재 목록에 추가
        activeReports.value.push(newFireReport);
        
        // 검토 필요 제보 목록에서 제거
        pendingReports.value = pendingReports.value.filter(report => report.id !== currentReport.value.id);
        
      } else if (changeType.value === 'false') {
        // 오보로 처리하고 제보 목록에서 제거
        const falseReport = {
          ...currentReport.value,
          isDeleted: true,
          closeReason: statusChangeReason.value,
          closedTime: formattedTime
        };
        
        // 종결된 이벤트 목록에 추가
        closedReports.value.unshift(falseReport);
        
        // 검토 필요 제보 목록에서 제거
        pendingReports.value = pendingReports.value.filter(report => report.id !== currentReport.value.id);
        
      } else if (changeType.value === 'moreInfo') {
        // 추가 정보 요청 처리 (실제로는 알림 전송 등의 로직 필요)
        console.log(`추가 정보 요청: ${currentReport.value.id}`);
        
      } else if (changeType.value === 'delete') {
        // 화재 이벤트를 오보로 처리하고 종결된 이벤트로 이동
        const deletedReport = {
          ...currentReport.value,
          isDeleted: true,
          closeReason: statusChangeReason.value,
          closedTime: formattedTime
        };
        
        // 종결된 이벤트 목록에 추가
        closedReports.value.unshift(deletedReport);
        
        // 활성 화재 목록에서 제거
        activeReports.value = activeReports.value.filter(report => report.id !== currentReport.value.id);
        
      } else if (changeType.value === 'status' && currentReport.value.status === '종결') {
        // 화재 상태를 종결로 변경
        const closedReport = {
          ...currentReport.value,
          status: '종결',
          closeReason: statusChangeReason.value,
          closedTime: formattedTime
        };
        
        // 종결된 이벤트 목록에 추가
        closedReports.value.unshift(closedReport);
        
        // 활성 화재 목록에서 제거
        activeReports.value = activeReports.value.filter(report => report.id !== currentReport.value.id);
        
      } else if (changeType.value === 'restore') {
        // 종결된 이벤트를 다시 활성화
        const restoredReport = {
          ...currentReport.value,
          status: '진행 중',
          isDeleted: false
        };
        
        // 활성 화재 목록에 추가
        activeReports.value.push(restoredReport);
        
        // 종결된 이벤트 목록에서 제거
        closedReports.value = closedReports.value.filter(report => report.id !== currentReport.value.id);
        
      } else if (changeType.value === 'firefighter') {
        // 소방관 확인 취소
        currentReport.value.confirmedByFireDept = false;
        currentReport.value.confirmedAt = null;
      }
      
      // 변경 사항 로깅 (실제로는 서버에 저장)
      console.log(`상태 변경: ${changeType.value}, 보고서 ID: ${currentReport.value.id}, 사유: ${statusChangeReason.value}`);
      
      // 알림 전송 (실제로는 서버에 요청)
      if (statusChangeNotification.value) {
        console.log(`알림 전송: ${statusChangeNotification.value}`);
      }
      
      // 모달 닫기
      showStatusModal.value = false;
      statusChangeReason.value = '';
      statusChangeNotification.value = '';
      currentReport.value = null;
    };
  
    // 컴포넌트 마운트 시 초기화
    onMounted(() => {
      console.log('AdminPage 마운트됨');
    });
  
    // return 문에 navigateToFullRiskMap 추가
    return {
      activeTab,
      activeReports,
      pendingReports,
      closedReports,
      showStatusModal,
      statusModalTitle,
      statusChangeReason,
      statusChangeNotification,
      showFullRiskMap,
      handleStatusChange,
      toggleFirefighterConfirmation,
      deleteFireEvent,
      confirmAsFireReport,
      markAsFalseReport,
      requestMoreInfo,
      restoreReport,
      cancelStatusChange,
      confirmStatusChange,
      navigateToFullRiskMap
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

.shadow-soft {
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}
</style>