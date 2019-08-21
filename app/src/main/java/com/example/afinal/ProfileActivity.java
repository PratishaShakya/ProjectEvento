package com.example.afinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    public Button logout;
  public TextView myProfile , myEmail , username ;


    DatabaseReference userRef, eventRef;
    String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = firebaseAuth.getInstance();
//        myProfile = (TextView) findViewById(R.id.my_profile);
//        myEmail = (TextView) findViewById(R.id.profile_email);
//        username = (TextView) findViewById(R.id.profile_name);

            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            userRef = FirebaseDatabase.getInstance().getReference("User");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.getKey().equals(currentUserId)) {
                                User user = ds.getValue(User.class);
                                TextView name = findViewById(R.id.profile_name);
                                TextView email = findViewById(R.id.profile_email);
                                name.setText(user.userName);
                                email.setText(user.userEmail);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        getSupportActionBar().setTitle("Evento Profile");

        }

}
