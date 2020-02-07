package com.remove.duplicate.contacts;


import com.remove.duplicate.contacts.models.Contact;

import java.util.List;

public class Presenter {

    ViewInterface view;

    Presenter(ViewInterface view) {
        this.view = view;
    }

    void getAllContacts() {
        view.getAllContacts();
    }



    void deleteAllDuplicates(List<Contact> contacts)
    {
        view.deleteDuplicates(contacts);
    }

    void deleteSingleDuplicate(String lookupKey)
    {
        view.deleteSingleDuplicate(lookupKey);
    }

    public interface ViewInterface {
        List<Contact> getAllContacts();
        void deleteDuplicates(List<Contact> contacts);
        void deleteSingleDuplicate(String lookupKey);
    }

}
