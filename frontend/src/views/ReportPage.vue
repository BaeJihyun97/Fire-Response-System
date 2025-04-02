<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 헤더 -->
    <div class="bg-white border-b border-gray-200 sticky top-0 z-10">
      <div class="container mx-auto px-4 py-3 max-w-lg">
        <div class="flex items-center justify-between">
          <div class="flex items-center">
            <button @click="$router.go(-1)" class="mr-3">
              <ChevronLeft class="h-6 w-6 text-gray-600" />
            </button>
            <h1 class="text-xl font-bold text-gray-800">화재 제보</h1>
          </div>
        </div>
      </div>
    </div>

    <!-- 메인 컨텐츠 -->
    <div class="container mx-auto px-4 py-4 max-w-lg">
      <!-- 안내 메시지 -->
      <div class="mb-6 bg-primary-50 border border-primary-200 rounded-lg p-4">
        <div class="flex items-start">
          <Info class="h-5 w-5 text-primary-600 mt-0.5 mr-2 flex-shrink-0" />
          <div>
            <p class="text-sm text-primary-800 font-medium">실시간 영상 제보만 가능합니다</p>
            <p class="text-xs text-primary-700 mt-1">
              화재 현장의 정확한 정보 전달을 위해 실시간으로 촬영한 영상만 업로드할 수 있습니다.
              영상은 최대 10초까지 촬영 가능합니다.
            </p>
          </div>
        </div>
      </div>

      <!-- 카메라 뷰 -->
      <div class="mb-6 rounded-lg overflow-hidden bg-black relative">
        <video
          ref="videoElement"
          class="w-full h-64 object-cover"
          autoplay
          playsinline
          muted
        ></video>

        <!-- 녹화 중 표시 및 카운트다운 -->
        <div v-if="isRecording" class="absolute top-4 left-4 flex items-center bg-black bg-opacity-60 rounded-full px-3 py-1">
          <div class="h-3 w-3 rounded-full bg-red-500 mr-2 animate-pulse"></div>
          <span class="text-white text-xs font-medium">녹화 중</span>
          <span class="text-white text-xs ml-2">{{ recordingTime }}</span>
        </div>

        <!-- 카운트다운 원형 프로그레스 -->
        <div v-if="isRecording" class="absolute top-4 right-4">
          <svg class="w-10 h-10" viewBox="0 0 36 36">
            <path
              d="M18 2.0845
                a 15.9155 15.9155 0 0 1 0 31.831
                a 15.9155 15.9155 0 0 1 0 -31.831"
              fill="none"
              stroke="#ffffff"
              stroke-width="1"
              stroke-opacity="0.3"
            />
            <path
              d="M18 2.0845
                a 15.9155 15.9155 0 0 1 0 31.831
                a 15.9155 15.9155 0 0 1 0 -31.831"
              fill="none"
              stroke="#ff4444"
              stroke-width="2"
              :stroke-dasharray="`${progressValue}, 100`"
            />
            <text x="18" y="20.5" text-anchor="middle" fill="white" font-size="10">{{ remainingSeconds }}</text>
          </svg>
        </div>

        <!-- 카메라 전환 버튼 -->
        <button
          @click="switchCamera"
          class="absolute top-4 right-4 bg-black bg-opacity-60 rounded-full p-2"
          v-if="!isRecording && hasMultipleCameras"
        >
          <RefreshCw class="h-5 w-5 text-white" />
        </button>

        <!-- 녹화 컨트롤 -->
        <div class="absolute bottom-4 left-0 right-0 flex justify-center">
          <button
            v-if="!isRecording"
            @click="startRecording"
            class="bg-red-500 h-14 w-14 rounded-full flex items-center justify-center shadow-lg"
          >
            <Circle class="h-6 w-6 text-white" />
          </button>

          <button
            v-else
            @click="stopRecording"
            class="bg-white h-14 w-14 rounded-full flex items-center justify-center shadow-lg"
          >
            <Square class="h-5 w-5 text-red-500" />
          </button>
        </div>
      </div>

      <!-- 녹화된 영상 미리보기 -->
      <div v-if="recordedVideoUrl" class="mb-6">
        <h2 class="text-lg font-medium mb-2">녹화된 영상</h2>
        <video
          ref="recordedVideo"
          class="w-full rounded-lg"
          controls
          :src="recordedVideoUrl"
        ></video>

        <div class="flex justify-between mt-3">
          <button
            @click="discardRecording"
            class="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 text-sm font-medium hover:bg-gray-50"
          >
            다시 촬영
          </button>

          <button
            @click="useRecording"
            class="px-4 py-2 bg-primary-600 text-white rounded-lg text-sm font-medium hover:bg-primary-700"
          >
            이 영상 사용
          </button>
        </div>
      </div>

      <!-- 제보 폼 -->
      <div v-if="showReportForm" class="mb-6">
        <h2 class="text-lg font-medium mb-4">화재 정보 입력</h2>

        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">위치</label>
            <div class="relative">
              <input
                v-model="reportData.location"
                type="text"
                class="w-full p-3 pr-10 border border-gray-300 rounded-lg"
                placeholder="화재 발생 위치"
                readonly
              />
              <button
                @click="handleLocationClick"
                class="absolute right-3 top-3 text-gray-400 hover:text-gray-600"
              >
                <MapPin class="h-5 w-5" />
              </button>
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">설명</label>
            <textarea
              v-model="reportData.description"
              class="w-full p-3 border border-gray-300 rounded-lg"
              rows="3"
              placeholder="화재 상황에 대한 설명을 입력해주세요"
            ></textarea>
          </div>
        </div>

        <button
          @click="submitReport"
          :disabled="isSubmitting"
          class="w-full mt-6 py-3 bg-primary-600 text-white rounded-lg font-medium hover:bg-primary-700 disabled:bg-gray-400 disabled:cursor-not-allowed"
        >
          <div v-if="isSubmitting" class="flex items-center justify-center">
            <Loader class="animate-spin h-5 w-5 mr-2" />
            제보 중...
          </div>
          <span v-else>화재 제보하기</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, computed } from 'vue';
