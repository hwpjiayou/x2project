package com.renren.mobile.x2.components.login;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.components.chat.message.Subject.DATA;
import com.renren.mobile.x2.network.mas.HttpMasService;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.Methods;

/**
 * 完善学校信息页面
 * @author shichao.song
 *
 */ 
public class CompleteSchoolInfoFragment extends BaseFragment<DATA> {
  
	private View mView;
	private Activity mActivity;
	private FrameLayout mSchoolLayout;
	private TextView mScholloNameText;
	private TextView mDepartmentNameText;
	private TextView mEnrollYearText;
	
	private FrameLayout mDepartmentLayout,mEnrollYearLayout;
//	private EditText mDepartmentEdit;
//	private Spinner mEnrollTimeSpi;
	private ArrayAdapter enrollTimeAdap;   
	
	private String mSchoolId;
	private String mSchoolName;
	private Handler mHandler;
	private String departmentId;
	private String departmentName;
	private String enrollYear;
	private String[] enrollYearArray = null;
	private int yearIndex=0;
	private int index = 0;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) { 
		this.mActivity=(CompleteSchoolInfoActivity) getActivity();
		mView = View.inflate(mActivity, R.layout.login_complete_department_info, null);
		  
		Bundle mBundle = getArguments();
		if(mBundle != null){
			mSchoolId = mBundle.getString("school_id");
			mSchoolName = mBundle.getString("school_name");
			CommonUtil.log("tag", "CompleteSchoolInfoFragment schoolId = " + mSchoolId + "  mSchoolName = " + mSchoolName);
		}
		
		getTitleBar().setRightAction(mActivity.getResources().getDrawable(R.drawable.v1_publisher_location_ok_selector), new OnClickListener(){
			@Override
			public void onClick(View v) {
				
				new AlertDialog.Builder(mActivity)
				.setMessage(R.string.complete_department_dialog_message)
				.setPositiveButton(mActivity.getResources().getString(R.string.complete_department_dialog_ok), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(!TextUtils.isEmpty(mSchoolName)&& !TextUtils.isEmpty(departmentName) 
								&& !TextUtils.isEmpty(enrollYear)){
							StringBuilder schoolInfo = new StringBuilder();
							schoolInfo.append(mSchoolName + " " + departmentName);
							CommonUtil.log("tag", "info = " + mSchoolId + "-" +mSchoolName+ "-" + departmentId +"-" + departmentName + "-" +enrollYear);
							SynInfoFragment.updateInfo(mSchoolId, mSchoolName, departmentId, departmentName, enrollYear);
						
							SelectCollegeFragment.finishSelf();
							mActivity.finish();
						}else{
							CommonUtil.toast(R.string.complete_department_notice);
						}
						
					}
				})
				.setNegativeButton(mActivity.getResources().getString(R.string.complete_department_dialog_rewrite), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
				
			}
			});
		getTitleBar().setLeftAction(R.drawable.v1_comment_title_left_selector,
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						startActivity(new Intent(mActivity,SelectCollegeActivity.class));
						mActivity.finish();
					}
				});
		getTitleBar().setTitle(mActivity.getResources().getString(R.string.login_complete_department_info_title));
		
		return mView;
	}

	@Override
	protected void onPreLoad() { 
		mHandler = new Handler();
		
		mScholloNameText = (TextView) mView.findViewById(R.id.complete_department_school_name);
		mDepartmentNameText = (TextView) mView.findViewById(R.id.complete_department_collegeName);
		mEnrollYearText = (TextView) mView.findViewById(R.id.complete_department_enrollYear);
		mDepartmentLayout = (FrameLayout)mView.findViewById(R.id.complete_department_college_layout);
		mEnrollYearLayout = (FrameLayout)mView.findViewById(R.id.complete_department_enrollYear_layout);
//		mEnrollTimeSpi = (Spinner) mView.findViewById(R.id.comeplete_enroll_time);
		
		mScholloNameText.setText(mSchoolName);
		
		mDepartmentLayout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				showDialog();
			}
		});
		
		mEnrollYearLayout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				showEnrollYearDialog();
			}
		});
		
		//将可选内容与ArrayAdapter连接起来   
//		enrollTimeAdap = ArrayAdapter.createFromResource(mActivity, R.array.enroll_time, android.R.layout.simple_spinner_item);   
		calculateYear();
//		enrollTimeAdap = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, enrollYearArray);   

		
		//设置下拉列表的风格    
