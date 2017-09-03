package ru.alegor.skypebot.service.plugins;

import ru.alegor.skypebot.model.configuration.NodeDTO;

public interface Configurable {
    NodeDTO getConfiguration();
    void applyConfiguration(NodeDTO rootNode);
}
