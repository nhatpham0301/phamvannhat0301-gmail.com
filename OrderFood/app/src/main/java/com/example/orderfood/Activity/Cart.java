package com.example.orderfood.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.Common.Common;
import com.example.orderfood.Common.Config;
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
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Cart extends AppCompatActivity {

    public static final int PAYPAL_REQUEST_CODE = 999;

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

    // PayPal payment
    static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    String address_pay, comment;


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
        setContentView(R.layout.activity_cart);

        // Init payPal;
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

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

                // Show PayPal to payment
                // First get Address and Comment from alert Dialog
                address_pay = edtAddress.getText().toString();
                comment = edtComment.getText().toString();

                String formatAmount = txtTotalPrice.getText().toString()
                        .replace("$", "")
                        .replace(",", "");

                float amount = Float.parseFloat(formatAmount);
                PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(amount),
                        getString(R.string.USD),
                        getString(R.string.App_Order_Food),
                        PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
                startActivityForResult(intent, PAYPAL_REQUEST_CODE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetail = confirmation.toJSONObject().toString(4);
                        JSONObject jsonObject = new JSONObject(paymentDetail);

                        // Create new request
                        Request request = new Request(
                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                address_pay,
                                txtTotalPrice.getText().toString(),
                                "0",
                                comment,
                                jsonObject.getJSONObject("response").getString("state"),
                                lat,
                                log,
                                cart
                        );

                        // Submit FireBase
                        // We will using System.CurrentMilli to key
                        String order_number = String.valueOf(System.currentTimeMillis());
                        reference.child(order_number)
                                .setValue(request);
                        new Database(getBaseContext()).cleanToCart();
                        Toast.makeText(Cart.this, "Thank you Order place", Toast.LENGTH_SHORT).show();
                        loadListCart();
                        sendNotificationOrder(order_number);
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "Payment cancel", Toast.LENGTH_SHORT).show();
            else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
                Toast.makeText(this, "Invalid Payment", Toast.LENGTH_SHORT).show();
        }
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
