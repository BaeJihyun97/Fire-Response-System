export const getCurrentLocation = () => {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error('Geolocation is not supported by your browser'));
      return;
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        resolve({
          latitude: position.coords.latitude,
          longitude: position.coords.longitude
        });
      },
      (error) => {
        console.error('위치 정보를 가져오는데 실패했습니다:', error);
        // 기본값으로 서울 시청 좌표 반환
        resolve({
          latitude: 37.5665,
          longitude: 126.9780
        });
      }
    );
  });
}; 