package ru.alegor.skypebot.service.botframework;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.alegor.skypebot.service.botframework.model.JWKBookDTO;
import ru.alegor.skypebot.service.botframework.model.JWKDTO;
import ru.alegor.skypebot.service.botframework.model.OpenIDConfigurationDTO;

import javax.annotation.PostConstruct;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ServerValidationService {

    private final String serverTokenType = "Bearer";

    @Value("${bot.framework.uri.openid}")
    private String configurationURI;
    private OpenIDConfigurationDTO configuration;
    private Map<String, JWKDTO> keys;
    private OffsetDateTime keysExpires;

    @Autowired
    private BotFrameworkService frameworkService;

    public boolean validateServerToken(String authHeader) {
        synchronized (keysExpires) {
            if (keysExpires == null || keysExpires.isBefore(OffsetDateTime.now())) {
                loadConfiguration();
            }
        }
        //TODO: Server auth validation
        return true;
        /*//Auth header can't be empty
        if (authHeader == null || authHeader.isEmpty()) return false;
        //Check auth method
        String[] splited = authHeader.split(" ");
        if (!serverTokenType.equals(splited[0])) return false;
        //Parse token
        String base64token = splited[1];
        String parsed = new String(Base64.encode(base64token.getBytes()));
        return true;*/
    }

    private void loadConfiguration() {
        this.configuration = frameworkService.get(this.configurationURI, OpenIDConfigurationDTO.class);
        JWKBookDTO book = frameworkService.get(this.configuration.getJwksURI(), JWKBookDTO.class);
        keys = book.getKeys().stream().collect(Collectors.toMap(
               _k -> _k.getId(),
               _k -> _k
        ));
        keysExpires = OffsetDateTime.now().plus(1, ChronoUnit.DAYS);
        log.info("Конфигурация безопасности загружена, {} ключей, действительны до {}", keys.size(), keysExpires);
    }

    @PostConstruct
    private void init() {
        loadConfiguration();
    }
}
