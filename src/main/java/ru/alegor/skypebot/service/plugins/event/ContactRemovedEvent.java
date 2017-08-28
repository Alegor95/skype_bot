package ru.alegor.skypebot.service.plugins.event;

import ru.alegor.skypebot.service.botframework.model.ActivityDTO;

public interface ContactRemovedEvent {
    void onContactRemoved(ActivityDTO activity);
}
