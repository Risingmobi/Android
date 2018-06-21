package com.alex.tur.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.alex.tur.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.DrawableTransformation;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import timber.log.Timber;

public class GlideHelper {

    public static void load(Context context, ImageView imageView, @DrawableRes int resId) {
        Glide.with(context)
                .load(resId)
                .into(imageView);
    }

    public static void loadAvatar(Context context, ImageView imageView, String path) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.avatar_empty)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);
        Glide.with(context)
                .load(path)
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        Timber.tag("AvatarView").e(e, "onLoadFailed");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        Timber.tag("AvatarView").d("onResourceReady");
                        return false;
                    }
                })
                .into(imageView);
    }

    public static void load(Context context, ImageView imageView, String path) {
        Timber.d("load path");
        Glide.with(context)
                .load(path)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        Timber.e(e, "load path onLoadFailed");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        Timber.d("load path onResourceReady");
                        return false;
                    }
                })
                .into(imageView);
    }

    public static void clear(Context context, ImageView imageView) {
        Glide.with(context).clear(imageView);
    }
}
