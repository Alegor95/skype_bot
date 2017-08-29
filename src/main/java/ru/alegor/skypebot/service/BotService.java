package ru.alegor.skypebot.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.alegor.skypebot.service.botframework.model.ActivityDTO;
import ru.alegor.skypebot.service.plugins.AbstractPlugin;
import ru.alegor.skypebot.service.plugins.event.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class BotService {

    @Getter
    private Map<String, AbstractPlugin> plugins;

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
        //Check event type
        log.info("Получено сообщение типа {}", activity.getType());
        if (activity.getRecipient().equals(activity.getFrom())) {
            log.warn("Получатель и отправитель сообщения совпадают, игнорируем его.");
            return;
        }
        switch (activity.getType()) {
            case "message": {
                log.debug("Найдено событие: получение сообщения");
                plugins.values().stream()
                        .filter(_p -> _p instanceof MessageRecievedEvent)
                        .map(_p -> (MessageRecievedEvent)_p)
                        .forEach(_p -> _p.messageReceived(activity));
            } break;
            case "conversationUpdate": {
                if (activity.getMembersRemoved() != null && !activity.getMembersRemoved().isEmpty()) {
                    log.debug("Найдено событие: удаление пользователя");
                    plugins.values().stream()
                        .filter(_p -> _p instanceof UserRemoveEvent)
                        .map(_p -> (UserRemoveEvent)_p)
                        .forEach(_p -> {
                            _p.onUsersRemove(activity, activity.getConversation(), activity.getMembersRemoved());
                        });
                }
                if (activity.getMembersAdded() != null && !activity.getMembersAdded().isEmpty()) {
                    log.debug("Найдено событие: добавление пользователя");
                    plugins.values().stream()
                            .filter(_p -> _p instanceof UsersAddEvent)
                            .map(_p -> (UsersAddEvent)_p)
                            .forEach(_p -> {
                                _p.onUsersAdd(activity, activity.getConversation(), activity.getMembersAdded());
                            });
                }
            } break;
            case "contactRelationUpdate": {
                if ("add".equals(activity.getAction())) {
                    log.debug("Найдено событие: добавление в список контактов");
                    plugins.values().stream()
                            .filter(_p -> _p instanceof ContactAddedEvent)
                            .map(_p -> (ContactAddedEvent)_p)
                            .forEach(_p -> {
                                _p.onContactAdded(activity);
                            });
                }
                if ("remove".equals(activity.getAction())) {
                    log.debug("Найдено событие: удаление из списка контактов");
                    plugins.values().stream()
                            .filter(_p -> _p instanceof ContactRemovedEvent)
                            .map(_p -> (ContactRemovedEvent)_p)
                            .forEach(_p -> {
                                _p.onContactRemoved(activity);
                            });

                }
            } break;
        }
    }

    public BotService() {
        plugins = new HashMap<>();
    }
}
