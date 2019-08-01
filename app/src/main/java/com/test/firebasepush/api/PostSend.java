package com.test.firebasepush.api;

import android.content.Context;
import android.util.Log;

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
        JSONObject dataJsonObject = new JSONObject();
        JSONObject notificationJsonObject = new JSONObject();
        JsonObject gsonObject = new JsonObject();
        try {
            jsonObject.put("to", "/topics/push");
            dataJsonObject.put("extra_information", "This is a some extra information");
            //jsonObject.put("data", dataJsonObject);
            notificationJsonObject.put("title", "" + title);
            notificationJsonObject.put("body", "" + data);
            jsonObject.put("notification", notificationJsonObject);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        gsonObject = (JsonObject) jsonParser.parse(jsonObject.toString());

        Call<MessageResponse> responseCall = apiInterface.sendFirebasePush(context.getResources().getString(R.string.key), gsonObject);
        responseCall.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                Log.d("MainActivity", "" + response.isSuccessful());
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Log.d("MainActivity", "" + t.toString());
            }
        });

    }
}
