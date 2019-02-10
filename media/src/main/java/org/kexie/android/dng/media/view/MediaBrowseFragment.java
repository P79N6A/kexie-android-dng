package org.kexie.android.dng.media.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.kexie.android.dng.media.R;
import org.kexie.android.dng.media.databinding.FragmentMediaBrowseBinding;
import org.kexie.android.dng.media.entity.MediaInfo;
import org.kexie.android.dng.media.viewmodel.MediaBrowseViewModel;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import org.kexie.android.common.widget.ProgressHelper;

public class MediaBrowseFragment
        extends Fragment
{
    private MediaBrowseViewModel viewModel;

    private FragmentMediaBrowseBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_media_browse,
                container,
                false);
        binding.getRoot().setOnTouchListener((v, event) -> true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        Map<String, View.OnClickListener> actions = getActions();
        binding.setActions(actions);
        binding.dataContent.setLayoutManager(
                new StaggeredGridLayoutManager(4,
                        StaggeredGridLayoutManager.VERTICAL));
        binding.setOnCreateAdapter((adapter) -> {
            TextView textView = new TextView(getContext());
            textView.setTextSize(20);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(getResources()
                    .getColor(R.color.colorBlackAlpha54));
            textView.setText("空空如也");
            adapter.setEmptyView(textView);
            adapter.setOnItemClickListener((adapter1, view1, position) -> {
                MediaInfo info = (MediaInfo) adapter.getData().get(position);
                switch (info.getType())
                {
                    case MediaInfo.TYPE_PHOTO:
                    {
                        getFragmentManager()
                                .beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .add(getId(), PhotoViewFragment.newInstance(info,
                                        () -> adapter1.remove(position)))
                                .addToBackStack(null)
                                .commit();
                    }
                    break;
                    case MediaInfo.TYPE_VIDEO:
                    {
                        getFragmentManager()
                                .beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .add(getId(), VideoPlayerFragment.newInstance(info))
                                .addToBackStack(null)
                                .commit();
                    }
                    break;
                }
            });
        });
        viewModel = ViewModelProviders.of(this)
                .get(MediaBrowseViewModel.class);
        viewModel.getTitle().observe(this,
                (value) -> binding.setTitle(value));
        ProgressHelper.observe(viewModel.getLoading(), getFragmentManager()
                , getId());
        viewModel.getMediaInfo().observe(this,
                (value) -> binding.setMediaInfos(value));
        viewModel.loadPhoto();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        System.gc();
    }

    private Map<String, View.OnClickListener> getActions()
    {
        return new ArrayMap<String, View.OnClickListener>()
        {
            {
                put("相册", v ->{
                    viewModel.loadPhoto();
                    binding.dataContent.stopScroll();
                    binding.dataContent.stopNestedScroll();
                });
                put("视频",v ->{
                    viewModel.loadVideo();
                    binding.dataContent.stopScroll();
                    binding.dataContent.stopNestedScroll();
                });
            }
        };
    }
}