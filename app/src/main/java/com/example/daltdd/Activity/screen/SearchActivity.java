package com.example.daltdd.Activity.screen;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.daltdd.Activity.adapter.MovieRecyclerAdapter;
import com.example.daltdd.Activity.adapter.MovieRecyclerSearchAdapter;
import com.example.daltdd.Activity.base.IMovieOnLickListener;
import com.example.daltdd.Activity.model.ListMoviePopular;
import com.example.daltdd.Activity.model.MoviePopular;
import com.example.daltdd.Activity.network.HomeApi;
import com.example.daltdd.Activity.network.HomeApiService;
import com.example.daltdd.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements IMovieOnLickListener {


    RecyclerView MoviewRV;
    List<MoviePopular> listMoviewPopular;
    HomeApiService homeApiProvider;
    EditText editTextSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        MoviewRV= findViewById(R.id.recycler_searchMovie);

        editTextSearch = findViewById(R.id.editSearch);
        
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            private Timer timer=new Timer();
            private final long DELAY = 500; // m
            @Override
            public void afterTextChanged(Editable s) {
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                getItemSearch();
                            }
                        },
                        DELAY
                );

            }
        });


    }
    void getItemSearch(){
        listMoviewPopular = new ArrayList<>();
        final MovieRecyclerSearchAdapter movieRecyclerAdapter = new MovieRecyclerSearchAdapter(getBaseContext(),listMoviewPopular,this);


        homeApiProvider = HomeApi.getHomeApiProvider();

        homeApiProvider.searchMovieByName(String.valueOf(editTextSearch.getText().toString()),1).enqueue(
                new Callback<ListMoviePopular>() {
                    @Override
                    public void onResponse(Call<ListMoviePopular> call, Response<ListMoviePopular> response) {
                        ListMoviePopular res = response.body();
                        if(response.isSuccessful()){
                            for(int i=0;i<res.getResults().size();i++){
                                listMoviewPopular.add(new MoviePopular(res.getResults().get(i)));
                            }
                        }

                        MoviewRV.setAdapter(movieRecyclerAdapter);
                        MoviewRV.setLayoutManager(new LinearLayoutManager(getBaseContext(),LinearLayoutManager.VERTICAL,false));

                    }

                    @Override
                    public void onFailure(Call<ListMoviePopular> call, Throwable t) {

                    }
                }
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onMovieClick(MoviePopular moviePopular, ImageView imageView) {
       // Toast.makeText(this,moviePopular.getId().toString(),Toast.LENGTH_LONG).show();

        try{
            Intent intent = new Intent(this,DetailMovieActivity.class);

            String img="";
            String overView="";
            if( moviePopular.getOverview()==null){
                moviePopular.setOverview("");
            }
            if(moviePopular.getBackdropPath()==null){
                moviePopular.setBackdropPath("");
            }
            intent.putExtra("title",moviePopular.getTitle());
            intent.putExtra("image",moviePopular.getPosterPath());
            intent.putExtra("backgroundVideo",moviePopular.getBackdropPath());

            intent.putExtra("popularity",moviePopular.getPopularity());
            intent.putExtra("vote_count",moviePopular.getVoteCount());
            intent.putExtra("vote_average",moviePopular.getVoteAverage());

            intent.putExtra("name_movie",moviePopular.getTitle());
            intent.putExtra("create_day",moviePopular.getReleaseDate());

            intent.putExtra("overview",moviePopular.getOverview());

            intent.putExtra("idmovie",moviePopular.getId());

            // Toast.makeText(this,moviePopular.getId(),Toast.LENGTH_LONG).show();
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,imageView,"sharedName");
            startActivity(intent,options.toBundle());
        }catch (Exception e){
            Toast.makeText(this,"Đã có lỗi ",Toast.LENGTH_LONG).show();
        }

    }
}
