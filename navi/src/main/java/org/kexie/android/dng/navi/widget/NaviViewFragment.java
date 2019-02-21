package org.kexie.android.dng.navi.widget;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.navi.AMapNaviView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NaviViewFragment
        extends Fragment
{
    private AMapNaviView innerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        return innerView;
    }

    @Override
    public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs, @Nullable Bundle savedInstanceState)
    {
        super.onInflate(context, attrs, savedInstanceState);
        if (innerView == null)
        {
            innerView = new AMapNaviView(context);
        }
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        if (innerView == null)
        {
            innerView = new AMapNaviView(context);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        innerView.onCreate(savedInstanceState);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        innerView.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        innerView.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (innerView != null)
        {
            innerView.onDestroy();
            innerView = null;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        innerView.onSaveInstanceState(outState);
    }

    public AMapNaviView getInnerView()
    {
        return innerView;
    }
}