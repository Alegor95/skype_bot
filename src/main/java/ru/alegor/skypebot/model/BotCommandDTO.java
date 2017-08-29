package ru.alegor.skypebot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.alegor.skypebot.service.plugins.event.MessageRecievedEvent;

@Data
@AllArgsConstructor
public class BotCommandDTO<T extends MessageRecievedEvent> {
    private String command;
    private String description;
    private Class<T> commandOwner;
}
