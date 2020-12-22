 package com.quintus.labs.smarthome.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.quintus.labs.smarthome.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.*;
import java.io.UnsupportedEncodingException;

import androidx.appcompat.app.AppCompatActivity;

public class RoomDetailsActivity extends AppCompatActivity {
    MqttAndroidClient client;
    String stringJson;
    float temperature;
    float humidity;
    float gas;
    int rain;
    int led1=0;
    int led2=0;
    String send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);
        MqttConnectOptions options = new MqttConnectOptions();
        LoginActivity Obj =new LoginActivity();
        options.setUserName("client01");
        options.setPassword("client01".toCharArray());
        String clientId ="client01";

        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://node02.myqtthub.com:1883", clientId);

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("mqtt", "onSuccess");
                    setButtuon((Switch)findViewById(R.id.switch1));
                    setButtuon((Switch)findViewById(R.id.switch2));
                    Sub();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("mqtt", "onFailure");

                }
            });
        }
        catch (MqttException e) {
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
        }
        catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }
    void Sub(){
        String topic = "IoT_BT2";
        int qos = 0;
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
        }
        catch (MqttException e) {
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
                gas=objectJson.getInt("gas");
                rain=objectJson.getInt("rain");
                PB((CircularProgressBar) findViewById(R.id.progress_circular1),temperature);
                PB((CircularProgressBar) findViewById(R.id.progress_circular2),humidity);
                setTex((TextView)findViewById(R.id.textView),temperature);
                setTex((TextView)findViewById(R.id.textView2),humidity);
                Log.d("mqtt", String.valueOf(temperature));
                if(gas>400){
                   TextView myTextView1=(TextView)findViewById(R.id.textView5);
                    myTextView1.setText("Cảnh báo khí gas vượt quá mức!");
                    myTextView1.setTextColor(Color.parseColor("#FF0000"));
                }
                if(gas<=400){
                    TextView myTextView1=(TextView)findViewById(R.id.textView5);
                    myTextView1.setText("Gas ở mức an toàn");
                    myTextView1.setTextColor(Color.parseColor("#00CED1"));
                }
                if(rain==1){
                    TextView myTextView2=(TextView)findViewById(R.id.textView4);
                    myTextView2.setText("Cảnh báo trời mưa!");
                    myTextView2.setTextColor(Color.parseColor("#FF0000"));
                }
                if(rain==0){
                    TextView myTextView2=(TextView)findViewById(R.id.textView4);
                    myTextView2.setText("Trời không mưa");
                    myTextView2.setTextColor(Color.parseColor("#00CED1"));
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
    //set ProgressBar
    void PB(CircularProgressBar pb, float value){

        CircularProgressBar circularProgressBar = pb;
// Set Progress Max
        circularProgressBar.setProgressMax(100f);
// Set Progress
        //     circularProgressBar.setProgress(75f);
// or with animation
        circularProgressBar.setProgressWithAnimation(value, (long)1000);// =1s
// Set ProgressBar Color
        circularProgressBar.setProgressBarColor(Color.BLACK);
// or with gradient
        circularProgressBar.setProgressBarColorStart(Color.WHITE);
        if(circularProgressBar==(CircularProgressBar) findViewById(R.id.progress_circular1)){
            circularProgressBar.setProgressBarColorEnd(Color.RED);
        }
        else if(circularProgressBar==(CircularProgressBar) findViewById(R.id.progress_circular2)){
            circularProgressBar.setProgressBarColorEnd(Color.BLUE);
        }
        circularProgressBar.setProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);
// Set background ProgressBar Color
        circularProgressBar.setBackgroundProgressBarColor(Color.LTGRAY);
// or with gradient
        // circularProgressBar.setBackgroundProgressBarColorStart(Color.WHITE);
        //  circularProgressBar.setBackgroundProgressBarColorEnd(Color.RED);
        // circularProgressBar.setBackgroundProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);

// Set Width
        circularProgressBar.setProgressBarWidth(15f); // in DP
        circularProgressBar.setBackgroundProgressBarWidth(15f); // in DP
    }
    void setTex(TextView myTextView, float value){
        if(myTextView==(TextView)findViewById(R.id.textView)){
            myTextView.setText("Nhiệt độ: "+value+"°C");
        }
        if(myTextView==(TextView)findViewById(R.id.textView2)){
            myTextView.setText("Độ ẩm: "+value+"%");
        }

    }
    void setButtuon(Switch mySwitch){
        if(mySwitch==(Switch)findViewById(R.id.switch1)){
            mySwitch.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    boolean checked = ((Switch) v).isChecked();
                    if (checked) {
                        led1 = 1;
                        send= "{"+ "\"led1\":" +led1+ ", \"led2\":" +led2+"}";
                        Pub(send);
                    } else {
                        led1 = 0;
                        send= "{"+ "\"led1\":" +led1+ ", \"led2\":" +led2+"}";
                        Pub(send);
                    }
                }
            });
        }
        if(mySwitch==(Switch)findViewById(R.id.switch2)){
            mySwitch.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    boolean checked = ((Switch) v).isChecked();
                    if (checked) {
                        led2 = 1;
                        Log.d("mqtt", "led2=1");
                        send= "{"+ "\"led1\":" +led1+ ", \"led2\":" +led2+"}";
                        Pub(send);
                    } else {
                        led2 = 0;
                        Log.d("mqtt", "led2=0");
                        send= "{"+ "\"led1\":" +led1+ ", \"led2\":" +led2+"}";
                        Pub(send);
                    }
                }
            });
        }
    }
}