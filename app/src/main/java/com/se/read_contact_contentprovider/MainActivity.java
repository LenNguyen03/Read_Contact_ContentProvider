package com.se.read_contact_contentprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //variable Recycleview
    RecyclerView recyclerView;

    ArrayList<ContactModel> arrayList = new ArrayList<ContactModel>();

    RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //link recycle view
        recyclerView = findViewById(R.id.recycle_view);


        //check permision
        checkPermission();




    }

    private void checkPermission() {
        //check condition
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
        != PackageManager.PERMISSION_GRANTED){
            //when permission not granted
            //request permision
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
        }else{
            //when permission is granted
            //create method
            getContactList();
        }
    }

    private void getContactList() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //Sort by Accessding
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
        //create Cursor
        Cursor cursor = getContentResolver().query(
                uri,null,null,null,sort
        );
        //check condition
        if(cursor.getCount() > 0){

            while(cursor.moveToNext()){
                //Cursor move to next
                //Get contact id
                String id = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts._ID
                ));
                //get contact name
                String name = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                ));

                //uri
                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                //selecttion
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                        +" =?";

                //phone cursor
                Cursor phoneCursor = getContentResolver().query(
                        uriPhone,null, selection,
                        new String[]{id}, null
                );
                //check condition
                if(phoneCursor.moveToNext()){
                    //when phone cursor move to next
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    ));
                    // contact model
                    ContactModel model = new ContactModel();
                    //setname
                    model.setName(name);
                    //setnumber
                    model.setNumber(number);

                    //add it to array list
                    arrayList.add(model);
                    //Close phone cursor
                    phoneCursor.close();
                }

            }
            cursor.close();


        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //adapter
        adapter = new RecyclerAdapter(this, arrayList);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //check condition
        if(requestCode == 100 && grantResults.length > 0 && grantResults[0]
        == PackageManager.PERMISSION_GRANTED){
            //when permission is granted
            //Call method
            getContactList();
        }else{
            //permission is deny

            Toast.makeText(MainActivity.this, "Permission is Denied !!"
                    ,Toast.LENGTH_SHORT).show();
            //check permission method again
            checkPermission();
        }
    }
}