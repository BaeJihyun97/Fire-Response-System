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

export const getAllNotifications = async (userId) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/notification/${userId}`);
    return response.data.alarms.map(alarm => ({
      id: alarm.userAlarmId,
      type: alarm.alarmType.toLowerCase(),
      title: '화재 알림',
      message: alarm.content,
      time: new Date(alarm.createdAt).toLocaleString('ko-KR'),
      read: false,
      actionText: '지도에서 보기',
      actionLink: `/map?lat=${alarm.latitude}&lng=${alarm.longitude}`
    }));
  } catch (error) {
    console.error('알림 목록 조회 실패:', error);
    return [];
  }
}; 