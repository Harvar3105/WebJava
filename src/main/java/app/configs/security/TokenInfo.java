package app.configs.security;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
public class TokenInfo {

    String username;
    List<String> roles;

    public TokenInfo(String userName, String roles) {
        this.username = userName;
        this.roles = List.of(roles.split(", "));
    }

    public String getRolesAsString() {
        return String.join(", ", roles);
    }

}
