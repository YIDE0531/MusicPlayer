package com.nuu.sinopulsarmusicplayer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nuu.sinopulsarmusicplayer.AlbumActivity;
import com.nuu.sinopulsarmusicplayer.Model.albumModel;
import com.nuu.sinopulsarmusicplayer.MusicListActivity;
import com.nuu.sinopulsarmusicplayer.R;
import com.nuu.sinopulsarmusicplayer.util.DialogUtility;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHandler> {
    ArrayList<albumModel> PIModel;
    Context context;

    public AlbumAdapter(Context context, ArrayList<albumModel> PIModel){
        this.context = context;
        this.PIModel = PIModel;
    }

    @NonNull
    @Override
    public AlbumAdapter.ViewHandler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item,parent,false);
        return new AlbumAdapter.ViewHandler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.ViewHandler holder, final int position) {
        Glide.with(context).load(PIModel.get(position).getAlbumPicture())
                .thumbnail(Glide.with(context).load(R.drawable.t1))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.imvAlbum);
        holder.tvAlbumName.setText(PIModel.get(position).getAlbumName());
        holder.tvAlbumNum.setText(PIModel.get(position).getAlbumNum() + " 首歌曲");

        holder.imbMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtility.showDialog(context);

            }
        });

        holder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, MusicListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("ShopName", PIModel.get(position).getAlbumName());
                bundle.putString("ShopImage", PIModel.get(position).getAlbumNum());
                bundle.putInt("posistion", position);
                intent.putExtras(bundle);
                ((AlbumActivity) context).startActivityForResult(intent, 101);
            }
        });

    }

    @Override
    public int getItemCount() {
        return PIModel.size();
    }

    public class ViewHandler extends RecyclerView.ViewHolder {
        ImageView imvAlbum;
        TextView tvAlbumName, tvAlbumNum;
        ImageView imbMore;
        LinearLayout llItem;

        public ViewHandler(@NonNull View itemView) {
            super(itemView);
            imvAlbum = itemView.findViewById(R.id.imv_album);
            tvAlbumName = itemView.findViewById(R.id.tv_albumName);
            tvAlbumNum = itemView.findViewById(R.id.tv_num);
            imbMore = itemView.findViewById(R.id.imb_more);
            llItem = itemView.findViewById(R.id.ll_item);
        }
    }
}