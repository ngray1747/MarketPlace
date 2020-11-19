package com.modul.marketplace.restful;



import com.google.gson.Gson;
import com.modul.marketplace.paser.AbsResultData;
import com.modul.marketplace.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiRequest<T> {


    private static final String TAG = "ApiRequest";
    private  int retryCount=0;
    private int countRetry=1;
    public void setCallBack(Call<T> response, Listener<T> res, ErrorListener error) {

        response.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {

                try {
                    Log.i(TAG, "Response: " + response.toString());
                    if (response.isSuccessful()) {

                        res.onResponse(response.body());
                    } else {
                        String body = "";
                        String message = response.message();
                        ApiError apiError =new ApiError(message,response.code(),message);
                        try {
                        //
                            body = response.errorBody().string();
                            Log.d(TAG,"Not succ "+response.errorBody().string()+"/ "+body);
                            AbsResultData errorMessage = new Gson().fromJson(body,AbsResultData.class);
                            message=errorMessage.getError().getMessage();
                            apiError.setMessageBody(message);
                            apiError.setCodeContent(errorMessage.getError().getCode());
                        } catch (Exception e) {
                            Log.d(TAG,"Error parrser "+e.getMessage());
//                            e.printStackTrace();
                            apiError.setMessageBody("Fabi Error: "+e.getMessage());
                        }
                        error.onError(apiError);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG,"Error When process => "+e.getMessage());
                    error.onError(new ApiError(e.getMessage(), 0, ""));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                t.printStackTrace();
                Log.d(TAG, "Error API " + t.getMessage());
                error.onError(new ApiError(t));
                retry(call,this);
            }
        });
    }
    private void retry(Call<T> call,Callback<T> callback) {
        if (countRetry>retryCount) return;
        countRetry++;
        Log.d(TAG,"retry api "+countRetry);
        call.clone().enqueue(callback);
    }

    /**
     * Callback interface for delivering parsed responses.
     */
    public interface Listener<T> {
        /**
         * Called when a response is received.
         */
          void onResponse(T response);
    }

    /**
     * Callback interface for delivering error responses.
     */
    public interface ErrorListener {
        /**
         * Callback method that an error has been occurred with the
         * provided error codeContent and optional user-readable message.
         */
          void onError(ApiError error);
    }
}
