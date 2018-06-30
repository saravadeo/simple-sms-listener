package com.hilti.sms_listener;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiver = null;
    private TextView title, content;
    final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 12903;

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle bundle = intent.getExtras();
                    readLatestSms(bundle);
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
            intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
            this.registerReceiver(broadcastReceiver, intentFilter);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title = (TextView) findViewById(R.id.title);
        content = (TextView) findViewById(R.id.content);
        requestPermission();
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_SMS},
                    MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECEIVE_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this.getApplicationContext(), "Permission granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this.getApplicationContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void readLatestSms(Bundle bundle) {
        Object[] message = (Object[]) bundle.get("pdus");
        String smsBody = "";
        String address = "";
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < message.length; i++) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) message[i]);
            smsBody = smsMessage.getMessageBody().toString();
            address = smsMessage.getOriginatingAddress();
            title.setText(address);
            content.setText(smsBody);
        }
        if (broadcastReceiver != null) {
            this.unregisterReceiver(broadcastReceiver);
        }
    }
}
