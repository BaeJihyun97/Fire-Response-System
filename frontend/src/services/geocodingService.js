/**
 * 좌표를 주소로 변환 (역지오코딩)
 * @param {number} lat - 위도
 * @param {number} lng - 경도
 * @returns {Promise<Object>} - 주소 정보 객체
 */
export async function reverseGeocode(lat, lng) {
  try {
    // 개발 환경에서는 좌표 정보만 반환
    // 실제 서비스에서는 서버 측에서 API를 호출하거나 프록시를 사용해야 합니다
    console.log('역지오코딩 시도:', lat, lng);
    
    // 좌표에 따라 다른 주소 반환 (테스트용)
    let addressInfo;
    
    // 강남역 좌표 (37.498095, 127.027610)
    if (Math.abs(lat - 37.498095) < 0.001 && Math.abs(lng - 127.027610) < 0.001) {
      addressInfo = {
        fullAddress: '서울특별시 강남구 강남대로 396',
        shortAddress: '강남역',
        details: {
          sido: '서울특별시',
          sigungu: '강남구',
          dongmyun: '역삼동',
          roadName: '강남대로',
          buildingNumber: '396',
          buildingName: '강남역'
        }
      };
    }
    // 여의도 공원 좌표 (37.526120, 126.925771)
    else if (Math.abs(lat - 37.526120) < 0.001 && Math.abs(lng - 126.925771) < 0.001) {
      addressInfo = {
        fullAddress: '서울특별시 영등포구 여의공원로 68',
        shortAddress: '여의도공원',
        details: {
          sido: '서울특별시',
          sigungu: '영등포구',
          dongmyun: '여의도동',
          roadName: '여의공원로',
          buildingNumber: '68',
          buildingName: '여의도공원'
        }
      };
    }
    // 서울시청 좌표 (37.566535, 126.977969)
    else if (Math.abs(lat - 37.566535) < 0.001 && Math.abs(lng - 126.977969) < 0.001) {
      addressInfo = {
        fullAddress: '서울특별시 중구 세종대로 110',
        shortAddress: '서울시청',
        details: {
          sido: '서울특별시',
          sigungu: '중구',
          dongmyun: '태평로1가',
          roadName: '세종대로',
          buildingNumber: '110',
          buildingName: '서울시청'
        }
      };
    }
    // 강동구 천호동 좌표 (37.538617, 127.094454)
    else if (Math.abs(lat - 37.538617) < 0.001 && Math.abs(lng - 127.094454) < 0.001) {
      addressInfo = {
        fullAddress: '서울특별시 강동구 올림픽로 702',
        shortAddress: '천호동',
        details: {
          sido: '서울특별시',
          sigungu: '강동구',
          dongmyun: '천호동',
          roadName: '올림픽로',
          buildingNumber: '702',
          buildingName: ''
        }
      };
    }
    // 기타 좌표
    else {
      addressInfo = {
        fullAddress: `위도: ${lat}, 경도: ${lng}`,
        shortAddress: `${lat.toFixed(5)}, ${lng.toFixed(5)}`,
        isCoordinatesOnly: true
      };
    }
    
    return addressInfo;
  } catch (error) {
    console.error('역지오코딩 실패:', error);
    
    // 오류 발생 시 좌표 정보만 반환
    return {
      fullAddress: `위도: ${lat}, 경도: ${lng}`,
      shortAddress: `${lat.toFixed(5)}, ${lng.toFixed(5)}`,
      isCoordinatesOnly: true
    };
  }
}