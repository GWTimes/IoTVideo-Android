package com.tencentcs.iotvideodemo.videoplayer;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory;
import com.google.android.exoplayer2.source.hls.HlsExtractorFactory;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.RandomTrackSelection;
import com.google.android.exoplayer2.ui.DebugTextViewHelper;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;

import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseActivity;

import androidx.annotation.Nullable;

public class ExoPlayerActivity extends BaseActivity {
    private static final String TAG = "ExoPlayerActivity";

    private PlayerView playerView;
    private TextView debugTextView;
    private SimpleExoPlayer exoPlayer;
    private DefaultDataSourceFactory mediaDataSourceFactory;
    private MediaSource mMediaSource;
    private DefaultTrackSelector trackSelector;
    private DebugTextViewHelper debugViewHelper;
    private String mFileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exoplayer);
        playerView = findViewById(R.id.exo_play_context_id);
        debugTextView = findViewById(R.id.debug_text_view);
        mFileUrl = getIntent().getStringExtra("URI");
        if (!TextUtils.isEmpty(mFileUrl)) {
            mFileUrl = mFileUrl.replace("https://", "http://");
        }
        LogUtils.i(TAG, "mFileUrl = " + mFileUrl);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || exoPlayer == null) {
            initializePlayer();
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
    }

    private void initializePlayer() {
        if (exoPlayer == null) {
            exoPlayer = new SimpleExoPlayer.Builder(this).build();
            exoPlayer.setPlayWhenReady(true);
            exoPlayer.addListener(new Player.EventListener() {
                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    LogUtils.e(TAG, "onPlayerError " + getErrorMessage(error).toString());
                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    LogUtils.i(TAG, "onPlayerStateChanged playWhenReady " + playWhenReady + " playbackState " + playbackState);
                }
            });
            Log.setLogLevel(Log.LOG_LEVEL_ALL);
            trackSelector = new DefaultTrackSelector(this, new RandomTrackSelection.Factory());
            trackSelector.setParameters(new DefaultTrackSelector.ParametersBuilder(this).build());
            exoPlayer.addAnalyticsListener(new EventLogger(trackSelector));
            playerView.setPlayer(exoPlayer);
            playerView.requestFocus();
            debugViewHelper = new DebugTextViewHelper(exoPlayer, debugTextView);
            debugViewHelper.start();
            mediaDataSourceFactory = new DefaultDataSourceFactory(this,
                    Util.getUserAgent(this, "IoTVideoDemo"));

            mMediaSource = buildMediaSource(Uri.parse(mFileUrl), null);
        }
        exoPlayer.prepare(mMediaSource);
    }

    private MediaSource buildMediaSource(Uri uri, @Nullable String overrideExtension) {
        @C.ContentType int type = Util.inferContentType(uri, overrideExtension);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
            case C.TYPE_HLS:
                HlsExtractorFactory extractorFactory =
                        new DefaultHlsExtractorFactory(DefaultTsPayloadReaderFactory.FLAG_DETECT_ACCESS_UNITS, true);
                return new HlsMediaSource.Factory(mediaDataSourceFactory)
                        .setExtractorFactory(extractorFactory)//解决NAL unit没有AUDs导致无法从TS流中提取H264视频流
                        .createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
            debugViewHelper.stop();
            debugViewHelper = null;
            trackSelector = null;
        }
    }

    public Pair<Integer, String> getErrorMessage(ExoPlaybackException e) {
        String errorString = "Playback failed";
        if (e.type == ExoPlaybackException.TYPE_RENDERER) {
            Exception cause = e.getRendererException();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                // Special case for decoder initialization failures.
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                        (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException.codecInfo == null) {
                    if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                        errorString = "Unable to query device decoders";
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        errorString = "This device does not provide a secure decoder for " +
                                decoderInitializationException.mimeType;
                    } else {
                        errorString = "This device does not provide a decoder for " +
                                decoderInitializationException.mimeType;
                    }
                } else {
                    errorString = "Unable to instantiate decoder " + decoderInitializationException.codecInfo;
                }
            }
        }
        return Pair.create(0, errorString);
    }
}
