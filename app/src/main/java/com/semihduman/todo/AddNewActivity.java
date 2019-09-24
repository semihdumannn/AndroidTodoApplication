package com.semihduman.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;



import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

import database.FirabaseDb;
import helpers.adapters.Helper;
import objects.Todo;

public class AddNewActivity extends AppCompatActivity {
    EditText titleText,descText,dateText;
    TextView addNewTodo,subAddNew;
    Button cancelBtn;
    String uuid ;
    FirabaseDb firabaseDb;
    String action = "",key = "", userId ;
    DatePickerDialog datePicker;//Datepicker objemiz
    TimePickerDialog timePicker; //Time Picker referansımızı oluşturduk
    String dateResult;
    Boolean finished;


    //Integer key = new Random().nextInt();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        //initialize
        titleText = findViewById(R.id.titleAddNewEditText);
        descText = findViewById(R.id.descAddNewEditText);
        dateText = findViewById(R.id.dateAddNewEditText);
        addNewTodo = findViewById(R.id.addNewTodoTextView);
        subAddNew = findViewById(R.id.subAddNewTextView);
        cancelBtn = findViewById(R.id.btnCancelNewActivity);

        uuid = UUID.randomUUID().toString();
        firabaseDb =  new FirabaseDb("Todo");



        Intent intent = getIntent();
        action = intent.getStringExtra("action");
        key = intent.getStringExtra("key");
        finished = intent.getBooleanExtra("finished",false);
        userId = intent.getStringExtra("userId");

        if (action.equals("old")){
            initEditText();
            addNewTodo.setText("Todo Güncelle");
            subAddNew.setText("Güncelleme İşlemlerini Yapabilirsiniz");
            cancelBtn.setText("Sil");
            uuid = key;

        }
    }

    private void initEditText() {

        FirebaseDatabase.getInstance().getReference().child("Todo").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 HashMap<String, Object> dataMap = (HashMap<String, Object>) dataSnapshot.getValue();
                 titleText.setText(dataMap.get("title").toString());
                 descText.setText(dataMap.get("desc").toString());
                 dateText.setText(dataMap.get("date").toString());
                 uuid = dataMap.get("key").toString();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Helper.showToast(databaseError.getMessage(),AddNewActivity.this);
            }
        });
    }
    public void goBack(View view){
        goMainActivity(MainActivity.class);
    }
    public void save(View view){
        Todo object = new Todo(
                titleText.getText().toString(),
                descText.getText().toString(),
                dateText.getText().toString(),
                uuid,
                finished,
               userId) ;
        if (action.equals("new")){
           Helper.showToast(firabaseDb.insert(object,object.getKey()),AddNewActivity.this);
        }else{
            Helper.showToast(firabaseDb.update(object,object.getKey()),AddNewActivity.this);
        }

        goMainActivity(MainActivity.class);
    }

    public Boolean delete(){

        boolean result = false;
        try{
            FirebaseDatabase.getInstance().getReference().child("Todo").child(key).removeValue();
            Helper.showToast("Silme İşlemi Başarılı Şekilde Gerçekleşti :)",AddNewActivity.this);
            result = true;
        }catch (Exception e){
            Helper.showToast(e.getMessage(),AddNewActivity.this);
        }
        return result;



    }
    public void cancel(View view){
        if (action.equals("old")){
            new AlertDialog.Builder(this)
                    .setTitle("Uyarı !!")
                    .setMessage("Silmek istediğinize emin misiniz ?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                          delete();
                          goMainActivity(MainActivity.class);
                        }})
                    .setNegativeButton("Hayır", null).show();
        }else{
            goMainActivity(MainActivity.class);
        }
    }

    private void goMainActivity(Class<MainActivity> mainActivityClass) {
        Intent i =  new Intent(AddNewActivity.this,mainActivityClass);
        startActivity(i);
    }

    public void showDateTime (View view){
        int year,month,day;
        if (action.equals("old")){
            String[] dateTimeSplit = dateText.getText().toString().split("-");
            String[] date = dateTimeSplit[0].split("/");

            year = Integer.parseInt(date[2]);
            month = Integer.parseInt(date[1]) -1;
            day  = Integer.parseInt(date[0]);
        }else{
            Calendar mcurrentTime = Calendar.getInstance();
            year = mcurrentTime.get(Calendar.YEAR);//Güncel Yılı alıyoruz
            month = mcurrentTime.get(Calendar.MONTH);//Güncel Ayı alıyoruz
            day = mcurrentTime.get(Calendar.DAY_OF_MONTH);//Güncel Günü alıyoruz
        }


        datePicker = new DatePickerDialog(AddNewActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                dateResult = dayOfMonth + "/" + (monthOfYear+1)+ "/"+year;
                showTimer();
            }
        },year,month,day);//başlarken set edilcek değerlerimizi atıyoruz
        datePicker.setTitle("Tarih Seçiniz");
        datePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, "Ayarla", datePicker);
        datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", datePicker);
        datePicker.show();
    }

    private void showTimer() {

        int hour,minute;
        if (action.equals("old")){
            String[] dateTimeSplit = dateText.getText().toString().split("-");
            String[] time = dateTimeSplit[1].split(":");

            hour = Integer.parseInt(time[0]);
            minute = Integer.parseInt(time[1]);
        }else {
            Calendar mcurrentTime = Calendar.getInstance();//
            hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);//Güncel saati aldık
            minute = mcurrentTime.get(Calendar.MINUTE);//Güncel dakikayı aldık
        }

        //TimePicker objemizi oluşturuyor ve click listener ekliyoruz
        timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                dateResult+="-"+selectedHour + ":" + selectedMinute;//Ayarla butonu tıklandığında textview'a yazdırıyoruz
                dateText.setText(dateResult);
            }
        }, hour, minute, true);//true 24 saatli sistem için
        timePicker.setTitle("Saat Seçiniz");
        timePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, "Ayarla", timePicker);
        timePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", timePicker);

        timePicker.show();
    }
}
