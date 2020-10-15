package com.modul.minhhailib;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.modul.marketplace.app.ApplicationIpos;
import com.modul.marketplace.util.Log;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        com.modul.marketplace.app.ApplicationIpos appli = new ApplicationIpos();
//        appli.initContext(getBaseContext());
//        appli.initBussiness();
//        appli.loadData("aaaa","bbb","aaaa","aaaa", new ArrayList(),new ArrayList());
//
//        Log.e("aaaacas","test: "+ appli.getCartBussiness().getCompanyId());
    }
}