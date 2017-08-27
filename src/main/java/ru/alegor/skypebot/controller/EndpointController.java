package ru.alegor.skypebot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import ru.alegor.skypebot.service.SkypeBotService;
import ru.alegor.skypebot.service.botframework.ServerValidationService;

@RestController
public class EndpointController {

    @Autowired
    private ServerValidationService serverValidationService;
    @Autowired
    private SkypeBotService botService;
}
