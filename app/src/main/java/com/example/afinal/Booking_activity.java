package com.example.afinal;

import android.app.ProgressDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.amazonaws.regions.ServiceAbbreviations.Email;

public class Booking_activity extends AppCompatActivity {

    EditText useremail, username, phonenumber, password, no_of_tickets;
    Button booking, cancel_booking;
    String email, name, phn, tickets,title;


    private DatabaseReference mdatabaseRef;
    ProgressDialog mProgressDialog;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_activity);
        useremail = findViewById(R.id.booking_email);
        username = findViewById(R.id.booking_username);
        phonenumber = findViewById(R.id.booking_phonenumber);
        password = findViewById(R.id.booking_password);
        no_of_tickets = findViewById(R.id.booking_tickets);
        booking = findViewById(R.id.book_button);
        cancel_booking = findViewById(R.id.cancelbook_button);

        mProgressDialog = new ProgressDialog(Booking_activity.this);
        mdatabaseRef = FirebaseDatabase.getInstance().getReference("Booking details");

        booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   validate(useremail.getText().toString().trim(), password.getText().toString().trim());
                getData();
                startActivity(new Intent(Booking_activity.this,EventsDetails.class));

            }
        });

        cancel_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


    }
//    private Boolean validate(){
//        Boolean res=false;
//        String email = useremail.getText().toString();
//        String name = username.getText().toString();
//        String phone = phonenumber.getText().toString();
//        String pwd = password.getText().toString();
//
//
//        if (email.isEmpty() && name.isEmpty() && phone.isEmpty() && pwd.isEmpty() ) {
//            Toast.makeText(Booking_activity.this, " fields should not be blank ", Toast.LENGTH_SHORT).show();
//        }
//        else {
//           if (email.equals("") && password.equals(""))
//           {
//
//           }
//        }
//        return res;


    //  }

    public void getData() {
        email = useremail.getText().toString().trim();
        name = username.getText().toString().trim();
        phn = phonenumber.getText().toString().trim();
        tickets = no_of_tickets.getText().toString().trim();

        final Pattern phonePattern = Pattern.compile(
                "^"+ "(0|94)" + "\\d{9,10}");



        if (email.isEmpty()) {
            useremail.setError("Email is required");
            return;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            Toast.makeText(this, "Email pattern is wrong", Toast.LENGTH_SHORT).show();}
//if(email.equals(useremail)){
//    Toast.makeText(this,"Email Matched",Toast.LENGTH_SHORT).show();
//}
//else{
//    Toast.makeText(this,"Email Missmatched",Toast.LENGTH_SHORT).show();
//}
//        if (name.isEmpty()) {
//            username.setError(Username  ");
//            return;
//        }

        if (phn.equals("")) {
            phonenumber.setError("Phone number is required ");
            return;
        }
        else if(!phonePattern.matcher(phn).matches()){

            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
        }


        if (tickets.equals("")) {
            no_of_tickets.setError("Ticket field shouldnot be blank ");
            return;
        }
//        else if(tickets > 5)
//        {
//            Toast.makeText(this, "One cannot book more than 5 tickets", Toast.LENGTH_SHORT).show();
//        }


        uploadFile(email, name, phn, tickets);



    }

    public void uploadFile(final String email, final String name, final String phn, final String tickets ) {


        File file = new File(email, name, phn, tickets);
        mdatabaseRef.push().setValue(file).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    finish();
                }
            }
        });
        Toast.makeText(Booking_activity.this, "booking details uploaded successful", Toast.LENGTH_LONG).show();
                           sendNotifications();

    }
    private void validate(String userEmail, String userPassword) {


        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    //Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    checkEmailVerification();
                    FirebaseMessaging.getInstance().subscribeToTopic("eventsUser");

                }else{
                    Toast.makeText(Booking_activity.this, "Book Failed", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

    private void checkEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        Boolean emailflag = firebaseUser.isEmailVerified();

        startActivity(new Intent(Booking_activity.this, MainActivity.class));

    }
    public void sendNotifications() {

        ApiInterface notificationInterface = RetrofitClient.getFormData().create(ApiInterface.class);
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("to", "/topics/eventsAdmin");

        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("title", "evento");
        jsonObject1.addProperty("body", " Tickets");
        jsonObject1.addProperty("content_available", true);
        jsonObject1.addProperty("priority", "high");
        jsonObject1.addProperty("sound", "default");
        jsonObject.add("notification", jsonObject1);

        System.out.println("Json : " + jsonObject);

        Call<JsonObject> call = notificationInterface.sendNotification(jsonObject);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(Booking_activity.this, "Send notification sucessfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });


    }
}