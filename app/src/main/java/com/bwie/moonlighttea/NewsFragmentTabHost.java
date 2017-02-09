package com.bwie.moonlighttea;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * 类描述:
 * 作者：Administrator
 * 时间:2017/2/9 19:45
 */

public class NewsFragmentTabHost extends TabHost implements TabHost.OnTabChangeListener{
    private final ArrayList<NewsFragmentTabHost.TabInfo> mTabs = new ArrayList<>();

    private FrameLayout mRealTabContent;
    private Context mContext;
    private FragmentManager mFragmentManager;
    private int mContainerId;
    private TabHost.OnTabChangeListener mOnTabChangeListener;
    private NewsFragmentTabHost.TabInfo mLastTab;
    private boolean mAttached;
    private TextView tab_label;

    static final class TabInfo {
        final String tag;
        final  Class<?> clss;
        final Bundle args;
        Fragment fragment;

        TabInfo( String _tag,  Class<?> _class,  Bundle _args) {
            tag = _tag;
            clss = _class;
            args = _args;
        }
    }

    static class DummyTabFactory implements TabHost.TabContentFactory {
        private final Context mContext;

        public DummyTabFactory(Context context) {
            mContext = context;
        }

        @Override
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }

    static class SavedState extends BaseSavedState {
        String curTab;

        SavedState(Parcelable superState) {
            super(superState);
        }

        SavedState(Parcel in) {
            super(in);
            curTab = in.readString();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(curTab);
        }

        @Override
        public String toString() {
            return "FragmentTabHost.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " curTab=" + curTab + "}";
        }

