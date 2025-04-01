import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './assets/tailwind.css'

// 네이버 지도 API 로드 함수
const loadNaverMapsScript = () => {
  return new Promise((resolve, reject) => {
    if (window.naver && window.naver.maps) {
      console.log('네이버 지도 API가 이미 로드되었습니다.');
      resolve();
      return;
    }

    // 환경 변수에서 API 키 가져오기
    const clientId = import.meta.env.VITE_NAVER_MAPS_CLIENT_ID;
    
    if (!clientId) {
      console.error('네이버 지도 API 클라이언트 ID가 설정되지 않았습니다.');
      reject(new Error('네이버 지도 API 클라이언트 ID가 설정되지 않았습니다.'));
      return;
    }

    console.log('네이버 지도 API 로드 시작:', clientId);
    
    const script = document.createElement('script');
    script.src = `https://openapi.map.naver.com/openapi/v3/maps.js?ncpClientId=${clientId}&submodules=geocoder`;
    script.async = true;
    
    script.onload = () => {
      console.log('네이버 지도 API 로드 완료');
      resolve();
    };
    
    script.onerror = (error) => {
      console.error('네이버 지도 API 로드 실패:', error);
      reject(error);
    };
    
    document.head.appendChild(script);
  });
};

const app = createApp(App);
const pinia = createPinia();

app.use(pinia);
app.use(router);

// 네이버 지도 API 로드 후 앱 마운트
loadNaverMapsScript()
  .then(() => {
    console.log('앱 마운트 준비 완료');
    app.mount('#app');
  })
  .catch((error) => {
    console.error('네이버 지도 API 로드 실패, 앱을 마운트합니다:', error);
    // API 로드 실패해도 앱은 마운트
    app.mount('#app');
  });