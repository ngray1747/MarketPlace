package com.modul.minhhailib;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.modul.marketplace.app.ApplicationMarketPlace;
import com.modul.marketplace.util.Log;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        com.modul.marketplace.app.ApplicationMarketPlace appli = new ApplicationMarketPlace();
//        appli.initContext(getBaseContext());
//        appli.initBussiness();
//        appli.loadData("O78LYZQLY72G","FOODBOOK","admin","POSPC", new ArrayList(),new ArrayList());
//
//        Log.e("aaaacas","test: "+ appli.getCartBussiness().getCompanyId());
//        Intent intent = new Intent(this, com.modul.marketplace.activity.marketplace.SelectStartAddressActivity.class);
//        startActivity(intent);
    }
}