package com.example.ehdus.testscan;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BarcodeAdapter extends RecyclerView.Adapter<BarcodeAdapter.ViewHolder> {

    private static ArrayList<Barcode> mBarcodes;

    BarcodeAdapter(ArrayList<Barcode> barcodes) {
        mBarcodes = barcodes;
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public BarcodeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                        int viewType) {
        return new BarcodeAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.barcode_item, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull BarcodeAdapter.ViewHolder holder, int pos) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Barcode r = mBarcodes.get(pos);
        holder.mName.setText(r.getName());
        holder.mDesc.setText(r.getDesc());
        holder.mPic.setImageResource(r.getPic());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mBarcodes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mName, mDesc;
        private final ImageView mPic;

        private ViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.b_name);
            mDesc = itemView.findViewById(R.id.b_desc);
            mPic = itemView.findViewById(R.id.b_pic);
        }
    }
}