package com.example.ultrasandbox.ui.pixabayApp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.ultrasandbox.R;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdapterRecycler extends RecyclerView.Adapter<AdapterRecycler.mViewHolder> {

    private Context context;
    private ArrayList<ModelItem> itemArrList;

    private int itemPos;


    public AdapterRecycler(Context context, ArrayList<ModelItem> itemArrList) {
        this.context = context;
        this.itemArrList = itemArrList;
    }

    @NonNull
    @NotNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card_recycler, parent, false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull mViewHolder holder, int position) {
        ModelItem currentItem = itemArrList.get(position);

        String imageUrl = currentItem.getImageUrl();
        String authorName = currentItem.getAuthor();
        int likesCount = currentItem.getLikes();

        holder.tvAuthor.setText(authorName);
        holder.tvLikesCount.setText(likesCount + " 🥰");

        //manages images display / load errors
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.mipmap.ic_launcher)
                        .placeholder(R.mipmap.ic_launcher_round);

        Glide.with(holder.ivImageView)
                .load(imageUrl)
                .apply(options)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivImageView);
        Log.i("BVH", "onBindViewHolder: " + position + "item position");
        itemPos = position + 1;

    }

    @Override
    public int getItemCount() {
        return itemArrList.size();
    }

    public static class mViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImageView;
        TextView tvAuthor, tvLikesCount;
        public mViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivImageView = itemView.findViewById(R.id.iV_picture);
            tvAuthor = itemView.findViewById(R.id.tV_authorName);
            tvLikesCount = itemView.findViewById(R.id.tV_likes);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mOnItemClickListener != null){
                        int position = getBindingAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mOnItemClickListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }


    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public int getItemPos() {
        return itemPos;
    }

    private static OnItemClickListener mOnItemClickListener;


    public void setMOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }
}
