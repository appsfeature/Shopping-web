package com.browser.player;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.browser.BrowserSdk;
import com.browser.R;
import com.browser.util.BrowserConstant;
import com.browser.util.BrowserUtil;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;

public class YTPlayerActivity extends YTPlayerBaseActivity {

    protected VideoProperty extraProperty;

    private TextView tvTitle, tvDescription;
    private String mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yt_activity_youtube_player);
        getArguments(getIntent());
    }

    private void getArguments(Intent intent) {
        if (intent.getSerializableExtra(BrowserConstant.EXTRA_PROPERTY) instanceof VideoProperty) {
            extraProperty = (VideoProperty) intent.getSerializableExtra(BrowserConstant.EXTRA_PROPERTY);
            if(extraProperty != null && extraProperty.getVideoId() != null){
                mTitle = extraProperty.getVideoTitle();
                mDescription = extraProperty.getDescription();
                setVideoDetail(extraProperty.getVideoId(), extraProperty.getVideoTime(), extraProperty.getVideoTitle(), extraProperty.isAutoPlayVideo());
                initView();
                loadDetail();
                loadVideoFromDB(extraProperty.getVideoId(), extraProperty.getVideoTime());
            }else {
                BrowserSdk.showToast(this, BrowserConstant.INVALID_PROPERTY);
                finish();
            }
        } else {
            BrowserSdk.showToast(this, BrowserConstant.INVALID_PROPERTY);
            finish();
        }
    }

    public void loadVideoFromDB(String videoId, int videoTime) {
//        if(videoTime <= 0) {
//            getVideoDurationFromDB(videoId, new Response.Status<DMVideo>() {
//                @Override
//                public void onSuccess(DMVideo response) {
//                    if(response != null) {
//                        loadVideo(response.getVideoTime());
//                    }else {
//                        loadVideo(videoTime);
//                    }
//                }
//            });
//        }else {
            loadVideo(videoTime);
//        }
    }

//    private void getVideoDurationFromDB(String videoId, Response.Status<DMVideo> callback) {
//        DMDataManager.get(this).getVideoDetail(videoId, new Response.Status<DMVideo>() {
//            @Override
//            public void onSuccess(DMVideo response) {
//                callback.onSuccess(response);
//            }
//        });
//    }

    private void loadDetail() {
        if(tvTitle != null){
            tvTitle.setText(mTitle);
            tvTitle.setVisibility(TextUtils.isEmpty(mTitle) ? View.GONE : View.VISIBLE);
        }
        if(tvDescription != null){
            tvDescription.setText(BrowserUtil.fromHtml(mDescription));
            tvDescription.setVisibility(TextUtils.isEmpty(mDescription) ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void initView() {
        super.initView();
        tvTitle = findViewById(R.id.tv_title);
        tvDescription = findViewById(R.id.tv_description);
    }

    @Override
    public void onUpdateUI() {
        if (menuShare != null) {
            menuShare.setVisibility(View.GONE);
        }
    }


    @Override
    public void onPlayerReady(YouTubePlayer youTubePlayer, String mVideoId) {

    }

    @Override
    public void onShareVideo(String mVideoId, String mVideoTitle) {
//        new DynamicUrlCreator(YTPlayerActivity.this)
//                .shareVideo(mVideoId, extraProperty, mVideoTitle);
    }

    @Override
    public void onCloseYoutubePlayer() {
//        DMVideo mVideo = new DMVideo();
//        mVideo.setVideoId(mVideoId);
//        mVideo.setCatId(extraProperty.getId());
//        mVideo.setVideoTime(getCurrentPlayedTime());
//        mVideo.setVideoDuration(getVideoDuration());
//        DMDataManager.get(this).insertVideo(mVideo);
    }

}
