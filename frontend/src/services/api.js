import axios from 'axios';

// 이벤트 API 기본 설정
const eventApi = axios.create({
  baseURL: 'http://20.249.180.106:8080',
  headers: {
    'Content-Type': 'application/json'
  }
});

// 리포트 API 기본 설정
const reportsApi = axios.create({
  baseURL: 'http://20.249.180.114:8080',
  headers: {
    'Content-Type': 'application/json',
  },
});

// 비디오 API 기본 설정
const videoApi = axios.create({
  baseURL: 'http://20.214.124.99:8080',
  headers: {
    'Content-Type': 'multipart/form-data'
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
  
  return tokenAge >= (expiresIn * 1000);
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

// 요청 인터셉터 - 토큰 추가 (각 API 인스턴스에 적용)
const addTokenInterceptor = (instance) => {
  instance.interceptors.request.use(
    async (config) => {
      if (config.url === '/login') {
        return config;
      }

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
};

// 응답 인터셉터 - 에러 처리 (각 API 인스턴스에 적용)
const addErrorInterceptor = (instance) => {
  instance.interceptors.response.use(
    (response) => response,
    async (error) => {
      if (error.response?.status === 401) {
        try {
          await refreshToken();
        } catch (refreshError) {
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
};

// 각 API 인스턴스에 인터셉터 적용
addTokenInterceptor(eventApi);
addTokenInterceptor(reportsApi);
addTokenInterceptor(videoApi);
addErrorInterceptor(eventApi);
addErrorInterceptor(reportsApi);
addErrorInterceptor(videoApi);

// 이벤트 API
export const eventApiService = {
  getEvents: (params) => axios.get('/events', { params }),
  getEventById: (id) => axios.get(`/events/${id}`),
  createEvent: (eventData) => axios.post('/events', eventData),
  updateEvent: (id, eventData) => axios.put(`/events/${id}`, eventData),
  updateEventStatus: (id, status) => axios.patch(`/events/${id}/status`, { status }),
  deleteEvent: (id) => axios.delete(`/events/${id}`),
  getActiveEvents: () => axios.get('/events/active'),
  getPendingEvents: () => axios.get('/events/pending'),
  getVerifiedEvents: () => axios.get('/events/verified'),
  getEventStatistics: (id) => axios.get(`/events/${id}/statistics`)
};

// 리포트 API
export const reportApiService = {
  getReports: () => reportsApi.get('/reports'),
  getReport: (id) => reportsApi.get(`/reports/${id}`),
  createReport: (reportData) => reportsApi.post('/reports', reportData),
  updateReport: (id, reportData) => reportsApi.put(`/reports/${id}`, reportData),
  deleteReport: (id) => reportsApi.delete(`/reports/${id}`),
  submitReport: (reportData) => reportsApi.post('/reports/receivereport', reportData),
  getReportsByUserId: async (userId, config = {}) => {
    console.log('Fetching reports for userId:', userId);
    try {
      const response = await reportsApi.get(`/reports/user/${userId}`, config);
      console.log('Reports API response:', response.data);
      return response;
    } catch (error) {
      console.error('Error fetching reports:', error);
      throw error;
    }
  },
  getReportsByUsername: (username, config = {}) => reportsApi.get(`/reports/username/${username}`, config)
};

// 비디오 API
export const videoApiService = {
  uploadVideo: (file, reportId) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('report_id', reportId);
    return videoApi.post('/videos/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  },
  getVideo: (videoId) => videoApi.get(`/videos/${videoId}`),
  deleteVideo: (videoId) => videoApi.delete(`/videos/${videoId}`)
};

// 인증 API
export const authApi = {
  login: (credentials) => {
    const url = '/login';
    console.log('Login Request URL:', url);
    console.log('Login Request Data:', credentials);
    return axios.post(url, credentials, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
  },
  register: (userData) => {
    const url = '/signup';
    console.log('Signup Request URL:', url);
    console.log('Signup Request Data:', userData);
    return axios.post(url, userData, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
  },
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('token_expires_in');
    localStorage.removeItem('token_timestamp');
    window.location.href = '/login';
  },
  getProfile: () => reportsApi.get('/profile'),
  updateProfile: (profileData) => reportsApi.put('/profile', profileData)
};

// 게시물 API
export const postApi = {
  getPosts: (params) => axios.get('/posts', { params }),
  getPostById: (id) => axios.get(`/posts/${id}`),
  createPost: (postData) => axios.post('/posts', postData),
  updatePost: (id, postData) => axios.put(`/posts/${id}`, postData),
  updatePostStatus: (id, status) => axios.patch(`/posts/${id}/status`, { status }),
  deletePost: (id) => axios.delete(`/posts/${id}`),
  getPostsByEventId: (eventId) => axios.get(`/events/${eventId}/posts`)
};

// 관리자 API
export const adminApi = {
  getStatistics: () => axios.get('/admin/statistics'),
  getUsers: (params) => axios.get('/admin/users', { params }),
  updateUserRole: (userId, role) => axios.patch(`/admin/users/${userId}/role`, { role }),
  getSystemLogs: (params) => axios.get('/admin/logs', { params })
};

// 미디어 API
export const mediaApi = {
  uploadVideo: (file) => {
    const formData = new FormData();
    formData.append('video', file);
    return axios.post('/media/video', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  },
  uploadImage: (file) => {
    const formData = new FormData();
    formData.append('image', file);
    return axios.post('/media/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  },
  deleteMedia: (id) => axios.delete(`/media/${id}`)
};

// 위치 API
export const locationApi = {
  searchLocation: (query) => axios.get('/locations/search', { params: { query } }),
  getCoordinates: (address) => axios.get('/locations/coordinates', { params: { address } }),
  getAddress: (coordinates) => axios.get('/locations/address', { params: coordinates })
};

// 지도 API
export const mapApi = {
  getRiskAreas: () => axios.get('/map/risk-areas'),
  getFireStations: () => axios.get('/map/fire-stations'),
  getActiveEvents: () => axios.get('/map/active-events'),
  getHeatmapData: () => axios.get('/map/heatmap'),
  getRiskAnalysis: (area) => axios.get('/map/risk-analysis', { params: { area } })
};

export default {
  eventApi: eventApiService,
  reportApi: reportApiService,
  videoApi: videoApiService,
  authApi,
  postApi,
  adminApi,
  mediaApi,
  locationApi,
  mapApi
}; 