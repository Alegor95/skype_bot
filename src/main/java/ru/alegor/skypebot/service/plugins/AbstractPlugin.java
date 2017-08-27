package ru.alegor.skypebot.service.plugins;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import ru.alegor.skypebot.service.BotService;
import ru.alegor.skypebot.service.botframework.BotFrameworkService;

import javax.annotation.PostConstruct;

public class AbstractPlugin {

    @Getter
    private final String pluginName;

    @Autowired
    protected BotService botService;
    @Autowired
    protected BotFrameworkService botFrameworkService;

    public AbstractPlugin(String pluginName) {
        this.pluginName = pluginName;
    }

    @PostConstruct
    protected void init() {
        botService.registerPlugin(this);
    }

}
