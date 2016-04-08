package com.zyloushi.zyls.rmcard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zyloushi.zyls.http.HttpGetThread;
import com.zyloushi.zyls.http.ThreadPoolUtils;
import com.zyloushi.zyls.until.MyPreference;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout signOut;
    private Button query,scanning;
    private EditText num,phone;
    private String num_get,phone_get;
    private RelativeLayout progress;
    private String EXITAPP="再按一次退出程序";
    private long exitTime = 0;
    private final static int SCANNIN_GREQUEST_CODE = 1;
    private String url="http://www.zyloushi.com/extended/recard.php?m=Index&a=CheckCard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        x.Ext.init(getApplication());
        x.Ext.setDebug(true);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        signOut.setOnClickListener(this);
        query.setOnClickListener(this);
        scanning.setOnClickListener(this);
    }

    private void initView() {
        signOut= (LinearLayout) findViewById(R.id.sign_out);
        query= (Button) findViewById(R.id.query);
        scanning= (Button) findViewById(R.id.scanning);
        num= (EditText) findViewById(R.id.num);
        phone= (EditText) findViewById(R.id.phone);
        progress= (RelativeLayout) findViewById(R.id.process_lpmoreDetail);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_out:
                MyPreference.getInstance(getBaseContext()).SetIsFlag(false);
                MyPreference.getInstance(getBaseContext()).SetLoginName("");
                MyPreference.getInstance(getBaseContext()).SetPassword("");
                startActivity(new Intent(getBaseContext(),LoginActivity.class));
                finish();
                break;
            case R.id.query:
                num_get=num.getText().toString().trim();
                phone_get=phone.getText().toString().trim();
                if(num_get==null||num_get.length()==0){
                    Toast.makeText(getBaseContext(),"编号为空!",Toast.LENGTH_SHORT).show();
                }else if (phone_get==null||phone_get.length()==0){
                    Toast.makeText(getBaseContext(),"电话为空!",Toast.LENGTH_SHORT).show();
                }else {
                    progress.setVisibility(View.VISIBLE);
                    queryCard(phone_get,num_get);
                }
                break;
            case R.id.scanning:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                break;
            default:
                break;
        }
    }
    private void queryCard(String name,String num){
        RequestParams params=new RequestParams(url+"&uname=" + name + "&cardnum=" + num);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject datas = new JSONObject(result).getJSONObject("cardlists");
                    if ("1".equals(datas.getString("cardstatus"))){
                        ArrayList<String> list2 = new ArrayList<>();
                        list2.add(datas.getString("total_value"));
                        list2.add(datas.getString("notify_time"));
                        list2.add(datas.getString("end_time"));
                        list2.add(datas.getString("trade_no"));
                        list2.add(phone.getText().toString());
                        startActivity(new Intent(getBaseContext(), queryResultActivity.class).putStringArrayListExtra("rmcard", list2));
                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("该卡已使用或已退款!!!");
                        builder.setIcon(R.mipmap.ic_zyls);
                        builder.setTitle("温馨提示");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.create().show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    String data = new JSONObject(result).getString("cont");
                    if("0".equals(new JSONObject(result).getString("msg"))){
                        Toast.makeText(getBaseContext(),data,Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ConnectivityManager connectivityManager = (ConnectivityManager)getBaseContext().getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if(networkInfo == null || !networkInfo.isAvailable())
                {
                    Toast.makeText(getBaseContext(),"请保持网络畅通!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getBaseContext(),"地址错误!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
               progress.setVisibility(View.GONE);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    String datas=bundle.getString("result");
                    //显示扫描到的内容
                    //这里做出判断,获取的字符串是否含有","且分割后的第一个数组(返金卡编号)长度为13位
                    if (datas.contains(",")&&datas.split(",")[0].length()==13){
                        String [] result=datas.split(",");
                        num.setText(result[0]);
                        phone.setText(result[1]);
                        queryCard(result[1],result[0]);
                    }else {
                        Toast.makeText(getBaseContext(),"数据格式错误",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            // 判断是否在两秒之内连续点击返回键，是则退出，否则不退出
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(getApplicationContext(),EXITAPP ,Toast.LENGTH_SHORT).show();
                // 将系统当前的时间赋值给exitTime
                exitTime = System.currentTimeMillis();
            } else {
                exitApp();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * 退出应用程序的方法，发送退出程序的广播
     */
    private void exitApp() {
        Intent intent = new Intent();
        intent.setAction("net.exitapp");
        this.sendBroadcast(intent);
    }
}
