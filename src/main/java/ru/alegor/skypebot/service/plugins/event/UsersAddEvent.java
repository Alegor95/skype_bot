package ru.alegor.skypebot.service.plugins.event;

import ru.alegor.skypebot.service.botframework.model.ActivityDTO;
import ru.alegor.skypebot.service.botframework.model.ChannelAccountDTO;
import ru.alegor.skypebot.service.botframework.model.ConversationAccountDTO;

import java.util.Collection;

/**
 * Интерфейс, позволяющий подписаться на добавление пользователя в диалог.
 */
public interface UsersAddEvent {
    void onUsersAdd(ActivityDTO context, ConversationAccountDTO conversation, Collection<ChannelAccountDTO> users);
}
