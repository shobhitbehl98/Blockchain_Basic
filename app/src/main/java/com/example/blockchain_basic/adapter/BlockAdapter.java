package com.example.blockchain_basic.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.example.blockchain_basic.R;
import com.example.blockchain_basic.model.BlockModel;
import com.example.blockchain_basic.viewholder.RecyclerViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BlockAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private List<BlockModel>blocks;
    private int lastPosition=-1;
    public BlockAdapter(@Nullable List<BlockModel> blocks) {
        this.blocks = blocks;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_block_data,parent,false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        int idx=blocks.get(position).getIndex();
        StringBuilder g=new StringBuilder();
        g.append(idx);
        holder.txtIndex.setText(g.toString());
        holder.txtPreviousHash.setText(blocks.get(position).getPreviousHash()!=null?blocks.get(position).getPreviousHash():"Null");
        holder.txtData.setText(blocks.get(position).getData());
        long yourmilliseconds = blocks.get(position).getTimestamp();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy  HH:mm:ss");
        Date resultdate = new Date(yourmilliseconds);
        holder.txtTimeStamp.setText(sdf.format(resultdate));
        holder.txtHash.setText(blocks.get(position).getHash());
    }


    @Override
    public int getItemViewType(int position) {
        return R.layout.item_block_data;
    }

    @Override
    public int getItemCount() {
        return blocks==null? 0:blocks.size();
    }
}
