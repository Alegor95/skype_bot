package ru.alegor.skypebot.model.configuration;

import lombok.Data;

@Data
public class InputDTO extends NodeDTO {

    public enum Type {
        TEXT,
        CHECKBOX,
        TEXTAREA
    }

    private String label;
    private String name;
    private Type type;
    private String value;
    private String tip;

    public static InputBuilder builder() {
        return new InputBuilder();
    }

    protected InputDTO() {
        super(NodeType.INPUT);
    }

    public static class InputBuilder {

        private InputDTO obj;

        public InputBuilder setLabel(String label) {
            obj.setLabel(label);
            return this;
        }

        public InputBuilder setName(String name) {
            obj.setName(name);
            return this;
        }

        public InputBuilder setType(Type type) {
            obj.setType(type);
            return this;
        }

        public InputBuilder setValue(String value) {
            obj.setValue(value);
            return this;
        }

        public InputBuilder setTip(String tip) {
            obj.setTip(tip);
            return this;
        }

        public InputDTO get() {
            return obj;
        }

        InputBuilder() {
            this.obj = new InputDTO();
        }
    }
}
