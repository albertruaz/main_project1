package com.company.todolistproject;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText item;
    Button add;
    ListView listView;
    ArrayList<String> itemlist = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    public ContactList extract() {
        try {

            InputStream inputStream = getAssets().open("address.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder StringBuilder = new StringBuilder();
            for (int data = reader.read(); data != -1; data = reader.read()) {
                StringBuilder.append((char)data);
            }
            String jsonString = StringBuilder.toString();

            System.out.println("jsonString: " + jsonString);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString); // 이걸 경로로 변경

            // 주소록 데이터를 저장할 ContactList 생성
            ContactList contactList = new ContactList();

            // "address" 배열에 대한 반복
            for (JsonNode addressNode : rootNode.path("address")) {
                String name = addressNode.path("name").asText();
                String phoneNum = addressNode.path("phone_num").asText();

                // Contact 객체 생성 및 ContactList에 추가
                Contact contact = new Contact(name, phoneNum);
                contactList.addContact(contact);

                System.out.println("Name: " + name);
                System.out.println("Phone Number: " + phoneNum);
                System.out.println("--------------");
            }
            // ContactList에서 데이터 가져와서 활용 가능
            List<Contact> savedContacts = contactList.getContacts();
            for (Contact savedContact : savedContacts) {
                // 저장된 데이터 활용 예시
                System.out.println("Saved Contact - Name: " + savedContact.getName() + ", Phone Number: " + savedContact.getPhoneNum());
            }
            return contactList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ContactList contactList = new ContactList();
            return contactList;
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        item = findViewById(R.id.editText);
        add = findViewById(R.id.button);
        listView = findViewById(R.id.list);

        itemlist = FileHelper.readData(this);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, itemlist);
        listView.setAdapter(arrayAdapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = item.getText().toString();
                itemlist.add(itemName);
                item.setText("");
                FileHelper.writeData(itemlist, getApplicationContext());
                arrayAdapter.notifyDataSetChanged();
            }
        });
        ContactList cl = extract();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Delete");
                alert.setMessage("Do you want to delete this item from list?");
                alert.setCancelable(false);
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        itemlist.remove(position);
                        arrayAdapter.notifyDataSetChanged();
                        FileHelper.writeData(itemlist, getApplicationContext());
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
            }
        });
    }
}
