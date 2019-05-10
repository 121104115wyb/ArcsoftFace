package com.ytlz.arcsoftface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


import com.ytlz.mylibrary.CommonData;
import com.ytlz.mylibrary.FaceData;
import com.ytlz.mylibrary.LitepalUtils;
import com.ytlz.mylibrary.Person;
import com.ytlz.mylibrary.conData;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void save(View view) {

//        Person person = new Person();
//        person.setAddress("12213");
//        person.setName("wyb");

//        CommonData commonData = new CommonData();
//        commonData.setCountry("china");
//        commonData.setLength("100");
//        commonData.setWith("200");
//        conData data = new conData();
//        data.setAddress("test1111");
//        data.setName("1111111111");
//        FaceData faceData = new FaceData();
//        faceData.setColor("00000");
//        faceData.setType("white");
        LitepalUtils.SavePerson();

    }

    public void select(View view) {

        List<String> strings = LitepalUtils.selectPerson();

        System.out.println("--------->" + strings);
        ;
    }
}
