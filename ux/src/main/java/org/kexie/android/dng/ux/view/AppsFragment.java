package org.kexie.android.dng.ux.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.kexie.android.dng.ux.R;
import org.kexie.android.dng.ux.databinding.FragmentAppsBinding;
import org.kexie.android.dng.ux.viewmodel.AppsViewModel;
import org.kexie.android.dng.ux.viewmodel.entity.LiteAppInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import eightbitlab.com.blurview.RenderScriptBlur;
import es.dmoral.toasty.Toasty;

import static com.uber.autodispose.AutoDispose.autoDisposable;
import static com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider.from;

public class AppsFragment extends Fragment
{
    private FragmentAppsBinding binding;
    private AppsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_apps,
                container,
                false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(AppsViewModel.class);
        //dataBinding
        binding.blurView.setupWith((ViewGroup) view.getParent())
                .setFrameClearDrawable(getActivity().getWindow()
                        .getDecorView()
                        .getBackground())
                .setBlurAlgorithm(new RenderScriptBlur(getContext()))
                .setBlurRadius(20f)
                .setHasFixedTransformationMatrix(true);
        binding.dataContent.setLayoutManager(new GridLayoutManager(getContext(),
                5));
        binding.setOnItemClick((adapter, view1, position) ->
                viewModel.requestJumpBy((LiteAppInfo) adapter.getData().get(position)));
        //liveData
        viewModel.getAppInfo().observe(this, binding::setAppInfo);
        viewModel.loadAppInfo();
        //rx
        viewModel.getOnJumpTo()
                .as(autoDisposable(from(this)))
                .subscribe();
        viewModel.getOnErrorMessage()
                .as(autoDisposable(from(this)))
                .subscribe(s -> Toasty.error(getContext(), s).show());
        viewModel.getOnSuccessMessage()
                .as(autoDisposable(from(this)))
                .subscribe(s -> Toasty.success(getContext(), s).show());
    }
}
