package ru.alegor.skypebot.model.configuration;

import lombok.Data;

import java.util.Collection;

@Data
public class NodeSetDTO extends NodeDTO {

    private String legend;
    private Collection<NodeDTO> nodes;

    public NodeSetDTO(String legend, Collection<NodeDTO> nodes) {
        super(NodeType.SET);
        this.legend = legend;
        this.nodes = nodes;
    }

}
