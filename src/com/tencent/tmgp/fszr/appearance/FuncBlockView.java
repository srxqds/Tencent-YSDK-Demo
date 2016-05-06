package com.tencent.tmgp.fszr.appearance;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.tencent.tmgp.fszr.MainActivity;
import com.tencent.tmgp.fszr.R;
import com.tencent.tmgp.fszr.module.BaseModule;
import com.tencent.tmgp.fszr.module.YSDKApi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class FuncBlockView {
	private LinearLayout parentView;
	private BaseModule module;
	
	public FuncBlockView(LinearLayout parentView, BaseModule module)
	{
		this.parentView = parentView;
		this.module = module;
	}	
	
	/**
	 * 根据title和apiList生成一个功能块视图
	 * @param activity
	 * @param blockTitle 功能块的标题
	 * @param apiList	该功能块具有的api
	 */
    public void addView(final Activity activity, String blockTitle, ArrayList<YSDKApi> apiList)
	{
    	LayoutInflater inflater = activity.getLayoutInflater();
		LinearLayout block = (LinearLayout)inflater.inflate(R.layout.linearlayout_template, null);
		LayoutParams linearParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		linearParams.leftMargin = Util.dp(8);
		linearParams.rightMargin = Util.dp(8);
		linearParams.topMargin = Util.dp(0);
		linearParams.bottomMargin = Util.dp(20);
		block.setLayoutParams(linearParams);
		
		TextView title = new TextView(activity);
		title.setGravity(Gravity.CENTER);
		title.setTextColor(activity.getResources().getColor(R.color.holo_color));
		title.setText(blockTitle);		
		title.setTextSize(22);
		LayoutParams txtParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		txtParams.gravity = Gravity.CENTER; 
		txtParams.setMargins(0, Util.dp(10), 0, Util.dp(10));
		// 加入一个标题view
		block.addView(title, txtParams);
		
		LayoutParams btnParams =
				new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		btnParams.setMargins(Util.dp(20), 0, Util.dp(20), Util.dp(5));
		
		for(final YSDKApi api : apiList)
		{
			Button btn = (Button)inflater.inflate(R.layout.btn_demo, null);
			btn.setLayoutParams(btnParams);
			btn.setText(api.displayName);
			btn.setTextSize(16);
			btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(api.apiSet != null) {
						FuncSelectView selectDialog = new FuncSelectView(activity, module);
						selectDialog.createDialogView(api.displayName, api.apiSet);
					} else if (api.inputName != "" && api.inputName != null) {
					    FuncSelectView selectDialog = new FuncSelectView(activity, module);
                        selectDialog.createInputView(api);
					} else {
						String methodName = api.apiName;
		                try {
		                    // 这里通过反射调用所有的Demo接口
		                    Class<? extends BaseModule> clazz = module.getClass();	                  
		                    Method methodToCall = clazz.getDeclaredMethod(methodName);
		                    String result = (String) methodToCall.invoke(module);
		                    
		                    MainActivity.title = api.displayName;
		                    MainActivity.callAPI = api.rawApiName;
		                    MainActivity.desripton = api.desc;
		                    // 从接口中返回结果，或者从回调通过广播获取结果信息
		                    if(result != null && result != "") {
		                    	if(activity instanceof MainActivity) {
		                    		((MainActivity)activity).displayResult(result);
		                    	}
		                    }
		                } catch (NoSuchMethodException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// 重新抛出异常用于测试Crash上报
							Throwable exc = e.getTargetException();
							if(exc instanceof ArithmeticException) {
								throw (ArithmeticException)exc;
							} else if(exc instanceof NullPointerException) {
								throw (NullPointerException)exc;
							} else {
	                        	e.printStackTrace();
	                        }
						}
					}
				}
			});
			block.addView(btn, btnParams);
		}
		parentView.addView(block);
	}


}
