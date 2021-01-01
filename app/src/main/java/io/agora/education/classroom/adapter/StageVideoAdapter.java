package io.agora.education.classroom.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.agora.education.R;
import io.agora.education.api.stream.data.EduStreamInfo;
import io.agora.education.classroom.BaseClassActivity_bak;
import io.agora.education.classroom.bean.group.StageStreamInfo;
import io.agora.education.classroom.widget.StageVideoView;

public class StageVideoAdapter extends BaseQuickAdapter<StageStreamInfo, StageVideoAdapter.ViewHolder> {
    private static final String TAG = "StageVideoAdapter";

    private static String localUserUuid;
    private String rewardUuid;

    public StageVideoAdapter() {
        super(0);
        setDiffCallback(new DiffUtil.ItemCallback<StageStreamInfo>() {
            @Override
            public boolean areItemsTheSame(@NonNull StageStreamInfo oldItem, @NonNull StageStreamInfo newItem) {
                EduStreamInfo oldStream = oldItem.getStreamInfo();
                EduStreamInfo newStream = newItem.getStreamInfo();
                boolean a = oldStream.getHasVideo() == newStream.getHasVideo()
                        && oldStream.getHasAudio() == newStream.getHasAudio()
                        && oldStream.getStreamUuid().equals(newStream.getStreamUuid())
                        && oldStream.getStreamName().equals(newStream.getStreamName())
                        && oldStream.getPublisher().equals(newStream.getPublisher())
                        && oldStream.getVideoSourceType().equals(newStream.getVideoSourceType());
                return a;
            }

            @Override
            public boolean areContentsTheSame(@NonNull StageStreamInfo oldItem, @NonNull StageStreamInfo newItem) {
                EduStreamInfo oldStream = oldItem.getStreamInfo();
                EduStreamInfo newStream = newItem.getStreamInfo();
                boolean a = oldStream.getHasVideo() == newStream.getHasVideo()
                        && oldStream.getHasAudio() == newStream.getHasAudio()
                        && oldStream.getStreamUuid().equals(newStream.getStreamUuid())
                        && oldStream.getStreamName().equals(newStream.getStreamName())
                        && oldStream.getPublisher().equals(newStream.getPublisher())
                        && oldStream.getVideoSourceType().equals(newStream.getVideoSourceType());
                return a;
            }

            @Nullable
            @Override
            public Object getChangePayload(@NonNull StageStreamInfo oldItem, @NonNull StageStreamInfo newItem) {
                EduStreamInfo oldStream = oldItem.getStreamInfo();
                EduStreamInfo newStream = newItem.getStreamInfo();
                boolean a = oldStream.getHasVideo() == newStream.getHasVideo()
                        && oldStream.getHasAudio() == newStream.getHasAudio()
                        && oldStream.getStreamUuid().equals(newStream.getStreamUuid())
                        && oldStream.getStreamName().equals(newStream.getStreamName())
                        && oldStream.getPublisher().equals(newStream.getPublisher())
                        && oldStream.getVideoSourceType().equals(newStream.getVideoSourceType());
                if (a) {
                    return true;
                } else {
                    return null;
                }
            }
        });
    }

    @NonNull
    @Override
    protected ViewHolder onCreateDefViewHolder(@NonNull ViewGroup parent, int viewType) {
        StageVideoView item = new StageVideoView(getContext());
        item.init();
        int width = getContext().getResources().getDimensionPixelSize(R.dimen.dp_95);
        int height = parent.getMeasuredHeight() - parent.getPaddingTop() - parent.getPaddingBottom();
        item.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        return new ViewHolder(item);
    }

    @Override
    protected void convert(@NonNull ViewHolder viewHolder, StageStreamInfo item, @NonNull List<?> payloads) {
        super.convert(viewHolder, item, payloads);
//        if (payloads.size() > 0) {
//            viewHolder.convert(item);
//        }
        if(payloads.isEmpty()) {
            viewHolder.convert(item);
        } else {
            /*判断是否需要播放奖励动画，同时动画播放完成后就把播放标志置空*/
            boolean a = !TextUtils.isEmpty(item.getGroupUuid()) && item.getGroupUuid().equals(rewardUuid);
            if(a || item.getStreamInfo().getPublisher().getUserUuid().equals(rewardUuid)) {
                viewHolder.rewardAnim();
            }
            if(getItemPosition(item) == getData().size() - 1) {
                this.rewardUuid = null;
            }
        }
    }

