package com.modul.marketplace.restful;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.modul.marketplace.app.ApplicationMarketPlace;
import com.modul.marketplace.model.marketplace.AddressModel;
import com.modul.marketplace.model.marketplace.AddressModelData;
import com.modul.marketplace.model.marketplace.AhamoveSearchData;
import com.modul.marketplace.model.marketplace.ArticlesCountModelData;
import com.modul.marketplace.model.marketplace.ArticlesImageModelData;
import com.modul.marketplace.model.marketplace.ArticlesModel;
import com.modul.marketplace.model.marketplace.ArticlesModelData;
import com.modul.marketplace.model.marketplace.ArticlesModelDataObject;
import com.modul.marketplace.model.marketplace.FeedbackModelData;
import com.modul.marketplace.model.marketplace.LocationModelData;
import com.modul.marketplace.model.marketplace.LocationModelDataObject;
import com.modul.marketplace.model.marketplace.NvlModelData;
import com.modul.marketplace.model.marketplace.NvlOnlineModelData;
import com.modul.marketplace.model.marketplace.NvlOnlineModelDataList;
import com.modul.marketplace.model.marketplace.TagsModelData;
import com.modul.marketplace.util.SharedPref;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


public class WSRestFull extends AbsRestful {

    public WSRestFull(Context context) {
        this.context = context;
        config = new SharedPref(context);
    }

    public void apiSCMUpload(File filePath, Response.Listener<ArticlesImageModelData> onresponse) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file",filePath.getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"),filePath))
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(ApplicationMarketPlace.instance.getSCM_LINK() +SCM_UPLOAD + "upload/image?thumb_size=100, 100")
                .method("POST", body)
                .addHeader("access-token", ApplicationMarketPlace.instance.getSCM_ACCESS_TOKEN())
                .addHeader("secret-key", ApplicationMarketPlace.instance.getSCM_SECRET_KEY())
                .build();

//            okhttp3.Response response = client.newCall(request).execute();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    onresponse.onResponse(new Gson().fromJson(response.body().string(), ArticlesImageModelData.class));
                }
            });
    }
}