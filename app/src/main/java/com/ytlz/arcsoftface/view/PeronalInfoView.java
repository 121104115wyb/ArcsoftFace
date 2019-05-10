package com.ytlz.arcsoftface.view;


import com.ytlz.arcsoftface.model.PersonalInfoData;

import java.util.List;

/**
 * Created by INTG-02 on 2018/3/21.
 */

public interface PeronalInfoView {
    void addInfoSuccess(PersonalInfoData personalInfoData);
    void addInfoFailed(String message);

//    void getUserInfoSuccess(GuestDO personalInfoData);
    void getUserInfoFailed(String message);

    void getUserNameInfoSuccess(List<PersonalInfoData> personalInfoData);
    void getUserNameInfoFailed(String message);

    void getUserphoneNumInfoSuccess(List<PersonalInfoData> personalInfoData);
    void getUserphoneNumInfoFailed(String message);

    void getUseraddressInfoSuccess(List<PersonalInfoData> personalInfoData);
    void getUseraddressInfoFailed(String message);

    void getUserportionInfoSuccess(List<PersonalInfoData> personalInfoData, int difference);
    void getUserportionInfoFailed(String message);

    void deleteInfoSuccess();
    void deleteInfoFailed(String message);

    void getUserInfoListSuccess(List<PersonalInfoData> personalInfoData);

//    void upLoadUserHeadFeture(UserProfileResnponse featureUrl);
}
