package com.example.orderfood.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.orderfood.Common.Common;
import com.example.orderfood.Interface.ItemClickListener;
import com.example.orderfood.Model.Category;
import com.example.orderfood.R;
import com.example.orderfood.Service.ListenOrder;
import com.example.orderfood.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class Home extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener{

    SwipeRefreshLayout srlLayout;
    FirebaseDatabase database;
    DatabaseReference reference;
    Toolbar toolbar;
    FloatingActionButton fab;
    DrawerLayout drawer;
    NavigationView navigationView;
    View viewHeader;
    TextView txtFullName;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        addControl();

        toolbar.setTitle("Menu");
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });


        if(Common.isConnectToInternet(getBaseContext())){
            loadMenu();
        } else {
            Toast.makeText(Home.this, "Please Check your connection !!!", Toast.LENGTH_SHORT).show();
            return;
        }
        addEvent();

        // Register service
        Intent service = new Intent(Home.this, ListenOrder.class);
        startService(service);

    }

    private void loadMenu() {
        FirebaseRecyclerOptions<Category> optionsCategory =
                new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(reference,Category.class)
                .build();
         adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(optionsCategory) {
            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.row_menu, parent, false);

                return new MenuViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(MenuViewHolder viewHolder, int i, Category model) {
                viewHolder.txtNameMenu.setText(model.getName());
                Glide.with(getApplicationContext())
                        .load(model.getImage())
                        .placeholder(R.drawable.my_bg)
                        .into(viewHolder.imageMenu);
                final Category clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void OnClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(getApplicationContext(), FoodList.class);
                        intent.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(intent);
                        Toast.makeText(Home.this, ""+ clickItem.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void addEvent() {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Cart.class);
                startActivity(intent);

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        viewHeader = navigationView.getHeaderView(0);
        txtFullName = viewHeader.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentUser.getName());
    }

    @SuppressLint("ResourceAsColor")
    private void addControl() {
        toolbar = findViewById(R.id.toolbar);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Category");
        fab = findViewById(R.id.fab);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        srlLayout = findViewById(R.id.srlLayoutHome);

        srlLayout.setColorSchemeColors(R.color.colorGreen, R.color.colorBlue);
        srlLayout.setOnRefreshListener(this);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Paper.init(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.refresh){
            loadMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatemenWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item){
       switch (item.getItemId()){
           case R.id.nav_menu:
               Toast.makeText(this, "menu", Toast.LENGTH_SHORT).show();
               break;
           case R.id.nav_cart:
               startActivity(new Intent(getApplicationContext(), Cart.class));
               Toast.makeText(this, "cart", Toast.LENGTH_SHORT).show();
               break;
           case R.id.nav_order:
               startActivity(new Intent(getApplicationContext(), OrderStatus.class));
               Toast.makeText(this, "order", Toast.LENGTH_SHORT).show();
               break;
           case R.id.nav_change_password:
               showDialogChangePassword();
               break;
           case R.id.nav_logout:

               // delete user
               Paper.book().destroy();

               Intent intent = new Intent(getApplicationContext(), MainActivity.class);
               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               startActivity(intent);
               Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
               break;
       }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void showDialogChangePassword() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle(R.string.change_password);
        alertDialog.setMessage(R.string.please_fill_all_information);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View viewDialog = layoutInflater.inflate(R.layout.change_password, null);

        final MaterialEditText edtPassword = viewDialog.findViewById(R.id.edtPassword);
        final MaterialEditText edtNewPassword = viewDialog.findViewById(R.id.edtNewPassword);
        final MaterialEditText edtRepeatPassword = viewDialog.findViewById(R.id.edtRepeatPassword);

        alertDialog.setView(viewDialog);
        alertDialog.setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final android.app.AlertDialog waitingDialog = new SpotsDialog.Builder().setContext(Home.this).build();
                waitingDialog.show();

                if (edtPassword.getText().toString().equals(Common.currentUser.getPassword())){
                    if (edtNewPassword.getText().toString().equals(edtRepeatPassword.getText().toString())){
                        HashMap<String, Object> updatePassword = new HashMap<>();
                        updatePassword.put(getString(R.string.firebase_password), edtNewPassword.getText().toString());
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_Users));
                        reference.child(Common.currentUser.getPhone())
                                .updateChildren(updatePassword)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        waitingDialog.dismiss();
                                        Toast.makeText(Home.this, R.string.Password_was_update, Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        waitingDialog.dismiss();
                                        Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }else {
                        waitingDialog.dismiss();
                        Toast.makeText(Home.this, R.string.new_password_does_not_math, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    waitingDialog.dismiss();
                    Toast.makeText(Home.this, R.string.wrong_old_password, Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                loadMenu();
                recyclerView.setAdapter(adapter);
                srlLayout.setRefreshing(false);
            }
        }, 2500);
    }
}
