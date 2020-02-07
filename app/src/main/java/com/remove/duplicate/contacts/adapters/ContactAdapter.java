package com.remove.duplicate.contacts.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.remove.duplicate.contacts.R;
import com.remove.duplicate.contacts.models.Contact;
import com.remove.duplicate.contacts.models.Duplicate;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {
    private List<Contact> mContactList;
    private Context mContext;
    MyViewHolder.MergeInterface mergeInterface;


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mPhoneNumber, mName;
        ImageView mDeleteImage;
        RecyclerView mDuplicatesRecyclerView;

        MyViewHolder(View v, final MergeInterface mergeInterface) {
            super(v);
            mDuplicatesRecyclerView = v.findViewById(R.id.ID_item_duplicates_recyclerview);
            mPhoneNumber = v.findViewById(R.id.ID_item_number);
            mDeleteImage = v.findViewById(R.id.ID_item_duplicate_delete);
            mName = v.findViewById(R.id.ID_item_name);



            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mergeInterface.deleteDuplicatesFromAdapter(getAdapterPosition());
                }
            });
        }


        public interface MergeInterface{
            void deleteDuplicatesFromAdapter(int pos);
        }

    }

    public ContactAdapter(Context context, List<Contact> contacts, MyViewHolder.MergeInterface mergeInterface) {
        this.mContext = context;
        this.mContactList = contacts;
        this.mergeInterface = mergeInterface;
    }

    private void setupRecyclerView(List<Duplicate> duplicates, ContactAdapter.MyViewHolder viewHolder) {
        if (duplicates.size() > 0) {
            viewHolder.mDuplicatesRecyclerView.setVisibility(View.VISIBLE);
            viewHolder.mDuplicatesRecyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            viewHolder.mDuplicatesRecyclerView.setLayoutManager(layoutManager);
            DuplicateAdapter mAdapter = new DuplicateAdapter(mContext, duplicates);
            viewHolder.mDuplicatesRecyclerView.setAdapter(mAdapter);
        } else {
            viewHolder.mDuplicatesRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public ContactAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);

        MyViewHolder vh = new MyViewHolder(v, mergeInterface);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Contact currentContact = mContactList.get(position);
        holder.mName.setText(currentContact.getContactName());
        holder.mPhoneNumber.setText(currentContact.getContactPhoneNumber());
        setupRecyclerView(currentContact.getContactDuplicates(), holder);

    }
    @Override
    public int getItemCount() {
        return mContactList.size();
    }
}