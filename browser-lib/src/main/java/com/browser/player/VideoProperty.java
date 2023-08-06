package com.browser.player;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class VideoProperty implements Cloneable, Serializable {

    private String videoId;
    private String videoTitle;
    private String description;
    private int videoTime = 0;
    private int videoDuration = 0;
    private int isFavourite = 0;
    private String channelId;
    private boolean isAutoPlayVideo = true;

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVideoTime() {
        return videoTime;
    }

    public void setVideoTime(int videoTime) {
        this.videoTime = videoTime;
    }

    public int getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(int videoDuration) {
        this.videoDuration = videoDuration;
    }

    public int getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(int isFavourite) {
        this.isFavourite = isFavourite;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public boolean isAutoPlayVideo() {
        return isAutoPlayVideo;
    }

    public void setAutoPlayVideo(boolean autoPlayVideo) {
        isAutoPlayVideo = autoPlayVideo;
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public VideoProperty getClone() {
        try {
            return (VideoProperty) clone();
        } catch (CloneNotSupportedException e) {
            return new VideoProperty();
        }
    }
}
