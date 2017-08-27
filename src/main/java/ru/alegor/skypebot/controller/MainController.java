package ru.alegor.skypebot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alegor.skypebot.model.InfoDTO;
import ru.alegor.skypebot.service.BotService;

@RestController
@RequestMapping("/")
public class MainController {

    @Value("${application.version}")
    private String version;

    @Autowired
    private BotService botService;

    @GetMapping("/info")
    public InfoDTO info() {
        return new InfoDTO(version, botService.getPlugins().keySet());
    }


}
