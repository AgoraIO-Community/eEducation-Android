package io.agora.education.classroom.fragment;

import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import io.agora.education.R;
import io.agora.education.api.user.data.EduUserInfo;
import io.agora.education.base.BaseFragment;
import io.agora.education.classroom.BaseClassActivity_bak;
import io.agora.education.classroom.adapter.StudentListAdapter;
import io.agora.education.classroom.bean.group.GroupMemberInfo;

public class StudentListFragment extends BaseFragment implements OnItemChildClickListener {
    private static final String TAG = StudentListFragment.class.getSimpleName();

    @BindView(R.id.rcv_students)
    RecyclerView rcvStudents;

    private String localUserUuid;
    private StudentListAdapter studentListAdapter;

    public StudentListFragment() {
    }

    public StudentListFragment(String localUserUuid) {
        this.localUserUuid = localUserUuid;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_studentlist_layout;
    }

    @Override
    protected void initData() {
        studentListAdapter = new StudentListAdapter(localUserUuid);
        studentListAdapter.setOnItemChildClickListener(this);
    }

    @Override
    protected void initView() {
        rcvStudents.setAdapter(studentListAdapter);
    }

    public void updateLocalUserUuid(String userUuid) {
        localUserUuid = userUuid;
        studentListAdapter.updateLocalUserUuid(userUuid);
    }

    public void updateStudentList(List<GroupMemberInfo> allStudent) {
        List<GroupMemberInfo> onlineStudents = new ArrayList<>();
        if (allStudent != null && allStudent.size() > 0) {
            /**本地用户始终在第一位*/
            if (!TextUtils.isEmpty(localUserUuid)) {
                for (int i = 0; i < allStudent.size(); i++) {
                    GroupMemberInfo memberInfo = allStudent.get(i);
                    if (memberInfo.getUuid().equals(localUserUuid)) {
                        if (i != 0) {
                            Collections.swap(allStudent, 0, i);
                            break;
                        }
                    }
                }
            }
            for (GroupMemberInfo memberInfo : allStudent) {
                if (memberInfo.getOnline()) {
                    onlineStudents.add(memberInfo);
                }
            }
            if (rcvStudents.isComputingLayout()) {
                rcvStudents.postDelayed(() -> {
                    studentListAdapter.updateStudentList(onlineStudents);
                }, 300);
            } else {
                rcvStudents.post(() -> studentListAdapter.updateStudentList(onlineStudents));
            }
        }
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
