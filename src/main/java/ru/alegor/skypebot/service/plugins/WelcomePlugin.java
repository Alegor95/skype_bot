package ru.alegor.skypebot.service.plugins;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.alegor.skypebot.service.botframework.ActivityBuilder;
import ru.alegor.skypebot.service.botframework.model.ActivityDTO;
import ru.alegor.skypebot.service.botframework.model.ChannelAccountDTO;
import ru.alegor.skypebot.service.botframework.model.ConversationAccountDTO;
import ru.alegor.skypebot.service.botframework.model.ConversationDTO;
import ru.alegor.skypebot.service.plugins.event.ContactAddedEvent;
import ru.alegor.skypebot.service.plugins.event.UsersAddEvent;

import java.util.Collection;
import java.util.Collections;

@Component
@Slf4j
public class WelcomePlugin extends AbstractPlugin implements UsersAddEvent, ContactAddedEvent {

    public WelcomePlugin() {
        super("welcome");
    }

    @Override
    public void onUsersAdd(ActivityDTO context,
                           ConversationAccountDTO conversation, Collection<ChannelAccountDTO> users) {
        for (ChannelAccountDTO user : users) {
            //Игнорируем добавление пользователя, если это бот
            if (!user.equals(context.getRecipient())) sendWelcomeMessage(context, conversation, user);
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
                .setReplyToId(activity.getId())
                .get();
        botFrameworkService.sendReplyMessage(activity.getServiceUrl(), welcome);
    }
}
