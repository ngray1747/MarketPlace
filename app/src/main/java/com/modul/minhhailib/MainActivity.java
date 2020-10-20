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
//        appli.loadData("O78LYZQLY72G","admin","POSPC");
//        appli.AddLink("https://apiscmdev.ipos.vn/api/partners/v1/","4HPR9FGCBEITGBYW7EYPVZEAJCFGMYKDSRGC","42e78966590635e6e88ac9beec4e01ea","https://apibilling.iposdev.com/billing/api/","GYIXZKY3UOQNF55S37IGUNWYLN5SPRFY");
//
//        Log.e("aaaacas","test: "+ appli.getCartBussiness().getCompanyId());
//        Intent intent = new Intent(this, com.modul.marketplace.activity.marketplace.SelectStartAddressActivity.class);
//        startActivity(intent);
    }
}