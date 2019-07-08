package com.rasalghul.finalproject.Bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FeedResponse {
    @SerializedName("feeds") private List<Feed> feeds;
    @SerializedName("success") private String success;

    public List<Feed> getFeeds() {
        return feeds;
    }

    public String getSuccess(){
        return success;
    }

    @Override
    public String toString() {
        return feeds.toString()+"\n";
    }
}
