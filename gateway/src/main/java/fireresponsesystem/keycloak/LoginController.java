package fireresponsesystem.keycloak;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate; 
import org.springframework.http.HttpEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.beans.factory.annotation.Value;
import java.util.Base64;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
@RequiredArgsConstructor
public class LoginController {

    private final RestTemplate restTemplate;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        // System.out.println("🔍 login: " + authServerUrl);

        String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("username", request.getUsername());
        formData.add("password", request.getPassword());

        HttpEntity<MultiValueMap<String, String>> loginRequest = new HttpEntity<>(formData, headers);

        // System.out.println("🔍 loginRequest: " + loginRequest);

        try {
            ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                tokenUrl,
                loginRequest,
                TokenResponse.class
            );
            TokenResponse token = response.getBody();

            // 🔍 토큰 값 출력
            try {
                // 🔍 Access Token 디코딩
                String[] chunks = token.getAccess_token().split("\\.");
                String payload = chunks[1];
            
                byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
                String json = new String(decodedBytes);
            
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> tokenBody = mapper.readValue(json, Map.class);
            
                // ✅ 원하는 값 꺼내기
                String userId = (String) tokenBody.get("sub");
                String username = (String) tokenBody.get("preferred_username");
            
                System.out.println("🎯 Decoded Token Payload: " + json);
                System.out.println("👤 userId (sub): " + userId);
                System.out.println("👤 username (preferred_username): " + username);
            
            } catch (Exception e) {
                System.err.println("❌ Access Token 디코딩 실패: " + e.getMessage());
            }
        
            return ResponseEntity.ok(token);
        } catch (HttpClientErrorException e) {
            System.out.println("🔍 error: " + e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }
}

