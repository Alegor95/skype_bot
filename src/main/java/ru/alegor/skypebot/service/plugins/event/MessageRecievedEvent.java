package ru.alegor.skypebot.service.plugins.event;

import ru.alegor.skypebot.service.botframework.model.ActivityDTO;

public interface MessageRecievedEvent {
    void messageReceived(ActivityDTO activity, String pureText);
}
