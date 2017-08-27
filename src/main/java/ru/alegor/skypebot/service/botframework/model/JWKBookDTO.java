package ru.alegor.skypebot.service.botframework.model;

import lombok.Data;

import java.util.Collection;

@Data
public class JWKBookDTO {

    private Collection<JWKDTO> keys;

}
