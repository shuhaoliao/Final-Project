package com.rasalghul.finalproject.Bean;

public class PostVideoResponse {
    // url: (POST) http://test.androidcamp.bytedance.com/mini_douyin/invoke/video?student_id={student_id}&user_name={user_name}
    // body
    // + cover_image，file
    // + video, file
    // response
    // {
    //    "result": {},
    //    "url": "https://lf1-hscdn-tos.pstatp
    //    .com/obj/developer-baas/baas/tt7217xbo2wz3cem41/a8efa55c5c22de69_1560563154288.mp4",
    //    "success": true
    //}
    private Object result;
    private String url;
    private boolean success;

    public Object getResult(){return result;}
    public String getUrl(){return url;}
    public boolean getSuccess(){return success;}
}
