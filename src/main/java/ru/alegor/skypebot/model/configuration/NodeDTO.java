package ru.alegor.skypebot.model.configuration;

import lombok.Data;

@Data
public abstract class NodeDTO {

    protected enum NodeType {
        INPUT(InputDTO.class),
        SET(NodeSetDTO.class),
        EDITABLE_COLLECTION(EditableCollectionDTO.class);

        private final Class<? extends NodeDTO> dtoClass;

        NodeType(Class<? extends NodeDTO> dtoClass) {
            this.dtoClass = dtoClass;
        }
    }

    private final NodeType nodeType;

    public NodeDTO(NodeType nodeType) {
        if (!nodeType.dtoClass.equals(this.getClass())) {
            throw new IllegalArgumentException(String.format(
                    "Для класса %s нельзя использовать тип ячейки %s",
                    this.getClass().getSimpleName(), nodeType.name()
            ));
        }
        this.nodeType = nodeType;
    }
}
