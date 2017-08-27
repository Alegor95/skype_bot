package ru.alegor.skypebot.service.botframework;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.alegor.skypebot.service.botframework.model.AuthenticationDTO;

import javax.annotation.PostConstruct;
import java.time.OffsetDateTime;

import static java.time.temporal.ChronoUnit.SECONDS;

@Service
@Slf4j
public class AuthenticationService {

    @Value("${microsoft.app.id}")
    private String appId;
    @Value("${microsoft.app.password}")
    private String appPassword;
    @Value("${bot.framework.uri.auth}")
    private String authURI;

    private String token;
    private String tokenType;
    private OffsetDateTime expiresIn;

    private RestTemplate restTemplate;

    public String getAuth() {
        synchronized (expiresIn) {
            if (expiresIn == null || expiresIn.isBefore(OffsetDateTime.now())) {
                log.info("Необходимо обновить токен авторизации");
                authenticate();
            }
        }
        return tokenType + " " + token;
    }

    private void authenticate() {
        //Build authentication
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "client_credentials");
        params.add("client_id", appId);
        params.add("client_secret", appPassword);
        params.add("scope", "https://api.botframework.com/.default");
        AuthenticationDTO dto = restTemplate.postForObject(authURI, params, AuthenticationDTO.class);
        token = dto.getAccessToken();
        tokenType = dto.getTokenType();
        expiresIn = OffsetDateTime.now().plus(dto.getExpiresIn(), SECONDS);
        log.info("Бот успешно авторизован, токен действителен до {}", expiresIn);
    }

    @PostConstruct
    private void init() {
        authenticate();
    }

    public AuthenticationService() {
        restTemplate = new RestTemplate();
    }
}
