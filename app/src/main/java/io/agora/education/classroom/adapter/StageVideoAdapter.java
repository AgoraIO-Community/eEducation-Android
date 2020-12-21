package io.agora.education.classroom.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import io.agora.education.R;
import io.agora.education.api.stream.data.EduStreamInfo;
import io.agora.education.classroom.BaseClassActivity_bak;
import io.agora.education.classroom.bean.group.StageStreamInfo;
import io.agora.education.classroom.widget.StageVideoView;

public class StageVideoAdapter extends BaseQuickAdapter<StageStreamInfo, StageVideoAdapter.ViewHolder> {

    private static String localUserUuid;
    private static String rewardUuid;

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
        if (payloads.size() > 0) {
            viewHolder.convert(item);
        }
    }

    @Override
    protected void convert(@NonNull ViewHolder viewHolder, StageStreamInfo item) {
        viewHolder.convert(item);
        Activity activity = (Activity) viewHolder.view.getContext();
        if (item.getStreamInfo().getHasVideo() && activity instanceof BaseClassActivity_bak)
            ((BaseClassActivity_bak) activity).renderStream(
                    ((BaseClassActivity_bak) activity).getMainEduRoom(), item.getStreamInfo(),
                    viewHolder.view.getVideoLayout());
        if (!TextUtils.isEmpty(rewardUuid) && !TextUtils.isEmpty(item.getGroupUuid())) {
            if (item.getGroupUuid().equals(rewardUuid) || item.getStreamInfo().getPublisher()
                    .getUserUuid().equals(rewardUuid)) {
                viewHolder.rewardAnim();
            }
        }
        List<StageStreamInfo> streamInfos = getData();
        if(getItemPosition(item) == streamInfos.size() - 1) {
            this.rewardUuid = null;
        }
    }

    public void setNewList(@Nullable List<StageStreamInfo> data, String localUserUuid) {
        this.localUserUuid = localUserUuid;
        ((Activity) getContext()).runOnUiThread(() -> {
            List<StageStreamInfo> list = new ArrayList<>();
            list.addAll(data);
            setNewData(list);
            notifyDataSetChanged();
        });
    }

    public void notifyRewardByUser(String userUuid) {
        List<StageStreamInfo> streamInfos = getData();
        for (int i = 0; i < streamInfos.size(); i++) {
            String uuid = streamInfos.get(i).getStreamInfo().getPublisher().getUserUuid();
            if (uuid.equals(userUuid)) {
                this.rewardUuid = uuid;
                int finalI = i;
                ((Activity) getContext()).runOnUiThread(() -> notifyItemChanged(finalI));
            }
        }
    }

    public void notifyRewardByGroup(String groupUuid) {
        this.rewardUuid = groupUuid;
        ((Activity) getContext()).runOnUiThread(() -> notifyDataSetChanged());
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
