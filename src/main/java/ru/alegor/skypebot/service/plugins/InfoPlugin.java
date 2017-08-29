package ru.alegor.skypebot.service.plugins;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.alegor.skypebot.service.botframework.ActivityBuilder;
import ru.alegor.skypebot.service.botframework.model.ActivityDTO;
import ru.alegor.skypebot.service.plugins.event.MessageRecievedEvent;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InfoPlugin extends AbstractPlugin implements MessageRecievedEvent {

    @Data
    @AllArgsConstructor
    public class InfoCommandDTO {
        private String command;
        private String description;
        private String text;
    }

    private static final String pluginName = "info";
    private final Map<String, InfoCommandDTO> commands;
    private final String defaultText;

    @Override
    protected void init() {
        super.init();
        commands.entrySet()
                .forEach(_e -> this.botService.registerCommand(_e.getValue().getCommand(), _e.getValue().getDescription(), this.getClass()));
        log.info("Плагин {} инициализирован, {} команд", this.getPluginName(), commands.size());
    }

    public InfoPlugin() {
        super(pluginName);
        defaultText = "Команда %s не распознана плагином!";
        commands = new HashMap<>();
        commands.put("redmine", new InfoCommandDTO("redmine", "информация о Redmine", "Ссылка на Redmine: [нажать](http://project.vistar.su/redmine/)"));
    }

    @Override
    public void messageReceived(ActivityDTO context, String pureText) {
        log.debug("Реакция на сообщение {}", context.getText());
        ActivityDTO reply = ActivityBuilder.buildMessageActivity()
                .setConversation(context.getConversation())
                .setFrom(context.getRecipient())
                .setLocale(context.getLocale())
                .setRecipient(context.getFrom())
                .setReplyToId(context.getId())
                .setText(commands.containsKey(pureText)
                        ? commands.get(pureText).getText()
                        : String.format(defaultText, pureText))
                .get();
        botFrameworkService.sendReplyMessage(context.getServiceUrl(), reply);
    }
}
