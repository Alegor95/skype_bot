package ru.alegor.skypebot.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
public class InfoDTO {

    private String version;
    private Collection<String> plugins;

}
