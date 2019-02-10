package org.kexie.android.dng.ux.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import org.kexie.android.common.util.ZoomTransformation;
import org.kexie.android.dng.media.view.MediaBrowseFragment;
import org.kexie.android.dng.ux.R;
import org.kexie.android.dng.ux.entity.Function;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public final class FunctionFactory
        extends AsyncTask<Void,Void,List<Function>>
{
    private static final int BORDER_SIZE = 250;
    private final ZoomTransformation zoomTransformation;
    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private final Callback callback;
    private final int id;

    private FunctionFactory(Context context,
                            int size,
                            Callback callback, int id)
    {
        super();
        this.context = context.getApplicationContext();
        this.zoomTransformation = new ZoomTransformation(size);
        this.callback = callback;
        this.id = id;
    }

    @Override
    protected List<Function> doInBackground(Void... voids)
    {
        List<SubLoadTask> targets
                = new LinkedList<SubLoadTask>()
        {
            {
                add(create("天气",
                        R.mipmap.image_weather,
                        v -> {

                        }));
                add(create("多媒体",
                        R.mipmap.image_media,
                        v -> {
                            AppCompatActivity activity
                                    = (AppCompatActivity) v.getContext();
                            activity.getSupportFragmentManager()
                                    .beginTransaction()
                                    .add(id,new MediaBrowseFragment())
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .addToBackStack(null)
                                    .commit();
                        }));
                add(create("APPS",
                        R.mipmap.image_apps,
                        v -> {

                        }));
            }
        };
        List<Function> functions = new ArrayList<>(targets.size());
        try
        {
            for (SubLoadTask task : targets)
            {
                functions.add(task.get());
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return functions;
    }

    public interface Callback
    {
        void onResult(List<Function> functions);
    }

    private SubLoadTask create(String name,
                               int res,
                               View.OnClickListener listener)
    {
        return new SubLoadTask(context, zoomTransformation, name, res, listener);
    }

    static void getDefault(Fragment fragment,
                           Callback callback)
    {
        new FunctionFactory(fragment.getContext()
                , BORDER_SIZE,
                callback, fragment.getId()).execute();
    }

    @Override
    protected void onPostExecute(List<Function> functions)
    {
        if (callback != null)
        {
            callback.onResult(functions);
        }
    }

    private static final class SubLoadTask
            implements RequestListener<Drawable>
    {
        private final FutureTarget<Drawable> innerTask;
        private Function function;
        private final String name;
        private final int mipmap;
        private final View.OnClickListener action;
        private final Context context;

        private SubLoadTask(Context context,
                            ZoomTransformation transformation,
                            String name,
                            int mipmap,
                            View.OnClickListener action)
        {
            this.name = name;
            this.mipmap = mipmap;
            this.action = action;
            this.context = context.getApplicationContext();
            this.innerTask = Glide.with(this.context)
                    .load(mipmap)
                    .apply(RequestOptions.bitmapTransform(transformation))
                    .listener(this).submit();
        }

        private Function get()
                throws ExecutionException, InterruptedException
        {
            innerTask.get();
            return function;
        }

        @Override
        public boolean onLoadFailed(@Nullable GlideException e,
                                    Object model,
                                    Target<Drawable> target,
                                    boolean isFirstResource)
        {
            Resources resources = context.getResources();
            function = new Function.Builder()
                    .action(action)
                    .icon(new BitmapDrawable(resources,
                            BitmapFactory.decodeResource(resources, mipmap)))
                    .name(name)
                    .build();
            return true;
        }

        @Override
        public boolean onResourceReady(Drawable resource,
                                       Object model,
                                       Target<Drawable> target,
                                       DataSource dataSource,
                                       boolean isFirstResource)
        {
            this.function = new Function.Builder()
                    .action(action)
                    .icon(resource)
                    .name(name)
                    .build();
            return true;
        }
    }
}