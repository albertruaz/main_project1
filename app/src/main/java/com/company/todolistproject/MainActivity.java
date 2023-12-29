package com.company.todolistproject;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.widget.SimpleAdapter;

import java.util.Map;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    EditText item;
    Button add;
    ListView listView;
    ListView listContact;
    ArrayList<String> itemlist = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    public List<Map<String, String>> extract() {
        try {
            //jsonString 추출
            InputStream inputStream = getAssets().open("contact_data.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder StringBuilder = new StringBuilder();
            for (int data = reader.read(); data != -1; data = reader.read()) {
                StringBuilder.append((char)data);
            }
            String jsonString = StringBuilder.toString();

            //jsonString 파싱
            List<Map<String, String>> contactList = new ArrayList<Map<String, String>>();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);
            for (JsonNode addressNode : rootNode.path("contact")) {
                String name = addressNode.path("name").asText();
                String phoneNum = addressNode.path("phone_num").asText();

                Map<String, String> contact = new HashMap<String, String>(2);
                contact.put("name", name);
                contact.put("phoneNum", phoneNum);
                contactList.add(contact);
            }
            System.out.println(contactList.size());
            return contactList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        return data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        item = findViewById(R.id.editText);
        add = findViewById(R.id.button);
        listView = findViewById(R.id.list);
        listContact = findViewById(R.id.listContact);

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

        // contactlist 출력
        List<Map<String, String>> contactList = extract();

        // contact adapter 설정
        SimpleAdapter adapter = new SimpleAdapter(this, contactList,
                android.R.layout.simple_list_item_2,
                new String[] {"name", "phoneNum"},
                new int[] {android.R.id.text1, android.R.id.text2});
        listContact.setAdapter(adapter);

    }
}
