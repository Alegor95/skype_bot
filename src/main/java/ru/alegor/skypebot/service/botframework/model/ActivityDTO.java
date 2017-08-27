package ru.alegor.skypebot.service.botframework.model;

import lombok.Data;

import java.util.Collection;

@Data
public class ActivityDTO {

    private String id;
    private String action;
    private String attachmentLayout;
    private String channelId;
    private ConversationAccountDTO conversation;
    private ChannelAccountDTO from;
    private ChannelAccountDTO recipient;
    private boolean historyDisclosed;
    private String inputHint;
    private String locale;
    private String localTimestamp;
    private Collection<ChannelAccountDTO> membersAdded;
    private Collection<ChannelAccountDTO> membersRemoved;
    private ConversationReferenceDTO relatesTo;
    private String replyToId;
    private String serviceUrl;
    private String speak;
    private SuggestedActionsDTO suggestedActions;
    private String summary;
    private String text;
    private String textFormat;
    private String timestamp;
    private String topicName;
    private String type;
}
