package com.firstprojects.thetravelsbook_try1.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firstprojects.thetravelsbook_try1.R;
import com.firstprojects.thetravelsbook_try1.adapters.CustomAdapter;
import com.firstprojects.thetravelsbook_try1.models.AddressRegister;

import java.util.ArrayList;

public class MainMenu extends AppCompatActivity {
ListView listView;
CustomAdapter addressRegisterCustomAdapter;
ArrayList<AddressRegister> addressRegisterArrayList;
SQLiteDatabase sqLiteDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        sqLiteDatabase = this.openOrCreateDatabase("Address",MODE_PRIVATE,null);
        addressRegisterArrayList = new ArrayList<>();
        listView = findViewById(R.id.listView);

try {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS address(id INTEGER PRIMARY KEY , name VANCHAR, latitude REAL, longitude REAL)");
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM address",null);

        int nameIX = cursor.getColumnIndex("name");
        int latitudeIX = cursor.getColumnIndex("latitude");
        int longitudeIX = cursor.getColumnIndex("longitude");
        while (cursor.moveToNext()) {
         String name = cursor.getString(nameIX);
         Double latitude = cursor.getDouble(latitudeIX);
         Double longitude = cursor.getDouble(longitudeIX);
         AddressRegister addressRegister = new AddressRegister(latitude,longitude,name);
         addressRegisterArrayList.add(addressRegister);

        }
        addressRegisterCustomAdapter.notifyDataSetChanged();
        cursor.close();
}
catch (Exception e) {
    e.printStackTrace();
}
        addressRegisterCustomAdapter = new CustomAdapter(this,addressRegisterArrayList);
        listView.setAdapter(addressRegisterCustomAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainMenu.this, MapsActivity.class);
                intent.putExtra("addressSerial",addressRegisterArrayList.get(position));
                intent.putExtra("whichPage","old");
                startActivity(intent);
                finish();

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.simple_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       if(item.getItemId() == R.id.menu_item_id1) {
           Intent intent = new Intent(MainMenu.this,MapsActivity.class);
           intent.putExtra("whichPage","new");
           startActivity(intent);
           finish();
       }
        return super.onOptionsItemSelected(item);
    }
}