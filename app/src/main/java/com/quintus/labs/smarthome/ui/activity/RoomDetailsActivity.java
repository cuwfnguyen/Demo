package com.quintus.labs.smarthome.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quintus.labs.smarthome.R;
import com.quintus.labs.smarthome.adapter.SingleRoomAdapter;
import com.quintus.labs.smarthome.model.Room;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class RoomDetailsActivity extends AppCompatActivity {
    private List<Room> roomList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SingleRoomAdapter mAdapter;
    MqttAndroidClient client;
    String stringJson;
    float temperature;
    float humidity;
    int led1=0;
    int led2=0;
    String send;
    public static void setWindowFlag(Activity activity, final int bits, boolean on) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

        recyclerView = findViewById(R.id.recycler_view);

        mAdapter = new SingleRoomAdapter(roomList, getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        prepareRoomData();
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://mqtt.eclipse.org:1883", clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("mqtt", "onSuccess");
                   // setButtuon((Switch)findViewById(R.id.switch1));
                   // setButtuon((Switch)findViewById(R.id.switch2));
                    Sub();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("mqtt", "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    void Pub(String content){
        String topic = "IoT_BT1";
        String payload = content;
        byte[] encodedPayload = new byte[0];

        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }
    void Sub(){
        String topic = "IoT_BT2";
        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("mqtt","subcribe success");
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Log.d("mqtt", "onFailure: ");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("mqtt", message.toString());
                stringJson=message.toString();
                JSONObject objectJson = new JSONObject(stringJson);
                temperature=objectJson.getInt("temperature");
                humidity=objectJson.getInt("humidity");
               // PB((CircularProgressBar) findViewById(R.id.progress_circular1),temperature);
                //PB((CircularProgressBar) findViewById(R.id.progress_circular2),humidity);
                setTex((TextView)findViewById(R.id.temp),temperature);
                setTex((TextView)findViewById(R.id.hum),humidity);
                Log.d("mqtt", String.valueOf(temperature));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private void prepareRoomData() {
        Room room = new Room("1", "Light");
        roomList.add(room);
        room = new Room("2", "Fan");
        roomList.add(room);
        room = new Room("3", "TV");
        roomList.add(room);
        room = new Room("4", "Table Light");
        roomList.add(room);
        room = new Room("5", "Stand Fan");
        roomList.add(room);

        mAdapter.notifyDataSetChanged();
    }

    public void onBackClicked(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
    void setTex(TextView myTextView, float value){
        if(myTextView==(TextView)findViewById(R.id.temp)){
            myTextView.setText(value+"°C");
        }

        if(myTextView==(TextView)findViewById(R.id.hum)){
            myTextView.setText(value+"%");
        }

    }
}
