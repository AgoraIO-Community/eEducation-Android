package io.agora.edu.classroom.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import io.agora.edu.R2;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.edu.R;
import io.agora.education.api.stream.data.EduStreamInfo;
import io.agora.education.api.user.data.EduBaseUserInfo;

public class UserListAdapter extends BaseQuickAdapter<EduStreamInfo, UserListAdapter.ViewHolder> {

    private String localUserUuid;
    //    private EduStreamInfo localCameraStream;
//    private int localUserIndex;
    private List<String> grantedUuids = new ArrayList<>();

    public void setGrantedUuids(List<String> grantedUuids) {
        if (grantedUuids != null && this.grantedUuids.equals(grantedUuids)) {
            this.grantedUuids = grantedUuids;
            notifyDataSetChanged();
        }
    }

    public void setLocalUserUuid(@NonNull String localUserUuid) {
        this.localUserUuid = localUserUuid;
    }

    public void refreshStreamStatus(@NonNull EduStreamInfo streamInfo) {
        List<EduStreamInfo> list = getData();
        for (int i = 0; i < list.size(); i++) {
            EduStreamInfo element = list.get(i);
            if (element.same(streamInfo)) {
                list.set(i, streamInfo);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public UserListAdapter() {
        super(R.layout.item_user_list);
        addChildClickViewIds(R.id.iv_btn_mute_audio, R.id.iv_btn_mute_video);
    }

    @Override
    protected void convert(@NonNull ViewHolder viewHolder, EduStreamInfo streamInfo) {
        EduBaseUserInfo userInfo = streamInfo.getPublisher();
        viewHolder.tv_name.setText(userInfo.getUserName());
        viewHolder.iv_btn_grant_board.setSelected(grantedUuids.contains(userInfo.getUserUuid()));
        viewHolder.iv_btn_mute_audio.setSelected(streamInfo.getHasAudio());
        viewHolder.iv_btn_mute_video.setSelected(streamInfo.getHasVideo());
        boolean isLocal = userInfo.getUserUuid().equals(localUserUuid);
        viewHolder.iv_btn_mute_audio.setEnabled(isLocal);
        viewHolder.iv_btn_mute_video.setEnabled(isLocal);
        if (!isLocal) {
            viewHolder.iv_btn_mute_audio.setAlpha(0.5f);
            viewHolder.iv_btn_mute_video.setAlpha(0.5f);
        }
    }


    static class ViewHolder extends BaseViewHolder {
        @BindView(R2.id.tv_name)
        TextView tv_name;
        @BindView(R2.id.iv_btn_grant_board)
        ImageView iv_btn_grant_board;
        @BindView(R2.id.iv_btn_mute_audio)
        ImageView iv_btn_mute_audio;
        @BindView(R2.id.iv_btn_mute_video)
        ImageView iv_btn_mute_video;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
