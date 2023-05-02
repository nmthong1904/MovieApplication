package com.example.daltdd.Activity.base;

import android.widget.ImageView;

import com.example.daltdd.Activity.model.MoviePopular;

public interface IMovieOnLickListener {
    void onMovieClick(MoviePopular moviePopular, ImageView imageView);
}
