import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://20.249.194.117:8080',
        changeOrigin: true,
        secure: false,
        ws: true,
      },
      '/events': {
        target: 'http://20.249.180.106:8080',
        changeOrigin: true,
        secure: false,
        ws: true,
      },
      '/videos': {
        target: 'http://20.214.124.99:8080',
        changeOrigin: true,
        secure: false,
        ws: true,
      },
      '/login': {
        target: 'http://20.249.194.117:8080',
        changeOrigin: true,
        secure: false,
        ws: true,
      },
      '/signup': {
        target: 'http://20.249.194.117:8080',
        changeOrigin: true,
        secure: false,
        ws: true,
      },
      '/reports': {
        target: 'http://20.249.180.114:8080',
        changeOrigin: true,
        secure: false,
        ws: true,
      },
      '/posts': {
        target: 'http://20.214.121.111:8080',
        changeOrigin: true,
        secure: false,
        ws: true,
      },
      '/notifications': {
        target: 'http://20.249.170.119:8080',
        changeOrigin: true,
        secure: false,
        ws: true,
      },
      '/blob': {
        target: 'https://team05sa.blob.core.windows.net',
        changeOrigin: true,
        secure: false,
        ws: true,
      }
    }
  }
});
