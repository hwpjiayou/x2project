package com.renren.mobile.x2.emotion;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class TextViewClickableSpan extends ClickableSpan {

	View.OnClickListener clickListener;
	int textColor;

	public TextViewClickableSpan(int color, View.OnClickListener listener) {
		clickListener = listener;
		this.textColor = color;
	}

	@Override
	public void updateDrawState(TextPaint textPaint) {
		textPaint.setColor(textColor);
	}

	@Override
	public void onClick(View widget) {
		clickListener.onClick(widget);
	}

}
	