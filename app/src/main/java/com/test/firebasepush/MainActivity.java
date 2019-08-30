package com.test.firebasepush;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.test.firebasepush.api.ApiClient;
import com.test.firebasepush.api.ApiInterface;
import com.test.firebasepush.api.PostSend;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements ListShowAdapter.onCallBack {

    public ProgressDialog progressDialog;
    public static final String TAG = MainActivity.class.getSimpleName();
    private DatabaseReference databaseReference;

    public PostSend postSend;
    public ApiInterface apiInterface;

    @BindView(R.id.recycleview)
    RecyclerView recyclerView;
    public ListShowAdapter allEmployeeAdapter;
    public StringAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading ...");
        postSend = new PostSend(getApplicationContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        subscribeToTopic();

    }


    private void sendDateToFirebaseDatebase(String name, String designation) {
        String key = databaseReference.push().getKey();
        Post post = new Post(name, designation, key);

        databaseReference.child("data")
                .child(key)
                .setValue(post)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            postSend.sendTopicNotification("Database Notification", "New Data Added");
                            databaseQuary();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_all_item) {
            Intent intent = new Intent(MainActivity.this, ListShowActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("push")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);

                    }
                });

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


    @OnClick(R.id.button_show)
    public void databaseArea() {
        progressDialog.show();
        databaseReference
                .child("Area")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> stringArrayList = new ArrayList<>();

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            String data = dataSnapshot1.getValue(String.class);
                            stringArrayList.add(data);
                        }
                        progressDialog.dismiss();
                        dataSetIntoStringAdapter(stringArrayList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }
                });
    }


    public void dataSetIntoStringAdapter(ArrayList<String> stringArrayList) {
        adapter = new StringAdapter(getApplicationContext(), stringArrayList);
        recyclerView.setAdapter(adapter);
    }

    public void dataSetIntoAdapter(List<Post> postList) {
        allEmployeeAdapter = new ListShowAdapter(getApplicationContext(), postList, this);
        recyclerView.setAdapter(allEmployeeAdapter);
    }

    @Override
    public void onClickList(int position, Post post) {
        CallUpdateAndDeleteDialog(post.getPostId(), post.getName(), post.getDesignation());
    }


}
