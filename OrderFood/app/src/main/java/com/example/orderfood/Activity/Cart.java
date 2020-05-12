package com.example.orderfood.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.Common.Common;
import com.example.orderfood.Database.Database;
import com.example.orderfood.Model.MyResponse;
import com.example.orderfood.Model.Notification;
import com.example.orderfood.Model.Order;
import com.example.orderfood.Model.Request;
import com.example.orderfood.Model.Sender;
import com.example.orderfood.Model.Token;
import com.example.orderfood.R;
import com.example.orderfood.Remote.APIService;
import com.example.orderfood.ViewHolder.CartAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity {

    Button btnPlaceOrder;
    TextView txtTotalPrice;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference reference;

    List<Order> cart = new ArrayList<>();
    CartAdapter cartAdapter;
    APIService mService;
    ArrayList<String> address;
    Double lat,log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //add address from Common dataAddress
        address = new ArrayList();
        for (int i = 0; i < Common.dataAddress.size(); i++){
            address.add(Common.dataAddress.get(i).getAddress());
        }

        addControl();
        loadListCart();
        addEvent();
    }

    private void loadListCart() {
        cart = new Database(this).getCarts();
        cartAdapter = new CartAdapter(cart, this);
        cartAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(cartAdapter);
        int total = 0;
        for (Order order : cart) {
            int itemPrice = Integer.parseInt(order.getPrice());
            int itemQuantity = Integer.parseInt(order.getQuantity());
            total += itemPrice * itemQuantity;
        }
            Locale locale = new Locale("en", "US");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

            txtTotalPrice.setText(fmt.format(total));
    }

    private void addEvent() {
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(cart.size() > 0)
                showAlertDialog();
            else
                Toast.makeText(Cart.this, "Your cart is empty !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("Enter your address: ");

        LayoutInflater inflater = this.getLayoutInflater();
        View view_order = inflater.inflate(R.layout.row_send_order,null);

        final AutoCompleteTextView edtAddress = view_order.findViewById(R.id.editTextAddressOrder);
        final MaterialEditText edtComment = view_order.findViewById(R.id.editTextCommentOrder);

        //set data for AutoCompleteTextView
        ArrayAdapter adapterAddress = new ArrayAdapter(this,android.R.layout.simple_list_item_1,address);
        edtAddress.setAdapter(adapterAddress);
        edtAddress.setThreshold(1);

        alertDialog.setView(view_order);
        alertDialog.setIcon(R.drawable.ic_cart);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < Common.dataAddress.size(); i++){
                    if(Common.dataAddress.get(i).getAddress().equals(edtAddress.getText().toString())){
                        lat = Common.dataAddress.get(i).getLatitude();
                        log = Common.dataAddress.get(i).getLongitude();
                    }
                }
                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        edtAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        "0",
                        edtComment.getText().toString(),
                        lat,
                        log,
                        cart);
                String order_number = String.valueOf(System.currentTimeMillis());
                reference.child(order_number).setValue(request);
                new Database(getBaseContext()).cleanToCart();
                loadListCart();
                sendNotificationOrder(order_number);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void sendNotificationOrder(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("serverToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Token serverToken = ds.getValue(Token.class);

                    Notification notification = new Notification("Order Food", "You have new order "+ order_number);
                    Sender content = new Sender(serverToken.getToken(), notification);

                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200) {
                                        if (response.body().success == 1) {
                                            Toast.makeText(Cart.this, "Thank you Order place", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(Cart.this, "Failed!!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addControl() {
        mService = Common.getFCMService();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Requests");
        btnPlaceOrder = findViewById(R.id.btnCartPlaceOrder);
        txtTotalPrice = findViewById(R.id.txtCartTotal);

        recyclerView = findViewById(R.id.recycler_cart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals(Common.DELETE)){
            deleteCart(item.getOrder());
        }
        return true;
    }

    private void deleteCart(int order) {
        cart.remove(order);

        new Database(this).cleanToCart();

        for (Order item: cart)
            new Database(this).addToCart(item);

        loadListCart();
    }
}
