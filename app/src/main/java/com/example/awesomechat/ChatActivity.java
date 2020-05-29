package com.example.awesomechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {


    private ProgressBar progressBar;
    private ImageButton sendImageButton;
    private Button sendMessageButton;
    private EditText messageEditText;
    private androidx.appcompat.widget.Toolbar toolbar;


    private static final int RC_IMAGE_PICKER = 123;
    private String userName;
    private String recepientUserId;
    private String recepientUserName;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference messagesDatabaseReference;
    private ChildEventListener messagesChildEventListener;

    private DatabaseReference usersDatabaseReference;
    private ChildEventListener usersChildEventListener;

    private FirebaseStorage storage;
    private StorageReference chatImageStorageReferences;


    private RecyclerView messageListRecyclerView;
    private RecyclerView.LayoutManager messageLayoutManager;
    private ArrayList<AwesomeMessage> messageArrayList;
    private AwesomeMessageAdapter adapter;

    String datePattern = "MMMM d, hh:mm:ss";
    final SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern, Locale.getDefault());

    @RequiresApi(api = Build.VERSION_CODES.N)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        messageArrayList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        if (intent != null) {
            userName = intent.getStringExtra("userName");
            recepientUserId = intent.getStringExtra("recepientUserId");
            recepientUserName = intent.getStringExtra("recepientUserName");
        }

        setTitle(recepientUserName);

        database = FirebaseDatabase.getInstance();
        messagesDatabaseReference = database.getReference().child("messages");
        usersDatabaseReference = database.getReference().child("users");

        storage = FirebaseStorage.getInstance();
        chatImageStorageReferences = storage.getReference().child("chatImages");

        progressBar = findViewById(R.id.progressBar);
        sendImageButton = findViewById(R.id.sendPhotoButton);
        sendMessageButton = findViewById(R.id.sendMassageButton);
        messageEditText = findViewById(R.id.editMassageText);

        progressBar.setVisibility(ProgressBar.INVISIBLE);

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {
                if (s.toString().trim().length() > 0) {
                    sendMessageButton.setEnabled(true);
                } else {
                    sendMessageButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        messageEditText.setFilters(new InputFilter[]
                {new InputFilter.LengthFilter(200)}
        );

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AwesomeMessage message = setMessage(messageEditText.getText().toString(), null);
                messagesDatabaseReference.push().setValue(message);
                messageEditText.setText("");
            }
        });

        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Choose an image"), RC_IMAGE_PICKER);
            }
        });

        usersChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    userName = user.getName();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        usersDatabaseReference.addChildEventListener(usersChildEventListener);

        messagesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                AwesomeMessage message = dataSnapshot.getValue(AwesomeMessage.class);

                if (message == null) {
                    return;

                }

                if (message.getSender() != null &&
                        message.getSender().equals(auth.getCurrentUser().getUid()) &&
                        message.getRecepient().equals(recepientUserId)) {
                    messageArrayList.add(message);
                    adapter.notifyDataSetChanged();

                } else if (message.getRecepient() != null &&
                        message.getRecepient().equals(auth.getCurrentUser().getUid()) &&
                        message.getSender().equals(recepientUserId)) {
                    messageArrayList.add(message);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        messagesDatabaseReference.addChildEventListener(messagesChildEventListener);

        buildRecyclerView();
    }

    private void buildRecyclerView() {
        messageListRecyclerView = findViewById(R.id.massageListRecycierView);
        messageListRecyclerView.setHasFixedSize(true);

        messageLayoutManager = new LinearLayoutManager(this);
        adapter = new AwesomeMessageAdapter(messageArrayList);
        messageListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageListRecyclerView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ChatActivity.this, SignInActivity.class));
                finishAffinity();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_IMAGE_PICKER && resultCode == RESULT_OK) {
            Uri selectImageUrl = data.getData();
            final StorageReference imageReference = chatImageStorageReferences
                    .child(selectImageUrl.getLastPathSegment());

            UploadTask uploadTask = imageReference.putFile(selectImageUrl);

            uploadTask = imageReference.putFile(selectImageUrl);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        AwesomeMessage message = setMessage(null, downloadUri.toString());
                        messagesDatabaseReference.push().setValue(message);
                    } else {
                    }
                }
            });
        }
    }

    public AwesomeMessage setMessage (String messageText, String messageImg) {
        AwesomeMessage message = new AwesomeMessage();
        if (messageText != null) {
            message.setText(messageEditText.getText().toString());
            message.setImageUrl(null);
        } else if (messageImg != null) {
            message.setImageUrl(messageImg);
            message.setText(null);
        }
        message.setSender(auth.getCurrentUser().getUid());
        message.setRecepient(recepientUserId);
        message.setName(userName);
        message.setMessageDate(dateFormat.format(new Date()));

        return message;
    }
}