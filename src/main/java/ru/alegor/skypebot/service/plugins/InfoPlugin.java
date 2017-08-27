package ru.alegor.skypebot.service.plugins;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InfoPlugin extends AbstractPlugin {

    private static final String pluginName = "info";

    @Override
    protected void init() {
        super.init();
        log.info("Плагин {} инициализирован, {} команд", this.getPluginName());
    }

    public InfoPlugin() {
        super(pluginName);
    }
}
