import axios from 'axios';

// API 기본 설정
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:3000/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

// 요청 인터셉터 - 토큰 추가
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터 - 에러 처리
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // 인증 에러 처리
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// 인증 API
export const authApi = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
  logout: () => api.post('/auth/logout'),
  getProfile: () => api.get('/auth/profile'),
  updateProfile: (profileData) => api.put('/auth/profile', profileData)
};

// 이벤트 API
export const eventApi = {
  getEvents: (params) => api.get('/events', { params }),
  getEventById: (id) => api.get(`/events/${id}`),
  createEvent: (eventData) => api.post('/events', eventData),
  updateEvent: (id, eventData) => api.put(`/events/${id}`, eventData),
  updateEventStatus: (id, status) => api.patch(`/events/${id}/status`, { status }),
  deleteEvent: (id) => api.delete(`/events/${id}`),
  getActiveEvents: () => api.get('/events/active'),
  getPendingEvents: () => api.get('/events/pending'),
  getVerifiedEvents: () => api.get('/events/verified'),
  getEventStatistics: (id) => api.get(`/events/${id}/statistics`)
};

// 게시물 API
export const postApi = {
  getPosts: (params) => api.get('/posts', { params }),
  getPostById: (id) => api.get(`/posts/${id}`),
  createPost: (postData) => api.post('/posts', postData),
  updatePost: (id, postData) => api.put(`/posts/${id}`, postData),
  updatePostStatus: (id, status) => api.patch(`/posts/${id}/status`, { status }),
  deletePost: (id) => api.delete(`/posts/${id}`),
  getPostsByEventId: (eventId) => api.get(`/events/${eventId}/posts`)
};

// 관리자 API
export const adminApi = {
  getStatistics: () => api.get('/admin/statistics'),
  getUsers: (params) => api.get('/admin/users', { params }),
  updateUserRole: (userId, role) => api.patch(`/admin/users/${userId}/role`, { role }),
  getSystemLogs: (params) => api.get('/admin/logs', { params })
};

// 미디어 API
export const mediaApi = {
  uploadVideo: (file) => {
    const formData = new FormData();
    formData.append('video', file);
    return api.post('/media/video', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  },
  uploadImage: (file) => {
    const formData = new FormData();
    formData.append('image', file);
    return api.post('/media/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  },
  deleteMedia: (id) => api.delete(`/media/${id}`)
};

// 위치 API
export const locationApi = {
  searchLocation: (query) => api.get('/locations/search', { params: { query } }),
  getCoordinates: (address) => api.get('/locations/coordinates', { params: { address } }),
  getAddress: (coordinates) => api.get('/locations/address', { params: coordinates })
};

// 지도 API
export const mapApi = {
  getRiskAreas: () => api.get('/map/risk-areas'),
  getFireStations: () => api.get('/map/fire-stations'),
  getActiveEvents: () => api.get('/map/active-events'),
  getHeatmapData: () => api.get('/map/heatmap'),
  getRiskAnalysis: (area) => api.get('/map/risk-analysis', { params: { area } })
};

export default api; 