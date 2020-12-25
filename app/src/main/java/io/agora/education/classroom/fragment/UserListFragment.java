package io.agora.education.classroom.fragment;

import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import io.agora.education.R;
import io.agora.education.api.stream.data.EduStreamInfo;
import io.agora.education.api.stream.data.VideoSourceType;
import io.agora.education.api.user.data.EduBaseUserInfo;
import io.agora.education.api.user.data.EduUserRole;
import io.agora.education.base.BaseFragment;
import io.agora.education.classroom.BaseClassActivity_bak;
import io.agora.education.classroom.adapter.UserListAdapter;

public class UserListFragment extends BaseFragment implements OnItemChildClickListener {

    @BindView(R.id.rcv_users)
    protected RecyclerView rcv_users;

    private UserListAdapter adapter;
    private String localUserUuid;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_user_list;
    }

    @Override
    protected void initData() {
        if (context instanceof BaseClassActivity_bak) {
            adapter = new UserListAdapter();
            adapter.setOnItemChildClickListener(this);
        }
    }

    @Override
    protected void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rcv_users.setLayoutManager(layoutManager);
        rcv_users.setAdapter(adapter);
    }

    public void setUserList(List<EduStreamInfo> users) {
        /*copy一份数据，防止出现多个adapter使用同一数据源而导致的数据错乱问题*/
        List<EduStreamInfo> userList = new ArrayList<>(users.size());
        for (EduStreamInfo element : users) {
            userList.add(element.copy());
        }
        /**过滤掉非学生和非摄像头流的user*/
        List<EduStreamInfo> students = new ArrayList<>(userList.size());
        for (EduStreamInfo streamInfo : userList) {
            EduBaseUserInfo userInfo = streamInfo.getPublisher();
            if (userInfo.getRole().equals(EduUserRole.STUDENT) &&
                    streamInfo.getVideoSourceType().equals(VideoSourceType.CAMERA)) {
                students.add(streamInfo);
            }
        }
        /**本地用户始终在第一位*/
        if (!TextUtils.isEmpty(localUserUuid)) {
            for (int i = 0; i < students.size(); i++) {
                EduStreamInfo streamInfo = students.get(i);
                if (streamInfo.getPublisher().getUserUuid().equals(localUserUuid)) {
                    if (i != 0) {
                        Collections.swap(students, 0, i);
                    }
                }
            }
        }
        getActivity().runOnUiThread(() -> {
            adapter.setNewData(students);
        });
    }

    public void setGrantedUuids(List<String> grantedUuids) {
        adapter.setGrantedUuids(grantedUuids);
    }

    public void setLocalUserUuid(String userUuid) {
        localUserUuid = userUuid;
        adapter.setLocalUserUuid(userUuid);
    }

    public void updateLocalStream(EduStreamInfo streamInfo) {
        getActivity().runOnUiThread(() -> {
            if (rcv_users.isComputingLayout()) {
                rcv_users.postDelayed(() -> {
                    adapter.refreshStreamStatus(streamInfo);
                }, 300);
            } else {
                rcv_users.post(() -> adapter.refreshStreamStatus(streamInfo));
            }
        });
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (context instanceof BaseClassActivity_bak) {
            boolean isSelected = view.isSelected();
            switch (view.getId()) {
                case R.id.iv_btn_mute_audio:
                    ((BaseClassActivity_bak) context).muteLocalAudio(isSelected);
                    break;
                case R.id.iv_btn_mute_video:
                    ((BaseClassActivity_bak) context).muteLocalVideo(isSelected);
                    break;
                default:
                    break;
            }
        }
    }

}
