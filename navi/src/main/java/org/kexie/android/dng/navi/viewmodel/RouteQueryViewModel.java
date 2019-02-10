package org.kexie.android.dng.navi.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.RouteSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.kexie.android.dng.navi.entity.Point;
import org.kexie.android.dng.navi.entity.Query;
import org.kexie.android.dng.navi.entity.SdkRoute;
import org.kexie.android.dng.navi.util.Points;

public class RouteQueryViewModel extends AndroidViewModel
{
    private static final String DEBUG_TEXT = "火车站";
    private static final String CITY = "西安";
    private final RouteSearch routeSearch;
    private final Executor singleTask = Executors.newSingleThreadExecutor();
    private final MutableLiveData<List<SdkRoute>> routes = new MutableLiveData<>();
    private final MutableLiveData<List<Tip>> tips = new MutableLiveData<>();
    private final MutableLiveData<String> queryText = new MutableLiveData<>();
    private final MutableLiveData<String> loading = new MutableLiveData<>();

    public RouteQueryViewModel(@NonNull Application application)
    {
        super(application);
        routeSearch = new RouteSearch(application);
    }

    public MutableLiveData<String> getLoading()
    {
        return loading;
    }

    public MutableLiveData<String> getQueryText()
    {
        return queryText;
    }

    public MutableLiveData<List<Tip>> getTips()
    {
        return tips;
    }

    public MutableLiveData<List<SdkRoute>> getRoutes()
    {
        return routes;
    }

    public Map<String, View.OnClickListener> getActions()
    {
        return new HashMap<String, View.OnClickListener>()
        {
            {
                put("开始查询", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        textQuery(DEBUG_TEXT);
                    }
                });
            }
        };
    }

    private void textQuery(final String text)
    {
        if (!TextUtils.isEmpty(text))
        {
            loading.setValue("加载中");
            singleTask.execute(new Runnable()
            {
                @Override
                public void run()
                {

                    final InputtipsQuery inputtipsQuery
                            = new InputtipsQuery(text, CITY);
                    Inputtips inputtips = new Inputtips(getApplication(), inputtipsQuery);
                    try
                    {
                        List<Tip> rawResult = inputtips.requestInputtips();
                        List<Tip> result = new ArrayList<>();
                        int i=0;
                        for (Tip tip : rawResult)
                        {
                            if (!TextUtils.isEmpty(tip.getPoiID()))
                            {
                                result.add(tip);
                            }
                        }
                        tips.postValue(result);
                        loading.postValue(null);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        tips.postValue(null);
                        loading.postValue(null);
                    }
                }
            });
        }
    }

    private void routeQuery(final Query query)
    {
        List<List<LatLonPoint>> lists = new ArrayList<>();
        if (query.avoids != null)
        {
            for (List<Point> points : query.avoids)
            {
                lists.add(Points.toLatLatLonPoints(points));
            }
        }
        final RouteSearch.DriveRouteQuery driveRouteQuery
                = new RouteSearch.DriveRouteQuery(
                new RouteSearch.FromAndTo(query.from.toLatLonPoint(),
                        query.to.toLatLonPoint()),
                query.mode,
                Points.toLatLatLonPoints(query.ways),
                lists, "");
        singleTask.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final List<SdkRoute> routes = new ArrayList<>();
                    for (DrivePath path : routeSearch
                            .calculateDriveRoute(driveRouteQuery)
                            .getPaths())
                    {
                        routes.add(new SdkRoute.Builder()
                                .from(query.from)
                                .to(query.to)
                                .path(path)
                                .build());
                    }
                    RouteQueryViewModel.this.routes.setValue(routes);
                } catch (Exception e)
                {
                    e.printStackTrace();
                    RouteQueryViewModel.this.routes.setValue(null);
                }
            }
        });
    }

    @Override
    protected void onCleared()
    {
        System.gc();
    }
}