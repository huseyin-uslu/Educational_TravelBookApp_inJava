package com.firstprojects.thetravelsbook_try1.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firstprojects.thetravelsbook_try1.R;

public class RegisterScreen extends AppCompatActivity {
 TextView locationText;
 EditText addressName;
    Intent receive;
    double latLngReceive1 , latLngReceive2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);
        locationText = findViewById(R.id.locationText);
        addressName = findViewById(R.id.addressName);
        receive = getIntent();
        latLngReceive1 = receive.getDoubleExtra("latitude",0);
        latLngReceive2 = receive.getDoubleExtra("longitude",0);
        locationText.setText("Latitude//" + String.valueOf(latLngReceive1) + "                                            Longitude//" + String.valueOf(latLngReceive2) );
    }
    public void saveButton(View view) {
   if(addressName.length() > 0 && !addressName.getText().toString().matches("")) {
       AlertDialog.Builder alert = new AlertDialog.Builder(RegisterScreen.this);
       alert.setTitle("Are You Sure?");
       alert.setMessage(addressName.getText().toString() + " ' Did you want to save this place?");
       alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
             try{
                 SQLiteDatabase sqLiteDatabase = RegisterScreen.this.openOrCreateDatabase("Address",MODE_PRIVATE,null);
                 sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS address(id INTEGER PRIMARY KEY, name VANCHAR, latitude REAL, longitude REAL)");
                 String sqlString = "INSERT INTO address(name,latitude,longitude) VALUES (?,?,?)";
                 SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement(sqlString);
                 sqLiteStatement.bindString(1,addressName.getText().toString());
                 sqLiteStatement.bindDouble(2,latLngReceive1);
                 sqLiteStatement.bindDouble(3,latLngReceive2);
                 sqLiteStatement.execute();
                 Intent intent = new Intent(RegisterScreen.this, MainMenu.class);
                 startActivity(intent);
                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


             }catch (Exception e) {
                 e.printStackTrace();
             }

           }
       });
       alert.setNegativeButton("no", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               finish();

           }
       });
       alert.setCancelable(false);
       alert.show();


   }else {
       Toast.makeText(RegisterScreen.this, "You have to write something in the blank", Toast.LENGTH_LONG).show();
   }

    }
}