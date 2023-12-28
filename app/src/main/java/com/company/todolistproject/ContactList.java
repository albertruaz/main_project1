package com.company.todolistproject;

import java.util.ArrayList;
import java.util.List;

public class ContactList {
    private List<Contact> contacts;

    public ContactList() {
        this.contacts = new ArrayList<>();
    }

    public void addContact(Contact contact) {
        contacts.add(contact);
    }

    public List<Contact> getContacts() {
        return contacts;
    }
}