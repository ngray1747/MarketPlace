package com.modul.marketplace.restful;


import com.modul.marketplace.model.marketplace.AddressModelData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIInterface {
//    @POST(SCM_CITIES)
//    Call<AddressModelData> login(@Body DmEmployee dmEmployee);

    @GET("cities")
    Call<AddressModelData> apiSCMCity(@Query("results_per_page") int results);
}