package ru.alegor.skypebot.model.configuration;

import lombok.Data;

import java.util.Collection;

@Data
public class NodeSetDTO extends NodeDTO {

    private String legend;
    private Collection<? extends NodeDTO> nodes;

    public NodeSetDTO(String legend, Collection<? extends NodeDTO> nodes) {
        super(NodeType.SET);
        this.legend = legend;
        this.nodes = nodes;
    }

}
