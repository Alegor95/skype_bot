package ru.alegor.skypebot.service.botframework.model;

import lombok.Data;

@Data
public class ConversationReferenceDTO {

    private String activityId;
    private ChannelAccountDTO bot;
    private String channelId;
    private ConversationAccountDTO conversation;
    private String serviceUrl;
    private ChannelAccountDTO user;
}
