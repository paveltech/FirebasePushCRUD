package com.test.firebasepush;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

    }

    private void CallUpdateAndDeleteDialog(final String userid, String username, final String email, String monumber) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout., null);
        dialogBuilder.setView(dialogView);
        //Access Dialog views
        final EditText updateTextname = (EditText) dialogView.findViewById(R.id.updateTextname);
        final EditText updateTextemail = (EditText) dialogView.findViewById(R.id.updateTextemail);
        final EditText updateTextmobileno = (EditText) dialogView.findViewById(R.id.updateTextmobileno);
        updateTextname.setText(username);
        updateTextemail.setText(email);
        updateTextmobileno.setText(monumber);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateUser);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteUser);
        //username for set dialog title
        dialogBuilder.setTitle(username);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        // Click listener for Update data
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = updateTextname.getText().toString().trim();
                String email = updateTextemail.getText().toString().trim();
                String mobilenumber = updateTextmobileno.getText().toString().trim();
                //checking if the value is provided or not Here, you can Add More Validation as you required

                if (!TextUtils.isEmpty(name)) {
                    if (!TextUtils.isEmpty(email)) {
                        if (!TextUtils.isEmpty(mobilenumber)) {
                            //Method for update data
                            updateUser(userid, name, email, mobilenumber);
                            b.dismiss();
                        }
                    }
                }

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

}
