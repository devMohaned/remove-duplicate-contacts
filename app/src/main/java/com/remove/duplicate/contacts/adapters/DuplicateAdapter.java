package com.remove.duplicate.contacts.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.remove.duplicate.contacts.R;
import com.remove.duplicate.contacts.models.Duplicate;

import java.util.List;

public class DuplicateAdapter extends RecyclerView.Adapter<DuplicateAdapter.MyViewHolder> {
    private List<Duplicate> mDuplicateList;
    private Context mContext;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mDuplicateName;

        MyViewHolder(View v) {
            super(v);
            mDuplicateName = v.findViewById(R.id.ID_item_duplicate_name);
        }
    }

    public DuplicateAdapter(Context context, List<Duplicate> contacts) {
        this.mContext = context;
        this.mDuplicateList = contacts;
    }


    @Override
    public DuplicateAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.duplicate_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Duplicate currentDuplicate = mDuplicateList.get(position);
        holder.mDuplicateName.setText(currentDuplicate.getContactName());

    }
    @Override
    public int getItemCount() {
        return mDuplicateList.size();
    }
}