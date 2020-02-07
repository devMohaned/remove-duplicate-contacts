package com.remove.duplicate.contacts.models;

import java.util.ArrayList;
import java.util.List;

public class Contact extends Info {
    private List<Duplicate> contactDuplicates = new ArrayList<>();

    public Contact(){}


    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }

    public List<Duplicate> getContactDuplicates() {
        return contactDuplicates;
    }

    public void updateContactDuplicates(Duplicate unAddedDuplicated) {
        this.contactDuplicates.add(unAddedDuplicated);
    }


}
