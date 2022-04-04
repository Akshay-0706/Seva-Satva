package com.official.sevasatva;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class studentNewsAdapter extends RecyclerView.Adapter<studentNewsAdapter.ViewHolder> {
    private final ArrayList<studentNewsModel> articlesArrayList;

    public studentNewsAdapter(ArrayList<studentNewsModel> articlesArrayList, Context context) {
        this.articlesArrayList = articlesArrayList;
        this.context = context;
    }

    private final Context context;

    @NonNull
    // @org.jetbrains.annotations.NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_student_news_recycler_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        studentNewsModel articles = articlesArrayList.get(position);
        holder.title.setText(articles.getTitle());
        if (articles.getUrlToImage() != null)
            Picasso.get().load(articles.getUrlToImage()).into(holder.image);
        else
            Picasso.get().load(R.drawable.student_news_default_image).into(holder.image);
        Log.i("image", "onBindViewHolder: " + articles.getTitle() + "\t" + articles.getUrlToImage());
        if (articles.getUrlToImage() == null)
            Log.i("image", "onBindViewHolder: " + "YESSSSSSSSSSSSSSSSSSSSSSS");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, studentNewsDetails.class);
                intent.putExtra("title", articles.getTitle());
                intent.putExtra("info", articles.getContent());
                intent.putExtra("desc", articles.getDescription());
                intent.putExtra("image", articles.getUrlToImage());
                intent.putExtra("url", articles.getUrl());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articlesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.studentNewsTitle);
            this.image = itemView.findViewById(R.id.studentNewsImage);
        }
    }
}
