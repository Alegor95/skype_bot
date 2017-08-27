package ru.alegor.skypebot.service.botframework;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class BotFrameworkService {

    private RestTemplate restTemplate;

    @Autowired
    private AuthenticationService authenticationService;

    private HttpEntity prepareEntity(Object requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authenticationService.getAuth());
        return requestBody == null
                ? new HttpEntity(headers)
                : new HttpEntity(requestBody, headers);
    }

    public <T> T post(String uri, Object requestBody, Class<T> responseType) {
        return restTemplate.postForObject(uri, prepareEntity(requestBody), responseType);
    }

    public <T> T get(String uri, Class<T> responseType) {
        return restTemplate.exchange(uri, HttpMethod.GET, prepareEntity(null), responseType).getBody();
    }

    public BotFrameworkService() {
        restTemplate = new RestTemplate();
    }

}
