import axios from 'axios';

// API 기본 설정
const api = axios.create({
  baseURL: '/api',  // 프록시를 통해 요청
  headers: {
    'Content-Type': 'application/json'
  }
});

// 토큰 만료 체크 함수
const isTokenExpired = () => {
  const expiresIn = localStorage.getItem('token_expires_in');
  if (!expiresIn) return true;
  
  const tokenTimestamp = localStorage.getItem('token_timestamp');
  if (!tokenTimestamp) return true;
  
  const now = Date.now();
  const tokenAge = now - parseInt(tokenTimestamp);
  
  return tokenAge >= (expiresIn * 1000); // expiresIn은 초 단위이므로 밀리초로 변환
};

// 토큰 리프레시 함수
const refreshToken = async () => {
  try {
    const refresh_token = localStorage.getItem('refresh_token');
    if (!refresh_token) throw new Error('리프레시 토큰이 없습니다.');

    const response = await axios.post('/api/token/refresh', {
      refresh_token
    });

    const { access_token, expires_in } = response.data;
    localStorage.setItem('token', access_token);
    localStorage.setItem('token_expires_in', expires_in);
    localStorage.setItem('token_timestamp', Date.now().toString());

    return access_token;
  } catch (error) {
    console.error('토큰 리프레시 실패:', error);
    localStorage.removeItem('token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('token_expires_in');
    localStorage.removeItem('token_timestamp');
    window.location.href = '/login';
    throw error;
  }
};

// 요청 인터셉터 - 토큰 추가
api.interceptors.request.use(
  async (config) => {
    // 로그인 요청인 경우 토큰 체크 건너뛰기
    if (config.url === '/login') {
      return config;
    }

    // 토큰이 만료되었는지 확인
    if (isTokenExpired()) {
      try {
        const newToken = await refreshToken();
        config.headers.Authorization = `Bearer ${newToken}`;
      } catch (error) {
        return Promise.reject(error);
      }
    } else {
      const token = localStorage.getItem('token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
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
  async (error) => {
    if (error.response?.status === 401) {
      try {
        await refreshToken();
      } catch (refreshError) {
        // 리프레시 토큰도 실패하면 로그인 페이지로 리디렉션
        localStorage.removeItem('token');
        localStorage.removeItem('refresh_token');
        localStorage.removeItem('token_expires_in');
        localStorage.removeItem('token_timestamp');
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

// 인증 API
export const authApi = {
  login: (credentials) => {
    const url = '/login';
    console.log('Login Request URL:', url);
    console.log('Login Request Data:', {
      username: credentials.username,
      password: credentials.password
    });
    return api.post(url, {
      username: credentials.username,
      password: credentials.password
    }, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
  },
  register: (userData) => api.post('/signup', userData, {
    headers: {
      'Content-Type': 'application/json'
    }
  }),
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('token_expires_in');
    localStorage.removeItem('token_timestamp');
    window.location.href = '/login';
  },
  getProfile: () => api.get('/profile'),
  updateProfile: (profileData) => api.put('/profile', profileData)
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

// 비디오 API
export const videoApi = {
  uploadVideo: (file, reportId) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('report_id', reportId);
    const token = localStorage.getItem('token');
    return axios.post('http://20.214.124.99:8080/videos/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': `Bearer ${token}`
      }
    });
  }
};

// 화재 신고 API
export const reportApi = {
  getReports: () => api.get('/reports'),
  getReport: (id) => api.get(`/reports/${id}`),
  createReport: (reportData) => api.post('/reports', reportData),
  updateReport: (id, reportData) => api.put(`/reports/${id}`, reportData),
  deleteReport: (id) => api.delete(`/reports/${id}`),
  submitReport: (reportData) => {
    const token = localStorage.getItem('token');
    return axios.post('http://20.249.180.114:8080/reports/receivereport', reportData, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    });
  }
};

export default api; 