package ru.alegor.skypebot.service.plugins;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.alegor.skypebot.service.botframework.ActivityBuilder;
import ru.alegor.skypebot.service.botframework.model.ActivityDTO;
import ru.alegor.skypebot.service.botframework.model.ChannelAccountDTO;
import ru.alegor.skypebot.service.botframework.model.ConversationAccountDTO;
import ru.alegor.skypebot.service.botframework.model.ConversationDTO;
import ru.alegor.skypebot.service.plugins.event.ContactAddedEvent;
import ru.alegor.skypebot.service.plugins.event.MessageRecievedEvent;
import ru.alegor.skypebot.service.plugins.event.UsersAddEvent;

import java.util.Collection;
import java.util.Collections;

@Component
@Slf4j
public class InfoPlugin extends AbstractPlugin implements UsersAddEvent, MessageRecievedEvent, ContactAddedEvent {

    private static final String pluginName = "info";

    @Override
    protected void init() {
        super.init();
        log.info("Плагин {} инициализирован, {} команд", this.getPluginName());
    }

    public InfoPlugin() {
        super(pluginName);
    }

    @Override
    public void messageReceived(ActivityDTO context) {
        log.debug("Реакция на сообщение {}", context.getText());
        ActivityDTO reply = ActivityBuilder.buildMessageActivity()
                .setConversation(context.getConversation())
                .setFrom(context.getRecipient())
                .setLocale(context.getLocale())
                .setRecipient(context.getFrom())
                .setText("Важная информация: " + context.getText())
                .get();
        botFrameworkService.sendMessage(context.getServiceUrl(), reply);
    }

    @Override
    public void onUsersAdd(ActivityDTO context,
                           ConversationAccountDTO conversation, Collection<ChannelAccountDTO> users) {
        for (ChannelAccountDTO user : users) {
            sendWelcomeMessage(context, conversation, user);
        }
    }

    @Override
    public void onContactAdded(ActivityDTO activity) {
        log.debug("Создаем диалог с новым пользователем");
        ConversationDTO newConversation = new ConversationDTO();
        newConversation.setBot(activity.getRecipient());
        newConversation.setMembers(Collections.singleton(activity.getFrom()));
        newConversation.setTopicName(activity.getRecipient().getName());
        newConversation.setGroup(false);
        String id = botFrameworkService.createConversation(activity.getServiceUrl(), newConversation);
        //Send welcome message
        sendWelcomeMessage(activity, new ConversationAccountDTO(id), activity.getFrom());
    }

    private void sendWelcomeMessage(ActivityDTO activity, ConversationAccountDTO conversation, ChannelAccountDTO user) {
        log.debug("Приветствуем пользователя {}", user.getName());
        ActivityDTO welcome = ActivityBuilder.buildMessageActivity()
                .setConversation(conversation)
                .setFrom(activity.getRecipient())
                .setRecipient(user)
                .setText("Дороу " + user.getName())
                .get();
        botFrameworkService.sendMessage(activity.getServiceUrl(), welcome);
    }
}
