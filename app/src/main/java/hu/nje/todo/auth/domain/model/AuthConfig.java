package hu.nje.todo.auth.domain.model;

import lombok.Data;

@Data
public class AuthConfig {

    private String authEndpoint;
    private String tokenEndpoint;
    private String registrationEndpoint;
    private String clientId;
    private String redirectUri;
    private String scopes;
    private String profileEndpoint;
    private String endSessionEndpoint;

}
