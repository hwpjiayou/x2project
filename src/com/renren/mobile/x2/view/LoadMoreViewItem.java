package com.renren.mobile.x2.view;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.DipUtil;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author jia.xia
 *
 */
public class LoadMoreViewItem extends RelativeLayout {

	public static LoadMoreViewItem getLoadMoreViewItem(Context context){

		LayoutInflater minflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LoadMoreViewItem item = (LoadMoreViewItem)minflater 
				.inflate(R.layout.load_more_item, null);
		item.progressBar = (ProgressBar) item
				.findViewById(R.id.add_more_progress);
		item.textView = (TextView) item.findViewById(R.id.add_more_textview);
		return item;
	}

	protected String hintText = "查看更多";
	protected String hintLoading = "加载中...";

	private ProgressBar progressBar;
	private TextView textView;

	private boolean loading = false;

	public boolean isLoading() {
		return loading;
	}

	public void setSize(int height){
		
	}
	
	public void setHintText(String hintText) {
		this.textView.setText(hintText);
	}

	public void setProgressVisible(boolean visible) {
		this.progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	public LoadMoreViewItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onLoadListener != null && !loading) {
					wasClick();
				}
			}
		});
	}

	public void wasClick() {
		loading = true;
		progressBar.setVisibility(View.VISIBLE);
		setHintText(hintLoading);
		onLoadListener.onLoad();
	}

	public void performLoad() {
		if (onLoadListener != null && !loading) {
			loading = true;
			progressBar.setVisibility(View.VISIBLE);
			setHintText(hintLoading);
			onLoadListener.onLoad();
		}
	}

	/**
	 * note : MUST be called from UI thread!!
	 */
	public void syncNotifyLoadComplete() {
		loading = false;
		progressBar.setVisibility(View.GONE);
		setHintText(hintText);
	}

	/**
	 * 有时会不成功，原因见post注释：Returns true if the Runnable was successfully placed in
	 * to the message queue. Returns false on failure, usually because the
	 * looper processing the message queue is exiting.
	 * 
	 * 如有问题可在 ui线程调用 syncNotifyLoadComplete .
	 */
	public void notifyLoadComplete() {
		this.post(new Runnable() {

			@Override
			public void run() {
				loading = false;
				progressBar.setVisibility(View.GONE);
				setHintText(hintText);
			}
		});
	}

	private onLoadListener onLoadListener;

	public void setOnLoadListener(onLoadListener onLoadListener) {
		this.onLoadListener = onLoadListener;
	}

	public interface onLoadListener {

		public void onLoad();

	}

	/*public static LoadMoreViewItem getLoadMoreViewItem() {

		LoadMoreViewItem item = (LoadMoreViewItem) VarComponent
				.getRootActivity().getLayoutInflater()
				.inflate(R.layout.load_more_item, null);
		item.progressBar = (ProgressBar) item
				.findViewById(R.id.add_more_progress);
		item.textView = (TextView) item.findViewById(R.id.add_more_textview);
		return item;
	}*/

}
