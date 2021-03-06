package org.kexie.android.dng.media.view;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dl7.player.media.IjkPlayerView;

import org.kexie.android.dng.media.R;
import org.kexie.android.dng.media.databinding.FragmentVideoPlayerBinding;
import org.kexie.android.dng.media.viewmodel.entity.Media;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

@Route(path = "/media/video")
public class VideoPlayerFragment
        extends Fragment
        implements OnBackPressedCallback
{

    private IjkPlayerView player;

    private FragmentVideoPlayerBinding binding;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_video_player,
                container,
                false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        Media info = requireArguments().getParcelable("media");
        requireActivity().addOnBackPressedCallback(this, this);
        if (info != null)
        {
            player = binding.playerView;
            Glide.with(this)
                    .load(info.uri)
                    .apply(RequestOptions.fitCenterTransform())
                    .into(player.mPlayerThumb);

            binding.playerView.init()
                    .setSaveDir(Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                            .getAbsolutePath() + "/dng")
                    .setTitle(info.title)    // set title
                    .setVideoPath(info.uri)
                    .alwaysFullScreen()
                    .setMediaQuality(IjkPlayerView.MEDIA_QUALITY_HIGH)  // set the initial video url
                    .start();   // Start playing
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (player != null)
        {
            player.onPause();
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if (player != null)
        {
            player.onDestroy();
            player = null;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (player != null)
        {
            player.onResume();
        }
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if (player != null)
        {
            player.configurationChanged(newConfig);
        }
    }

    @Override
    public boolean handleOnBackPressed()
    {
        if (player != null)
        {
            return player.onBackPressed();
        }
        return false;
    }
}
