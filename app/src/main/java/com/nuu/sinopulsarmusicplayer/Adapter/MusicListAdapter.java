package com.nuu.sinopulsarmusicplayer.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.nuu.sinopulsarmusicplayer.Model.MusicListModel;
import com.nuu.sinopulsarmusicplayer.R;
import com.nuu.sinopulsarmusicplayer.util.DialogUtility;
import java.util.ArrayList;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHandler> implements View.OnClickListener{
    private OnItemClickListener mOnItemClickListener = null;
    ArrayList<MusicListModel> PIModel;
    Context context;
    //define interface
    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public MusicListAdapter(Context context, ArrayList<MusicListModel> PIModel){
        this.context = context;
        this.PIModel = PIModel;
    }

    @NonNull
    @Override
    public MusicListAdapter.ViewHandler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item,parent,false);
        view.setOnClickListener(this);
        return new MusicListAdapter.ViewHandler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicListAdapter.ViewHandler holder, final int position) {
        holder.imvAlbum.setVisibility(View.GONE);
        holder.llTime.setVisibility(View.VISIBLE);
        holder.tvAlbumName.setText(PIModel.get(position).getAlbumName());
        holder.tvAlbumSinger.setText(PIModel.get(position).getSinger());
        holder.tvSerial.setText(PIModel.get(position).getAlbumSerial());
        holder.tvTime.setText(PIModel.get(position).getAlbumTime());
        holder.itemView.setTag(position);

        if(PIModel.get(position).isFlag()){
            holder.tvSerial.setBackground(ContextCompat.getDrawable(context, R.drawable.text_round_background));
        }else {
            holder.tvSerial.setBackground(null);
        }

        holder.imbMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtility.showDialog(context);

            }
        });
    }

    @Override
    public int getItemCount() {
        return PIModel.size();
    }

    public class ViewHandler extends RecyclerView.ViewHolder {
        ImageView imvAlbum;
        TextView tvAlbumName, tvAlbumSinger, tvSerial, tvTime;
        ImageView imbMore;
        LinearLayout llItem, llTime;

        public ViewHandler(@NonNull View itemView) {
            super(itemView);
            imbMore = itemView.findViewById(R.id.imb_more);
            imvAlbum = itemView.findViewById(R.id.imv_album);
            tvAlbumName = itemView.findViewById(R.id.tv_albumName);
            tvAlbumSinger = itemView.findViewById(R.id.tv_num);
            tvSerial = itemView.findViewById(R.id.tv_serial);
            tvTime = itemView.findViewById(R.id.tv_time);
            llItem = itemView.findViewById(R.id.ll_item);
            llTime = itemView.findViewById(R.id.ll_time);
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }

}