package com.example.orderfood.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.orderfood.Model.Address;
import com.example.orderfood.Model.User;
import com.example.orderfood.Remote.APIService;
import com.example.orderfood.Remote.RetrofitClient;

import java.util.ArrayList;

public class Common {
    public static User currentUser;

    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static String convertCodeToStatus(String code) {
        if (code.equals("0"))
            return "Placed";
        else if (code.equals("1"))
            return "On my way";
        else return "Shipped";
    }

    public static final String DELETE  = "Delete";
    public static final String USER_KEY  = "User";
    public static final String PWD_KEY  = "Password";

    public static boolean isConnectToInternet(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null){
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();

            if(info != null){
                for(int i = 0; i< info.length; i++){
                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static ArrayList<Address> dataAddress = new ArrayList<Address>(){{
       add(new Address("1 Võ Văn Ngân, Linh Chiểu, Thủ Đức, Hồ Chí Minh, Việt Nam", 10.849740,106.770241));
    }};
}
