package com.example.afinal;

import android.annotation.SuppressLint;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ViewFlipper viewFlipper;
    private DrawerLayout drawer;
    GridView gridView;
    MainAdapter adapter;
    ImageView imageView;
    DatabaseReference userRef;
    SliderLayout sliderLayout;


    String currentUserId;
//    String categoryType;
//    String[] category = {"Educational","Art","Food","Sport","Musical","Sale"};


    List<String> imageList;

    private final int sports = R.drawable.sports;

    String[] grid = {"Educational","Art","Food","Sport","Musical","Sale"};
   int[] gridimages = {R.drawable.edu, R.drawable.art, R.drawable.food,R.drawable.sports,R.drawable.music,R.drawable.sales};


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       sliderLayout = findViewById(R.id.imageslider);
        sliderLayout.setIndicatorAnimation(IndicatorAnimations.FILL);
        sliderLayout.setScrollTimeInSec(5);
        setSliderViews();






//GridView
        gridView = findViewById(R.id.grid_view);

        MainAdapter adapter = new MainAdapter(MainActivity.this, grid, gridimages);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(), "You clicked "+grid[position],Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                intent.putExtra("categoryType", grid[position]);
                startActivity(intent);

            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("eventsUser");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getUserDetails(navigationView);



      //  viewFlipper = findViewById(R.id.view_image);
//        getImages();



    }


        protected ImageView getNewImageView () {
            ImageView image = new ImageView(getApplicationContext());
            image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            return image;
        }

//    private void getImages() {
//
//        final DatabaseReference query = FirebaseDatabase.getInstance().getReference("uploads").child(categoryType);
//        FirebaseApp.initializeApp(this);
////        mdatabaseRef = FirebaseDatabase.getInstance().getReference("uploads").child("categoryType");
////        mdatabaseRef.addValueEventListener(new ValueEventListener() {
//
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                imageList = new ArrayList<>();
//                if(dataSnapshot.hasChildren()) {
//                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                        Model event = ds.getValue(Model.class);
//
//                        imageList.add(event.getImgUrl());
//                        for (String images : imageList) {
//                            flipperImages(images);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//    }
//    public void flipperImages(String image)
//    {
//        ImageView imageView = new ImageView(this);
//
//        Picasso.get().load(image).into(imageView);
//
//
////        imageView.setBackgroundResource(imageList);
//
//        viewFlipper.addView(imageView);
//        viewFlipper.setFlipInterval(3000); // 4sec
//        viewFlipper.setAutoStart(true);
//
//        //animation
//        viewFlipper.setInAnimation(this,android.R.anim.slide_in_left);
//        viewFlipper.setInAnimation(this,android.R.anim.slide_out_right);
//
//    }

        private void getUserDetails(final NavigationView navigationView){
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            userRef = FirebaseDatabase.getInstance().getReference("User");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.getKey().equals(currentUserId)) {
                                User user = ds.getValue(User.class);
                                TextView name = navigationView.findViewById(R.id.nav_name);
                                TextView email = navigationView.findViewById(R.id.nav_email);
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
        }


//        viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
//        for (int i = 0; i < grid_images.length; i++) {
//            //  This will create dynamic image view and add them to ViewFlipper
//            setFlipperImage(grid_images[i]);
//        }
//    }
//
//
//    private void setFlipperImage(int res) {
//        Log.i("Set Filpper Called", res+"");
//        ImageView image = new ImageView(getApplicationContext());
//        image.setBackgroundResource(res);
//        viewFlipper.addView(image);
//    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater=getMenuInflater();
//        inflater.inflate(R.menu.commonmenu,menu);
//        return true;
//            }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.mnuNotification) {
//            Toast.makeText(this, "Notification menu is clicked", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(MainActivity.this, NotificationActivity.class));
//        } else if (id == R.id.app_bar_search) {
//            Toast.makeText(this, "Search menu is clicked", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(MainActivity.this, SignIn.class));
//        }
//        return super.onOptionsItemSelected(item);
//
//    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_profile:
                // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;

            case R.id.nav_addevent:
                startActivity(new Intent(MainActivity.this,AddEventActivity.class));
                break;
//            case R.id.nav_reviews:
//                startActivity(new Intent(MainActivity.this,ReviewActivity.class));
//                break;
//            case R.id.nav_fav:
//                startActivity(new Intent(MainActivity.this,FavouritesActivity.class));
//                break;
                case R.id.logout:
                startActivity(new Intent(getApplicationContext(),SignIn.class));
                FirebaseAuth.getInstance().signOut();


        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void setSliderViews() {

    String[] slider = {"\"https://firebasestorage.googleapis.com/v0/b/events-b9343.appspot.com/o/uploads%2F1565494944618.jpg?alt=media&token=4282f198-7222-4a02-ba79-e9e2328a3d4a\" );\n" +
            "                "};

        for ( int i = 0; i <= 2; i++) {
            DefaultSliderView sliderView = new DefaultSliderView(this);

            switch (i) {
                case 0:
                    sliderView.setImageUrl("https://firebasestorage.googleapis.com/v0/b/events-b9343.appspot.com/o/uploads%2F1565494944618.jpg?alt=media&token=4282f198-7222-4a02-ba79-e9e2328a3d4a" );

                    break;
                case 1:
                    sliderView.setImageUrl("https://firebasestorage.googleapis.com/v0/b/events-b9343.appspot.com/o/uploads%2F1565495955037.jpg?alt=media&token=ba233151-39c0-4792-a4b8-f083e6555d56");
                    sliderView.setOnSliderClickListener(sliderView1 -> {
                        System.out.println("I am here!!!");
                        Toast.makeText(getApplicationContext(), "clicked image", Toast.LENGTH_SHORT).show();
                    });
                    break;
                case 2:
                    sliderView.setOnSliderClickListener(sliderView1 -> {
                        System.out.println("I am here!!!");
                        Toast.makeText(getApplicationContext(), "clicked image", Toast.LENGTH_SHORT).show();
                    });
                    sliderView.setImageUrl("https://firebasestorage.googleapis.com/v0/b/events-b9343.appspot.com/o/uploads%2F1565495176912.jpg?alt=media&token=25693cb3-f065-4d9f-a550-baa6e69743c8");
                    break;

            }

            sliderView.setOnSliderClickListener(sliderView1 -> {
                System.out.println("I am here!!!");
                Toast.makeText(getApplicationContext(), "clicked image", Toast.LENGTH_SHORT).show();
            });

//            sliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
//                @Override
//                public void onSliderClick(BaseSliderView slider) {
//                    Toast.makeText(mContext, "clicked image="+urls.get(finalI), Toast.LENGTH_SHORT).show();
//                }
//            });

//            sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
//                @Override
//                public void onSliderClick(SliderView sliderView) {
//                    Toast.makeText(MainActivity.this,"fksjdhfdh" ,Toast.LENGTH_SHORT).show();
//
//                    //startActivity(new Intent(MainActivity.this,EventsDetails.class));
//                }
//            });
            sliderLayout.addSliderView(sliderView);




        }




    }

}
