package edu.dartmouth.cs.xiankai_yang.myruns.util;

/**
 * Created by yangxk15 on 2/1/17.
 */

public enum InputType {
    MANUAL_ENTRY("Manual Entry"),
    GPS("GPS"),
    AUTOMATIC("Automatic");

    String text;
    InputType(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static String[] getTypes() {
        InputType[] inputTypes = InputType.values();
        String[] types = new String[inputTypes.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = inputTypes[i].toString();
        }
        return types;
    }

    public static InputType fromString(String s) {
        for (InputType inputType : InputType.values()) {
            if (inputType.toString().equals(s)) {
                return inputType;
            }
        }
        return null;
    }
}
