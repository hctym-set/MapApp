package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button button = findViewById(R.id.login);
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        resultTextView = findViewById(R.id.resultTextView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postForm(username.getText().toString(), password.getText().toString());
            }
        });
    }

    private void postForm(final String username, final String password) {
        OkHttpClient okHttpClient = new OkHttpClient();

        // 创建FormBody对象，封装数据（键值对信息）
        FormBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .post(formBody)
                .url("http://192.168.1.106:8090/login")
                .build();

        Call call = okHttpClient.newCall(request);

        // 异步执行网络操作
        call.enqueue(new Callback() {
            // 请求本身失败时调用
            @Override
            public void onFailure(Call call, IOException e) {
                showResultOnUiThread("请求失败");
            }

            // 请求成功时调用
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                showResultOnUiThread(result);
            }
        });
    }

    private void showResultOnUiThread(final String result) {
        // 创建一个Handler对象，用于切换到UI线程
        Handler handler = new Handler(Looper.getMainLooper());

        // 使用Handler的post方法将更新UI的操作提交到UI线程执行
        handler.post(new Runnable() {
            @Override
            public void run() {
                // 在UI线程中设置TextView的文本内容为网络请求的结果
                resultTextView.setText(result);
                Log.d("TAG", "run: " + result);
                if (result.contains("username")) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "输入用户名或密码有错误！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}