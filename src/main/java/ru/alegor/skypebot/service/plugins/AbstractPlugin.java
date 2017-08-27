package ru.alegor.skypebot.service.plugins;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.alegor.skypebot.service.SkypeBotService;

import javax.annotation.PostConstruct;

public class AbstractPlugin {

    @Getter
    private final String pluginName;

    @Autowired
    private SkypeBotService botService;

    public AbstractPlugin(String pluginName) {
        this.pluginName = pluginName;
    }

    @PostConstruct
    protected void init() {
        botService.registerPlugin(this);
    }

}
