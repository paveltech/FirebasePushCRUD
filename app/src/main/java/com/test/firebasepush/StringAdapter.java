package com.test.firebasepush;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StringAdapter extends RecyclerView.Adapter<StringAdapter.CustomStringAdapter> {

    public Context context;
    public ArrayList<String> stringArrayList;


    public StringAdapter(Context context, ArrayList<String> stringArrayList) {
        this.context = context;
        this.stringArrayList = stringArrayList;
    }


    @NonNull
    @Override
    public CustomStringAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_string_show, null);
        return new CustomStringAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomStringAdapter holder, int position) {
        String textString = stringArrayList.get(position);
        holder.textView.setText("" + textString);

    }

    @Override
    public int getItemCount() {
        return stringArrayList.size();
    }

    public class CustomStringAdapter extends RecyclerView.ViewHolder {

        @BindView(R.id.text)
        TextView textView;


        public CustomStringAdapter(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
