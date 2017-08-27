package ru.alegor.skypebot.service.botframework.model;

import lombok.Data;

@Data
public class ConversationAccountDTO {

    private String id;
    private boolean isGroup;
    private String name;

}
