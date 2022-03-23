package com.example.pdfeditormadtpeeps.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pdfeditormadtpeeps.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class ImagePdfAdapter extends
        RecyclerView.Adapter<ImagePdfAdapter.ViewHolder> {

    public ArrayList<String> fileDataList;
    Activity context;


    public ImagePdfAdapter(Activity context, ArrayList<String> fileDataList) {
        this.fileDataList = fileDataList;
        this.context = context;
    }

    // Create new views
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View itemLayoutView;

        itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_image_layout, null);


        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final int pos = position;
        viewHolder.tv_pageno.setText(String.valueOf(pos+1));
        Bitmap bmImg = BitmapFactory.decodeFile(fileDataList.get(pos));
        viewHolder.iv_pdf.setImageBitmap(bmImg);

    }



    // Return the size arraylist
    @Override
    public int getItemCount() {
        return fileDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_pageno;
        public RoundedImageView iv_pdf;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemLayoutView.setLayoutParams(lp);
            tv_pageno = (TextView) itemLayoutView
                    .findViewById(R.id.tv_pageno);
            iv_pdf = (RoundedImageView) itemLayoutView.findViewById(R.id.iv_pdf);
        }
    }

}