        public static final Parcelable.Creator<NewsFragmentTabHost.SavedState> CREATOR
                = new Parcelable.Creator<NewsFragmentTabHost.SavedState>() {
            @Override
            public NewsFragmentTabHost.SavedState createFromParcel(Parcel in) {
                return new NewsFragmentTabHost.SavedState(in);
            }

            @Override
            public NewsFragmentTabHost.SavedState[] newArray(int size) {
                return new NewsFragmentTabHost.SavedState[size];
            }
        };
    }








    public NewsFragmentTabHost(Context context) {
        super(context, null);
        initFragmentTabHost(context, null);
    }

    public NewsFragmentTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFragmentTabHost(context, attrs);
    }

    private void initFragmentTabHost(Context context, AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(attrs,
                new int[] { android.R.attr.inflatedId }, 0, 0);
        mContainerId = a.getResourceId(0, 0);
        a.recycle();

        super.setOnTabChangedListener(this);
    }

    private void ensureHierarchy(Context context) {
        // If owner hasn't made its own view hierarchy, then as a convenience
        // we will construct a standard one here.
        if (findViewById(android.R.id.tabs) == null) {
            LinearLayout ll = new LinearLayout(context);
            ll.setOrientation(LinearLayout.VERTICAL);
            addView(ll, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            TabWidget tw = new TabWidget(context);
            tw.setId(android.R.id.tabs);
            tw.setOrientation(TabWidget.HORIZONTAL);
            ll.addView(tw, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 0));

            FrameLayout fl = new FrameLayout(context);
            fl.setId(android.R.id.tabcontent);
            ll.addView(fl, new LinearLayout.LayoutParams(0, 0, 0));

            mRealTabContent = fl = new FrameLayout(context);
            mRealTabContent.setId(mContainerId);
            ll.addView(fl, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
        }
    }

    @Override @Deprecated
    public void setup() {
        throw new IllegalStateException(
                "Must call setup() that takes a Context and FragmentManager");
    }

    public void setup(Context context, FragmentManager manager) {
        ensureHierarchy(context);  // Ensure views required by super.setup()
        super.setup();
        mContext = context;
        mFragmentManager = manager;
        ensureContent();
    }

    public void setup(Context context, FragmentManager manager, int containerId) {
        ensureHierarchy(context);  // Ensure views required by super.setup()
        super.setup();
        mContext = context;
        mFragmentManager = manager;
        mContainerId = containerId;
        ensureContent();
        mRealTabContent.setId(containerId);

        // We must have an ID to be able to save/restore our state.  If
        // the owner hasn't set one at this point, we will set it ourselves.
        if (getId() == View.NO_ID) {
            setId(android.R.id.tabhost);
        }
    }

    private void ensureContent() {
        if (mRealTabContent == null) {
            mRealTabContent = (FrameLayout)findViewById(mContainerId);
            if (mRealTabContent == null) {
                throw new IllegalStateException(
                        "No tab content FrameLayout found for id " + mContainerId);
            }
        }
    }

    @Override
    public void setOnTabChangedListener(OnTabChangeListener l) {
        mOnTabChangeListener = l;
    }

    public void addTab( TabHost.TabSpec tabSpec,  Class<?> clss,
                        Bundle args) {
        tabSpec.setContent(new NewsFragmentTabHost.DummyTabFactory(mContext));

        final String tag = tabSpec.getTag();
        final NewsFragmentTabHost.TabInfo info = new NewsFragmentTabHost.TabInfo(tag, clss, args);

        if (mAttached) {
            // If we are already attached to the window, then check to make
            // sure this tab's fragment is inactive if it exists.  This shouldn't
            // normally happen.
            info.fragment = mFragmentManager.findFragmentByTag(tag);
            if (info.fragment != null && !info.fragment.isDetached()) {
                final FragmentTransaction ft = mFragmentManager.beginTransaction();
                ft.detach(info.fragment);
                ft.commit();
                View view=View.inflate(mContext,R.layout.view_tab_indicator,null);
                tab_label = (TextView) view.findViewById(R.id.tab_label);
                tab_label.setTextColor(Color.BLUE);
            }
        }

        mTabs.add(info);
        addTab(tabSpec);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        final String currentTag = getCurrentTabTag();

        // Go through all tabs and make sure their fragments match
        // the correct state.
        FragmentTransaction ft = null;
        for (int i = 0, count = mTabs.size(); i < count; i++) {
            final NewsFragmentTabHost.TabInfo tab = mTabs.get(i);
            tab.fragment = mFragmentManager.findFragmentByTag(tab.tag);
            if (tab.fragment != null && !tab.fragment.isDetached()) {
                if (tab.tag.equals(currentTag)) {
                    // The fragment for this tab is already there and
                    // active, and it is what we really want to have
                    // as the current tab.  Nothing to do.
                    mLastTab = tab;
                } else {
                    // This fragment was restored in the active state,
                    // but is not the current tab.  Deactivate it.
                    if (ft == null) {
                        ft = mFragmentManager.beginTransaction();
                    }
                    ft.detach(tab.fragment);
                }
            }
        }

        // We are now ready to go.  Make sure we are switched to the
        // correct tab.
        mAttached = true;
        ft = doTabChanged(currentTag, ft);
        if (ft != null) {
            ft.commit();
            mFragmentManager.executePendingTransactions();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAttached = false;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        NewsFragmentTabHost.SavedState ss = new NewsFragmentTabHost.SavedState(superState);
        ss.curTab = getCurrentTabTag();
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof NewsFragmentTabHost.SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        NewsFragmentTabHost.SavedState ss = (NewsFragmentTabHost.SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setCurrentTabByTag(ss.curTab);
    }


    @Override
    public void onTabChanged(String tabId) {
        if (mAttached) {
            final FragmentTransaction ft = doTabChanged(tabId, null);
            if (ft != null) {
                ft.commit();
            }
        }
        if (mOnTabChangeListener != null) {
            mOnTabChangeListener.onTabChanged(tabId);
        }
    }


    private FragmentTransaction doTabChanged( String tag,
                                              FragmentTransaction ft) {
        final NewsFragmentTabHost.TabInfo newTab = getTabInfoForTag(tag);
        if (mLastTab != newTab) {
            if (ft == null) {
                ft = mFragmentManager.beginTransaction();
            }

            if (mLastTab != null) {
                if (mLastTab.fragment != null) {
                    // 将detach替换为hide，隐藏Fragment
                    // ft.detach(mLastTab.fragment);
                    ft.hide(mLastTab.fragment);
                }
            }

            if (newTab != null) {
                if (newTab.fragment == null) {
                    newTab.fragment = Fragment.instantiate(mContext,
                            newTab.clss.getName(), newTab.args);
                    ft.add(mContainerId, newTab.fragment, newTab.tag);
                } else {
                    // 将attach替换为show，显示Fragment
                    // ft.attach(newTab.fragment);
                    ft.show(newTab.fragment);
                }
            }

            mLastTab = newTab;
        }

        return ft;
    }

    private NewsFragmentTabHost.TabInfo getTabInfoForTag(String tabId) {
        for (int i = 0, count = mTabs.size(); i < count; i++) {
            final NewsFragmentTabHost.TabInfo tab = mTabs.get(i);
            if (tab.tag.equals(tabId)) {
                return tab;
            }
        }
        return null;
    }

}
