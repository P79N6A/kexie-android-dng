package kexie.android.dng.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import kexie.android.common.util.SystemUI;
import kexie.android.dng.R;

public final class HostActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        SystemUI.hide(getWindow());
        DataBindingUtil.setContentView(this,
                R.layout.activity_host);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container,
                        new DesktopFragment())
                .commit();
    }


}