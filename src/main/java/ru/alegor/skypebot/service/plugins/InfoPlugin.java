package ru.alegor.skypebot.service.plugins;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.alegor.skypebot.model.configuration.EditableCollectionDTO;
import ru.alegor.skypebot.model.configuration.InputDTO;
import ru.alegor.skypebot.model.configuration.NodeDTO;
import ru.alegor.skypebot.model.configuration.NodeSetDTO;
import ru.alegor.skypebot.service.botframework.ActivityBuilder;
import ru.alegor.skypebot.service.botframework.model.ActivityDTO;
import ru.alegor.skypebot.service.plugins.event.MessageRecievedEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InfoPlugin extends AbstractPlugin implements MessageRecievedEvent, Configurable {

    @Data
    @AllArgsConstructor
    public class InfoCommandDTO {
        private String command;
        private String description;
        private String text;
    }

    private static final String pluginName = "info";
    private final Map<String, InfoCommandDTO> commands;
    private final String defaultText;

    private final String commandInputName = "command";
    private final String descriptionInputName = "description";
    private final String textInputName = "text";

    @Override
    protected void init() {
        super.init();
        commands.values().forEach(_c -> this.botService.registerCommand(
                _c.getCommand(), _c.getDescription(), this.getClass()
        ));
        log.info("Плагин {} инициализирован, {} команд", this.getPluginName(), commands.size());
    }

    public InfoPlugin() {
        super(pluginName);
        defaultText = "Команда %s не распознана плагином!";
        commands = new HashMap<>();
        commands.put("redmine", new InfoCommandDTO("redmine", "информация о Redmine", "Ссылка на Redmine: [нажать](http://project.vistar.su/redmine/)"));
    }

    @Override
    public void messageReceived(ActivityDTO context, String pureText) {
        log.debug("Реакция на сообщение {}", context.getText());
        ActivityDTO reply = ActivityBuilder.buildMessageActivity()
                .setConversation(context.getConversation())
                .setFrom(context.getRecipient())
                .setLocale(context.getLocale())
                .setRecipient(context.getFrom())
                .setReplyToId(context.getId())
                .setText(commands.containsKey(pureText)
                        ? commands.get(pureText).getText()
                        : String.format(defaultText, pureText))
                .get();
        botFrameworkService.sendReplyMessage(context.getServiceUrl(), reply);
    }

    @Override
    public NodeDTO getConfiguration() {
        EditableCollectionDTO commandCollections = new EditableCollectionDTO();
        commandCollections.setEmptyNode(buildNodeSet("Новая команда", null, null, null));
        commandCollections.setNodes(commands.values().stream()
                .map(_ic -> buildNodeSet("Команда " + _ic.getCommand(), _ic.getCommand(), _ic.getDescription(), _ic.getText()))
                .collect(Collectors.toSet())
        );
        return commandCollections;
    }

    private NodeSetDTO buildNodeSet(String legend, String command, String description, String text) {
        Set<InputDTO> inputs = new HashSet<>();
        //Create command input
        inputs.add(InputDTO.builder()
                .setLabel("Команда")
                .setName(commandInputName)
                .setTip("Введите текст команды")
                .setType(InputDTO.Type.TEXT)
                .setValue(command)
                .get()
        );
        //Create description input
        inputs.add(InputDTO.builder()
                .setLabel("Описание команды")
                .setName(descriptionInputName)
                .setTip("Введите описание команды")
                .setType(InputDTO.Type.TEXT)
                .setValue(description)
                .get()
        );
        //Create output input :)
        inputs.add(InputDTO.builder()
                .setLabel("Вывод команды")
                .setName(textInputName)
                .setTip("Введите вывод команды")
                .setType(InputDTO.Type.TEXTAREA)
                .setValue(description)
                .get()
        );
        return new NodeSetDTO(legend, inputs);
    }

    @Override
    public void applyConfiguration(NodeDTO rootNode) {
        if (!(rootNode instanceof EditableCollectionDTO)) {
            throw new IllegalArgumentException("Конфигурация должна состоять из EditableCollectionDTO");
        }
        commands.keySet().forEach(_s -> this.botService.unregisterCommand(_s));
        commands.clear();
        EditableCollectionDTO commandCollection = (EditableCollectionDTO)rootNode;
        for (NodeDTO commandNode : commandCollection.getNodes()) {
            if (!(commandNode instanceof NodeSetDTO)) {
                throw new IllegalArgumentException("Конфигурация должна состоять из списка NodeSetDTO");
            }
            Map<String, String> fields = ((NodeSetDTO) commandNode).getNodes().stream()
                    .filter(_n -> _n instanceof InputDTO)
                    .map(_n -> (InputDTO)_n)
                    .collect(Collectors.toMap(_n -> _n.getName(), _n -> _n.getValue()));
            InfoCommandDTO command = new InfoCommandDTO(
                    fields.get(commandInputName),
                    fields.get(descriptionInputName),
                    fields.get(textInputName)
            );
            commands.put(command.getCommand(), command);
        }
        commands.values().forEach(_c -> this.botService.registerCommand(
                        _c.getCommand(), _c.getDescription(), this.getClass()
                ));
        log.info("Конфигурация обновлена, загружено {} команд", commands.size());
    }
}
