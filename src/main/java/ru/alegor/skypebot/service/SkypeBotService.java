package ru.alegor.skypebot.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.alegor.skypebot.service.botframework.ActivityBuilder;
import ru.alegor.skypebot.service.botframework.BotFrameworkService;
import ru.alegor.skypebot.service.botframework.model.ActivityDTO;
import ru.alegor.skypebot.service.plugins.AbstractPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SkypeBotService {

    @Getter
    private Map<String, AbstractPlugin> plugins;

    @Autowired
    private BotFrameworkService botFrameworkService;

    public void registerPlugin(AbstractPlugin plugin) {
        if (plugins.containsKey(plugin.getPluginName())) {
            throw new IllegalStateException("Plugin " + plugin.getPluginName() + " already registered");
        }
        plugins.put(plugin.getPluginName(), plugin);
        log.info("Зарегистрирован плагин {}", plugin.getClass().getSimpleName());
    }

    public Map<String, AbstractPlugin> getPlugins() {
        return Collections.unmodifiableMap(this.plugins);
    }

    public void processActivity(ActivityDTO activity) {
        ActivityDTO reply = ActivityBuilder.buildMessageActivity()
                .setConversation(activity.getConversation())
                .setFrom(activity.getRecipient())
                .setLocale(activity.getLocale())
                .setRecipient(activity.getFrom())
                .setReplyToId(activity.getId())
                .setText("Спасибо за запрос! Он будет обработан в ближайшее время.")
                .get();
        botFrameworkService.sendMessage(activity.getServiceUrl(), reply);
    }

    public SkypeBotService() {
        plugins = new HashMap<>();
    }
}
