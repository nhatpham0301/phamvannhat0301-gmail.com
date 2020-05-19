package com.example.orderfood.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.widget.Toast;

import com.example.orderfood.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "EatIt.db";
    private static final int DB_VER = 1;
    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public List<Order> getCarts() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"ID","ProductId, ProductName, Quantity, Price, Discount"};
        String sqlTable = "OrderDetail";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db,sqlSelect, null, null, null, null, null);

        final List<Order> result = new ArrayList<>();
        if(c.moveToFirst())
        {
            do {
                result.add(new Order(c.getString(c.getColumnIndex("ID")),
                                     c.getString(c.getColumnIndex("ProductId")),
                                     c.getString(c.getColumnIndex("ProductName")),
                                     c.getString(c.getColumnIndex("Quantity")),
                                     c.getString(c.getColumnIndex("Price")),
                                     c.getString(c.getColumnIndex("Discount"))
                                    ));
            } while (c.moveToNext());
        }
        return result;
    }

    public void addToCart(Order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO OrderDetail(ProductId, ProductName, Quantity, Price, Discount) VALUES('%s','%s','%s','%s','%s');",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount());
        db.execSQL(query);
    }

    public void cleanToCart() {
        SQLiteDatabase db = getReadableDatabase();
        //String query = String.format("DELETE FROM OrderDetail");
        String query = String.format("UPDATE OrderDetail SET Quantity= '2' WHERE ID = 1");
        db.execSQL(query);
    }

    //Favorites

    public void addToFavorite(String idFood){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Favorites(FoodId) VALUES('%S');",
                idFood);
        db.execSQL(query);
    }

    public void removeFromFavorites(String idFood){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Favorites WHERE FoodId = '%s';", idFood);
        db.execSQL(query);
    }

    public boolean isFavorite(String idFood){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM Favorites WHERE FoodId = '%s';",idFood);
        Cursor cursor =  db.rawQuery(query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public int getCountCart() {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM OrderDetail;");
        Cursor cursor =  db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                count = cursor.getInt(0);
            }while (cursor.moveToNext());
        }
        return count;
    }

    public void updateCart(Order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity= %s WHERE ID = %d",order.getQuantity(),order.getID());
        //String query = String.format("UPDATE OrderDetail SET Quantity= '2' WHERE ID = 1");
        db.execSQL(query);
    }
}
