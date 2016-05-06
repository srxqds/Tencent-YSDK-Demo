package com.tencent.tmgp.fszr.module;

import com.tencent.tmgp.fszr.module.submodule.OthersFunction;
import com.tencent.tmgp.fszr.module.submodule.QQModule;
import com.tencent.tmgp.fszr.module.submodule.WXModule;

import java.util.ArrayList;

public class ModuleManager {
	private volatile static ModuleManager instance;
	public static String LANG;
	public ArrayList<BaseModule> modules = new ArrayList<BaseModule>();

	public static ModuleManager getInstance() {
		if(instance == null) {
			instance = new ModuleManager();
		}
		return instance;
	}

	private ModuleManager() {
		// 添加模块
		modules.add(new QQModule());
		modules.add(new WXModule());
		modules.add(new OthersFunction());
	}

}
