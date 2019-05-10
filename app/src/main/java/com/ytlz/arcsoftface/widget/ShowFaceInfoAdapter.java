package com.ytlz.arcsoftface.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ytlz.arcsoftface.R;
import com.ytlz.arcsoftface.faceserver.CompareResult1;
import com.ytlz.arcsoftface.faceserver.FaceServer1;

import java.io.File;
import java.util.List;

public class ShowFaceInfoAdapter extends RecyclerView.Adapter<ShowFaceInfoAdapter.CompareResultHolder> {
    private List<CompareResult1> compareResult1List;
    private LayoutInflater inflater;

    public ShowFaceInfoAdapter(List<CompareResult1> compareResult1List, Context context) {
        inflater = LayoutInflater.from(context);
        this.compareResult1List = compareResult1List;
    }

    @NonNull
    @Override
    public CompareResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_head, null, false);
        CompareResultHolder compareResultHolder = new CompareResultHolder(itemView);
        compareResultHolder.textView = itemView.findViewById(R.id.tv_item_name);
        compareResultHolder.imageView = itemView.findViewById(R.id.iv_item_head_img);
        return compareResultHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CompareResultHolder holder, int position) {
        if (compareResult1List == null) {
            return;
        }
        File imgFile = new File(FaceServer1.ROOT_PATH + File.separator + FaceServer1.SAVE_IMG_DIR + File.separator + compareResult1List.get(position).getUserName() + FaceServer1.IMG_SUFFIX);
        Glide.with(holder.imageView)
                .load(imgFile)
                .into(holder.imageView);
        holder.textView.setText(compareResult1List.get(position).getUserName());
    }

    @Override
    public int getItemCount() {
        return compareResult1List == null ? 0 : compareResult1List.size();
    }

    class CompareResultHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;

        CompareResultHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
