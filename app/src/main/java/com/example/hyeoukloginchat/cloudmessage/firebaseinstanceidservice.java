package com.example.hyeoukloginchat.cloudmessage;


import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class firebaseinstanceidservice extends FirebaseInstanceIdService {
    private  static  final  String tag = "MyFirebaseIdService";

    @Override
    public void onTokenRefresh() {

     String token = FirebaseInstanceId.getInstance().getToken();
        Log.e(tag,token);
        sendRegistrationToServer(token);
    }
    private  void sendRegistrationToServer(String token){

    }
}

