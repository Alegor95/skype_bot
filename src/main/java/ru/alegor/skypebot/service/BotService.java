package ru.alegor.skypebot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.alegor.skypebot.model.BotCommandDTO;
import ru.alegor.skypebot.service.botframework.ActivityBuilder;
import ru.alegor.skypebot.service.botframework.BotFrameworkService;
import ru.alegor.skypebot.service.botframework.model.ActivityDTO;
import ru.alegor.skypebot.service.plugins.AbstractPlugin;
import ru.alegor.skypebot.service.plugins.Configurable;
import ru.alegor.skypebot.service.plugins.event.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class BotService {

    @Autowired
    private BotFrameworkService botFrameworkService;
    @Autowired
    private BotConfigurationService configurationService;

    private Map<String, AbstractPlugin> plugins;
    private Map<String, BotCommandDTO> registeredCommands;
    private final char startSymbol = '/';
    private final String helpCommand = "help";

    public void registerPlugin(AbstractPlugin plugin) {
        if (plugins.containsKey(plugin.getPluginName())) {
            throw new IllegalStateException("Plugin " + plugin.getPluginName() + " already registered");
        }
        plugins.put(plugin.getPluginName(), plugin);
        if (plugin instanceof Configurable) {
            configurationService.registerConfiguration(plugin);
        }
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
                log.debug("Найдено событие: получено сообщение {}", activity.getText());
                String text = activity.getText();
                //Удаляем имя бота из начала сообщения, если оно есть
                if (text.indexOf(activity.getRecipient().getName()) == 0) {
                    text = text.replace(activity.getRecipient().getName(), "").substring(1);
                }
                if (text.length() == 0 || text.charAt(0) != startSymbol) {
                    log.debug("Команда {} не адресована боту {}", text, activity.getRecipient().getName());
                    return;
                }
                final String pureText = text.substring(1);
                if (helpCommand.equals(pureText)) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Доступные команды:\n");
                    for (BotCommandDTO command : registeredCommands.values()) {
                        builder.append(startSymbol)
                                .append(command.getCommand())
                                .append(" - ")
                                .append(command.getDescription())
                                .append("\n");
                    }
                    sendReply(activity, builder.toString());
                    return;
                }
                BotCommandDTO botCommand = registeredCommands.get(pureText.split(" ")[0]);
                if (botCommand == null) {
                    String commandUnsupportedText = new StringBuilder()
                            .append("Команда ").append(startSymbol).append(pureText).append(" не поддерживается.\n")
                            .append("Воспользуйтесь командой ").append(startSymbol).append(helpCommand)
                            .append(" для помощи")
                            .toString();
                    sendReply(activity, commandUnsupportedText);
                    return;
                }
                plugins.values().stream()
                        .filter(_p -> botCommand.getCommandOwner().isInstance(_p))
                        .map(_p -> (MessageRecievedEvent)_p)
                        .forEach(_p -> _p.messageReceived(activity, pureText));
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

    public void registerCommand(String command, String description, Class<? extends AbstractPlugin> commandOwner) {
        if (registeredCommands.containsKey(command) || helpCommand.equals(command)) {
            throw new IllegalStateException("Команда " + command + " уже зарегистрирована");
        }
        if (!MessageRecievedEvent.class.isAssignableFrom(commandOwner)) {
            throw new IllegalStateException("Команды может регистрировать только плагин типа MessageRecievedEvent");
        }
        registeredCommands.put(command, new BotCommandDTO(command, description, commandOwner));
    }

    public void unregisterCommand(String command) {
        if (!registeredCommands.containsKey(command)) {
            throw new IllegalStateException("Команда " + command + " не зарегистрирована");
        }
        if (helpCommand.equals(command)) {
            throw new IllegalStateException("Команду " + command + " нельзя удалить");
        }
        registeredCommands.remove(command);
    }

    protected void sendReply(ActivityDTO activity, String text) {
        ActivityDTO reply = ActivityBuilder.buildMessageActivity()
                .setConversation(activity.getConversation())
                .setFrom(activity.getRecipient())
                .setLocale(activity.getLocale())
                .setRecipient(activity.getFrom())
                .setReplyToId(activity.getId())
                .setText(text)
                .get();
        botFrameworkService.sendReplyMessage(activity.getServiceUrl(), reply);
    }

    public BotService() {
        plugins = new HashMap<>();
        registeredCommands = new HashMap<>();
    }
}
