package com.android.calcpointgpn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    int peopleNum = 1;          //переменная выбора человека
    boolean checkPM = true;     //переменная выбора начисления или списания

    double Vlad;
    double Dima;
    double Roma;
    double result;

    double oldVlad;
    double oldDima;
    double oldRoma;

    //переменные для буффера
    private ClipboardManager clipboardManager;
    String txtCopy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPlusMinus();
        checkPeople();
        loadText();             //метод загрузки данных
        setTextViewPoints();
        this.clipboardManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    //Метод для выбора операции начисление/списание
    void checkPlusMinus(){
        RadioGroup radioGroupPlusMinus = (RadioGroup) findViewById(R.id.radioGroupPlusMinus);
        radioGroupPlusMinus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case -1:
                        //ничего не делать
                        break;
                    case R.id.accrue:
                        checkPM = true;
                        break;
                    case R.id.writeOff:
                        checkPM = false;
                        break;
                }
            }
        });
    }

    //Метод для выбора человека
    void checkPeople(){
        RadioGroup radioGroupSelectPeople = (RadioGroup) findViewById(R.id.radioGroupSelectPeople);
        radioGroupSelectPeople.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case -1:
                        //ничего не делать
                        break;
                    case R.id.putVlad:
                        peopleNum = 1;
                        break;
                    case R.id.putDima:
                        peopleNum = 2;
                        break;
                    case R.id.putRoma:
                        peopleNum = 3;
                        break;
                    default:
                        peopleNum = 1;
                }
            }
        });
    }

    //Рассчет баллов
    void calc(){

        try {
            EditText inputPoint = (EditText)findViewById(R.id.inputPoint);
            double points = Double.parseDouble("" + inputPoint.getText());

            if (checkPM) {
                if (peopleNum == 1) {
                    oldVlad = Vlad;
                    Vlad = Vlad + points;
                    txtCopy = "Владек:" + String.format("%.2f", oldVlad) + " + " + points + " = " + String.format("%.2f", Vlad);
                }
                if (peopleNum == 2) {
                    oldDima = Dima;
                    Dima = Dima + points;
                    txtCopy = "Диман:" + String.format("%.2f", oldDima) + " + " + points + " = " + String.format("%.2f", Dima);
                }
                if (peopleNum == 3) {
                    oldRoma = Roma;
                    Roma = Roma + points;
                    txtCopy = "Ромик:" + String.format("%.2f", oldRoma) + " + "  + points + " = " + String.format("%.2f", Roma);
                }
            } else {
                if (peopleNum == 1) {
                    oldVlad = Vlad;
                    Vlad = Vlad - points;
                    txtCopy = "Владек:" + String.format("%.2f", oldVlad) + " - " + points + " = " + String.format("%.2f", Vlad);
                }
                if (peopleNum == 2) {
                    oldDima = Dima;
                    Dima = Dima - points;
                    txtCopy = "Диман:" + String.format("%.2f", oldDima) + " - " + points + " = " + String.format("%.2f", Dima);
                }
                if (peopleNum == 3) {
                    oldRoma = Roma;
                    Roma = Roma - points;
                    txtCopy = "Ромик:" + String.format("%.2f", oldRoma) + " - "  + points + " = " + String.format("%.2f", Roma);
                }
            }
            result = Vlad + Roma + Dima;
        } catch (NumberFormatException e){
            //ничего не делать
        }
    }

    //Метод загрузки данных
    void loadText(){
        SharedPreferences sharedPreferences = getSharedPreferences(
                "savePoint", Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Vlad =  Double.parseDouble(sharedPreferences.getString("Vlad", "0.00"));
        Dima =  Double.parseDouble(sharedPreferences.getString("Dima", "0.00"));
        Roma =  Double.parseDouble(sharedPreferences.getString("Roma", "0.00"));
        result =  Double.parseDouble(sharedPreferences.getString("result", "0.00"));

        //пока что ничего
    }

    //Метод сохранения данных
    void saveText(){
        SharedPreferences sharedPreferences = getSharedPreferences(
                "savePoint", Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("Vlad", "" + Vlad);
        editor.putString("Dima", "" + Dima);
        editor.putString("Roma", "" + Roma);
        editor.putString("result", "" + result);
        editor.apply();
    }

    //Отображение количества баллов
    void setTextViewPoints(){
        TextView pointVlad = findViewById(R.id.pointVlad);
        pointVlad.setText(String.format("%.2f", Vlad));

        TextView pointDima = findViewById(R.id.pointDima);
        pointDima.setText(String.format("%.2f", Dima));

        TextView pointRoma = findViewById(R.id.pointRoma);
        pointRoma.setText(String.format("%.2f", Roma));

        TextView pointAll = findViewById(R.id.pointAll);
        pointAll.setText(String.format("%.2f", result));
    }

    //Копирование в буффер обмена
    private void doCopy(){
        ClipData buffer = ClipData.newPlainText("text", txtCopy);
        // Copy ClipData to Clipboard.
        this.clipboardManager.setPrimaryClip(buffer);
        Toast.makeText(getApplicationContext(),"Скопировано в буффер обмена", Toast.LENGTH_SHORT).show();
    }

    //Действия при нажатии кнопки "Выполнить"
    public void onClickResult(View view) {
        calc();
        setTextViewPoints();
        doCopy();
    }

    //Вызов метода сохранения данных при закрытии/сворачивании приложения
    protected void onPause(){
        super.onPause();
        saveText();
    }
}
