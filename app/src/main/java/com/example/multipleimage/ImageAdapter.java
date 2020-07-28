package com.example.multipleimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder>
{
    ArrayList<String> imageList;
    Context ctx;

    public ImageAdapter(Context ctx, ArrayList<String> imageList)
    {
        this.imageList = imageList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ImageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View v = layoutInflater.inflate(R.layout.image_list,parent,false);
        return new ImageAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        Glide.with(ctx)
                .load(imageList.get(position))
                .into(holder.imageView);
       // holder.imageView.setImageBitmap(imageList.get(position));
    }

    @Override
    public int getItemCount()
    {
        return imageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);

        }
    }
}
