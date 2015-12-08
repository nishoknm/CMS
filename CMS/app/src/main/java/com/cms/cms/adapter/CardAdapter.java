package com.cms.cms.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cms.cms.R;
import com.cms.cms.fragment.CourseDialogFragment;
import com.cms.cms.model.NodeRequests;
import com.cms.cms.model.User;
import com.cms.cms.model.UserLocalStore;
import com.cms.cms.model.callback.GetListCallback;
import com.cms.cms.model.callback.GetModifyCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nishok on 12/7/2015.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    ArrayList<JSONObject> items;
    FragmentActivity activity;

    public CardAdapter(ArrayList<JSONObject> items, FragmentActivity activity) {
        super();
        this.items = items;
        this.activity = activity;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_card_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        try {
            viewHolder.etCourse.setText(items.get(i).get("name").toString());
            viewHolder.etDept.setText(items.get(i).get("department").toString());
        } catch (JSONException e) {
            //Do nothing
        }
        viewHolder.bDelete.setOnClickListener(new clickListener(viewHolder, this));
        viewHolder.etCourse.setOnClickListener(new clickListener(viewHolder, this));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView etCourse, etDept;
        public Button bDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            etCourse = (TextView) itemView.findViewById(R.id.etCourse);
            etDept = (TextView) itemView.findViewById(R.id.etDept);
            bDelete = (Button) itemView.findViewById(R.id.bDelete);
        }
    }

    public void removeItem(int position) {
        this.items.remove(position);
    }

    class clickListener implements View.OnClickListener {
        ViewHolder viewHolder;
        CardAdapter cardAdapter;

        public clickListener(ViewHolder viewHolder, CardAdapter cardAdapter) {
            this.viewHolder = viewHolder;
            this.cardAdapter = cardAdapter;
        }

        @Override
        public void onClick(View v) {
            String selectText = (String) ((TextView) viewHolder.itemView.findViewById(R.id.etCourse)).getText();
            String selectDpt = (String) ((TextView) viewHolder.itemView.findViewById(R.id.etDept)).getText();
            switch (v.getId()) {
                case R.id.bDelete:
                    deleteCourse(selectText, selectDpt);
                    break;
                case R.id.etCourse:
                    getCourseDetails(selectText, selectDpt);
                    break;
            }
        }

        private void deleteCourse(final String course, final String selectDpt) {
            final User user = (new UserLocalStore(cardAdapter.activity)).getLoggedInUser();
            NodeRequests serverRequest = new NodeRequests(cardAdapter.activity, user.email, course, selectDpt, true);
            serverRequest.updateCourseAsyncTask(new GetModifyCallback() {
                @Override
                public void done() {
                    if (user.account.equalsIgnoreCase("professor")) {
                        NodeRequests serverRequest = new NodeRequests(cardAdapter.activity, user.email, course, selectDpt, true);
                        serverRequest.updateCourseDeptAsyncTask(new GetModifyCallback() {
                            @Override
                            public void done() {
                                removeItem(viewHolder.getAdapterPosition());
                                cardAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                                Toast.makeText(
                                        cardAdapter.activity.getApplicationContext(), "Deleted " + course,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    } else if (user.account.equalsIgnoreCase("student")) {
                        removeItem(viewHolder.getAdapterPosition());
                        cardAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        Toast.makeText(
                                cardAdapter.activity.getApplicationContext(), "Deleted " + course,
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        private void getCourseDetails(final String course, final String department) {
            NodeRequests serverRequest = new NodeRequests(cardAdapter.activity, (new UserLocalStore(cardAdapter.activity)).getLoggedInUser().email, course, department);
            serverRequest.fetchCourseJSONAsyncTask(new GetListCallback() {
                @Override
                public void done(ArrayList<JSONObject> jsons) {
                    String name = null, details = null, time = null, professor = null;
                    try {
                        name = jsons.get(0).get("name").toString();
                        details = jsons.get(0).get("details").toString();
                        time = jsons.get(0).get("time").toString();
                        professor = jsons.get(0).get("professor").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    FragmentManager manager = cardAdapter.activity.getSupportFragmentManager();
                    Fragment frag = manager.findFragmentByTag("fragment_dialog");
                    if (frag != null) {
                        manager.beginTransaction().remove(frag).commit();
                    }
                    CourseDialogFragment diaFrag = new CourseDialogFragment(name, details, time, professor);
                    diaFrag.show(manager, "fragment_dialog");
                }
            });
        }
    }

}
