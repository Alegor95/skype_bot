package ru.alegor.skypebot.model.configuration;

import lombok.Data;

import java.util.Collection;

@Data
public class EditableCollectionDTO extends NodeDTO {

    private Collection<NodeDTO> nodes;
    private NodeDTO emptyNode;

    public EditableCollectionDTO() {
        super(NodeType.EDITABLE_COLLECTION);
    }
}
