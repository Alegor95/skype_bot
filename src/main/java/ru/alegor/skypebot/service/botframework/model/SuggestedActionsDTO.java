package ru.alegor.skypebot.service.botframework.model;

import lombok.Data;

import java.util.Collection;

@Data
public class SuggestedActionsDTO {

    private Collection<String> to;
    private Collection<CardActionDTO> actions;
}
