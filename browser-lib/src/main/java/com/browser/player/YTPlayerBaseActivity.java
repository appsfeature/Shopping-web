package com.browser.player;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.browser.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * @author Created by Abhijit on 25/06/2018.
 */
public abstract class YTPlayerBaseActivity extends AppCompatActivity implements YTPLayerListener{

    private static final long DELAY_TIME = 4000;
    protected String mVideoId;
    protected String mTitle = "Player";
    protected String mVideoTitle;

    public int mVideoTime;
    private boolean isAutoPlay;
    private boolean isFullScreen;
    private boolean isMute;
    private PlayerConstants.PlayerState videoPlayerState = PlayerConstants.PlayerState.UNKNOWN;
    private boolean isSeekBarActive;

    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayerTracker tracker;
    private FrameLayout fullscreenViewContainer;
    private View fullScreenContainer;
    @Nullable
    private YouTubePlayer youTubePlayer;
    private View actionBar;
    private SeekBar sbProgress, sbProgress2;
    private View llControls, llControls2;
    private TextView tvDuration, tvDuration2;
    private ImageView ivPlayPause, ivPlayPause2;
    private View ivPlaceholder, ivPlaceholder2;
    private ProgressBar pbAutoPLay;
    private ImageView ivVolume, ivVolume2;
    protected View menuFullScreen, menuShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setVideoDetail(String videoId, int videoTime, String videoTitle, boolean isAutoPlay) {
        this.mVideoId = videoId;
        this.mVideoTime = videoTime;
        this.mVideoTitle = videoTitle;
        this.isAutoPlay = isAutoPlay;
    }

    public void loadVideo(int videoTime) {
        this.mVideoTime = videoTime;
        initToolBarTheme(mTitle);
        applyYoutubeSetting();
        updateUi();
    }

    private void updateUi() {
        onUpdateUI();
        if(isAutoPlay){
            pbAutoPLay.setVisibility(View.VISIBLE);
        }else {
            pbAutoPLay.setVisibility(View.GONE);
        }
    }

