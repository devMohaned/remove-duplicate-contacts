package com.remove.duplicate.contacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.remove.duplicate.contacts.adapters.ContactAdapter;
import com.remove.duplicate.contacts.models.Contact;
import com.remove.duplicate.contacts.models.Duplicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.remove.duplicate.contacts.Utils.showAlertDialog;

public class MainActivity extends AppCompatActivity implements Presenter.ViewInterface, ContactAdapter.MyViewHolder.MergeInterface {

    Presenter presenter = new Presenter(this);

    Context mContext = this;
    RecyclerView mContactsRecyclerView;
    ContactAdapter mAdapter;
    List<Contact> mContactList = new ArrayList<>();


    private void setupViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Loading");
        mContactsRecyclerView = findViewById(R.id.ID_recyclerview);
        mContactsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mContactsRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ContactAdapter(mContext, mContactList, this);
        mContactsRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();
        presenter.getAllContacts();
    }

    @Override
    public List<Contact> getAllContacts() {
        mContactList.clear();
        HashMap<String, Integer> allDuplicatedPhoneNumbers = new HashMap<>();
        HashMap<String, String> allPhoneNumbers = new HashMap<>();
        Uri mContactsUri = ContactsContract.Contacts.CONTENT_URI;
        String[] mContactsProjection = {
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
        };

        String mContactsSelection = null;
        String[] mContactsSelectionArg = null;
        String mContactsSortOrder = null;


        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(mContactsUri,
                mContactsProjection, mContactsSelection, mContactsSelectionArg, mContactsSortOrder);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {
                String rId = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0 /* 1 = True / 0 = False*/) {

                    Uri rSingleContactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                    String[] rSingleContactProjection = {
                            ContactsContract.Contacts.LOOKUP_KEY,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.Contacts.DISPLAY_NAME,
                    };

                    String rSingleContactSelection = ContactsContract.Contacts.LOOKUP_KEY + " = ?";
                    String[] rSingleContactSelectionArg = new String[]{rId};
                    String rSingleContactSortOrder = null;


                    Cursor pCur = cr.query(rSingleContactUri, rSingleContactProjection, rSingleContactSelection, rSingleContactSelectionArg, rSingleContactSortOrder);

                    while (pCur != null && pCur.moveToNext()) {
                        String rName = pCur.getString(pCur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String rSinglePhoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (!allPhoneNumbers.containsKey(rSinglePhoneNo)) {
                            allPhoneNumbers.put(rSinglePhoneNo,/* FIRST_FOUND */ rName);
                        } else {
                            if (!allDuplicatedPhoneNumbers.containsKey(rSinglePhoneNo)) {
                                // First Duplication
                                // Size before addition = Index
                                allDuplicatedPhoneNumbers.put(rSinglePhoneNo, mContactList.size());
                                Contact contact = new Contact();
                                contact.setContactId(rId);
                                contact.setContactName(allPhoneNumbers.get(rSinglePhoneNo));
                                contact.setContactPhoneNumber(rSinglePhoneNo);
                                /*
                                 *
                                 *
                                 * Duplicate
                                 *
                                 * */
                                Duplicate duplicate = new Duplicate();
                                duplicate.setContactId(rId);
                                duplicate.setContactName(rName);
                                duplicate.setContactPhoneNumber(rSinglePhoneNo);

                                contact.updateContactDuplicates(duplicate);
                                mContactList.add(contact);
                            } else {
                                // More Duplication
                                Duplicate duplicate = new Duplicate();
                                duplicate.setContactId(rId);
                                duplicate.setContactName(rName);
                                duplicate.setContactPhoneNumber(rSinglePhoneNo);

                                mContactList.get(allDuplicatedPhoneNumbers.get(rSinglePhoneNo)).updateContactDuplicates(duplicate);
                            }

                        }
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
            mAdapter.notifyDataSetChanged();
            getSupportActionBar().setTitle(mContactList.size() + " Duplicates");
        }


        return null;
    }


    @Override
    public void deleteDuplicates(List<Contact> contacts) {
        int duplicateSize = mContactList.size();
        showAlertDialog(this, "Deleting " + duplicateSize + " Duplicates"
                , "You'll delete " + duplicateSize + " Duplicates \n" + "Be careful, This action cannot be undone",
                "Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAsyncTask();
                    }
                }, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

    }

    @Override
    public void deleteSingleDuplicate(String lookupKey) {
        Uri contactUri = ContactsContract.Contacts.CONTENT_URI;

        String selection =
                ContactsContract.Contacts.LOOKUP_KEY + " = ? ";
        String[] selectionArg = new String[]{lookupKey};

        Cursor cur = mContext.getContentResolver().query(contactUri, null, selection, selectionArg, null);
        try {
            if (cur != null && cur.moveToFirst()) {
                do {
                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                    mContext.getContentResolver().delete(uri, null, null);
                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_delete:
                presenter.deleteAllDuplicates(mContactList);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void deleteDuplicatesFromAdapter(int pos) {
        final Contact selectedContact = mContactList.get(pos);
        int duplicateSize = selectedContact.getContactDuplicates().size();
        showAlertDialog(this, "Deleting " + duplicateSize + " Duplicates"
                , "You'll delete " + duplicateSize + " Duplicates \n" + "Be careful, This action cannot be undone",
                "Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (Duplicate duplicate : selectedContact.getContactDuplicates()) {
                            presenter.deleteSingleDuplicate(duplicate.getContactId());
                        }
                        presenter.getAllContacts();

                    }
                }, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
    }


    AsyncTask<Void, String, Void> deletionAsyncTask;

    private void startAsyncTask() {
        final RelativeLayout deletionLayout = findViewById(R.id.ID_deletion_layout);
        final TextView deletionTextView = findViewById(R.id.ID_deletion_txtview);
        deletionAsyncTask = new AsyncTask<Void, String, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                deletionLayout.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                deletionLayout.setVisibility(View.GONE);
                presenter.getAllContacts();
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                deletionTextView.setText("Deleting " + values[0]);
            }

            @Override
            protected Void doInBackground(Void... aVoid) {
                String current = "Starting";
                for (Contact selectedContact : mContactList) {
                    for (Duplicate duplicate : selectedContact.getContactDuplicates()) {
                        current = duplicate.getContactName();
                        presenter.deleteSingleDuplicate(duplicate.getContactId());
                        publishProgress(current);
                    }
                }
                return null;
            }
        };

        deletionAsyncTask.execute();
    }
}
