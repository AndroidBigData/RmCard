package com.zyloushi.zyls.rmcard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zyloushi.zyls.http.HttpGetThread;
import com.zyloushi.zyls.http.ThreadPoolUtils;
import com.zyloushi.zyls.until.MyPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class queryResultActivity extends BaseActivity {
    private Button use;
    private TextView buy_date,to_date,num,price;
    private LinearLayout back;
    private RelativeLayout progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_result);
        initView();
        initData();
    }

    private void initView() {
        back= (LinearLayout) findViewById(R.id.back);
        progress= (RelativeLayout) findViewById(R.id.process_lpmoreDetail);
        use= (Button) findViewById(R.id.use);
        buy_date= (TextView) findViewById(R.id.buy_date);
        to_date= (TextView) findViewById(R.id.to_date);
        price= (TextView) findViewById(R.id.price);
        num= (TextView) findViewById(R.id.textView11);
    }

    private void initData() {
        final ArrayList<String> rmcard = getIntent().getStringArrayListExtra("rmcard");
        price.setText(rmcard.get(0));
        buy_date.setText(rmcard.get(1));
        to_date.setText(rmcard.get(2));
        num.setText(rmcard.get(3));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                ThreadPoolUtils.execute(new HttpGetThread(handler,
                        "http://www.zyloushi.com/extended/recard.php?m=Index&a=ConsCard&uname=" + rmcard.get(4) + "&cardnum=" + num.getText()+"&mname="+ MyPreference.getInstance(getBaseContext()).getLoginName()));
            }
        });
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 404) {
                progress.setVisibility(View.GONE);
                Toast.makeText(getBaseContext(), "找不到地址", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 100) {
                progress.setVisibility(View.GONE);
                Toast.makeText(getBaseContext(), "传输失败,请检查网络链接",Toast.LENGTH_SHORT).show();
            } else if (msg.what == 200) {
                String result = (String) msg.obj;
                progress.setVisibility(View.GONE);
                try {
                    if("1".equals(new JSONObject(result).getString("msg"))){
                        AlertDialog.Builder builder = new AlertDialog.Builder(queryResultActivity.this);
                        builder.setMessage("该卡已成功使用!");
                        builder.setIcon(R.mipmap.ic_zyls);
                        builder.setTitle("温馨提示");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        builder.create().show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
