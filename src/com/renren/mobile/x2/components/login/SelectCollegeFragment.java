package com.renren.mobile.x2.components.login;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.components.chat.message.Subject.DATA;
import com.renren.mobile.x2.components.login.SelectCollegeAdapter.SchoolModel;
import com.renren.mobile.x2.network.mas.HttpMasService;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.Methods;

public class SelectCollegeFragment extends BaseFragment<DATA> {

	private View mView;
	private static Activity mActivity;

	public FrameLayout mSearchLayout;
	public EditText mSearchText;
	public ImageView mSearchDelIcon;
	public ListView mContentListView;
	public View mLoading;
	private View mSelectCollegeView;

	/**
	 * 搜索框字符串
	 * */
	private String mStr;

	/**
	 * 选择的学校
	 * */
	private String mSchoolName;
	private String mSchoolId;
	private String mTempSchool;

	private Handler mHandler = new Handler();

	/**
	 * 搜索结果list
	 * */
	private ArrayList<SchoolModel> mData = new ArrayList<SchoolModel>();
	private SelectCollegeAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.mActivity = (SelectCollegeActivity) getActivity();
		mView = View.inflate(mActivity, R.layout.f_select_college, null);

		return mView;
	}

	@Override
	protected void onPreLoad() {
		mSearchLayout = (FrameLayout) mView.findViewById(R.id.search_layout);
		mSearchText = (EditText) mView.findViewById(R.id.search_edit);
		mSearchDelIcon = (ImageView) mView.findViewById(R.id.search_del);
		mContentListView = (ListView) mView.findViewById(R.id.college_list);
		mLoading = (View) mView.findViewById(R.id.college_loading);
		mSearchText.requestFocus();

		initView();
		initEvent();
		
		getTitleBar().setLeftAction(R.drawable.v1_comment_title_left_selector,
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						hideKeyboard();
						mActivity.finish(); 
					}
				});
		getTitleBar().setTitle(
				mActivity.getResources().getString(
						R.string.login_syn_info_title));
	}
	
	/**
	 * 隐藏输入法
	 */
	private void hideKeyboard() {
		((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(mActivity.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public void initView() {

		mSearchLayout.setPadding(10, 6, 10, 6);

		mAdapter = new SelectCollegeAdapter(mActivity);
		mContentListView.setAdapter(mAdapter);
		// mContentListView.setDivider(mActivity.getResources().getDrawable(R.drawable.listview_item_divider));
		// 滑动时不重绘页面
		mContentListView.setScrollingCacheEnabled(false);
		mContentListView.setDrawingCacheEnabled(false);
		mContentListView.setAlwaysDrawnWithCacheEnabled(false);
		mContentListView.setWillNotCacheDrawing(true);

		// no data
		// mNoDataTextView.setText(mActivity.getResources().getString(R.string.SelectCollegeScreen_3));
	}

	public void initEvent() {
		mContentListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mSchoolName = mData.get(position).getName();
				mSchoolId = mData.get(position).getId();

				mSearchText.setText(mSchoolName);
				mSearchText.setSelection(mSchoolName.length());

				CommonUtil.log("tag", "SelectCollegeFragment schoolId = "
						+ mSchoolId + "schoolName" + mSchoolName);
				// 跳到。。。
				CompleteSchoolInfoActivity.show(mActivity, mSchoolId,
						mSchoolName);

				mActivity.finish();

			}
		});

		mSearchText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mTempSchool = mSchoolName;
				// mSchoolName = "";
				mStr = mSearchText.getText().toString();
				if (mStr.toString().length() >= 2) {
					// mNoData.setVisibility(View.GONE);
					// mNoData.setVisibility(View.GONE);

					HttpMasService.getInstance().searchSchool(mStr, response,
							false);
				}
				if (mStr.toString().length() == 0) {
					// mSearchText.setHint("搜索");
					mSearchDelIcon.setVisibility(View.GONE);
					mAdapter.setAdapterData(null);

					return;
				}
				mSearchText.requestFocus();
				mSearchDelIcon.setVisibility(View.VISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		mSearchDelIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSearchText.setText("");
				mSearchDelIcon.setVisibility(View.GONE);
				// mNoData.setVisibility(View.GONE);
				// mNoData.setVisibility(View.GONE);
				mAdapter.setAdapterData(null);
			}
		});
		/** 按回车键后的处理事件 */
		mSearchText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (KeyEvent.KEYCODE_ENTER == keyCode) {
					return true;
				}
				return false;
			}
		});
	}

	/** 接口返回response */
	INetResponse response = new INetResponse() {
		@Override
		public void response(final INetRequest req, JSONObject obj) {
			if (obj != null) {
				final JSONObject map = (JSONObject) obj;
				CommonUtil.log("tag", "schoolInfo = " + map.toString());
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (Methods.checkNoError(req, map)) {
							mData.clear();
							int count = map.optInt("count");
							JSONArray schoolInfo = new JSONArray();
							try {
								schoolInfo = map.getJSONArray("school");
							} catch (JSONException e1) {
								e1.printStackTrace();
							}
							for (int i = 0; i < schoolInfo.length(); i++) {
								try {

									JSONObject tem = schoolInfo
											.getJSONObject(i);
									SchoolModel sm = new SchoolModel();
									sm.setId(String.valueOf(tem.getInt("id")));
									sm.setName(tem.getString("name"));
									CommonUtil.log("tag",
											"SelectCollegeFragment sm.toString() = "
													+ sm.toString());
									mData.add(sm);

								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							mAdapter.setAdapterData(mData);
						} else {
							// mNoData.setVisibility(View.VISIBLE);
							mAdapter.setAdapterData(null);
						}

					}
				});
			}
		}
	};

	@Override
	protected void onFinishLoad(DATA data) {

	}

	@Override
	protected DATA onLoadInBackground() {
		return null;
	}

	@Override
	protected void onDestroyData() {

	}
	
	public static void finishSelf(){
		if(mActivity != null){
			mActivity.finish();
		}
	
	}

}
