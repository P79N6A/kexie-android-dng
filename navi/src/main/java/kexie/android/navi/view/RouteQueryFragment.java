package kexie.android.navi.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.kexie.android.databinding.BT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import es.dmoral.toasty.Toasty;
import kexie.android.common.widget.ProgressWidget;
import kexie.android.navi.R;
import kexie.android.navi.databinding.FragmentRouteQueryBinding;
import kexie.android.navi.viewmodel.RouteQueryViewModel;

public class RouteQueryFragment extends Fragment
{
    private static final String WAIT_QUERY = "wait query";

    private FragmentRouteQueryBinding binding;
    private RouteQueryViewModel viewModel;

    @SuppressWarnings("All")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_route_query,
                container,
                false);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        binding.setTips(null);
        viewModel = ViewModelProviders.of(this)
                .get(RouteQueryViewModel.class);

        viewModel.getRoutes().observe(this,
                routes -> binding.setRoutes(routes));
        viewModel.getTips().observe(this,
                tips -> {
                    if (BT.isEmpty(tips))
                    {
                        Toasty.error(getContext().getApplicationContext(),
                                "发生错误，请检查网络连接")
                                .show();
                    } else
                    {
                        Toasty.success(getContext().getApplicationContext(),
                                "查询成功").show();
                    }
                    binding.setTips(tips);
                });
        viewModel.getLoading().observe(this,
                aBoolean -> {
                    if (aBoolean != null && aBoolean)
                    {
                        ProgressWidget progressWidget = new ProgressWidget();
                        progressWidget.show(getFragmentManager(),
                                WAIT_QUERY);
                    } else
                    {
                        ProgressWidget progressWidget
                                = (ProgressWidget) getFragmentManager()
                                .findFragmentByTag(WAIT_QUERY);
                        if (progressWidget != null)
                        {
                            progressWidget.dismiss();
                        }
                    }
                });
        viewModel.getQueryText().observe(this,
                s -> {
                    if (TextUtils.isEmpty(s))
                    {
                        Toasty.warning(getContext().getApplicationContext(),
                                "搜索内容为空").show();
                    } else
                    {
                        binding.setQueryText(s);
                    }
                });
        binding.setActions(viewModel.getActions());
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }
}