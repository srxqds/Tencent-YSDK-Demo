package com.tencent.tmgp.fszr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.tmgp.fszr.appearance.ResultView;
import com.tencent.tmgp.fszr.appearance.Util;
import com.tencent.tmgp.fszr.module.BaseModule;
import com.tencent.tmgp.fszr.module.ModuleManager;
import com.tencent.ysdk.api.YSDKApi;
import com.tencent.ysdk.framework.common.BaseRet;
import com.tencent.ysdk.framework.common.eFlag;
import com.tencent.ysdk.framework.common.ePlatform;
import com.tencent.ysdk.module.user.UserLoginRet;

import java.util.ArrayList;

/**
 * 说明:
 * 每个模块相关的接口调用示例在 .module.submodule 中;
 * 每个接口的详细使用说明在 jni/CommonFiles/YSDKApi.h 中;
 * PlatformTest是 YSDK c++ 接口调用示例;
 * 标签 TODO GAME 标识之处是游戏需要关注并处理的!!
 */
public class MainActivity extends Activity {
    //TODO 选择java还是cpp
    private String LANG = "java";// 开发语言 java cpp

    private ArrayList<BaseModule> nameList;
    private BaseModule seletedModule;

    public static ProgressDialog mAutoLoginWaitingDlg;
    public static ProgressDialog mProgressDialog;
    public static GridView mModuleListView;
    public static LinearLayout mModuleView;
    public static LinearLayout mResultView;
    public LocalBroadcastManager lbm;
    public BroadcastReceiver mReceiver;
    public static String title = "";
    public static String callAPI = "";
    public static String desripton = "";

    public static final String LOG_TAG = "YSDK DEMO";
    public static final String LOCAL_ACTION = "com.tencent.ysdkdemo";

    protected static int platform = ePlatform.None.val();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO GAME 游戏需自行检测自身是否重复, 检测到吃重复的Activity则要把自己finish掉
        // 注意：游戏需要加上去重判断finish重复的实例，否则可能发生重复拉起游戏的问题。游戏可自行决定重复的判定。
        if (YSDKApi.isDifferentActivity(this)) {
            Log.d(LOG_TAG,"Warning!Reduplicate game activity was detected.Activity will finish immediately.");
            this.finish();
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_layout);

        YSDKApi.onCreate(this);

        // 设置java层或c++层回调,如果两层都设置了则会只回调到java层
        if (LANG.equals("java")) {
            // 全局回调类，游戏自行实现
            YSDKApi.setUserListener(new YSDKCallback(this));
            YSDKApi.setBuglyListener(new YSDKCallback(this));
        } else {
            PlatformTest.setActivity(this);
        }

        // YSDKDemo 界面实现
        initView();

