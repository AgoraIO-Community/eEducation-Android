package io.agora.education.classroom.fragment;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.agora.education.R;
import io.agora.education.api.user.data.EduUserInfo;
import io.agora.education.base.BaseFragment;
import io.agora.education.classroom.adapter.StudentGroupAdapter;
import io.agora.education.classroom.bean.group.GroupInfo;
import io.agora.education.classroom.bean.group.GroupMemberInfo;

import static io.agora.education.util.AppUtil.dip2px;

public class StudentGroupListFragment extends BaseFragment {
    private static final String TAG = StudentGroupListFragment.class.getSimpleName();

    @BindView(R.id.rcv_groups)
    RecyclerView rcvGroups;

    private final int layoutId = R.layout.fragment_studentgrouplist_layout;
    private StudentGroupAdapter studentListAdapter;

    @Override
    protected int getLayoutResId() {
        return layoutId;
    }

    @Override
    protected void initData() {
        studentListAdapter = new StudentGroupAdapter();
        rcvGroups.addItemDecoration(new AdapterDecoration(10));
        rcvGroups.setAdapter(studentListAdapter);
    }

    public void updateGroupList(List<GroupInfo> groupInfos, List<GroupMemberInfo> allStudent) {
        getActivity().runOnUiThread(() -> {
            if (rcvGroups.isComputingLayout()) {
                rcvGroups.postDelayed(() -> {
                    studentListAdapter.updateGroupList(groupInfos, allStudent);
                }, 300);
            } else {
                rcvGroups.post(() -> studentListAdapter.updateGroupList(groupInfos, allStudent));
            }
        });
    }

    @Override
    protected void initView() {

    }

    private class AdapterDecoration extends RecyclerView.ItemDecoration {
        private int top;

        public AdapterDecoration(int top) {
            this.top = top;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            outRect.left = 0;
            if (parent.getChildAdapterPosition(view) != 0) {
                outRect.top = top;
            }
            outRect.right = 0;
            outRect.bottom = 0;
        }
    }
}
