package com.tencent.tmgp.fszr.module.submodule;


import android.util.Log;
import android.widget.LinearLayout;

import com.tencent.bugly.opengame.crashreport.CrashReport;
import com.tencent.tmgp.fszr.MainActivity;
import com.tencent.tmgp.fszr.PlatformTest;
import com.tencent.tmgp.fszr.appearance.FuncBlockView;
import com.tencent.tmgp.fszr.module.BaseModule;
import com.tencent.tmgp.fszr.module.ModuleManager;
import com.tencent.tmgp.fszr.module.YSDKApi;

import java.util.ArrayList;

public class OthersFunction extends BaseModule {

	public OthersFunction() {
		this.name = "其他";
	}
	
	@Override
	public void init(LinearLayout parent, MainActivity activity) {
		this.mMainActivity = activity;
		FuncBlockView othersView = new FuncBlockView(parent, this);
		
		// ^_^
        ArrayList<YSDKApi> othersFunction = new ArrayList<YSDKApi>();
        othersFunction.add(new YSDKApi(
        		"callGetChannelId",
        		"getChannelId",
        		"获取安装渠道号", 
        		"游戏上线前打包会根据不同渠道(例如应用宝,豌豆荚,91等)生成不同渠道号的apk包, " +
        		"在安装包中的渠道号称之为安装渠道"));
        othersFunction.add(new YSDKApi(
        		"callGetRegisterChannelId",
        		"getRegisterChannelId",
        		"获取注册渠道号", 
        		"用户首次登陆时, 游戏的安装渠道, 会在MSDK后台记录, 算作用户注册渠道"));
		othersFunction.add(new YSDKApi(
				"callGetVersion",
				"getVersion",
				"获取SDK的版本",
				""));
        
        ArrayList<YSDKApi> crashReport = new ArrayList<YSDKApi>();
        crashReport.add(new YSDKApi(
        		"nativeCrashTest",
        		" ",
        		"Native异常测试",
        		"测试异常上报功能，Crash信息可在RDM上查看。Crash信息为实时上报，但必须" +
        		"触发另一种Crash才能看到此Crash信息(可下次再启动Demo点击另一种异常测试" +
        		"来实现)。这里测试C++层的Crash。"));
        crashReport.add(new YSDKApi(
        		"arithmeticExceptionTest",
        		" ",
        		"算术异常测试",
        		"测试异常上报功能，Crash信息可在RDM上查看。Crash信息为实时上报，但必须" +
        		"触发另一种Crash才能看到此Crash信息(可下次再启动Demo点击另一种异常测试" +
        		"来实现)。这里测试除0的异常。"));
        crashReport.add(new YSDKApi(
        		"nullPointerExceptionTest",
        		" ",
        		"空指针异常测试",
        		"测试异常上报功能，Crash信息可在RDM上查看。Crash信息为实时上报，但必须" +
        		"触发另一种Crash才能看到此Crash信息(可下次再启动Demo点击另一种异常测试" +
        		"来实现)。这里测试空指针的Crash。"));
        othersFunction.add(new YSDKApi("异常上报测试", crashReport));
        othersView.addView(mMainActivity, "^_^", othersFunction);
	}

	
	public String callGetVersion() {
		String version = "";
		Log.d(MainActivity.LOG_TAG, com.tencent.ysdk.api.YSDKApi.getVersion());
		if ("cpp".equals(ModuleManager.LANG)) { // 使用C++调用MSDK, 游戏只需要用一种方式即可
			version = PlatformTest.getVersion();
		} else if ("java".equals(ModuleManager.LANG)) { // 使用Java调用MSDK
			version = com.tencent.ysdk.api.YSDKApi.getVersion();
		}
		return version;
	}

	public String callGetChannelId() {
		String channelId = "";
		Log.d(MainActivity.LOG_TAG, com.tencent.ysdk.api.YSDKApi.getChannelId());
		if ("cpp".equals(ModuleManager.LANG)) { // 使用C++调用MSDK, 游戏只需要用一种方式即可
			channelId = PlatformTest.getChannelId();
		} else if ("java".equals(ModuleManager.LANG)) { // 使用Java调用MSDK
			channelId = com.tencent.ysdk.api.YSDKApi.getChannelId();
		}
		return channelId;
	}

	public String callGetRegisterChannelId() {
		String channelId = "";
		Log.d(MainActivity.LOG_TAG, com.tencent.ysdk.api.YSDKApi.getRegisterChannelId());
		if ("cpp".equals(ModuleManager.LANG)) { // 使用C++调用MSDK, 游戏只需要用一种方式即可
			channelId = PlatformTest.getRegisterChannelId();
		} else if ("java".equals(ModuleManager.LANG)) { // 使用Java调用MSDK
			channelId = com.tencent.ysdk.api.YSDKApi.getRegisterChannelId();
		}
		return channelId;
	}
	
	public void nativeCrashTest() {
		// Native异常测试
		if ("cpp".equals(ModuleManager.LANG)) { // 使用C++调用MSDK, 游戏只需要用一种方式即可
			CrashReport.testNativeCrash();
		} else if ("java".equals(ModuleManager.LANG)) { // 使用Java调用MSDK
			PlatformTest.testNativeCrash();
		}
	}
	
	public void arithmeticExceptionTest() {
		// 除0异常测试
		int i=0;
		int j=100/i;
	}
	
	public void nullPointerExceptionTest() {
		// 空指针异常测试
		String aa = null;
		if (aa.equals("aa")) {
		}
	}
	
	
}
