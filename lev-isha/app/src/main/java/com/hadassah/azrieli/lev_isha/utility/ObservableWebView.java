package com.hadassah.azrieli.lev_isha.utility;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by Avihu Harush on 10/06/2017
 * E-Mail: tchvu3@gmail.com
 */


public class ObservableWebView extends WebView
{

    private OnScrollChangedCallback mOnScrollChangedCallback;

    public ObservableWebView(final Context context)
    {
        super(context);
    }

    public ObservableWebView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ObservableWebView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    protected void onScrollChanged(final int l, final int t, final int oldl, final int oldt)
    {
        super.onScrollChanged(l, t, oldl, oldt);
        if(mOnScrollChangedCallback != null)
            mOnScrollChangedCallback.onScroll(l, t);
    }

    public void setOnScrollChangedCallback(final OnScrollChangedCallback onScrollChangedCallback)
    {
        mOnScrollChangedCallback = onScrollChangedCallback;
    }


    public interface OnScrollChangedCallback
    {
        void onScroll(int l, int t);
    }
}