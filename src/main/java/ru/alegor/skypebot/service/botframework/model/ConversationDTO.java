package ru.alegor.skypebot.service.botframework.model;

import lombok.Data;

import java.util.Collection;

@Data
public class ConversationDTO {

    private String id;
    private String topicName;
    private boolean isGroup;
    private ChannelAccountDTO bot;
    private Collection<ChannelAccountDTO> members;

}
