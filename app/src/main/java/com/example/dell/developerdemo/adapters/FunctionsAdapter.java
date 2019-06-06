package com.example.dell.developerdemo.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dell.developerdemo.R;



public class FunctionsAdapter extends RecyclerView.Adapter<FunctionsAdapter.ViewHolder> {
    private String[] mFuns;

    public FunctionsAdapter(String[] mFuns) {
        this.mFuns = mFuns;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView funName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            funName = itemView.findViewById(R.id.fun_name);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fun_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String fun = mFuns[position];
        holder.funName.setText(fun);
    }


    @Override
    public int getItemCount() {
        return mFuns.length;
    }
}
