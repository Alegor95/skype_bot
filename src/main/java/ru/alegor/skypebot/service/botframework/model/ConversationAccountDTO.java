package ru.alegor.skypebot.service.botframework.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConversationAccountDTO {

    private String id;
    private boolean isGroup;
    private String name;

    public ConversationAccountDTO(String id) {
        this.id = id;
    }

}
