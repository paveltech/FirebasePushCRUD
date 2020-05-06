package com.test.firebasepush.api;

import android.content.Context;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.test.firebasepush.R;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostSend {

    public Context context;
    public ApiInterface apiInterface;

    public PostSend(Context context) {
        this.context = context;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
    }

    public void sendTopicNotification(String title, String data) {

        JSONObject jsonObject = new JSONObject();


        JSONObject notificationJsonObject = new JSONObject();
        JsonObject gsonObject = new JsonObject();

        JSONObject dataObject = new JSONObject();


        try {
            jsonObject.put("to", "/topics/general");

            notificationJsonObject.put("title", "" + title);
            notificationJsonObject.put("message", "" + data);
            dataObject.put("data", notificationJsonObject);
            jsonObject.put("data", dataObject);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        gsonObject = (JsonObject) jsonParser.parse(jsonObject.toString());

        Log.d("NOTIFICATION_JSON", "" + jsonObject.toString());

        Call<MessageResponse> responseCall = apiInterface.sendFirebasePush(context.getResources().getString(R.string.key), gsonObject);
        responseCall.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                Log.d("NOTIFICATION", "" + response.isSuccessful());
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Log.d("NOTIFICATION", "" + t.toString());
            }
        });

    }


}
