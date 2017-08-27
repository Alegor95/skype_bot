package ru.alegor.skypebot.service.botframework.model;

import lombok.Data;

@Data
public class CardActionDTO {

    private String type;
    private String title;
    private String image;
    private String value;

}
