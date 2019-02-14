package org.kexie.android.dng.ux.viewmodel.entity;

import android.graphics.drawable.Drawable;

public class Function
{
    public final Drawable icon;
    public final String name;

    public Function(String name,Drawable icon)
    {
        this.name = name;
        this.icon = icon;
    }
}
