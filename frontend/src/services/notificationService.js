import axios from 'axios';

const API_BASE_URL = 'http://20.249.170.119:8080';

export const getUnreadNotifications = async (userId, latitude, longitude) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/notification/unread`, {
      params: {
        userId,
        latitude,
        longitude
      }
    });
    return response.data;
  } catch (error) {
    console.error('알림 조회 실패:', error);
    return { count: 0 };
  }
}; 