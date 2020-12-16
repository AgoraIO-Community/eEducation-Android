package io.agora.education.classroom.fragment;

import android.text.TextUtils;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import io.agora.education.R;
import io.agora.education.api.user.data.EduUserInfo;
import io.agora.education.base.BaseFragment;
import io.agora.education.classroom.adapter.StudentListAdapter;
import io.agora.education.classroom.bean.group.GroupMemberInfo;

public class StudentListFragment extends BaseFragment {
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
        studentListAdapter = new StudentListAdapter();
    }

    @Override
    protected void initView() {
        rcvStudents.setAdapter(studentListAdapter);
    }

    public void updateLocalUserUuid(String userUuid) {
        localUserUuid = userUuid;
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
            /**过滤掉不在线的人数*/
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
}
