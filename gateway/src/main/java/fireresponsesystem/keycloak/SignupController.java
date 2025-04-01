package fireresponsesystem.keycloak;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class SignupController {

    private final KeycloakService keycloakService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        System.out.println("✅ signup controller hit!");
        System.out.println("✅ signup request: " + request);
        keycloakService.createUserAndAssignRole(request.getUsername(), request.getPassword(), request.getEmail(), "USER", request.getFirstname(), request.getLastname());
        return ResponseEntity.ok("회원가입 완료");
    }
}

