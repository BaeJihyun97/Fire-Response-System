package fireresponsesystem.keycloak;

import lombok.Data;

@Data
public class SignupRequest {
    private String username;
    private String password;
    private String email;
    private String firstname;
    private String lastname;
}
