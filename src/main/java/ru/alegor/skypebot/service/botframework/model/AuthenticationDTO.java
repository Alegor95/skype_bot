package ru.alegor.skypebot.service.botframework.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthenticationDTO {

    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private Long expiresIn;
    @JsonProperty("ext_expires_in")
    private Long extExpiresIn;
    @JsonProperty("access_token")
    private String accessToken;

}
