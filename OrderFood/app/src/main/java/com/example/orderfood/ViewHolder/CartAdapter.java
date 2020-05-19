package com.example.orderfood.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.orderfood.Common.Common;
import com.example.orderfood.Database.Database;
import com.example.orderfood.Interface.ItemClickListener;
import com.example.orderfood.Model.Order;
import com.example.orderfood.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener{

    public TextView txtPrice, txtCartName;
    public ElegantNumberButton btnQuantity;
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        txtPrice = itemView.findViewById(R.id.txtCartItemPrice);
        txtCartName = itemView.findViewById(R.id.txtCartItemPrice);
        btnQuantity = itemView.findViewById(R.id.numberButtonFoodQuantityCart);

        itemView.setOnCreateContextMenuListener(this);

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select action");
        menu.add(0,0,getAdapterPosition(), Common.DELETE);
    }
}

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {

    private List<Order> listOrder = new ArrayList<>();
    private Context context;

    public CartAdapter(List<Order> listOrder, Context context) {
        this.listOrder = listOrder;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, final int position) {
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int itemPrice = Integer.parseInt(listOrder.get(position).getPrice());
        int itemQuantity = Integer.parseInt(listOrder.get(position).getQuantity());
        int price = itemPrice * itemQuantity;
        holder.txtPrice.setText(fmt.format(price));
        holder.txtCartName.setText(listOrder.get(position).getProductName());
        holder.btnQuantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order1 = listOrder.get(position);
                order1.setQuantity(String.valueOf(newValue));
                new Database(context).updateCart(order1);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listOrder.size();
    }
}
