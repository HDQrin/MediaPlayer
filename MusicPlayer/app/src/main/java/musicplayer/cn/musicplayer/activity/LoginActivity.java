package musicplayer.cn.musicplayer.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;


import musicplayer.cn.musicplayer.module.Member;
import musicplayer.cn.musicplayer.util.Result;
import musicplayer.cn.myapplication.R;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends FragmentActivity {
    private EditText username_tx;
    private EditText password_tx;
    private Button login_btn;
    public String userName;
    public String userPassword;
    public TextView userNameinfo;


    private Handler loginHandler = new Handler(){
        private Result<Member> memberResult;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    login();
                    break;
                case 1:
                    memberResult = new Gson().fromJson((String)msg.obj, new TypeToken<Result<Member>>(){}.getType());
//                    Log.e("memberResult",memberResult.getValue().toString());
                    memberResult.setCode(0);
                    if(memberResult.getCode().equals(0)){
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, memberResult.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username_tx = findViewById(R.id.username_tx);

        password_tx = findViewById(R.id.password_tx);

        login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();

            }
        });
    }
    public void login(){
        OkHttpClient client = new OkHttpClient();
        setuserName(username_tx.getText().toString());
        setuserPassword(password_tx.getText().toString());
        userName = username_tx.getText().toString();
        userPassword = password_tx.getText().toString();
        RequestBody requestBody = new FormBody.Builder()
                .add("username", userName)
                .add("password", userPassword)
                .build();
        Request request = new Request.Builder().url("http://114.115.216.215:8080/MusicPlayer/login.action?userAccount="+userName+"&userPassword="+userPassword).post(requestBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Message message = loginHandler.obtainMessage();
                message.what = 0;
                loginHandler.sendMessage(message);
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                Message message = loginHandler.obtainMessage();
                message.what = 1;
                message.obj = response.body().string();
                Log.e("message",message.toString());
                loginHandler.sendMessage(message);
            }

        });
    }

    private String setuserPassword(String userPassword) {
        return this.userPassword = userPassword;
    }

    public String setuserName(String userName){
        return this.userName = userName;
    }

}
