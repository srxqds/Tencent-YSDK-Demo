package com.tencent.tmgp.fszr.module.submodule;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.LinearLayout;

import com.tencent.tmgp.fszr.MainActivity;
import com.tencent.tmgp.fszr.PlatformTest;
import com.tencent.tmgp.fszr.R;
import com.tencent.tmgp.fszr.YSDKCallback;
import com.tencent.tmgp.fszr.appearance.FuncBlockView;
import com.tencent.tmgp.fszr.module.BaseModule;
import com.tencent.tmgp.fszr.module.ModuleManager;
import com.tencent.tmgp.fszr.module.YSDKApi;
import com.tencent.ysdk.framework.common.ePlatform;
import com.tencent.ysdk.module.user.UserLoginRet;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class WXModule extends BaseModule {

    public WXModule() {
        this.name = "微信登录";
    }

    @Override
    public void init(LinearLayout parent, MainActivity activity) {
        this.mMainActivity = activity;
        FuncBlockView weixinView = new FuncBlockView(parent, this);

        // 登录相关
        ArrayList<YSDKApi> aboutLogin = new ArrayList<YSDKApi>();
        aboutLogin.add(new YSDKApi("letUserLogout", "letUserLogout", "登出", "退出微信登录"));
        aboutLogin.add(new YSDKApi(
                "callgetLoginRecord",
                "getLoginRecord",
                "登录记录",
                "读取本次微信登录票据"));
        weixinView.addView(mMainActivity, "登录相关", aboutLogin);

        // 通用接口
        ArrayList<YSDKApi> globalList = new ArrayList<YSDKApi>();
        globalList.add(new YSDKApi(
                "callGetIsPlatformInstalled",
                "isPlatformInstalled",
                "检查微信是否安装",
                ""
        ));
        globalList.add(new YSDKApi(
                "callgetPlatformAPPVersion",
                "getPlatformAPPVersion",
                "检查微信版本",
                "检查微信版本号。部分接口是不支持低版本微信，请仔细接入文档的相关说明。"
        ));
        globalList.add(new YSDKApi(
                "callgetPf",
                "getPf",
                "获取pf+pfKey",
                "pf+pfKey支付的时候会用到"));
        weixinView.addView(mMainActivity, "通用", globalList);

        // 关系链功能集合
        ArrayList<YSDKApi> relationList = new ArrayList<YSDKApi>();
        relationList.add(new YSDKApi(
                "callQueryWXUserInfo",
                "queryWXUserInfo",
                "个人信息",
                "查询个人基本信息"));
        weixinView.addView(mMainActivity, "关系链", relationList);

        // 登录相关
        ArrayList<YSDKApi> aboutPay = new ArrayList<YSDKApi>();
        aboutPay.add(new YSDKApi(
                "callLauncherPay",
                "recharge",
                "充值游戏币",
                "",
                "大区id 充值数额 充值数额是否可改，三个参数用空格分割"));
        weixinView.addView(mMainActivity, "支付相关", aboutPay);
    }

    // 登出游戏
    public void letUserLogout() {
        mMainActivity.letUserLogout();
        mMainActivity.endModule();
    }

    /**
     * ************MSDK API调用示例*************
     */

    public void callLauncherPay(String input){
        String[] paraArr = input.split(" ");
        if(paraArr.length > 0 && null != paraArr[0]){
            boolean isCanChange = true;
            if(null != paraArr[2]){
                try {
                    int value = Integer.parseInt(paraArr[2]);
                    if(value > 0){
                        isCanChange = false;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            Bitmap bmp = BitmapFactory.decodeResource(mMainActivity.getResources(), R.drawable.sample_yuanbao);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] appResData = baos.toByteArray();
            String ysdkExt = "ysdkExt";
            if ("cpp".equals(ModuleManager.LANG)) {
                PlatformTest.recharge(paraArr[0],paraArr[1],isCanChange,appResData,appResData.length,ysdkExt);
            } else if ("java".equals(ModuleManager.LANG)) {
                com.tencent.ysdk.api.YSDKApi.recharge(paraArr[0],paraArr[1],isCanChange,appResData,ysdkExt,new YSDKCallback(mMainActivity));
            }
        }else{
            Log.e(MainActivity.LOG_TAG,"para is bad:"+ input);
        }
    }

    public String callgetLoginRecord() {
        UserLoginRet ret = new UserLoginRet();
        int platform = 0;
        if ("cpp".equals(ModuleManager.LANG)) {
            platform = PlatformTest.getLoginRecord(ret);
        } else if ("java".equals(ModuleManager.LANG)) {
            platform = com.tencent.ysdk.api.YSDKApi.getLoginRecord(ret);
        }
        Log.d(MainActivity.LOG_TAG,"ret:"+ ret.toString());
        String result = "";
        if (platform == ePlatform.PLATFORM_ID_WX) {
            result += "platform = " + ret.platform + " 微信登录 \n";
            result += "accessToken = "
                    + ret.getAccessToken() + "\n";
            result += "refreshToken = "
                    + ret.getRefreshToken() + "\n";
        }
        result += "openid = " + ret.open_id + "\n";
        result += "flag = " + ret.flag + "\n";
        //result += "desc = " + ret.desc + "\n";
        result += "pf = " + ret.pf + "\n";
        result += "pf_key = " + ret.pf_key + "\n";
        return result;
    }

    public String callGetIsPlatformInstalled(){
        boolean isInstall = false;
        if ("cpp".equals(ModuleManager.LANG)) {
            isInstall =  PlatformTest.isPlatformInstalled(ePlatform.WX.val());
        } else if ("java".equals(ModuleManager.LANG)) {
            isInstall = com.tencent.ysdk.api.YSDKApi.isPlatformInstalled(ePlatform.WX);
        }

        return String.valueOf(isInstall);
    }

    public String callgetPlatformAPPVersion() {
        String wxVersion = "";

        if ("cpp".equals(ModuleManager.LANG)) {
            wxVersion =  PlatformTest.getPlatformAppVersion(ePlatform.WX.val());
        } else if ("java".equals(ModuleManager.LANG)) {
            wxVersion = com.tencent.ysdk.api.YSDKApi.getPlatformAppVersion(ePlatform.WX);
        }
        if (wxVersion == null || wxVersion == "") {
            return "Get mobile Weixin version failed!";
        } else {
            return wxVersion;
        }
    }

    public String callgetPf() {
        String result = "";
        String pf = "";
        String pfKey = "";
        if ("cpp".equals(ModuleManager.LANG)) {
            pf = PlatformTest.getPf();
            pfKey = PlatformTest.getPfKey();
        } else if ("java".equals(ModuleManager.LANG)) {
            pf = com.tencent.ysdk.api.YSDKApi.getPf();
            pfKey = com.tencent.ysdk.api.YSDKApi.getPfKey();
        }
        result = "Pf = " + pf;
        result += "\n pfKey = " + pfKey;
        return result;
    }

    public void callQueryWXUserInfo() {
        if ("cpp".equals(ModuleManager.LANG)) { // 使用C++调用MSDK, 游戏只需要用一种方式即可
            PlatformTest.queryUserInfo(ePlatform.WX.val());
        } else if ("java".equals(ModuleManager.LANG)) { // 使用Java调用MSDK
            com.tencent.ysdk.api.YSDKApi.queryUserInfo(ePlatform.WX);
        }
    }
}
