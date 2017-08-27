package ru.alegor.skypebot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.alegor.skypebot.service.BotService;
import ru.alegor.skypebot.service.botframework.ServerValidationService;
import ru.alegor.skypebot.service.botframework.model.ActivityDTO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/endpoint")
public class EndpointController {

    @Autowired
    private ServerValidationService serverValidationService;
    @Autowired
    private BotService botService;

    @PostMapping("activity")
    private void recieveActivity(@RequestBody ActivityDTO activity, @RequestHeader("Authorization") String auth,
                                 HttpServletResponse response) throws IOException {
        if (!serverValidationService.validateServerToken(auth)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        botService.processActivity(activity);
    }
}
