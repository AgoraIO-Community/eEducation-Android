package io.agora.education.classroom.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.education.R;
import io.agora.education.api.stream.data.EduStreamInfo;

public class RtcVideoView extends ConstraintLayout {

    @BindView(R.id.tv_name)
    protected TextView tv_name;
    @BindView(R.id.ic_audio)
    protected RtcAudioView ic_audio;
    @Nullable
    @BindView(R.id.ic_video)
    protected ImageView ic_video;
    @BindView(R.id.layout_place_holder)
    protected FrameLayout layout_place_holder;
    @BindView(R.id.layout_video)
    protected FrameLayout layout_video;

    public RtcVideoView(Context context) {
        super(context);
    }

    public RtcVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RtcVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(@LayoutRes int layoutResId, boolean showVideo) {
        inflate(getContext(), layoutResId, this);
        ButterKnife.bind(this);
        if (ic_video != null) {
            ic_video.setVisibility(showVideo ? VISIBLE : GONE);
        }
    }

    public void setViewVisibility(int visibility) {
        ((Activity) getContext()).runOnUiThread(() -> setVisibility(visibility));
    }

    public void update(EduStreamInfo streamInfo) {
        ((Activity) getContext()).runOnUiThread(() -> {
            tv_name.setText(streamInfo.getPublisher().getUserName());
            muteMedia(streamInfo);
        });
    }

    public void setName(String name) {
        ((Activity) getContext()).runOnUiThread(() -> tv_name.setText(name));
    }

    public void muteAudio(boolean muted) {
        ((Activity) getContext()).runOnUiThread(() -> {
            ic_audio.setState(muted ? RtcAudioView.State.CLOSED : RtcAudioView.State.OPENED);
        });
    }

    public boolean isAudioMuted() {
        return ic_audio.getState() == RtcAudioView.State.CLOSED;
    }

    public void muteVideo(boolean muted) {
        ((Activity) getContext()).runOnUiThread(() -> {
            if (ic_video != null) {
                ic_video.setSelected(!muted);
            }
            layout_video.setVisibility(muted ? GONE : VISIBLE);
            layout_place_holder.setVisibility(muted ? VISIBLE : GONE);
            Log.e("RtcVideoView", "muteVideo：" + muted);
        });
    }

    public boolean isVideoMuted() {
        if (ic_video != null) {
            return !ic_video.isSelected();
        }
        return true;
    }

    public void muteMedia(EduStreamInfo streamInfo) {
        ((Activity) getContext()).runOnUiThread(() -> {
            muteMedia(!streamInfo.getHasAudio(), !streamInfo.getHasVideo());
        });
    }

    public void muteMedia(boolean audioMuted, boolean videoMuted) {
        ((Activity) getContext()).runOnUiThread(() -> {
            ic_audio.setState(audioMuted ? RtcAudioView.State.CLOSED : RtcAudioView.State.OPENED);
            Log.e("RtcVideoView", "muteAudio：" + audioMuted);
            if (ic_video != null) {
                ic_video.setSelected(!videoMuted);
            }
            layout_video.setVisibility(videoMuted ? GONE : VISIBLE);
            layout_place_holder.setVisibility(videoMuted ? VISIBLE : GONE);
            Log.e("RtcVideoView", "muteVideo：" + videoMuted);
        });
    }

    public FrameLayout getVideoLayout() {
        return layout_video;
    }

    public TextView getTv_name() {
        return tv_name;
    }

    //    public SurfaceView getSurfaceView() {
//        if (layout_video.getChildCount() > 0) {
//            return (SurfaceView) layout_video.getChildAt(0);
//        }
//        return null;
//    }
//
//    public void setSurfaceView(SurfaceView surfaceView) {
//        layout_video.removeAllViews();
//        if (surfaceView != null) {
//            layout_video.addView(surfaceView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//        }
//    }

    public void setOnClickAudioListener(OnClickListener listener) {
        if (ic_audio != null) {
            ic_audio.setOnClickListener(listener);
        }
    }

    public void setOnClickVideoListener(OnClickListener listener) {
        if (ic_video != null) {
            ic_video.setOnClickListener(listener);
        }
    }

//    public void showLocal() {
//        VideoMediator.setupLocalVideo(this);
//    }
//
//    public void showRemote(int uid) {
//        VideoMediator.setupRemoteVideo(this, uid);
//    }

}
