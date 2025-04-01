import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      // Naver Maps API 요청을 프록시
      '/api/naver': {
        target: 'https://naveropenapi.apigw.ntruss.com',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/naver/, ''),
        headers: {
          'X-NCP-APIGW-API-KEY-ID': process.env.VITE_NAVER_MAPS_CLIENT_ID
        }
      }
    },
    host: true,
    allowedHosts: [
      '경로'
    ]
  }
});