//		enrollTimeAdap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);   
		  
        //将adapter2 添加到spinner中   
//		mEnrollTimeSpi.setAdapter(enrollTimeAdap);   
  
        //添加事件Spinner事件监听     
//		mEnrollTimeSpi.setOnItemSelectedListener(new SpinnerXMLSelectedListener());   
  
        //设置默认值   
//		mEnrollTimeSpi.setVisibility(View.VISIBLE);   
//		
//		mEnrollTimeSpi.setSelection(0, true);

	}
	
//	class SpinnerXMLSelectedListener implements OnItemSelectedListener{   
//        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,   
//                long arg3) {  
//            arg0.setVisibility(View.VISIBLE); 
//            // 给入学时间赋值
//            enrollYear = enrollYearArray[arg2];
//            CommonUtil.log("tag", "给入学时间赋值 = " + enrollYear);
////        	mEnrollTimeSpi.setSelection(arg2, true); 
//        }   
//  
//        public void onNothingSelected(AdapterView<?> arg0) {   
//        	arg0.setVisibility(View.VISIBLE); 
//        }   
//           
//    }   
	
	/**
	 * 计算显示的日期数量
	 */
	private void calculateYear(){
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		CommonUtil.log("tag", "year = " + year);
		ArrayList tempYearArray = new ArrayList();
		
		for(int i = 2002; i<=2020; i++){
			tempYearArray.add(String.valueOf(i));
		}
		
		enrollYearArray = (String[]) tempYearArray.toArray( new String[tempYearArray.size()] );
		
	}
	  
	public void showDialog(){
		new AlertDialog.Builder(mActivity)
//      .setIcon(R.drawable.login_logo)
      .setTitle(mActivity.getResources().getString(R.string.complete_department_collegeDialog_title))  
      .setItems(mDepartments, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				index = which;
				
				mDepartmentNameText.setText(mDepartments[index]);
         	 
         	 // 给院系ID和NAME赋值
         	 departmentId = tempHash.get(mDepartments[index]);
         	 departmentName = mDepartments[index];
         	 CommonUtil.log("tag", "CompleteSchoolInfoFragment 给院系ID和NAME赋值  departmentId = " + departmentId + "  departmentName = " + departmentName);
         
			}
      }) 
     .create().show();
	}


	@Override
	protected void onFinishLoad(DATA data) { 
		
	}

	@Override
	protected DATA onLoadInBackground() { 
		HttpMasService.getInstance().searchDepartment(mSchoolId, response, false);
		return null;
	}
	
	String departmentCount;
	private String[] mDepartments;
	private ArrayList tempArray = new ArrayList();
	private HashMap<String, String> tempHash = new HashMap<String, String>();
	
	INetResponse response = new INetResponse() {
		@Override
		public void response(final INetRequest req, JSONObject obj) {
			if (obj != null) {
				final JSONObject map = (JSONObject) obj;
				CommonUtil.log("tag", "departmentInfo = " + map.toString());
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (Methods.checkNoError(req, map)) {
							try {
								departmentCount = String.valueOf(map.getInt("department_count"));
								JSONArray departmentList = new JSONArray();
								departmentList = map.getJSONArray("department_list");
								
								for (int i = 0; i < departmentList.length(); i++) {
									JSONObject tem = departmentList.getJSONObject(i);
									
//									SchoolModel sm = new SchoolModel();
//									sm.setId(String.valueOf(tem.getInt("id")));
//									sm.setName(tem.getString("name"));
//									tempArray.add(sm);
									
									tempHash.put(tem.getString("name"), String.valueOf(tem.getInt("id")));
									tempArray.add(tem.getString("name"));
									
								}
								
								mDepartments = (String[]) tempArray.toArray(new String[tempArray.size()]);
								
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
			}
		}
	};
				
				

	@Override
	protected void onDestroyData() { 
		this.mView = null;
		this.mActivity = null;
		
	}
	
	private void showEnrollYearDialog(){
		new AlertDialog.Builder(mActivity)
//      .setIcon(R.drawable.login_logo)
      .setTitle(R.string.complete_department_enrollDialog_title)  
      .setItems(enrollYearArray, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				yearIndex = which;
				
				mEnrollYearText.setText(enrollYearArray[yearIndex]);
				enrollYear = enrollYearArray[yearIndex];
			}
      }) 
     .create().show();
	}

}