import { useRouter } from 'vue-router';
import {
  ChevronLeft,
  Info,
  Circle,
  Square,
  RefreshCw,
  MapPin,
  Loader
} from 'lucide-vue-next';
import { useNotificationStore } from '../stores/notificationStore';
import { reportApiService, videoApiService } from '../services/api';

const router = useRouter();
const notificationStore = useNotificationStore();

// 비디오 요소 참조
const videoElement = ref(null);
const recordedVideo = ref(null);

// 상태 변수
const stream = ref(null);
const mediaRecorder = ref(null);
const recordedChunks = ref([]);
const recordedVideoUrl = ref(null);
const isRecording = ref(false);
const recordingStartTime = ref(null);
const recordingTime = ref('00:00');
const recordingTimer = ref(null);
const showReportForm = ref(false);
const isSubmitting = ref(false);
const hasMultipleCameras = ref(false);
const currentCameraIndex = ref(0);
const availableCameras = ref([]);

// 10초 카운트다운 관련 변수
const MAX_RECORDING_SECONDS = 10;
const remainingSeconds = ref(MAX_RECORDING_SECONDS);
const progressValue = computed(() => {
  // 원형 프로그레스바를 위한 값 계산 (100에서 0으로 감소)
  return 100 - ((MAX_RECORDING_SECONDS - remainingSeconds.value) / MAX_RECORDING_SECONDS * 100);
});

// 제보 데이터
const reportData = ref({
  userId: 'user123', // 실제로는 로그인된 사용자의 ID를 사용
  coordinates: null,
  timestamp: null,
  reportId: null,
  description: ''
});

// Keycloak 토큰에서 사용자 ID 추출 함수
const getUserIdFromToken = () => {
  const token = localStorage.getItem('token');
  if (!token) {
    throw new Error('토큰이 없습니다. 로그인이 필요합니다.');
  }

  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.sub;
  } catch (error) {
    console.error('토큰 디코딩 오류:', error);
    throw new Error('토큰이 유효하지 않습니다.');
  }
};

