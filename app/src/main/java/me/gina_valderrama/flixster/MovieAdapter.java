package me.gina_valderrama.flixster;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import me.gina_valderrama.flixster.models.Config;
import me.gina_valderrama.flixster.models.Movie;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>{
    //list of movies
    ArrayList<Movie> movies;
    //config needed for image urls
    Config config;
    //context for rendering
    Context context;
    //initialize with list

    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    //creates and inflates a new view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        //get the context and create the inflater
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //create the view using the item_movie layout
        View movieView = inflater.inflate(R.layout.item_movie, parent, false);
        //return a new view (the view we created above, wrapped by a viewholder
        return new ViewHolder(movieView);
    }

    //binds an inflated view to a new item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder , int i) {
        //get the movie data at a specified position
        Movie movie = movies.get(i);
        //populate the view with the movie data
        viewHolder.tvOverview.setText(movie.getOverview());
        viewHolder.tvTitle.setText(movie.getTitle());

        //build url for poster image
        String imageURL = config.getImageURL(config.getPosterSize(), movie.getPosterPath());

        //load image w glide
        Glide.with(context)
                .load(imageURL)
                .apply(
                        RequestOptions.placeholderOf(R.drawable.flicks_movie_placeholder)
                        .error(R.drawable.flicks_movie_placeholder)
                        .fitCenter()
                        .transform(new RoundedCornersTransformation(25, 0))
                ).into(viewHolder.ivPosterImage);

    }

    //returns the num items in the list
    @Override
    public int getItemCount() {
        return movies.size();
    }

    // create the view holder as a static inner class
     public static class ViewHolder extends RecyclerView.ViewHolder{

        //track view objects
        ImageView ivPosterImage;
        TextView tvTitle;
        TextView tvOverview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //lookup view objects by id
             ivPosterImage = (ImageView) itemView.findViewById(R.id.ivPosterImage);
             tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
             tvOverview = (TextView) itemView.findViewById(R.id.tvOverview);
        }
    }
}
