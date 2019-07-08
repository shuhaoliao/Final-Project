package com.rasalghul.finalproject.Bean;

public class Feed {
    // response
    // {
    //    "feeds":[
    //        {
    //            "student_id":"2220186666",
    //            "user_name":"doudou",
    //            "image_url":"https://sf6-hscdn-tos.pstatp
    //            .com/obj/developer-baas/baas/tt7217xbo2wz3cem41/9c6bbc2aa5355504_1560563154279
    //            .jpg",
    //            "_id":"5d044dd222e26f0024157401",
    //            "video_url":"https://lf1-hscdn-tos.pstatp
    //            .com/obj/developer-baas/baas/tt7217xbo2wz3cem41/a8efa55c5c22de69_1560563154288
    //            .mp4",
    //            "createdAt":"2019-06-15T01:45:54.368Z",
    //            "updatedAt":"2019-06-15T01:45:54.368Z",
    //        }
    //        ...
    //    ],
    //    "success":true
    //}

    private String student_id;
    private String user_name;
    private String image_url;
    private String _id;
    private String video_url;
    private String createdAt;
    private String updatedAt;
    private int __v;

    public String getStudent_id(){return student_id;}
    public String getUser_name(){return user_name;}
    public String getImage_url(){return image_url;}
    public String get_id(){return _id;}
    public String getVideo_url(){return video_url;}
    public String getCreatedAt(){return createdAt;}
    public String getUpdatedAt(){return updatedAt;}
    public int get__v(){return __v;}
}
