package com.renren.mobile.x2.components.login;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.components.chat.message.Subject.DATA;
import com.renren.mobile.x2.components.home.HomeActivity;
import com.renren.mobile.x2.utils.CommonUtil;

public class UserGuideFragment extends BaseFragment<DATA> {

	private View mView;
	private Activity mActivity;

	private ViewPager mViewPager;// 声明ViewPager对象
	private PagerTitleStrip mPagerTitleStrip;// 声明动画标题
	private ImageView mPageImg;// 动画图片
	private int currIndex = 0;// 当前页面
	private ImageView mPage0, mPage1, mPage2, mPage3;// 声明导航图片对象
	private TextView mText0, mText1, mText2, mText3;
	private Button jumpInBtn;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = (UserGuideActivity) getActivity();
		mView = View.inflate(mActivity, R.layout.user_guide, null);
		return mView;
	}
  
	@Override
	protected void onPreLoad() {

		mViewPager = (ViewPager) mView.findViewById(R.id.viewpager);
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		// mPagerTitleStrip = (PagerTitleStrip)findViewById(R.id.pagertitle);
		// mPagerTitleStrip.set

		mPage0 = (ImageView) mView.findViewById(R.id.page0);
		mPage1 = (ImageView) mView.findViewById(R.id.page1);
		mPage2 = (ImageView) mView.findViewById(R.id.page2);
		mPage3 = (ImageView) mView.findViewById(R.id.page3);

		// 将要分页显示的View装入数组中
		LayoutInflater mLi = LayoutInflater.from(mActivity);
		FrameLayout view1 = (FrameLayout) mLi.inflate(R.layout.user_guide_item,
				null);
		view1.setBackgroundResource(R.drawable.test_welcome_cover_2);
		mText0 = (TextView) view1.findViewById(R.id.text_textview);
		FrameLayout view2 = (FrameLayout) mLi.inflate(R.layout.user_guide_item,
				null);
		view2.setBackgroundResource(R.drawable.test_welcome_cover_3);
		mText1 = (TextView) view2.findViewById(R.id.text_textview);
		FrameLayout view3 = (FrameLayout) mLi.inflate(R.layout.user_guide_item,
				null);
		view3.setBackgroundResource(R.drawable.test_welcome_cover_4);
		mText2 = (TextView) view3.findViewById(R.id.text_textview);
		FrameLayout view4 = (FrameLayout) mLi.inflate(R.layout.user_guide_final_item,
				null);
		view4.setBackgroundResource(R.drawable.test_welcome_cover_5);
		mText3 = (TextView) view4.findViewById(R.id.text_textview);
		jumpInBtn = (Button) view4.findViewById(R.id.guide_final_jump_in);
		jumpInBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mActivity, HomeActivity.class));
				mActivity.finish();
			}
		});

		// 每个页面的view数据
		final ArrayList<View> views = new ArrayList<View>();
		views.add(view1);
		views.add(view2);
		views.add(view3);
		views.add(view4);

		// 每一个也没得标题
		final ArrayList<String> titles = new ArrayList<String>();
		titles.add("①");
		titles.add("②");
		titles.add("③");
		titles.add("④");

		getTitleBar().hide();

		// 填充ViewPager的数据适配器，我们重写即可
		PagerAdapter mPagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(views.get(position));
			}

			@Override
			public CharSequence getPageTitle(int position) {
				return titles.get(position);
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(views.get(position));
				return views.get(position);
			}
		};

		mViewPager.setAdapter(mPagerAdapter);// 与ListView用法相同，设置重写的Adapter。这样就实现了ViewPager的滑动效果。

	}

	class MyOnPageChangeListener implements OnPageChangeListener {

		public void onPageSelected(int arg0) {// 参数arg0为选中的View

			CommonUtil.log("wyf", "onPageSelectecd and currentIndex is "
					+ currIndex);
			Animation animation = null;// 声明动画对象
			switch (arg0) {
			case 0: // 页面一
				mPage0.setImageDrawable(getResources().getDrawable(
						R.drawable.publisher_cancel_press));// 进入第一个导航页面，小圆点为选中状态，下一个页面的小圆点是未选中状态。
				mPage1.setImageDrawable(getResources().getDrawable(
						R.drawable.publisher_cancel_unpress));
				if (currIndex == arg0 + 1) {
					animation = new TranslateAnimation(arg0 + 1, arg0, 0, 0);// 圆点移动效果动画，从当前View移动到下一个View
				}
				break;
			case 1: // 页面二
				mPage1.setImageDrawable(getResources().getDrawable(
						R.drawable.publisher_cancel_press));// 当前View
				mPage0.setImageDrawable(getResources().getDrawable(
						R.drawable.publisher_cancel_unpress));// 上一个View
				mPage2.setImageDrawable(getResources().getDrawable(
						R.drawable.publisher_cancel_unpress));// 下一个View
				if (currIndex == arg0 - 1) {// 如果滑动到上一个View
					animation = new TranslateAnimation(arg0 - 1, arg0, 0, 0); // 圆点移动效果动画，从当前View移动到下一个View

				} else if (currIndex == arg0 + 1) {// 圆点移动效果动画，从当前View移动到下一个View，下同。

					animation = new TranslateAnimation(arg0 + 1, arg0, 0, 0);
				}
				break;
			case 2: // 页面三
				mPage2.setImageDrawable(getResources().getDrawable(
						R.drawable.publisher_cancel_press));
				mPage1.setImageDrawable(getResources().getDrawable(
						R.drawable.publisher_cancel_unpress));
				mPage3.setImageDrawable(getResources().getDrawable(
						R.drawable.publisher_cancel_unpress));
				if (currIndex == arg0 - 1) {
					animation = new TranslateAnimation(arg0 - 1, arg0, 0, 0);
				} else if (currIndex == arg0 + 1) {
					animation = new TranslateAnimation(arg0 + 1, arg0, 0, 0);
				}
				break;
			case 3:
				mPage3.setImageDrawable(getResources().getDrawable(
						R.drawable.publisher_cancel_press));
				mPage2.setImageDrawable(getResources().getDrawable(
						R.drawable.publisher_cancel_unpress));
				if (currIndex == arg0 - 1) {
					animation = new TranslateAnimation(arg0 - 1, arg0, 0, 0);
				}
				break;
			}

			currIndex = arg0;// 设置当前View
			animation.setFillAfter(true);// True:设置图片停在动画结束位置
			animation.setDuration(300);// 设置动画持续时间

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

	}

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
}
