package ru.alegor.skypebot.service.plugins.event;

import ru.alegor.skypebot.service.botframework.model.ActivityDTO;

public interface ContactAddedEvent {
    void onContactAdded(ActivityDTO activity);
}
