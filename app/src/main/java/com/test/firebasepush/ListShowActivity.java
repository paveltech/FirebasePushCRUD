package com.test.firebasepush;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.test.firebasepush.api.PostSend;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListShowActivity extends AppCompatActivity implements ListShowAdapter.onCallBack {

    @BindView(R.id.recycleview)
    RecyclerView recyclerView;
    public ListShowAdapter allEmployeeAdapter;
    private DatabaseReference databaseReference;
    public ProgressDialog progressDialog;
    public PostSend postSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_show);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(ListShowActivity.this);
        progressDialog.setMessage("Loading ....");

        postSend = new PostSend(getApplicationContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        databaseQuary();
    }

    public void databaseQuary() {
        progressDialog.show();
        databaseReference
                .child("data")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Post> postList = new ArrayList<>();

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Post post = dataSnapshot1.getValue(Post.class);
                            postList.add(post);
                        }

                        progressDialog.dismiss();
                        dataSetIntoAdapter(postList);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }
                });
    }

    public void dataSetIntoAdapter(List<Post> postList) {
        allEmployeeAdapter = new ListShowAdapter(getApplicationContext(), postList, this);
        recyclerView.setAdapter(allEmployeeAdapter);
    }

    @Override
    public void onClickList(int position, Post post) {
        CallUpdateAndDeleteDialog(post.getPostId(), post.getName(), post.getDesignation());
    }

    private void CallUpdateAndDeleteDialog(final String userid, String name, final String designation) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_update, null);
        dialogBuilder.setView(dialogView);
        //Access Dialog views
        final EditText updateName = (EditText) dialogView.findViewById(R.id.updateTextname);
        final EditText updateDesignation = (EditText) dialogView.findViewById(R.id.updateTextDesignation);

        updateName.setText(name);
        updateDesignation.setText(designation);

        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateUser);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteUser);
        //username for set dialog title
        dialogBuilder.setTitle("Dialog");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        // Click listener for Update data
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = updateName.getText().toString();
                String designation = updateDesignation.getText().toString().trim();

                if (updateName.getText().toString().trim().length() > 0 && updateDesignation.getText().toString().trim().length() > 0) {
                    updateUser(userid, name, designation);
                } else {
                    updateName.setError("Fill it");
                    updateDesignation.setError("Fill It");
                }
                b.dismiss();
            }
        });

        // Click listener for Delete data
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Method for delete data
                deleteUser(userid);
                b.dismiss();
            }
        });
    }

    private boolean updateUser(String id, String name, String designation) {
        //getting the specified User reference
        DatabaseReference UpdateReference = FirebaseDatabase.getInstance().getReference("data").child(id);

        Post post = new Post(name, designation, id);

        UpdateReference.setValue(post);
        Toast.makeText(getApplicationContext(), "User Updated", Toast.LENGTH_LONG).show();
        allEmployeeAdapter.notifyDataSetChanged();
        postSend.sendTopicNotification("List Update", "One list updated");
        databaseQuary();
        return true;
    }

    private boolean deleteUser(String id) {
        //getting the specified User reference
        DatabaseReference DeleteReference = FirebaseDatabase.getInstance().getReference("data").child(id);
        //removing User
        DeleteReference.removeValue();
        Toast.makeText(getApplicationContext(), "User Deleted", Toast.LENGTH_LONG).show();
        allEmployeeAdapter.notifyDataSetChanged();
        postSend.sendTopicNotification("List Delete", "One list delete");
        databaseQuary();
        return true;
    }


}
