package com.zyloushi.zyls.rmcard;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zyloushi.zyls.http.HttpGetThread;
import com.zyloushi.zyls.http.ThreadPoolUtils;
import com.zyloushi.zyls.until.MyPreference;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity {
    private EditText userName,passWord;
    private Button login;
    private String userName_empty="用户名为空，请重新填写！";
    private String passWord_empty="密码为空，请重新填写！";
    private RelativeLayout progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initData();
    }

    private void initData() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userName.getText().toString().trim() == null || userName.getText().toString().trim().length() == 0) {
                    Toast.makeText(getBaseContext(),userName_empty , Toast.LENGTH_SHORT).show();
                }else if (passWord.getText().toString().trim() == null || passWord.getText().toString().trim().length() == 0) {
                    Toast.makeText(getBaseContext(), passWord_empty, Toast.LENGTH_SHORT).show();
                }else {
                    progress.setVisibility(View.VISIBLE);
                    ThreadPoolUtils.execute(new HttpGetThread(handler,
                            "http://www.zyloushi.com/extended/recard.php?uname=" + userName.getText().toString().trim() + "&password=" + passWord.getText().toString().trim()));
                }
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 404) {
                progress.setVisibility(View.GONE);
                Toast.makeText(getBaseContext(), "找不到地址",Toast.LENGTH_SHORT).show();
            } else if (msg.what == 100) {
                progress.setVisibility(View.GONE);
                Toast.makeText(getBaseContext(), "传输失败,请检查网络链接",Toast.LENGTH_SHORT).show();
            } else if (msg.what == 200) {
                String result = (String) msg.obj;
                try {
                    JSONObject object = new JSONObject(result);
                    String cont = object.getString("cont");
                    int flag = object.getInt("msg");
                    if (flag == 1) {
                        MyPreference.getInstance(getBaseContext()).SetIsFlag(true);
                        MyPreference.getInstance(getBaseContext()).SetLoginName(userName.getText().toString().trim());
                        MyPreference.getInstance(getBaseContext()).SetPassword(passWord.getText().toString().trim());
                        startActivity(new Intent(getBaseContext(), MainActivity.class));
                        Toast.makeText(getBaseContext(), cont, Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(getBaseContext(), "无权限用户", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progress.setVisibility(View.GONE);
            }
        }
    };

    private void initView() {
        userName= (EditText) findViewById(R.id.login_userName);
        passWord= (EditText) findViewById(R.id.login_userPass);
        login= (Button) findViewById(R.id.personal_login_btnLogin);
        progress= (RelativeLayout) findViewById(R.id.process_lpmoreDetail);
    }
}
