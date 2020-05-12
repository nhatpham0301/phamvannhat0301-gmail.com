package com.example.serverside.Common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.example.serverside.Model.Request;
import com.example.serverside.Model.User;
import com.example.serverside.Remote.APIService;
import com.example.serverside.Remote.IGeoCoordinates;
import com.example.serverside.Remote.RetrofitClient;

import java.util.ArrayList;

public class Common {
    public static User currentUser;
    public static Request currentRequest;

    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";

    public static final String baseUrl = "https://maps.googleapis.com";

    public static final int PICK_IMAGE_REQUEST = 71;

    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static String convertCodeToStatus(String code) {
        if(code.equals("0"))
            return "Placed";
        else if(code.equals("1"))
            return "On my way";
        else return "Shipped";
    }

    public static IGeoCoordinates getGeoCodeService(){
        return RetrofitClient.getRetrofit(baseUrl).create(IGeoCoordinates.class);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight){
        Bitmap scaleBitmap2 = Bitmap.createBitmap(newWidth, newHeight,Bitmap.Config.ARGB_8888);

        float scaleX = newWidth /(float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0; float pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas = new Canvas(scaleBitmap2);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaleBitmap2;
    }

}
