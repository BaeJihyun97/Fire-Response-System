package fireresponsesystem.keycloak;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.List;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class KeycloakService {

    private final KeycloakTokenProvider tokenProvider;

    @Value("${keycloak.auth-server-url}")
    private String keycloakBaseUrl;

    @Value("${keycloak.realm}")
    private String realm;

    private final RestTemplate restTemplate = new RestTemplate();

    public void createUserAndAssignRole(String username, String password, String email, String roleName, String firstname, String lastname) {

        // System.out.println("✅ createUserAndAssignRole 1"); 
        String token = tokenProvider.getAdminAccessToken();


        // 1. 사용자 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // System.out.println("✅ createUserAndAssignRole 2"); 

        try{
            Map<String, Object> userPayload = Map.of(
                "username", username,
                "email", email,
                "enabled", true,
                "emailVerified", true,
                "firstName", firstname,
                "lastName", lastname,
                "credentials", List.of(
                    Map.of("type", "password", "value", password, "temporary", false)
                )
            );

            // System.out.println("✅ createUserAndAssignRole 3"); 


            HttpEntity<?> userRequest = new HttpEntity<>(userPayload, headers);
            restTemplate.postForEntity(keycloakBaseUrl + "/admin/realms/" + realm + "/users", userRequest, String.class);
    
            // System.out.println("✅ createUserAndAssignRole 4"); 

            // 2. 유저 ID 조회
            ResponseEntity<List> response = restTemplate.exchange(
                keycloakBaseUrl + "/admin/realms/" + realm + "/users?username=" + username,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                List.class
            );
            String userId = ((Map<String, Object>) response.getBody().get(0)).get("id").toString();

            // System.out.println("✅ createUserAndAssignRole 5"); 
    
    
            // 3. 롤 정보 조회
            ResponseEntity<Map> roleResponse = restTemplate.exchange(
                keycloakBaseUrl + "/admin/realms/" + realm + "/roles/" + roleName,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
            );
            Map<String, Object> roleData = roleResponse.getBody();

            // System.out.println("✅ createUserAndAssignRole 6"); 
    
    
                    // 4. 롤 할당
            HttpEntity<List<Map<String, Object>>> roleAssignRequest = new HttpEntity<>(List.of(roleData), headers);
            restTemplate.postForEntity(
                keycloakBaseUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm",
                roleAssignRequest,
                String.class
            );
    
            System.out.println("✅ roleAssignRequest" + roleAssignRequest); 
        } catch (Exception e) {
            System.out.println("🔍 error: " + e.getMessage());
            System.out.println("🔍 error: " + e);
        }

    }
}

