package edu.dartmouth.cs.xiankai_yang.myruns.backend.data;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by yangxk15 on 1/31/17.
 */

@Entity
public class ExerciseEntry {

    @Id String id;

    String inputType;
    String activityType;
    String dateTime;
    String duration;
    String distance;
    String avgPace;
    String avgSpeed;
    String calorie;
    String climb;
    String heartRate;
    String comment;

    public ExerciseEntry() {
    }

    public ExerciseEntry(String id, String inputType, String activityType, String dateTime,
                         String duration, String distance, String avgPace, String avgSpeed,
                         String calorie, String climb, String heartRate, String comment) {
        this.id = id;
        this.inputType = inputType;
        this.activityType = activityType;
        this.dateTime = dateTime;
        this.duration = duration;
        this.distance = distance;
        this.avgPace = avgPace;
        this.avgSpeed = avgSpeed;
        this.calorie = calorie;
        this.climb = climb;
        this.heartRate = heartRate;
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public String getInputType() {
        return inputType;
    }

    public String getActivityType() {
        return activityType;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getDuration() {
        return duration;
    }

    public String getDistance() {
        return distance;
    }

    public String getAvgPace() {
        return avgPace;
    }

    public String getAvgSpeed() {
        return avgSpeed;
    }

    public String getCalorie() {
        return calorie;
    }

    public String getClimb() {
        return climb;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public String getComment() {
        return comment;
    }
}
