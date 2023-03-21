package com.glootech.chatbit.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.glootech.chatbit.Adapter.MessagesAdapter;
import com.glootech.chatbit.ModelClass.Messages;
import com.glootech.chatbit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String reciverImage,reciverUID,reciverName,senderUID;
    CircleImageView profileImage;
    TextView reciverNames;
    FirebaseDatabase database;
    FirebaseAuth auth;
    public static String senderImage;
    public static String recImage;

    RecyclerView messageAdapter;
    ArrayList<Messages> messagesArrayList;

    CardView sendBtn;
    EditText edtMessage;

    String senderRoom,receiverRoom;

    MessagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        reciverImage = getIntent().getStringExtra("ReciverImage");
        reciverName = getIntent().getStringExtra("name");
        reciverUID = getIntent().getStringExtra("uid");
        senderUID = auth.getUid();

        messagesArrayList = new ArrayList<>();

        senderRoom = senderUID+reciverUID;
        receiverRoom = reciverUID+senderUID;

        profileImage = findViewById(R.id.profile_image);
        reciverNames = findViewById(R.id.reciverNames);
        sendBtn = findViewById(R.id.sendBtn);
        edtMessage = findViewById(R.id.edtMessage);
        messageAdapter = findViewById(R.id.messageAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdapter.setLayoutManager(linearLayoutManager);
        adapter = new MessagesAdapter(ChatActivity.this,messagesArrayList);
        messageAdapter.setAdapter(adapter);

        Picasso.get().load(reciverImage).into(profileImage);
        reciverNames.setText(reciverName);

        DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
        DatabaseReference chatReference = database.getReference().child("chats").child(senderRoom).child("messages");


        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messagesArrayList.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImage = snapshot.child("imageUri").getValue().toString();
                recImage = reciverImage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edtMessage.getText().toString();

                if(message.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"please enter something",Toast.LENGTH_SHORT).show();
                    return;
                }
                edtMessage.setText("");
                Date date = new Date();

                Messages messages = new Messages(message,senderUID,date.getTime());

                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push()
                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                database.getReference().child("chats")
                                        .child(receiverRoom)
                                        .child("messages")
                                        .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                            }
                        });


            }
        });


    }
}