// 주소 포맷팅 함수 수정
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

// 위치 정보 가져오기 함수 수정
const loadLocationInfo = async (coordinates) => {
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

// 현재 위치 가져오기 함수 수정
const getCurrentLocation = () => {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error('위치 정보를 지원하지 않는 브라우저입니다.'));
      return;
    }

    navigator.geolocation.getCurrentPosition(
      async (position) => {
        const coordinates = {
          lat: position.coords.latitude,
          lng: position.coords.longitude
        };

        // 주소 변환
        const address = await loadLocationInfo(coordinates);
        reportData.value.coordinates = coordinates;
        reportData.value.location = address;
        resolve(coordinates);
      },
      (error) => {
        reject(error);
      }
    );
  });
};

// 위치 클릭 핸들러 수정
const handleLocationClick = async () => {
  try {
    await getCurrentLocation();
  } catch (error) {
    console.error('위치 정보 가져오기 실패:', error);
    notificationStore.addNotification({
      type: 'error',
      message: '위치 정보를 가져오는데 실패했습니다.'
    });
  }
};

// 카메라 초기화
const initCamera = async () => {
  try {
    // 사용 가능한 카메라 장치 가져오기
    const devices = await navigator.mediaDevices.enumerateDevices();
    const videoDevices = devices.filter(device => device.kind === 'videoinput');
    availableCameras.value = videoDevices;
    hasMultipleCameras.value = videoDevices.length > 1;

    // 카메라 스트림 가져오기
    const constraints = {
      video: {
        deviceId: videoDevices.length > 0 ? { exact: videoDevices[currentCameraIndex.value].deviceId } : undefined,
        facingMode: 'environment', // 후면 카메라 우선
        width: { ideal: 1280 },
        height: { ideal: 720 }
      },
      audio: true
    };

    stream.value = await navigator.mediaDevices.getUserMedia(constraints);

    // 비디오 요소에 스트림 연결
    if (videoElement.value) {
      videoElement.value.srcObject = stream.value;
    }
  } catch (error) {
    console.error('카메라 접근 오류:', error);
    alert('카메라에 접근할 수 없습니다. 카메라 권한을 확인해주세요.');
  }
};

// 카메라 전환
const switchCamera = async () => {
  if (stream.value) {
    // 현재 스트림 정지
    stream.value.getTracks().forEach(track => track.stop());

    // 다음 카메라 인덱스로 변경
    currentCameraIndex.value = (currentCameraIndex.value + 1) % availableCameras.value.length;

    // 새 카메라로 초기화
    await initCamera();
  }
};

// 녹화 시작
const startRecording = () => {
  if (!stream.value) return;

  recordedChunks.value = [];

  // MediaRecorder 설정
  const options = { mimeType: 'video/mp4' };
  try {
    mediaRecorder.value = new MediaRecorder(stream.value, options);
  } catch (e) {
    console.error('지원되지 않는 MIME 타입:', e);
    // 대체 MIME 타입 시도
    try {
      mediaRecorder.value = new MediaRecorder(stream.value, { mimeType: 'video/webm' });
    } catch (e) {
      console.error('MediaRecorder를 생성할 수 없습니다:', e);
      alert('브라우저가 비디오 녹화를 지원하지 않습니다.');
      return;
    }
  }

  // 데이터 수집
  mediaRecorder.value.ondataavailable = (event) => {
    if (event.data && event.data.size > 0) {
      recordedChunks.value.push(event.data);
    }
  };

  // 녹화 완료 처리
  mediaRecorder.value.onstop = () => {
    const blob = new Blob(recordedChunks.value, { type: 'video/webm' });
    recordedVideoUrl.value = URL.createObjectURL(blob);
    reportData.value.videoBlob = blob;
    reportData.value.timestamp = new Date().toISOString();
  };

  // 녹화 시작
  mediaRecorder.value.start(100); // 100ms마다 데이터 수집
  isRecording.value = true;

  // 녹화 시간 타이머 시작
  recordingStartTime.value = Date.now();
  remainingSeconds.value = MAX_RECORDING_SECONDS;
  updateRecordingTime();
  recordingTimer.value = setInterval(updateRecordingTime, 1000);
};

