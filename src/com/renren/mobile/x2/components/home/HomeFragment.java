package com.renren.mobile.x2.components.home;

//~--- non-JDK imports --------------------------------------------------------

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.view.IHorizontalFlip;

/**
 * @author lu.yu
 */
public class HomeFragment extends BaseFragment<Integer>
    implements MenuLayout.OnMenuItemClickListener, ContentLayout.OnPublisherButtonClickListener,
    IHorizontalFlip.OnLeftAnimationListener, IHorizontalFlip.OnRightAnimationListener,
    IHorizontalFlip.OnOpenAnimationListener {
  private static final String TAG = "HomeFragment";
  private int mCurrent = 0;
  private ContentLayout mContainer;
  private MenuLayout mMenu;
  private HomeLayout mHome;
  private FrameLayout mSideContainer;
  private final HomeTab[] mTabs;
  private final View[] mTabViews;
  private View mSide;
  private final HomeCallback mCallback;
  private boolean mLoading;
  private SideDataAsyncTask mSideDataAsyncTask;

  public HomeFragment() {
    mCallback = new HomeCallbackImpl();
    mTabs = new HomeTab[Config.TABS.length];
    mTabViews = new View[Config.TABS.length];

    for (int i = 0; i < Config.TABS.length; i++) {
      try {
        mTabs[i] = (HomeTab) Config.TABS[i].newInstance();
      } catch (java.lang.InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }

// --------------------- Interface OnLeftAnimationListener ---------------------


  @Override
  public void onLeftAnimationStart() {
    mHome.onLeftAnimationStart();
    mCallback.onAnimateLeftStart();
  }

  @Override
  public void onLeftAnimationEnd() {
    mCallback.onAnimateLeftEnd();
  }

// --------------------- Interface OnMenuItemClickListener ---------------------


  @Override
  public void onMenuClick(int position, View v) {
    if (mLoading) {
      return;
    }

    RenrenChatApplication.sInChatList = mCurrent == 2;
    RenrenChatApplication.sInMessageList = mCurrent == 4;

    if (mCurrent == position) {
      mContainer.animateOpen();
    } else {
      invisibleCurrentTab();
      mTabs[mCurrent].onPause();
      mCurrent = position;
      reload();
    }
  }

// --------------------- Interface OnOpenAnimationListener ---------------------


  @Override
  public void onOpenAnimationStart() {
    mCallback.onAnimateOpenStart();
  }

  @Override
  public void onOpenAnimationEnd() {
    Log.d(TAG, "@onOpenAnimationEnd");
    mCallback.onAnimateOpenEnd();
  }

  @Override
  public void onPublisherClick(View view) {
    mCallback.onPublisherClick(view);
  }

  @Override
  public void onRightAnimationStart() {
    mHome.onRightAnimationStart();
    mCallback.onAnimateRightStart();
  }

  @Override
  public void onRightAnimationEnd() {
    mCallback.onAnimateRightEnd();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    for (HomeTab tab : mTabs) {
      final OnActivityResultListener listener = tab.onActivityResult();

      if (listener != null) {
        Log.d(TAG, "@onActivityResult");
        listener.onActivityResult(requestCode, resultCode, data);
      }

      mCallback.onActivityResult(requestCode, resultCode, data);
    }
  }

  public final boolean onBackPressed() {
    if (!mContainer.isAnimating()
        && (mContainer.getState() != ContentLayout.STATE_EXPAND)) {
      mContainer.animateOpen();
      return true;
    }
    return false;
  }

  @Override
  public final View onCreateView(final LayoutInflater inflater,
                           final ViewGroup container,
                           final Bundle savedInstanceState) {
    mHome = new HomeLayout(getActivity());
    mContainer = (ContentLayout) mHome.findViewById(R.id.container);
    mContainer.setOnPublisherButtonClickListener(this);
    mContainer.addOpenAnimationListener(this);
    mContainer.setLeftAnimationListener(this);
    mContainer.setRightAnimationListener(this);
    mMenu = (MenuLayout) mHome.findViewById(R.id.menu);
    mMenu.setOnMenuItemClickListener(this);

    for (int i = 0, length = mTabs.length; i < length; i++) {
      mMenu.setText(i, getResources().getString(mTabs[i].getNameResourceId()));
      mMenu.setIconDrawable(i, getResources().getDrawable(mTabs[i].getIconResourceId()));
    }

    mSideContainer = (FrameLayout) mHome.findViewById(R.id.side);

    return mHome;
  }

  @Override
  protected void onDestroyData() {
    for (HomeTab tab : mTabs) {
      tab.onDestroyData();
    }

    for (View view : mTabViews) {
      mContainer.removeView(view);
    }

    mCallback.onDestroyData();
    mSideContainer.removeView(mSide);
  }

  @Override
  protected final void onFinishLoad(final Integer integer) {
    Log.d(TAG, "@onFinishLoad");

    if (mContainer.getState() == IHorizontalFlip.STATE_EXPAND) {
      mTabs[mCurrent].onFinishLoad();
      mTabViews[mCurrent].setVisibility(View.VISIBLE);
      mLoading = false;
    } else {
      mContainer.animateOpen();
      mContainer.addOpenAnimationListener(new IHorizontalFlip.OnOpenAnimationListener() {
        @Override
        public void onOpenAnimationStart() {
        }

        @Override
        public void onOpenAnimationEnd() {
          mTabs[mCurrent].onFinishLoad();
          mTabViews[mCurrent].setVisibility(View.VISIBLE);
          mTabs[mCurrent].onResume();
          mLoading = false;
          mContainer.removeOpenAnimationListener(this);
        }
      });
    }
  }

  @Override
  protected final Integer onLoadInBackground() {
    Log.d(TAG, "@onLoadInBackground");
    mTabs[mCurrent].onLoadData();

    return mCurrent;
  }

  @Override
  public final void onPause() {
    super.onPause();
    RenrenChatApplication.sInChatList = false;
    RenrenChatApplication.sInMessageList = false;

    for (HomeTab tab : mTabs) {
      tab.onPause();
    }
  }

  @Override
  protected final void onPreLoad() {
    mLoading = true;

    if (mTabViews[mCurrent] == null) {
      mTabViews[mCurrent] = mTabs[mCurrent].onCreateView(getActivity());
      mContainer.addView(mTabViews[mCurrent], 0,
          new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
              ViewGroup.LayoutParams.FILL_PARENT));
      invisibleCurrentTab();
    }
  }

  private void invisibleCurrentTab() {
    mTabViews[mCurrent].setVisibility(View.INVISIBLE);
  }

  @Override
  public void onResume() {
    super.onResume();

    if (mSideDataAsyncTask == null) {
      mSideDataAsyncTask = new SideDataAsyncTask();
      mSideDataAsyncTask.execute((Void[]) null);
    }
  }

  public interface OnActivityResultListener {
    void onActivityResult(int requestCode, int resultCode, Intent data);
  }

  private class SideDataAsyncTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected void onPreExecute() {
      if (mSide == null) {
        mSide = mCallback.onCreateSideTab(getActivity());
        mSideContainer.addView(mSide,
            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
      }
    }

    @Override
    protected Void doInBackground(Void... params) {
      mCallback.onLoadSideData();

      return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      mCallback.onFinishSideData(mSide);
    }
  }
}

//~ Formatted by Jindent --- http://www.jindent.com