    @Override
    public void initView() {
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        pbAutoPLay = findViewById(R.id.pb_auto_play);
        View actionArea = findViewById(R.id.action_area);
        View actionArea2 = findViewById(R.id.action_area2);
        ivPlaceholder = findViewById(R.id.iv_placeholder);
        ivPlaceholder2 = findViewById(R.id.iv_placeholder2);
        sbProgress = findViewById(R.id.sb_progress);
        sbProgress2 = findViewById(R.id.sb_progress2);
        llControls = findViewById(R.id.ll_controls);
        llControls2 = findViewById(R.id.ll_controls2);
        ivPlayPause = findViewById(R.id.iv_play_pause);
        ivPlayPause2 = findViewById(R.id.iv_play_pause2);
        ivVolume = findViewById(R.id.iv_volume);
        ivVolume2 = findViewById(R.id.iv_volume2);
        tvDuration = findViewById(R.id.tv_duration);
        tvDuration2 = findViewById(R.id.tv_duration2);
        fullScreenContainer = findViewById(R.id.full_screen_container);
        fullscreenViewContainer = findViewById(R.id.full_screen_view_container);
        getLifecycle().addObserver(youTubePlayerView);

        sbProgress.setProgress(0);
        sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if(youTubePlayer!= null && isSeekBarActive){
                    updateTimeLine(progress);
                    youTubePlayer.seekTo(progress);
                    startPanelAutoHide();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarActive = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekBarActive = false;
            }
        });
        sbProgress2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if(youTubePlayer!= null && isSeekBarActive){
                    updateTimeLine(progress);
                    youTubePlayer.seekTo(progress);
                    startPanelAutoHide();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarActive = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekBarActive = false;
            }
        });
        ivPlayPause.setOnClickListener(view -> {
            if(youTubePlayer != null) {
                if (videoPlayerState == PlayerConstants.PlayerState.PLAYING) {
                    youTubePlayer.pause();
                } else if (videoPlayerState == PlayerConstants.PlayerState.ENDED) {
                    youTubePlayer.loadVideo(mVideoId, 0);
                } else {
                    youTubePlayer.play();
                }
            }
            updatePlayControl();
        });
        ivPlayPause2.setOnClickListener(view -> ivPlayPause.performClick());
        ivPlaceholder.setOnClickListener(view -> {
            pbAutoPLay.setVisibility(View.VISIBLE);
            isAutoPlay = true;
            playVideo();
        });
        ivPlaceholder2.setOnClickListener(view -> {
            isAutoPlay = true;
            playVideo();
        });
        ivVolume.setOnClickListener(view -> applyMute());
        ivVolume2.setOnClickListener(view -> applyMute());
        actionArea.setOnClickListener(view -> {
            ivPlayPause.performClick();
            startPanelAutoHide();
        });
        actionArea2.setOnClickListener(view -> {
            ivPlayPause.performClick();
            startPanelAutoHide();
        });
    }

    private void applyMute() {
        if(youTubePlayer != null) {
            isMute = !isMute;
            if (isMute) {
                youTubePlayer.mute();
                ivVolume.setImageResource(R.drawable.yt_ic_volume_off);
                ivVolume2.setImageResource(R.drawable.yt_ic_volume_off);
            } else {
                youTubePlayer.unMute();
                ivVolume.setImageResource(R.drawable.yt_ic_volume_on);
                ivVolume2.setImageResource(R.drawable.yt_ic_volume_on);
            }
        }
        startPanelAutoHide();
    }

    private void startPanelAutoHide() {
        llControls.setVisibility(View.VISIBLE);
        llControls2.setVisibility(View.VISIBLE);
        getHandler().removeCallbacksAndMessages(null);
        getHandler().postDelayed(() -> {
            llControls.setVisibility(View.GONE);
            llControls2.setVisibility(View.GONE);
        }, DELAY_TIME);
    }

    private void startPanelVisibleAlways() {
        llControls.setVisibility(View.VISIBLE);
        llControls2.setVisibility(View.VISIBLE);
        getHandler().removeCallbacksAndMessages(null);
    }

    private Handler mHandler;

    private Handler getHandler() {
        if(mHandler == null){
            mHandler = new Handler(Looper.myLooper());
        }
        return mHandler;
    }

    private void updatePlayControl() {
        if(videoPlayerState == PlayerConstants.PlayerState.PLAYING){
            ivPlayPause.setImageResource(R.drawable.yt_ic_pause);
            ivPlayPause2.setImageResource(R.drawable.yt_ic_pause);
            ivPlaceholder.setVisibility(View.GONE);
            ivPlaceholder2.setVisibility(View.GONE);
            startPanelAutoHide();
        }else if(videoPlayerState == PlayerConstants.PlayerState.ENDED){
            ivPlayPause.setImageResource(R.drawable.yt_ic_reload);
            ivPlayPause2.setImageResource(R.drawable.yt_ic_reload);
            ivPlaceholder.setVisibility(View.VISIBLE);
            ivPlaceholder2.setVisibility(View.VISIBLE);
            pbAutoPLay.setVisibility(View.GONE);
            startPanelVisibleAlways();
        }else {
            ivPlayPause.setImageResource(R.drawable.yt_ic_play);
            ivPlayPause2.setImageResource(R.drawable.yt_ic_play);
            startPanelVisibleAlways();
        }
    }


    private void applyYoutubeSetting() {
        tracker = new YouTubePlayerTracker();

        getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
//        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
//            @Override
//            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
//                loadVideo(youTubePlayer, true);
//            }
//        });
        youTubePlayerView.setEnableAutomaticInitialization(false);
        setPlayerOption();

        youTubePlayerView.getYouTubePlayerWhenReady(youTubePlayer -> {
            youTubePlayer.addListener(tracker);
//            PlayerConstants.PlayerState state = tracker.getState();
//            float currentSecond = getCurrentPlayedTime();
//            float duration = getVideoDuration();
        });

        youTubePlayerView.addFullscreenListener(new FullscreenListener() {
            @Override
            public void onEnterFullscreen(@NonNull View fullscreenView, @NonNull Function0<Unit> exitFullscreen) {
                isFullScreen = true;
                youTubePlayerView.setVisibility(View.GONE);
                fullScreenContainer.setVisibility(View.VISIBLE);
                fullscreenViewContainer.addView(fullscreenView);
                hideActionBar();
            }

            @Override
            public void onExitFullscreen() {
                isFullScreen = false;
                youTubePlayerView.setVisibility(View.VISIBLE);
                fullScreenContainer.setVisibility(View.GONE);
                fullscreenViewContainer.removeAllViews();
                showActionBar();
            }
        });
    }


    public void initToolBarTheme(String title) {
        actionBar = findViewById(R.id.layout_action_bar);
        menuFullScreen = findViewById(R.id.iv_action_full_screen);
        menuShare = findViewById(R.id.iv_action_share);
        TextView tvTitle = findViewById(R.id.tv_titile);
        tvTitle.setText(title);
        (findViewById(R.id.iv_action_back)).setOnClickListener(v -> onBackPressed());
        menuFullScreen.setOnClickListener(v -> {
            if(youTubePlayer != null) {
                youTubePlayer.toggleFullscreen();
            }
        });
        menuShare.setOnClickListener(v -> onShareVideo(mVideoId, mVideoTitle));
    }

    @Override
    public void onBackPressed() {
        if(isFullScreen){
            if(youTubePlayer != null) {
                youTubePlayer.toggleFullscreen();
            }
        } else {
            closeYoutubePlayer();
            super.onBackPressed();
        }
    }

    private void closeYoutubePlayer() {
        try {
            if (youTubePlayer != null) {
                youTubePlayer.pause();
                onCloseYoutubePlayer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @return current played time in sec.
     */
    protected int getCurrentPlayedTime() {
        if(youTubePlayer != null){
            return (int) tracker.getCurrentSecond();
        }
        return 0;
    }

    protected int getVideoDuration() {
        if(youTubePlayer != null){
            return (int) tracker.getVideoDuration();
        }
        return 0;
    }

    private void setPlayerOption() {
        IFramePlayerOptions options = new IFramePlayerOptions.Builder()
                .controls(0)
                .fullscreen(0)
                .rel(0)
                .ivLoadPolicy(3)
                .ccLoadPolicy(1)
                .build();
        youTubePlayerView.initialize(listener, options);
    }

    YouTubePlayerListener listener = new AbstractYouTubePlayerListener() {
        @Override
        public void onReady(@NonNull YouTubePlayer youTubePlayer) {
            YTPlayerBaseActivity.this.youTubePlayer = youTubePlayer;
            loadVideo(youTubePlayer, isAutoPlay);
            onPlayerReady(youTubePlayer, mVideoId);
            addPlayerListener(youTubePlayer);
            new Handler().postDelayed(() -> {
                ivPlaceholder.setEnabled(true);
                ivPlaceholder2.setEnabled(true);
            }, 800);
            checkIsAutoPlayed();
        }
    };

    private void checkIsAutoPlayed() {
        new Handler().postDelayed(() -> {
            if(isAutoPlay){
                if(videoPlayerState != PlayerConstants.PlayerState.PLAYING){
                    if (youTubePlayer != null) {
                        youTubePlayer.play();
                    }
                    checkIsAutoPlayed();
                }
            }
        }, 1000);
    }


    private void playVideo() {
//        if(youTubePlayer != null){
//            youTubePlayer.loadVideo(mVideoId, mVideoTime);
//        }
        checkIsAutoPlayed();
    }

    private void addPlayerListener(YouTubePlayer youTubePlayer) {
        youTubePlayer.addListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onApiChange(@NonNull YouTubePlayer youTubePlayer) {
                super.onApiChange(youTubePlayer);
            }

            @Override
            public void onCurrentSecond(@NonNull YouTubePlayer youTubePlayer, float second) {
                super.onCurrentSecond(youTubePlayer, second);
                updateProgress((int)second);
                updateTimeLine((int)second);
            }

            @Override
            public void onError(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError error) {
                super.onError(youTubePlayer, error);
            }

            @Override
            public void onPlaybackQualityChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlaybackQuality playbackQuality) {
                super.onPlaybackQualityChange(youTubePlayer, playbackQuality);
            }

            @Override
            public void onPlaybackRateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlaybackRate playbackRate) {
                super.onPlaybackRateChange(youTubePlayer, playbackRate);
            }

            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
            }

            @Override
            public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
                super.onStateChange(youTubePlayer, state);
                videoPlayerState = state;
                updatePlayControl();
            }

            @Override
            public void onVideoDuration(@NonNull YouTubePlayer youTubePlayer, float duration) {
                super.onVideoDuration(youTubePlayer, duration);
                sbProgress.setMax((int)duration);
                sbProgress2.setMax((int)duration);
                showYoutubeControls();
            }

            @Override
            public void onVideoId(@NonNull YouTubePlayer youTubePlayer, @NonNull String videoId) {
                super.onVideoId(youTubePlayer, videoId);
            }

            @Override
            public void onVideoLoadedFraction(@NonNull YouTubePlayer youTubePlayer, float loadedFraction) {
                super.onVideoLoadedFraction(youTubePlayer, loadedFraction);
            }
        });
    }

    private void updateProgress(int second) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sbProgress.setProgress(second, true);
            sbProgress2.setProgress(second, true);
        }else {
            sbProgress.setProgress(second);
            sbProgress2.setProgress(second);
        }
    }

    private void updateTimeLine(int totalSecs) {
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;
        String timeString;
        if(hours > 0){
            timeString = String.format(Locale.ENGLISH, "%d:%02d:%02d / %s", hours, minutes, seconds, getTotalDuration());
        }else {
            timeString = String.format(Locale.ENGLISH, "%02d:%02d / %s", minutes, seconds, getTotalDuration());
        }
        tvDuration.setText(timeString);
        tvDuration2.setText(timeString);
    }

    private String mTotalDuration = "";
    private String getTotalDuration() {
        if(mTotalDuration.isEmpty()) {
            int totalSecs = getVideoDuration();
            if (totalSecs > 0) {
                int hours = totalSecs / 3600;
                int minutes = (totalSecs % 3600) / 60;
                int seconds = totalSecs % 60;
                if(hours > 0) {
                    mTotalDuration = String.format(Locale.ENGLISH, "%d:%02d:%02d", hours, minutes, seconds);
                }else {
                    mTotalDuration = String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds);
                }
            }
        }
        return mTotalDuration;
    }

    private void showYoutubeControls() {
        sbProgress.setVisibility(View.VISIBLE);
        sbProgress2.setVisibility(View.VISIBLE);
    }


    private void loadVideo(YouTubePlayer youTubePlayer, boolean isAutoPlay) {
        if(isAutoPlay) {
            youTubePlayer.loadVideo(mVideoId, mVideoTime);
        }else {
            youTubePlayer.cueVideo(mVideoId, mVideoTime);
        }
    }

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (isFullScreen) {
                // if the player is in fullscreen, exit fullscreen
                youTubePlayer.toggleFullscreen();
            } else {
                finish();
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        youTubePlayerView.release();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (youTubePlayer != null) {
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                if(isFullScreen){
                    youTubePlayer.toggleFullscreen();
                }
            } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if(!isFullScreen){
                    youTubePlayer.toggleFullscreen();
                }
            }
        }
    }

    private void showActionBar() {
        if(actionBar != null) {
            actionBar.setVisibility(View.VISIBLE);
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void hideActionBar() {
        if(actionBar != null) {
            actionBar.setVisibility(View.GONE);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    public abstract void onUpdateUI();

    public abstract void onPlayerReady(YouTubePlayer youTubePlayer, String mVideoId);

    public abstract void onShareVideo(String mVideoId, String mVideoTitle);

    public abstract void onCloseYoutubePlayer();
}
