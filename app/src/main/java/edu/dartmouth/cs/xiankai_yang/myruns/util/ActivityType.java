package edu.dartmouth.cs.xiankai_yang.myruns.util;

/**
 * Created by yangxk15 on 2/1/17.
 */

public enum ActivityType {
    RUNNING("Running"),
    WALKING("Walking"),
    STANDING("Standing"),
    CYCLING("Cycling"),
    HIKING("Hiking"),
    DOWNHILL_SKIING("Downhill Skiing"),
    CROSS_COUNTRY_SKIING("Cross-Country Skiing"),
    SNOWBOARDING("Snowboarding"),
    SKATING("Skating"),
    SWIMMING("Swimming"),
    MOUNTAIN_BIKING("Mountain Biking"),
    WHEELCHAIR("Wheelchair"),
    ELLIPTICAL("Elliptical"),
    OTHER("Other");

    String text;
    private ActivityType(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }


    public static String[] getTypes() {
        ActivityType[] activityTypes = ActivityType.values();
        String[] types = new String[activityTypes.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = activityTypes[i].toString();
        }
        return types;
    }

    public static ActivityType fromString(String s) {
        for (ActivityType activityType : ActivityType.values()) {
            if (activityType.toString().equals(s)) {
                return activityType;
            }
        }
        return null;
    }
}
