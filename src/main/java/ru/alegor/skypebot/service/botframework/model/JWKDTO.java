package ru.alegor.skypebot.service.botframework.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class JWKDTO {

    @JsonProperty("kty")
    private String keyType;
    private String use;
    @JsonProperty("key_ops")
    private List<String> keyOperations;
    @JsonProperty("alg")
    private String algorithm;
    @JsonProperty("kid")
    private String id;
    private String x5u;

}