        // TODO GAME 处理游戏被拉起的情况
        // launchActivity的onCreat()和onNewIntent()中必须调用
        Log.d(LOG_TAG,"LoginPlatform is not Hall");
        YSDKApi.handleIntent(this.getIntent());
    }

    static{
//        System.loadLibrary("YSDKDemo"); // 游戏不需要这个
    }

    // TODO GAME 游戏需要集成此方法并调用YSDKApi.onRestart()
    @Override
    protected void onRestart() {
        super.onRestart();
        YSDKApi.onRestart(this);
    }

    // TODO GAME 游戏需要集成此方法并调用YSDKApi.onResume()
    @Override
    protected void onResume() {
        super.onResume();
        YSDKApi.onResume(this);
    }

    // TODO GAME 游戏需要集成此方法并调用YSDKApi.onPause()
    @Override
    protected void onPause() {
        super.onPause();
        YSDKApi.onPause(this);
    }

    // TODO GAME 游戏需要集成此方法并调用YSDKApi.onStop()
    @Override
    protected void onStop() {
        super.onStop();
        YSDKApi.onStop(this);
    }

    // TODO GAME 游戏需要集成此方法并调用YSDKApi.onDestory()
    @Override
    protected void onDestroy() {
        super.onDestroy();
        YSDKApi.onDestroy(this);
        Log.d(LOG_TAG, "onDestroy");

        if (lbm != null) {
            lbm.unregisterReceiver(mReceiver);
        }
    }

    // TODO GAME 在onActivityResult中需要调用YSDKApi.onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        YSDKApi.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG,"onActivityResult");
    }

    // TODO GAME 在onNewIntent中需要调用handleCallback将平台带来的数据交给YSDK处理
    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(LOG_TAG,"onNewIntent");
        super.onNewIntent(intent);

        // TODO GAME 处理游戏被拉起的情况
        Log.d(LOG_TAG,"LoginPlatform is not Hall");
        YSDKApi.handleIntent(intent);
    }

    // TODO GAME 在异账号时，模拟游戏弹框提示用户选择登陆账号
    public void showDiffLogin() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("异账号提示");
                builder.setMessage("你当前拉起的账号与你本地的账号不一致，请选择使用哪个账号登陆：");
                builder.setPositiveButton("本地账号",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                Toast.makeText(MainActivity.this, "选择使用本地账号", Toast.LENGTH_LONG).show();
                                if (LANG.equals("java")) {
                                    if (!YSDKApi.switchUser(false)) {
                                        letUserLogout();
                                    }
                                } else {
                                    if(!PlatformTest.switchUser(false)){
                                        letUserLogout();
                                    }
                                }
                            }
                        });
                builder.setNeutralButton("拉起账号",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                Toast.makeText(MainActivity.this, "选择使用拉起账号", Toast.LENGTH_LONG).show();
                                if (LANG.equals("java")) {
                                    if (!YSDKApi.switchUser(true)) {
                                        letUserLogout();
                                    }
                                } else {
                                    if(!PlatformTest.switchUser(true)){
                                        letUserLogout();
                                    }
                                }
                            }
                        });
                builder.show();
            }
        });

    }

    // 平台授权成功,让用户进入游戏. 由游戏自己实现登录的逻辑
    public void letUserLogin() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UserLoginRet ret = new UserLoginRet();
                YSDKApi.getLoginRecord(ret);
                Log.d(LOG_TAG,"flag: " + ret.flag);
                Log.d(LOG_TAG,"platform: " + ret.platform);
                if (ret.ret != BaseRet.RET_SUCC) {
                    Toast.makeText(MainActivity.this, "UserLogin error!!!",
                            Toast.LENGTH_LONG).show();
                    Log.d(LOG_TAG,"UserLogin error!!!");
                    letUserLogout();
                    return;
                }
                if (ret.platform == ePlatform.PLATFORM_ID_QQ) {
                    for (int i = 0; i < nameList.size(); i++) {
                        if ("QQ登录".equals(nameList.get(i).name)) {
                            seletedModule = nameList.get(i);
                            startModule();
                            break;
                        }
                    }
                } else if (ret.platform == ePlatform.PLATFORM_ID_WX) {
                    for (int i = 0; i < nameList.size(); i++) {
                        if ("微信登录".equals(nameList.get(i).name)) {
                            seletedModule = nameList.get(i);
                            startModule();
                            break;
                        }
                    }
                }
            }
        });

    }

    // 登出后, 更新view. 由游戏自己实现登出的逻辑
    public void letUserLogout() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if ("cpp".equals(ModuleManager.LANG)) { // 使用C++调用YSDK, 游戏只需要用一种方式即可
                    PlatformTest.logout();
                } else if ("java".equals(ModuleManager.LANG)) { // 使用Java调用YSDK
                    YSDKApi.logout();
                }
                endModule();
            }
        });

    }


    public void startWaiting() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(LOG_TAG,"startWaiting");
                stopWaiting();
                mAutoLoginWaitingDlg = new ProgressDialog(MainActivity.this);
                
                mAutoLoginWaitingDlg.setTitle("自动登录中...");
                mAutoLoginWaitingDlg.show();
            }
        });

    }

    public void stopWaiting() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(LOG_TAG,"stopWaiting");
                if (mAutoLoginWaitingDlg != null && mAutoLoginWaitingDlg.isShowing()) {
                    mAutoLoginWaitingDlg.dismiss();
                }
            }
        });

    }

    // 获取当前登录平台
    public ePlatform getPlatform() {
        UserLoginRet ret = new UserLoginRet();
        YSDKApi.getLoginRecord(ret);
        if (ret.flag == eFlag.Succ) {
            return ePlatform.getEnum(ret.platform);
        }
        return ePlatform.None;
    }

    // ***********************界面布局相关*************************
    // 初始化界面
    private void initView() {
        Util.init(getApplicationContext()); //初始化Demo需要的工具类
        ModuleManager.LANG = LANG;
        nameList = ModuleManager.getInstance().modules;

        lbm = LocalBroadcastManager.getInstance(getApplicationContext());

        mModuleListView = (GridView) findViewById(R.id.gridview);
        mModuleView = (LinearLayout) findViewById(R.id.module);
        mResultView = (LinearLayout) findViewById(R.id.result);

        //设置actionbar
        //隐藏后退按钮，并设置为不可点击
        LinearLayout llayout = (LinearLayout) findViewById(R.id.actionBarReturn);
        llayout.setFocusable(false);
        llayout.setClickable(false);
        llayout.setVisibility(View.GONE);

        TextView title = (TextView) findViewById(R.id.TactionBarTitle);
        LayoutParams textParams = (LayoutParams) title.getLayoutParams();
        textParams.leftMargin = Util.dp(9);
        title.setLayoutParams(textParams);
        title.setText("YSDKDemo 未登录");

        // 设置局部广播，处理回调信息
        lbm = LocalBroadcastManager.getInstance(this.getApplicationContext());
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String result = intent.getExtras().getString("Result");
                Log.d(LOG_TAG,result);
                displayResult(result);
            }

        };
        lbm.registerReceiver(mReceiver, new IntentFilter(LOCAL_ACTION));

        // 初始化下载进度条
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle("更新中");
        mProgressDialog.setMessage("下载进度");
        resetMainView();

        mModuleListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                seletedModule = nameList.get(position);
                if ("QQ登录".equals(seletedModule.name)) {
                    if (getPlatform() == ePlatform.QQ) {
                        // 如已登录直接进入相应模块视图
                        startModule();
                    } else if (getPlatform() == ePlatform.None) {
                        if ("cpp".equals(ModuleManager.LANG)) { // 使用C++调用YSDK, 游戏只需要用一种方式即可
                            PlatformTest.login(ePlatform.QQ.val());
                        } else if ("java".equals(ModuleManager.LANG)) { // 使用Java调用YSDK
                            YSDKApi.login(ePlatform.QQ);
                        }
                    } else {

                    }
                } else if ("微信登录".equals(seletedModule.name)) {
                    if (getPlatform() == ePlatform.WX) {
                        // 如已登录直接进入相应模块视图
                        startModule();
                    } else if (getPlatform() == ePlatform.None) {
                        if ("cpp".equals(ModuleManager.LANG)) { // 使用C++调用YSDK, 游戏只需要用一种方式即可
                            PlatformTest.login(ePlatform.WX.val());
                        } else if ("java".equals(ModuleManager.LANG)) { // 使用Java调用YSDK
                            YSDKApi.login(ePlatform.WX);
                        }
                    } else {

                    }
                } else {
                    // 进行其它功能模块
                    startModule();
                }

            }
        });
    }

    @SuppressLint("NewApi")
    public void resetMainView() {
        mModuleListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mModuleListView.setAdapter(new ArrayAdapter<BaseModule>(
                MainActivity.this, R.layout.gridview_item, nameList) {

            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    LayoutInflater inflater = (LayoutInflater) MainActivity.this
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.gridview_item, null);
                }
                TextView itemText = (TextView) view.findViewById(R.id.item_txt);
                String item = getItem(position).name;
                if (item != null) {
                    if (item.equals("微信登录") && getPlatform() == ePlatform.QQ) {
                        view.getBackground().setAlpha(60);
                        itemText.setTextColor(0x60000000);
                    } else if (item.equals("QQ登录") && getPlatform() == ePlatform.WX) {
                        view.getBackground().setAlpha(60);
                        itemText.setTextColor(0x60000000);
                    } else {
                        view.getBackground().setAlpha(255);
                        itemText.setTextColor(0xff000000);
                    }
                    TextView itemView = (TextView) view
                            .findViewById(R.id.item_txt);
                    if (itemView != null) {
                        itemView.setText(item);
                    }
                }
                return view;
            }
        });
    }


    // 展示相应的功能模块
    public void startModule() {
        mModuleListView.setVisibility(View.GONE);
        mResultView.setVisibility(View.GONE);
        mModuleView.removeAllViews();
        seletedModule.init(mModuleView, this);
        mModuleView.setVisibility(View.VISIBLE);

        //设置actionbar、模块通用布局
        LinearLayout llayout = (LinearLayout) findViewById(R.id.actionBarReturn);
        llayout.setVisibility(View.VISIBLE);
        TextView title = (TextView) findViewById(R.id.TactionBarTitle);
        title.setTextColor(Color.WHITE);
        title.setText(seletedModule.name);
        llayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                endModule();
            }

        });
    }

    // 退出功能模块
    public void endModule() {
        mModuleView.removeAllViews();
        mModuleView.setVisibility(View.GONE);
        mResultView.setVisibility(View.GONE);
        mModuleListView.setVisibility(View.VISIBLE);
        resetMainView();
        //设置actionbar
        //隐藏后退按钮，并设置为不可点击
        LinearLayout llayout = (LinearLayout) findViewById(R.id.actionBarReturn);
        llayout.setFocusable(false);
        llayout.setClickable(false);
        llayout.setVisibility(View.GONE);
        TextView title = (TextView) findViewById(R.id.TactionBarTitle);
        ePlatform platform = getPlatform();
        title.setTextColor(Color.RED);
        if (platform == ePlatform.QQ) {
            title.setText("YSDKDemo QQ登录中");
        } else if (platform == ePlatform.WX) {
            title.setText("YSDKDemo 微信登录中");
        } else {
            title.setText("YSDKDemo 未登录");
            title.setTextColor(Color.WHITE);
        }
    }

    public void displayResult(String result) {
        mModuleView.setVisibility(View.GONE);
        mModuleListView.setVisibility(View.GONE);
        mResultView.removeAllViews();

        ResultView block = new ResultView(this, mResultView);
        block.addView("CallAPI", callAPI);
        block.addView("Desripton", desripton);
        block.addView("Result", result);

        //设置actionbar、模块通用布局
        LinearLayout llayout = (LinearLayout) findViewById(R.id.actionBarReturn);
        llayout.setVisibility(View.VISIBLE);
        TextView title = (TextView) findViewById(R.id.TactionBarTitle);
        title.setText(this.title);
        llayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                endResult();
            }

        });
        mResultView.setVisibility(View.VISIBLE);
    }

    public void endResult() {
        mModuleView.setVisibility(View.VISIBLE);
        mResultView.removeAllViews();
        mResultView.setVisibility(View.GONE);
        mModuleListView.setVisibility(View.GONE);

        //设置actionbar、模块通用布局
        LinearLayout llayout = (LinearLayout) findViewById(R.id.actionBarReturn);
        llayout.setVisibility(View.VISIBLE);
        TextView title = (TextView) findViewById(R.id.TactionBarTitle);
        title.setText(seletedModule.name);
        llayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                endModule();
            }

        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mModuleListView.getVisibility() == View.VISIBLE) {
                this.finish();
            } else if (mModuleView.getVisibility() == View.VISIBLE) {
                endModule();
            } else if (mResultView.getVisibility() == View.VISIBLE) {
                endResult();
            }
        }
        return false;
    }

    public void sendResult(String result) {
        if(lbm != null) {
            Intent sendResult = new Intent(LOCAL_ACTION);
            sendResult.putExtra("Result", result);
            Log.d(LOG_TAG,"send: "+ result);
            lbm.sendBroadcast(sendResult);
        }
    }
}
