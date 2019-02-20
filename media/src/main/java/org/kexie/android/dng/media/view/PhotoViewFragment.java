package org.kexie.android.dng.media.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import org.kexie.android.common.util.AnimationAdapter;
import org.kexie.android.dng.media.R;
import org.kexie.android.dng.media.databinding.FragmentPhotoViewBinding;
import org.kexie.android.dng.media.viewmodel.MediaManagedViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProviders;
import eightbitlab.com.blurview.RenderScriptBlur;
import es.dmoral.toasty.Toasty;
import mapper.Mapping;

import static com.uber.autodispose.AutoDispose.autoDisposable;
import static com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider.from;

@Mapping("dng/media/photo")
public class PhotoViewFragment extends Fragment
{

    private FragmentPhotoViewBinding binding;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_photo_view,
                container,
                false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        //dataBinding
        binding.setLifecycleOwner(this);
        binding.getRoot().setOnTouchListener((v, event) -> true);
        binding.setInfo(getArguments().getParcelable("info"));
        binding.blurView.setupWith(binding.photo)
                .setFrameClearDrawable(
                        getActivity()
                                .getWindow()
                                .getDecorView()
                                .getBackground())
                .setBlurAlgorithm(new RenderScriptBlur(getContext()))
                .setBlurRadius(20f)
                .setHasFixedTransformationMatrix(true);
        binding.setHide(false);
        MediaManagedViewModel viewModel = ViewModelProviders.of(this)
                .get(MediaManagedViewModel.class);
        binding.setActions(new ArrayMap<String, View.OnClickListener>()
        {
            {
                View.OnClickListener a1 = v -> getActivity().onBackPressed();
                put("back", a1);
                put("delete", v -> {
                    if (viewModel.delete(binding.getInfo()))
                    {
                        Fragment fragment = getTargetFragment();
                        if (fragment != null)
                        {
                            fragment.onActivityResult(getTargetRequestCode(),
                                    Activity.RESULT_FIRST_USER,
                                    new Intent().putExtra("index", getArguments()
                                            .getInt("index")));
                        }
                        getActivity().onBackPressed();
                    }
                });
                put("hide", v -> doHideAnimation());
            }
        });
        //rx
        viewModel.getOnErrorMessage()
                .as(autoDisposable(from(this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(s -> Toasty.error(getContext(), s).show());
        viewModel.getOnSuccessMessage()
                .as(autoDisposable(from(this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(s -> Toasty.success(getContext(), s).show());
    }

    private void doHideAnimation()
    {
        AlphaAnimation animation
                = (AlphaAnimation) binding.blurView.getTag();
        if (animation != null)
        {
            animation.cancel();
        }
        if (binding.getHide())//show
        {
            binding.blurView.setVisibility(View.VISIBLE);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
            alphaAnimation.setDuration(200);
            binding.blurView.startAnimation(alphaAnimation);
            binding.blurView.setTag(alphaAnimation);
        } else//hide
        {
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setAnimationListener(new AnimationAdapter()
            {
                @Override
                public void onAnimationEnd(Animation animation)
                {
                    if (binding.getHide())
                    {
                        binding.blurView.setVisibility(View.GONE);
                    }
                }
            });
            alphaAnimation.setDuration(200);
            binding.blurView.startAnimation(alphaAnimation);
            binding.blurView.setTag(alphaAnimation);
        }
        binding.setHide(!binding.getHide());
    }
}
