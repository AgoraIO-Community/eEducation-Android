package io.agora.edu.launch;

import android.app.Activity;

import java.lang.ref.WeakReference;

import io.agora.edu.classroom.ReplayActivity;

public class AgoraEduReplay {
    private WeakReference<ReplayActivity> replayActivityWeak;
    private AgoraEduEvent curState = AgoraEduEvent.AgoraEduEventDestroyed;

    public AgoraEduReplay() {
    }

    public void add(Activity activity) {
        if (activity instanceof ReplayActivity) {
            replayActivityWeak = new WeakReference<>((ReplayActivity) activity);
        }
    }

    public boolean isReady() {
        /**/
        return this.curState.equals(AgoraEduEvent.AgoraEduEventDestroyed);
    }

    public void updateState(AgoraEduEvent state) {
        this.curState = state;
    }

    public void destroy() {
        if (curState != AgoraEduEvent.AgoraEduEventReady) {
            throw new IllegalStateException("curState is not AgoraEduEventReady, destroy() cannot be called");
        }
        if (replayActivityWeak != null) {
            if (replayActivityWeak.get() != null && !replayActivityWeak.get().isFinishing()
                    && !replayActivityWeak.get().isDestroyed()) {
                replayActivityWeak.get().finish();
            }
            replayActivityWeak.clear();
            replayActivityWeak = null;
        }
    }
}
