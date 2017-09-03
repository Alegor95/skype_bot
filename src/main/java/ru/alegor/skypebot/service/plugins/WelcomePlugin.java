package ru.alegor.skypebot.service.plugins;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.alegor.skypebot.model.configuration.InputDTO;
import ru.alegor.skypebot.model.configuration.NodeDTO;
import ru.alegor.skypebot.service.botframework.ActivityBuilder;
import ru.alegor.skypebot.service.botframework.model.ActivityDTO;
import ru.alegor.skypebot.service.botframework.model.ChannelAccountDTO;
import ru.alegor.skypebot.service.botframework.model.ConversationAccountDTO;
import ru.alegor.skypebot.service.botframework.model.ConversationDTO;
import ru.alegor.skypebot.service.plugins.event.ContactAddedEvent;
import ru.alegor.skypebot.service.plugins.event.UsersAddEvent;

import javax.xml.soap.Node;
import java.util.Collection;
import java.util.Collections;

@Component
@Slf4j
public class WelcomePlugin extends AbstractPlugin implements Configurable, UsersAddEvent, ContactAddedEvent {

    private final String userPlaceholder = "{username}";
    private String welcomeText = "Приветствую пользователя "+userPlaceholder+"!";
    private final String inputName = "welcome";

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
                .setText(welcomeText.replace(userPlaceholder, user.getName()))
                .setReplyToId(activity.getId())
                .get();
        botFrameworkService.sendReplyMessage(activity.getServiceUrl(), welcome);
    }

    @Override
    public NodeDTO getConfiguration() {
        return InputDTO.builder()
                .setLabel("Текст приветствия:")
                .setName(inputName)
                .setTip("Для ссылки на имя пользователя используйте " + userPlaceholder)
                .setType(InputDTO.Type.TEXTAREA)
                .setValue(welcomeText)
                .get();
    }

    @Override
    public void applyConfiguration(NodeDTO rootNode) {
        if (rootNode instanceof InputDTO) throw new IllegalArgumentException("Конфигурация должна состоять из InputDTO");
        InputDTO input = (InputDTO)rootNode;
        if (!inputName.equals(input.getName())) throw new IllegalArgumentException("Некорректное имя инпута");
        this.welcomeText = input.getValue();
    }
}
