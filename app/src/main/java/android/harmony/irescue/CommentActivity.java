package android.harmony.irescue;

import android.content.Context;
import android.content.Intent;
import android.harmony.irescue.adapters.CommentsAdapter;
import android.harmony.irescue.model.Comment;
import android.harmony.irescue.utility.Constants;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private ImageView userImageView;
    private static final String ALERT_ID = "ALERT_ID";
    private FirebaseFirestore mFirestore;
    private DatabaseReference mReference;
    private  String alertId;
    private RecyclerView commentsRecyclerView;

    public static Intent newInstance(Context context, String alertId) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra(ALERT_ID, alertId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.setFirestoreSettings(Constants.getSettings());
        final EditText commentText = findViewById(R.id.comment);
        final ProgressBar postingProgressBar=findViewById(R.id.posting_progress_bar);
        postingProgressBar.setVisibility(View.INVISIBLE);
        final TextView post = findViewById(R.id.post);
        userImageView = findViewById(R.id.user_image_view);
        alertId = getIntent().getStringExtra(ALERT_ID);
        mReference= FirebaseDatabase.getInstance().getReference("Comments").child(alertId);

        final Comment comment = new Comment();
        commentsRecyclerView = findViewById(R.id.comments_recycler_view);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    post.setVisibility(View.INVISIBLE);
                } else {
                    post.setVisibility(View.VISIBLE);
                    comment.setComment(s.toString());

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post.setVisibility(View.INVISIBLE);
                comment.setUser(Constants.getUser());
                comment.setTime(String.valueOf(System.currentTimeMillis()));
                postingProgressBar.setVisibility(View.VISIBLE);
                commentText.setEnabled(false);
                mReference.push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        postingProgressBar.setVisibility(View.INVISIBLE);
                        commentText.setText("");
                        post.setVisibility(View.INVISIBLE);
                        commentText.setEnabled(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        post.setVisibility(View.VISIBLE);
                        commentText.setEnabled(true);
                        postingProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(CommentActivity.this, "Error commenting", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });
        loadComments();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Glide.with(CommentActivity.this)
                .load(Constants.getUser().getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(userImageView);
    }
    private void loadComments() {
        //load comments
        final List<Comment> commentList=new ArrayList<>();
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    Comment comment=data.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentsRecyclerView.setAdapter(new CommentsAdapter(commentList,CommentActivity.this));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
