package ru.alegor.skypebot.model.configuration;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static ru.alegor.skypebot.model.configuration.NodeDTO.NodeType.valueOf;

@Slf4j
public class NodeDeserializer extends StdDeserializer<NodeDTO> {

    public NodeDeserializer() {
        this(null);
    }

    public NodeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public NodeDTO deserialize(JsonParser jp, DeserializationContext deserializationContext)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        return parseNode(node, jp);
    }

    protected NodeDTO parseNode(JsonNode node, JsonParser jp ) throws JsonProcessingException {
        NodeDTO parsed =  null;
        NodeDTO.NodeType type = valueOf(node.get("nodeType").textValue());
        switch (type) {
            case INPUT: parsed = parseInput(node, jp); break;
            case SET: parsed = parseSet(node, jp); break;
            case EDITABLE_COLLECTION: parsed = parseCollection(node, jp); break;
            default: {
                log.error("Неподдерживаемый тип конфигурации: " + type.name());
                throw new JsonMappingException(jp, "Неподдерживаемый тип конфигурации: " + type.name());
            }
        }
        return parsed;
    }

    protected NodeDTO parseInput(JsonNode node, JsonParser jp) {
        return InputDTO.builder()
                .setLabel(node.get("label").textValue())
                .setName(node.get("name").textValue())
                .setTip(node.get("tip").textValue())
                .setType(InputDTO.Type.valueOf(node.get("type").textValue()))
                .setValue(node.get("value").textValue())
                .get();
    }

    protected NodeDTO parseSet(JsonNode node, JsonParser jp) throws JsonProcessingException {
        JsonNode nodes = node.get("nodes");
        List<NodeDTO> parsedNodes = new LinkedList<>();
        for (int i = 0; i < nodes.size(); i++) {
            parsedNodes.add(parseNode(nodes.get(i), jp));
        }
        return new NodeSetDTO(node.get("legend").textValue(), parsedNodes);
    }

    protected NodeDTO parseCollection(JsonNode node, JsonParser jp) throws JsonProcessingException {
        EditableCollectionDTO collectionDTO = new EditableCollectionDTO();
        collectionDTO.setEmptyNode(parseNode(node.get("emptyNode"), jp));
        JsonNode nodes = node.get("nodes");
        List<NodeDTO> parsedNodes = new LinkedList<>();
        for (int i = 0; i < nodes.size(); i++) {
            parsedNodes.add(parseNode(nodes.get(i), jp));
        }
        collectionDTO.setNodes(parsedNodes);
        return collectionDTO;
    }
}
