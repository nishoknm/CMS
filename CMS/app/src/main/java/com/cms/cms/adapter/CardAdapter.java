package com.cms.cms.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cms.cms.R;
import com.cms.cms.model.NodeRequests;
import com.cms.cms.model.UserLocalStore;
import com.cms.cms.model.callback.GetModifyCallback;

import java.util.ArrayList;

/**
 * Created by Nishok on 12/7/2015.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    ArrayList<String> items;
    FragmentActivity activity;

    public CardAdapter(ArrayList<String> items, FragmentActivity activity) {
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
        viewHolder.etCourse.setText(items.get(i));
        viewHolder.bDelete.setOnClickListener(new clickListener(viewHolder, this));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView etCourse;
        public Button bDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            etCourse = (TextView) itemView.findViewById(R.id.etCourse);
            bDelete = (Button) itemView.findViewById(R.id.bDelete);
        }
    }

    public void removeItem(String text) {
        this.items.remove(text);
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
            removeItem(selectText);
            deleteCourse(selectText);
            cardAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
        }

        private void deleteCourse(final String course) {
            NodeRequests serverRequest = new NodeRequests(cardAdapter.activity, (new UserLocalStore(cardAdapter.activity)).getLoggedInUser().email, course, true);
            serverRequest.updateCourseAsyncTask(new GetModifyCallback() {
                @Override
                public void done() {
                    Toast.makeText(
                            cardAdapter.activity.getApplicationContext(), "Deleted " + course,
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}
