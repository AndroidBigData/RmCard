package com.zyloushi.zyls.until;

/**
 * Created by Administrator on 2016/3/18 0018.
 */

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 轻量级的缓存，记录用户名、密码等可以用到及保存的数据
 * @author Administrator
 *
 */
public class MyPreference {
    private static MyPreference preference = null;
    private SharedPreferences sharedPreference;
    private String packageName = "";

    private static final String LOGIN_NAME = "loginName"; //登录名
    private static final String PASSWORD = "password";  //密码
    private static final String IS_FLAG = "isFlag"; //是否已经登录
    private static final String UID = "uid"; //记录用户id
    private static final String ENTERNUM = "enterNum"; //记录用户进入的次数
    private static final String ISFLASH = "isFlash"; //记录用户是否是第一次使用app
    private static final String LIULANID = "liulanAid"; //记录用户浏览的楼盘id


    public static synchronized MyPreference getInstance(Context context){
        if(preference == null)
            preference = new MyPreference(context);
        return preference;
    }


    public MyPreference(Context context){
        packageName = context.getPackageName() + "_preferences";
        sharedPreference = context.getSharedPreferences(
                packageName, Context.MODE_PRIVATE);
    }


    public String getLoginName(){
        String loginName = sharedPreference.getString(LOGIN_NAME, "");
        return loginName;
    }


    public void SetLoginName(String loginName){
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(LOGIN_NAME, loginName);
        editor.commit();
    }


    public String getPassword(){
        String password = sharedPreference.getString(PASSWORD, "");
        return password;
    }


    public void SetPassword(String password){
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(PASSWORD, password);
        editor.commit();
    }



    public boolean IsFlag(){
        Boolean isFlag = sharedPreference.getBoolean(IS_FLAG, false);
        return isFlag;
    }


    public void SetIsFlag(Boolean isFlag){
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.putBoolean(IS_FLAG, isFlag);
        edit.commit();
    }

    public String getUid(){
        String uid = sharedPreference.getString(UID, null);
        return uid;
    }
    public void SetUid(String uid){
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(UID, uid);
        editor.commit();
    }


    public boolean IsFirstEnter(){
        Boolean isNum = sharedPreference.getBoolean(ENTERNUM, false);
        return isNum;
    }


    public void SetIsFirstEnter(Boolean isNum){
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.putBoolean(ENTERNUM, isNum);
        edit.commit();
    }

    public boolean IsFlash(){
        Boolean isFlash = sharedPreference.getBoolean(ISFLASH, false);
        return isFlash;
    }


    public void SetIsFlash(Boolean isFlash){
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.putBoolean(ISFLASH, isFlash);
        edit.commit();
    }

    //用户浏览id拼接
    public String getLLid(){
        String llAid = sharedPreference.getString(LIULANID, null);
        return llAid;
    }
    public void SetLLid(String llAid){
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(LIULANID, llAid);
        editor.commit();
    }

}