// 녹화 시간 업데이트
const updateRecordingTime = () => {
  const elapsedSeconds = Math.floor((Date.now() - recordingStartTime.value) / 1000);
  const minutes = Math.floor(elapsedSeconds / 60).toString().padStart(2, '0');
  const seconds = (elapsedSeconds % 60).toString().padStart(2, '0');
  recordingTime.value = `${minutes}:${seconds}`;

  // 남은 시간 계산
  remainingSeconds.value = Math.max(0, MAX_RECORDING_SECONDS - elapsedSeconds);

  // 최대 녹화 시간 (10초) 제한
  if (elapsedSeconds >= MAX_RECORDING_SECONDS) {
    stopRecording();
  }
};

// 녹화 중지
const stopRecording = () => {
  if (mediaRecorder.value && isRecording.value) {
    mediaRecorder.value.stop();
    isRecording.value = false;

    // 타이머 정지
    clearInterval(recordingTimer.value);
  }
};

// 녹화 폐기
const discardRecording = () => {
  recordedVideoUrl.value = null;
  recordedChunks.value = [];
  reportData.value.videoBlob = null;
  reportData.value.timestamp = null;
};

// 녹화 사용
const useRecording = () => {
  showReportForm.value = true;
};

// 제보 제출
const submitReport = async () => {
  if (!reportData.value.videoBlob) {
    alert('영상이 필요합니다. 다시 촬영해주세요.');
    return;
  }

  if (!reportData.value.location || !reportData.value.description) {
    alert('위치와 설명을 모두 입력해주세요.');
    return;
  }

  isSubmitting.value = true;

  try {
    // 토큰에서 사용자 ID 추출
    const userId = getUserIdFromToken();

    // 제보 데이터 준비
    const reportPayload = {
      userId: userId,
      longitude: reportData.value.coordinates.lng,
      latitude: reportData.value.coordinates.lat,
      description: reportData.value.description
    };

    // 제보 데이터 전송
    const reportResponse = await reportApiService.submitReport(reportPayload);
    const reportId = reportResponse.data.reportId;

    // 비디오 파일 생성
    const videoFile = new File([reportData.value.videoBlob], 'fire_report.mp4', { type: 'video/mp4' });

    // 비디오 업로드
    await videoApiService.uploadVideo(videoFile, reportId);

    // 알림 추가
    notificationStore.addNotification({
      type: 'verification',
      title: '화재 제보가 접수되었습니다',
      message: `${reportData.value.location}에 대한 화재 제보가 성공적으로 접수되었습니다. 소방서에서 확인 중입니다.`,
      actionText: '제보 확인하기',
      actionLink: '/report/new'
    });

    // 성공 페이지로 이동
    router.push('/report-success');
  } catch (error) {
    console.error('제보 제출 오류:', error);
    if (error.response) {
      console.error('서버 응답:', error.response.data);
      alert(`제보 제출 중 오류가 발생했습니다: ${error.response.data.message || '알 수 없는 오류'}`);
    } else {
      alert('제보 제출 중 오류가 발생했습니다. 다시 시도해주세요.');
    }
  } finally {
    isSubmitting.value = false;
  }
};

// 컴포넌트 마운트 시 카메라 초기화
onMounted(() => {
  initCamera();
});

// 컴포넌트 언마운트 시 리소스 정리
onBeforeUnmount(() => {
  if (recordingTimer.value) {
    clearInterval(recordingTimer.value);
  }

  if (stream.value) {
    stream.value.getTracks().forEach(track => track.stop());
  }

  if (recordedVideoUrl.value) {
    URL.revokeObjectURL(recordedVideoUrl.value);
  }
});
</script>

<style scoped>
/* 애니메이션 */
@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}

.animate-pulse {
  animation: pulse 1.5s infinite;
}
</style>