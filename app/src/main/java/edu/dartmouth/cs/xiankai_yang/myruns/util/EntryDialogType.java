package edu.dartmouth.cs.xiankai_yang.myruns.util;

/**
 * Created by yangxk15 on 2/6/17.
 */

public enum EntryDialogType {
    DATE("Date"),
    TIME("Time"),
    DURATION("Duration"),
    DISTANCE("Distance"),
    CALORIES("Calories"),
    HEART_RATE("HeartRate"),
    COMMENT("Comment");

    private String text;

    private EntryDialogType(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static String[] getTypes() {
        EntryDialogType[] entryDialogTypes = EntryDialogType.values();
        String[] types = new String[entryDialogTypes.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = entryDialogTypes[i].toString();
        }
        return types;
    }

    public static EntryDialogType fromString(String s) {
        for (EntryDialogType entryDialogType : EntryDialogType.values()) {
            if (entryDialogType.toString().equals(s)) {
                return entryDialogType;
            }
        }
        return null;
    }


}
