package com.tencent.tmgp.fszr.appearance;

import android.app.Activity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.tencent.tmgp.fszr.R;

public class ResultView {
	private LinearLayout parentView;
	private Activity mActivity;
	
	public ResultView(Activity activity ,LinearLayout parentView)
	{
		this.mActivity = activity;
		this.parentView = parentView;	
	}	
	
	/**
	 * 根据blockTitle和content生成一个展示块视图
	 * @param blockTitle 展示块的标题
	 * @param content	   展示的内容
	 */
    public void addView(String blockTitle, String content)
	{
    	LayoutInflater inflater = mActivity.getLayoutInflater();
		LinearLayout block = (LinearLayout)inflater.inflate(R.layout.layout_result_display, null);
		LayoutParams linearParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		linearParams.leftMargin = Util.dp(8);
		linearParams.rightMargin = Util.dp(8);
		linearParams.topMargin = Util.dp(0);
		linearParams.bottomMargin = Util.dp(10);
		block.setLayoutParams(linearParams);
		block.setPadding(0, Util.dp(5), 0, Util.dp(4));

		LayoutParams txtParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		txtParams.gravity = Gravity.LEFT; 
		txtParams.leftMargin = Util.dp(8);
		txtParams.rightMargin = Util.dp(8);
		txtParams.topMargin = Util.dp(0);
		txtParams.bottomMargin = Util.dp(5);
		
		TextView title = new TextView(mActivity);
		title.setGravity(Gravity.LEFT);
		title.setTextColor(mActivity.getResources().getColor(R.color.holo_color));
		title.setText(blockTitle + ":");		
		title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);;
		// 加入一个标题view
		block.addView(title, txtParams);
		
		TextView contentText = new TextView(mActivity);
		contentText.setGravity(Gravity.LEFT);
		contentText.setText(content);		
		contentText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);;
		// 加入一个内容view
		block.addView(contentText, txtParams);
		
		parentView.addView(block);
	}
}
