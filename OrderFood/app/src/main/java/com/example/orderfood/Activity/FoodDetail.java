package com.example.orderfood.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.andremion.counterfab.CounterFab;
import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.orderfood.Common.Common;
import com.example.orderfood.Database.Database;
import com.example.orderfood.Model.Foods;
import com.example.orderfood.Model.Order;
import com.example.orderfood.Model.Rating;
import com.example.orderfood.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener {

    TextView txtFoodName, txtFoodPrice, txtFoodDescription;
    ImageView imageFood;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton fabRating;
    CounterFab fabCart;
    ElegantNumberButton btnNumber;
    RatingBar ratingBar;

    String foodId = "";

    FirebaseDatabase database;
    DatabaseReference reference;
    DatabaseReference ratingDB;

    Foods currentFood;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Add font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/cf.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_food_detail);

        addControl();
        addEvent();
    }

    private void addEvent() {
        btnNumber.setOnClickListener(new ElegantNumberButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = btnNumber.getNumber();
                int price = Integer.parseInt(number) * 1000;
                txtFoodPrice.setText(price+"");
            }
        });


        fabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getApplicationContext()).addToCart(new Order(
                        foodId,
                        currentFood.getName(),
                        btnNumber.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount()
                ));
                Toast.makeText(FoodDetail.this, "Added to cart", Toast.LENGTH_SHORT).show();
            }
        });

        fabCart.setCount(new Database(this).getCountCart());

        fabRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setNegativeButtonText("Cancel")
                .setPositiveButtonText("Submit")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quite Ok", "Very Good", "Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate is good")
                .setDescription("Please select some stars and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here ...")
                .setHintTextColor(android.R.color.white)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetail.this)
                .show();
    }

    private void addControl() {
        txtFoodName = findViewById(R.id.txtNameFoodDetail);
        txtFoodPrice = findViewById(R.id.txtPriceFoodDetail);
        txtFoodDescription = findViewById(R.id.txtDescription);
        imageFood = findViewById(R.id.imageFoodDetail);
        fabCart = findViewById(R.id.fabCartFoodDetail);
        btnNumber = findViewById(R.id.numberButtonFoodDetail);
        fabRating = findViewById(R.id.fabFoodDetailRating);
        ratingBar = findViewById(R.id.ratingBar);

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Foods");
        ratingDB =  FirebaseDatabase.getInstance().getReference("Rating");

        if(getIntent() != null)
            foodId = getIntent().getStringExtra("FoodId");
        if(!foodId.isEmpty() && foodId != null){

            if(Common.isConnectToInternet(getBaseContext())) {
                loadFoodDetail(foodId);
                loadRatingFood(foodId);
            } else {
                Toast.makeText(FoodDetail.this, "Please Check your connection !!!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void loadRatingFood(String foodId) {
       Query foodRating = ratingDB.orderByChild("foodId").equalTo(foodId);

        foodRating.addValueEventListener(new ValueEventListener() {
            int count = 0, sum =0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){

                    Rating item = ds.getValue(Rating.class);
                    sum += Integer.parseInt(item.getRateValue());
                    count ++;
                }
                if(count != 0){
                    float average = sum/count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadFoodDetail(String Id) {
        reference.child(Id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Foods.class);

                Glide.with(getApplicationContext())
                        .load(currentFood.getImage())
                        .placeholder(R.drawable.my_bg)
                        .into(imageFood);
                collapsingToolbarLayout.setTitle(currentFood.getName());
                txtFoodName.setText(currentFood.getName());
                txtFoodPrice.setText(currentFood.getPrice());
                txtFoodDescription.setText(currentFood.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int value, String comment) {
        final String phone = Common.currentUser.getPhone();
        final Rating rating = new Rating(phone,foodId,String.valueOf(value),comment);

       ratingDB.child(phone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(phone).exists()){
                    ratingDB.child(phone).removeValue();
                    ratingDB.child(phone).setValue(rating);
                } else {
                    ratingDB.child(phone).setValue(rating);
                }
                Toast.makeText(FoodDetail.this, "Thank you for submit rating !!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
