package ru.alegor.skypebot.service.plugins.event;

import ru.alegor.skypebot.service.botframework.model.ActivityDTO;
import ru.alegor.skypebot.service.botframework.model.ChannelAccountDTO;
import ru.alegor.skypebot.service.botframework.model.ConversationAccountDTO;

import java.util.Collection;

/**
 * Интерфейс, позволяющий подписаться на удаление пользователя из диалога.
 */
public interface UserRemoveEvent {
    void onUsersRemove(ActivityDTO context, ConversationAccountDTO conversation, Collection<ChannelAccountDTO> users);
}
