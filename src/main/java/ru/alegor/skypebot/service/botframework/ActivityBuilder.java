package ru.alegor.skypebot.service.botframework;

import lombok.Getter;
import ru.alegor.skypebot.service.botframework.model.ActivityDTO;
import ru.alegor.skypebot.service.botframework.model.ChannelAccountDTO;
import ru.alegor.skypebot.service.botframework.model.ConversationAccountDTO;

public class ActivityBuilder {

    public enum Format {
        MARKDOWN("markdown"),
        PLAIN("plain"),
        /**
         * Supported only by Skype
         */
        XML("xml");

        @Getter
        private final String apiFormat;

        Format(String apiFormat) {
            this.apiFormat = apiFormat;
        }
    }

    public enum Type {
        MESSAGE("message"),
        CONVERSATION_TYPE("conversationUpdate"),
        CONTACT_RELATION_UPDATE("contactRelationUpdate"),
        TYPING("typing"),
        PING("ping"),
        DELETE_USER_DATA("deleteUserData"),
        END_OF_CONVERSATION("endOfConversation");

        @Getter
        private final String apiType;

        public Type getByApiType(String apiType) {
            for (Type type : Type.values()) {
                if (type.getApiType().equals(apiType)) {
                    return type;
                }
            }
            return null;
        }

        Type(String apiType) {
            this.apiType = apiType;
        }
    }

    private ActivityDTO object;

    public ActivityBuilder setTextFormat(Format format) {
        object.setTextFormat(format.getApiFormat());
        return this;
    }

    public ActivityBuilder setActivityType(Type type) {
        object.setType(type.getApiType());
        return this;
    }

    public ActivityBuilder setLocale(String locale) {
        object.setLocale(locale);
        return this;
    }

    public ActivityBuilder setText(String text) {
        object.setText(text);
        return this;
    }

    public ActivityBuilder setRecipient(ChannelAccountDTO recipient) {
        object.setRecipient(recipient);
        return this;
    }

    public ActivityBuilder setFrom(ChannelAccountDTO from) {
        object.setFrom(from);
        return this;
    }

    public ActivityBuilder setReplyToId(String id) {
        object.setReplyToId(id);
        return this;
    }

    public ActivityBuilder setConversation(ConversationAccountDTO conversation) {
        object.setConversation(conversation);
        return this;
    }

    public ActivityDTO get() {
        return this.object;
    }


    public static ActivityBuilder buildMessageActivity() {
        ActivityBuilder builder = new ActivityBuilder();
        return builder.setActivityType(Type.MESSAGE);
    }

    private ActivityBuilder() {
        object = new ActivityDTO();
    }

}
