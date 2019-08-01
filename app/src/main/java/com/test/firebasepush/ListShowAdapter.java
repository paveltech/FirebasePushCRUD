package com.test.firebasepush;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListShowAdapter extends RecyclerView.Adapter<ListShowAdapter.CustomEmployee> {

    public Context context;
    public List<Post> postList;

    public onCallBack onCallBack;

    public ListShowAdapter(Context context, List<Post> postList, onCallBack onCallBack) {
        this.context = context;
        this.onCallBack = onCallBack;
        this.postList = postList;
    }

    @NonNull
    @Override
    public CustomEmployee onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_list, null);
        CustomEmployee viewHolder = new CustomEmployee(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomEmployee holder, int position) {

        Post post = postList.get(position);
        holder.name.setText("" + post.getName());
        holder.designation.setText("" + post.getDesignation());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallBack.onClickList(holder.getAdapterPosition(), post);
            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    public class CustomEmployee extends RecyclerView.ViewHolder {

        @BindView(R.id.letter_image)
        ImageView profileImage;

        @BindView(R.id.contact_name)
        TextView name;

        @BindView(R.id.contact_number_item)
        TextView designation;

        View view;


        public CustomEmployee(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }

    public interface onCallBack {
        void onClickList(int position, Post post);

    }
}
