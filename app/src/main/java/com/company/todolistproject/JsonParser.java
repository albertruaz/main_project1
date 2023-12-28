package com.company.todolistproject;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class JsonParser {

    public ContactList extract() {
        try {
            System.out.println("Get Started");
            String jsonString = "{ \"address\": [ { \"name\": \"홍길동\", \"phone_num\": \"010-1234-5678\" }, { \"name\": \"조서윤\", \"phone_num\": \"010-0000-0000\" }, { \"name\": \"조윤서\", \"phone_num\": \"010-0000-0000\" } ] }";

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

}