    @Override
    protected void convert(@NonNull ViewHolder viewHolder, StageStreamInfo item) {
        viewHolder.convert(item);
        Activity activity = (Activity) viewHolder.view.getContext();
        if (item.getStreamInfo().getHasVideo() && activity instanceof BaseClassActivity_bak) {
            ((BaseClassActivity_bak) activity).renderStream(
                    ((BaseClassActivity_bak) activity).getMainEduRoom(), item.getStreamInfo(),
                    viewHolder.view.getVideoLayout());
        }
    }

    public void setNewList(@Nullable List<StageStreamInfo> newData, String localUserUuid) {
        Log.e(TAG, "视频:" + new Gson().toJson(newData));
        this.localUserUuid = localUserUuid;
        List<String> oldStreamId = new ArrayList<>(getData().size());
        for (StageStreamInfo element : getData()) {
            oldStreamId.add(element.getStreamInfo().getStreamUuid());
        }
        List<Integer> changed = new ArrayList<>();
        List<Integer> added = new ArrayList<>();
        List<Integer> removed = new ArrayList<>();
        for (int i = 0; i < newData.size(); i++) {
            StageStreamInfo element = newData.get(i);
            int pos = oldStreamId.indexOf(element.getStreamInfo().getStreamUuid());
            if (pos > -1) {
                StageStreamInfo old = getData().get(pos);
                if (!old.equals(element)) {
                    changed.add(i);
                }
            }
        }
        for (int i = 0; i < newData.size(); i++) {
            StageStreamInfo element = newData.get(i);
            if (!oldStreamId.contains(element.getStreamInfo().getStreamUuid())) {
                added.add(i);
            }
        }
        List<String> newStreamId = new ArrayList<>(newData.size());
        for (StageStreamInfo element : newData) {
            newStreamId.add(element.getStreamInfo().getStreamUuid());
        }
        for (int i = 0; i < getData().size(); i++) {
            StageStreamInfo element = getData().get(i);
            if (!newStreamId.contains(element.getStreamInfo().getStreamUuid())) {
                removed.add(i);
            }
        }
        List<StageStreamInfo> list = new ArrayList<>();
        list.addAll(newData);
        ((Activity) getContext()).runOnUiThread(() -> {
            setDiffNewData(list);
        });
    }

    public void notifyRewardByUser(String userUuid) {
        List<StageStreamInfo> streamInfos = getData();
        for (int i = 0; i < streamInfos.size(); i++) {
            String uuid = streamInfos.get(i).getStreamInfo().getPublisher().getUserUuid();
            if (uuid.equals(userUuid)) {
                this.rewardUuid = uuid;
                int finalI = i;
                ((Activity) getContext()).runOnUiThread(() -> notifyItemChanged(finalI, "notifyRewardByUser"));
            }
        }
    }

    public void notifyRewardByGroup(String groupUuid) {
        this.rewardUuid = groupUuid;
        int size = 0;
        List<StageStreamInfo> stageStreams = getData();
        for (int i = 0; i < stageStreams.size(); i++) {
            if(stageStreams.get(i).getGroupUuid().equals(groupUuid)) {
                size++;
            }
        }
        final int finalSize = size;
        ((Activity) getContext()).runOnUiThread(() ->
                notifyItemRangeChanged(0, finalSize, "notifyRewardByGroup"));
    }

    static class ViewHolder extends BaseViewHolder {
        private StageVideoView view;

        ViewHolder(StageVideoView view) {
            super(view);
            this.view = view;
        }

        void convert(StageStreamInfo item) {
            view.muteAudio(!item.getStreamInfo().getHasAudio());
            view.setName(item.getStreamInfo().getPublisher().getUserName());
            view.setReward(item.getReward());
            view.enableVideo(item.getStreamInfo().getHasVideo());
        }

        public void rewardAnim() {
            view.showRewardAnim();
        }
    }

}
