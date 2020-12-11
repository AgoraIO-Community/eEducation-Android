package io.agora.education.classroom.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.education.R;

public class StageVideoView extends ConstraintLayout {

    @BindView(R.id.tv_name)
    protected TextView tv_name;
    @BindView(R.id.ic_audio)
    protected StageAudioView ic_audio;
    @BindView(R.id.layout_place_holder)
    protected FrameLayout layout_place_holder;
    @BindView(R.id.layout_video)
    protected FrameLayout layout_video;
    @BindView(R.id.rewardAnim_ImageView)
    protected ImageView rewardAnimImageView;
    @BindView(R.id.reward_TextView)
    protected TextView rewardTextView;

    public StageVideoView(Context context) {
        super(context);
    }

    public StageVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StageVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init() {
        inflate(getContext(), R.layout.layout_video_stage, this);
        ButterKnife.bind(this);
    }

    public void setViewVisibility(int visibility) {
        post(() -> setVisibility(visibility));
    }

    public void setName(String name) {
        post(() -> tv_name.setText(name));
    }

    public void muteAudio(boolean muted) {
        post(() -> {
            ic_audio.setVisibility(VISIBLE);
            ic_audio.setState(muted ? StageAudioView.State.CLOSED : StageAudioView.State.OPENED);
        });
    }

    public void setReward(int reward) {
        post(() -> {
            rewardTextView.setText(String.valueOf(reward));
        });
    }

    public void enableVideo(boolean enable) {
        post(() -> {
            layout_place_holder.setVisibility(enable?GONE:VISIBLE);
        });
    }

    public boolean isAudioMuted() {
        return ic_audio.getState() == StageAudioView.State.CLOSED;
    }

    public FrameLayout getVideoLayout() {
        return layout_video;
    }

    public TextView getTv_name() {
        return tv_name;
    }

    public void setOnClickAudioListener(OnClickListener listener) {
        ic_audio.setOnClickListener(listener);
    }

    /**
     * 显示奖励动画
     */
    public void showRewardAnim() {
        rewardAnimImageView.setVisibility(GONE);
        Glide.with(getContext()).asGif().skipMemoryCache(true)
                .load(R.drawable.img_reward_anim).listener(new RequestListener<GifDrawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                        Target<GifDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GifDrawable resource, Object model,
                                           Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                resource.setLoopCount(1);
                resource.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                    public void onAnimationEnd(Drawable drawable) {
                        rewardAnimImageView.setVisibility(GONE);
                    }
                });
                return false;
            }
        }).into(rewardAnimImageView);
    }

}
