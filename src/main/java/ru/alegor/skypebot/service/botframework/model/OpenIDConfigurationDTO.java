package ru.alegor.skypebot.service.botframework.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OpenIDConfigurationDTO {

    private String issuer;
    @JsonProperty("authorization_endpoint")
    private String authorizationEndpoint;
    @JsonProperty("jwks_uri")
    private String jwksURI;
    @JsonProperty("id_token_signing_alg_values_supported")
    private List<String> signingAlgorithms;
    @JsonProperty("token_endpoint_auth_methods_supported")
    private List<String> authMethods;
}