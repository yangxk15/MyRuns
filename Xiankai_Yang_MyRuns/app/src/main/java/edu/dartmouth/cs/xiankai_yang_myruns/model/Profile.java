package edu.dartmouth.cs.xiankai_yang_myruns.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by yangxk15 on 1/9/17.
 */

@AllArgsConstructor(suppressConstructorProperties = true)
@Data
public class Profile {
    private String profile_name;
    private String profile_email;
    private String profile_phone;
    private int profile_gender;
    private String profile_class;
    private String profile_major;
    private String profile_image;
}
