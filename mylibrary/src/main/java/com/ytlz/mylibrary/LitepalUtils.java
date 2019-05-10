package com.ytlz.mylibrary;

import android.content.Context;

import org.litepal.LitePal;
import org.litepal.Operator;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by wyb on 2019-04-26.
 */

public class LitepalUtils {

    public static <T> T selectData(Class<T> modelClass, long id) {
        if (modelClass == null) {
            return null;
        }
        return LitePal.find(modelClass, id);
    }

    public static <T> List<T> selectAllData(Class<T> modelClass) {
        if (modelClass == null) {
            return null;
        }
        return LitePal.findAll(modelClass);
    }


    static <T> int deleteData(Class<T> modelClass, long id) {
        if (modelClass == null || selectData(modelClass, id) == null) {
            return -1;
        }
        return LitePal.delete(modelClass, id);

    }

    public static void init(Context context) {

        LitePal.initialize(context);
    }



    //    static <T> int deleteData(Class<T> modelClass, long id) {
//        if (modelClass == null || selectData(modelClass, id) == null) {
//            return -1;
//        }
//        return LitePal.delete(modelClass,id);
//
//    }
//
//
//
//    public static void saveData(Class<T> modelClass) {
//        if (modelClass == null) {
//            return;
//        }
//        LitePal.saveAll((Collection<LitePalSupport>) modelClass);
//        //palSupport.save();
//    }

    public static <T extends LitePalSupport> void saveAll(Collection<T> collection) {

        LitePal.saveAll(collection);

//        LitePal.saveAllAsync()

    }


//    public static void SavePerson(String test) {
//        Person person = new Person();
//        person.setName(test);
//        if (test != null) {
//            person.save();
//        }
//    }

    public static void SavePerson() {
        LitePal.deleteAll(Person.class);
        CommonData commonData = new CommonData();
        commonData.setCountry("china");
        commonData.setLength("100");
        commonData.setWith("200");

        List<CommonData> commonData1 = new ArrayList<>();
        commonData1.add(commonData);

        conData data = new conData();
        data.setAddress("test1111");
        data.setName("1111111111");
        List<conData> conData1 = new ArrayList<>();
        conData1.add(data);


        FaceData faceData = new FaceData();
        faceData.setColor("00000");
        faceData.setType("white");
        List<FaceData> faceDataList = new ArrayList<>();
        faceDataList.add(faceData);

        Person person = new Person();
        person.setCommonData(commonData1);
        person.setConData(conData1);
        person.setFaceData(faceDataList);
        if (data != null) {
            person.save();
        }
    }

    public static List<String> selectPerson() {

        List<Person> list = LitePal.findAll(Person.class);
        List<String> list1 = null;
        if (null != list) {
            list1 = new ArrayList<>();
            for (Person name : list) {
                //list1.add(name.getCommonData().getCountry()+name.getConData().getName()+name.getFaceData().getType());
            }
        }

        return list1;
    }
//
//    public static void savePerson(String name) {
//        if (person != null) {
//            person.save();
//        }
//    }
}
