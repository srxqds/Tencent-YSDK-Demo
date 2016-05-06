package com.tencent.tmgp.fszr.module;


import android.widget.LinearLayout;

import com.tencent.tmgp.fszr.MainActivity;

public abstract class BaseModule {
	public String name;
	protected MainActivity mMainActivity;
	
	public abstract void init(LinearLayout parent, MainActivity mainActivity);
	
}
