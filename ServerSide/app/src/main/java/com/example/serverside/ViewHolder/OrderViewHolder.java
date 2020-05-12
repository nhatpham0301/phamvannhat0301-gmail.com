package com.example.serverside.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.serverside.R;

public class OrderViewHolder extends RecyclerView.ViewHolder{

    public TextView txtId, txtStatus, txtPhone, txtAddress;
    public Button btnEdit, btnRemove, btnDetail, btnDirection;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtId = itemView.findViewById(R.id.txtOrderId);
        txtPhone = itemView.findViewById(R.id.txtOrderPhone);
        txtStatus = itemView.findViewById(R.id.txtOrderStatus);
        txtAddress = itemView.findViewById(R.id.txtOrderAddress);
        btnEdit = itemView.findViewById(R.id.btnEditOrder);
        btnRemove = itemView.findViewById(R.id.btnRemoveOrder);
        btnDetail = itemView.findViewById(R.id.btnDetailOrder);
        btnDirection = itemView.findViewById(R.id.btnDirectionOrder);
    }
}
