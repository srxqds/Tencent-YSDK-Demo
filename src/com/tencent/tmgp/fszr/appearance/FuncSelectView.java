package com.tencent.tmgp.fszr.appearance;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.tencent.tmgp.fszr.MainActivity;
import com.tencent.tmgp.fszr.R;
import com.tencent.tmgp.fszr.module.BaseModule;
import com.tencent.tmgp.fszr.module.YSDKApi;
import com.tencent.ysdk.module.user.UserLoginRet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class FuncSelectView {
	private Activity mActivity;
	private BaseModule module;
	
	public FuncSelectView(Activity activity, BaseModule module)
	{
		this.mActivity = activity;
		this.module = module;
	}	
	
    public void createDialogView(String viewTitle, ArrayList<YSDKApi> apiList)
	{
    	LayoutParams btnParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		btnParams.setMargins(Util.dp(15), 0, Util.dp(15), Util.dp(4));
		
    	LayoutInflater inflater = mActivity.getLayoutInflater();
    	LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.select_window, null);
		
    	final Dialog dialog = new Dialog(mActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(rootView);
        Window dWindow = dialog.getWindow();
        WindowManager.LayoutParams params = dWindow.getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        if(apiList.size() > 7) {
        	params.height = Util.dp(320);
        } else {
        	params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        dWindow.setGravity(Gravity.CENTER);
        dWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        dWindow.setAttributes(params);
		dialog.show();
		
		LinearLayout selectItem = (LinearLayout) rootView.findViewById(R.id.select_item);
		
		TextView title = (TextView) rootView.findViewById(R.id.title_dialog);
		title.setText(viewTitle);
		
		for(final YSDKApi api : apiList)
		{
			Button btn = (Button)inflater.inflate(R.layout.btn_demo, null);;
			btn.setText(api.displayName);			
			btn.setTextSize(16);
			btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(api.apiSet != null) {
						FuncSelectView selectDialog = new FuncSelectView(mActivity, module);
						selectDialog.createDialogView(api.displayName, api.apiSet);
					} else if (api.inputName != "" && api.inputName != null) {
					    FuncSelectView selectDialog = new FuncSelectView(mActivity, module);
                        selectDialog.createInputView(api);
                        if(dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
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
		                    	if(mActivity instanceof MainActivity) {
		                    		((MainActivity)mActivity).displayResult(result);
		                    	}
		                    }
		                    if(dialog != null && dialog.isShowing()) {
	                            dialog.dismiss();
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
			selectItem.addView(btn, btnParams);
		}
	}
    
    public void createInputView(final YSDKApi api) {
    	LayoutInflater inflater = mActivity.getLayoutInflater();
    	LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.select_window, null);
		
    	final Dialog dialog = new Dialog(mActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(rootView);
		
		Window dWindow = dialog.getWindow();
        WindowManager.LayoutParams params = dWindow.getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dWindow.setGravity(Gravity.CENTER);
        dWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        dWindow.setAttributes(params);
		dialog.show();
		
		LinearLayout selectItem = (LinearLayout) rootView.findViewById(R.id.select_item);
		
		TextView title = (TextView) rootView.findViewById(R.id.title_dialog);
		title.setText(api.displayName);
		
		LayoutParams txetParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
		txetParams.setMargins(Util.dp(12), Util.dp(20), Util.dp(10), Util.dp(0));
		txetParams.gravity = Gravity.LEFT;
		TextView inputItem = new TextView(mActivity);
		inputItem.setTextSize(Util.dp(6));
		inputItem.setText(api.inputName);
		selectItem.addView(inputItem, txetParams);
		
		LayoutParams editParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
		editParams.setMargins(Util.dp(10), Util.dp(6), Util.dp(10), Util.dp(0));
		final EditText editText = new EditText(mActivity);
		editText.setTextSize(Util.dp(7));
		editText.setBackgroundDrawable(mActivity.
		        getResources().getDrawable(R.drawable.editshape));
		if (api.inputName.contains("公告栏")) {
			editText.setText("1");
		} else if (api.inputName.contains("openId")) {
			UserLoginRet ret = new UserLoginRet();
			com.tencent.ysdk.api.YSDKApi.getLoginRecord(ret);
			editText.setText(ret.open_id);
		} else if (api.inputName.contains("URL")) {
			editText.setText("http://www.qq.com");
		}
		selectItem.addView(editText, editParams);
		
		LayoutParams btnParams = new LayoutParams(Util.dp(120),
            LayoutParams.WRAP_CONTENT);
		btnParams.setMargins(Util.dp(10), Util.dp(12), Util.dp(10), Util.dp(30));
		btnParams.gravity = Gravity.CENTER;
		Button openButton = (Button)inflater.inflate(R.layout.btn_demo, null);;
		openButton.setText("确定");
		openButton.setTextSize(16);
		openButton.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    String methodName = api.apiName;
                    String param = editText.getText().toString();
                    try {
                        // 这里通过反射调用有一个输入参数的Demo接口
                        Class<? extends BaseModule> clazz = module.getClass();                    
                        Method methodToCall = clazz.getDeclaredMethod(
                        		methodName, String.class);
                        String result = (String) methodToCall.invoke(module, param);
                        
                        MainActivity.title = api.displayName;
                        MainActivity.callAPI = api.rawApiName;
                        MainActivity.desripton = api.desc;
                        // 从接口中返回结果，或者从回调通过广播获取结果信息
                        if(result != null && result != "") {
                            if(mActivity instanceof MainActivity) {
                                ((MainActivity)mActivity).displayResult(result);
                            }
                        } 
                        if(dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
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
                
            });
		selectItem.addView(openButton, btnParams);
				
    }
}
