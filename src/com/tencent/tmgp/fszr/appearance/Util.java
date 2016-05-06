package com.tencent.tmgp.fszr.appearance;

import android.content.Context;

public class Util {
	
	private static Context mContext;
	
	public static void init(Context context) {
		mContext = context;
	}
	
	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px 
     */  
    public static int dp(float dpValue) {  
        final float scale = mContext.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }
    /** 
     * 根据手机的分辨率从 sp 的单位 转成为 px 
     */
    public static int sp(float spValue) { 
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity; 
        return (int) (spValue * fontScale + 0.5f);
    }
}
