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
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.quintus.labs.smarthome.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class LoginActivity extends AppCompatActivity {
    MqttAndroidClient client;
    String user;
    String pass;

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
        setContentView(R.layout.activity_login);

    }
    public void onLoginClicked(View view) {
        EditText editText1 = (EditText) findViewById(R.id.username);
        EditText editText2 = (EditText) findViewById(R.id.password);
        user = String.valueOf(editText1.getText());
        pass = String.valueOf(editText2.getText());
        //if (user)
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(user);
        options.setPassword(pass.toCharArray());
        String clientId = user;

        if (user.length()!=8) {
            Toast.makeText(getApplicationContext(), "Nhập sai tài khoản hoặc mật khẩu", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        else {
            if(!(user.substring(0,6)).equals("client")){
                Toast.makeText(getApplicationContext(), "Nhập sai tài khoản hoặc mật khẩu", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }

            else {
                try {
                    client = new MqttAndroidClient(this.getApplicationContext(), "tcp://node02.myqtthub.com:1883", clientId);
                    IMqttToken token = client.connect(options);
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // We are connected
                            Log.d("mqtt", "onSuccess1");
                            try {
                                client.disconnect();
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getApplicationContext(), "Đăng nhập thành công", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            // Something went wrong e.g. connection timeout or firewall problems
                            Log.d("mqtt", "onFailure1");

                        }
                    });
                } catch (MqttException e) {

                    e.printStackTrace();
                }
            }
        }
    }
}
