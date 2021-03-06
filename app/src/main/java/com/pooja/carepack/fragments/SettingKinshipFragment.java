package com.pooja.carepack.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.pooja.carepack.R;
import com.pooja.carepack.activities.WebServicesConst;
import com.pooja.carepack.model.ModelKinship;
import com.pooja.carepack.model.ModelKinshipDetail;
import com.pooja.carepack.utils.MyPrefs;
import com.pooja.carepack.utils.Utility;
import com.pooja.carepack.volly.LibPostListner;
import com.pooja.carepack.volly.PostLibResponse;
import com.rey.material.widget.EditText;

import java.util.HashMap;

/**
 * Created by Yudiz on 23/12/15.
 */
public class SettingKinshipFragment extends BaseFragment implements LibPostListner {

    public int menu_item = 0;
    private View view;
    private Menu menu;
    private EditText etFirstname, etLastname, etEmail, etPhone;
    private ModelKinship modelKinship;
    private Animation shakeAnim;
    private String id = "0";
    private int kinshipId = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_setting_kinship1, container, false);
        intiUI();
        onGetDetails();
        setHasOptionsMenu(true);
        return view;

    }

    private void intiUI() {
        etFirstname = (EditText) view.findViewById(R.id.frg_kinship1_etFirstName);
        etLastname = (EditText) view.findViewById(R.id.frg_kinship1_etLastName);
        etEmail = (EditText) view.findViewById(R.id.frg_kinship1_etEmail);
        etPhone = (EditText) view.findViewById(R.id.frg_kinship1_etPhoneNumber);
        shakeAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        kinshipId = Integer.parseInt(getArguments().getString("kinship", "0"));
    }

    private void onGetDetails() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("userid", prefs.get(MyPrefs.keys.ID));
        new PostLibResponse(SettingKinshipFragment.this, new ModelKinship(), getActivity(), hashMap, WebServicesConst.KINSHIP + "/" + prefs.get(MyPrefs.keys.ID), WebServicesConst.RES.GET_KINSHIP, true, true);
    }

    private void setData(ModelKinshipDetail kinship) {
        id = kinship.id;
        etFirstname.setText(kinship.firstname);
        etLastname.setText(kinship.lastname);
        etEmail.setText(kinship.email);
        etPhone.setText(kinship.phone);
    }

    private void onSaveDetails() {
        if (valid()) {
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("device_token", prefs.get(MyPrefs.keys.GCMKEY));
            hashMap.put("action", "submit_kinship");
            hashMap.put("userid", prefs.get(MyPrefs.keys.ID));
            hashMap.put("id", id);
            hashMap.put("no", String.valueOf(kinshipId + 1));
            hashMap.put("firstname", etFirstname.getText().toString());
            hashMap.put("lastname", etLastname.getText().toString());
            hashMap.put("email", etEmail.getText().toString());
            hashMap.put("phone", etPhone.getText().toString());
            new PostLibResponse(SettingKinshipFragment.this, new ModelKinship(), getActivity(), hashMap, WebServicesConst.KINSHIP, WebServicesConst.RES.UPDATE_KINSHIP, true, true);
        }
    }

    private boolean valid() {
//        if (etFirstname.getText().length() == 0) {
//            etFirstname.startAnimation(shakeAnim);
//            etFirstname.requestFocus();
//            toast(R.string.toast_invalid_,etFirstname.getHint().toString());
//            return false;
//        } else if (etLastname.getText().length() == 0) {
//            etLastname.startAnimation(shakeAnim);
//            etLastname.requestFocus();
//            toast(R.string.toast_invalid_,etLastname.getHint().toString());
//            return false;
//        } else
        if (etEmail.getText().length() == 0 || !Utility.isEmailValid(etEmail.getText().toString())) {
            etEmail.startAnimation(shakeAnim);
            etEmail.requestFocus();
            toast(R.string.toast_invalid_email);
            return false;
        }
//        else if (etPhone.getText().length() < 10) {
//            etPhone.startAnimation(shakeAnim);
//            etPhone.requestFocus();
//            toast(R.string.toast_invalid_phone);
//            return false;
//        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (menu != null && menu.size() > menu_item)
            menu.getItem(menu_item).setVisible(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (menu != null && menu.size() > menu_item) {
            menu.getItem(menu_item).setVisible(true);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
        this.menu = menu;
        menu.getItem(menu_item).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.getItem(menu_item).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_save:
                onSaveDetails();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onPostResponseComplete(Object clsGson, int requestCode) {
        modelKinship = (ModelKinship) clsGson;
        if (requestCode == WebServicesConst.RES.UPDATE_KINSHIP) {
            if (modelKinship != null) {
                int Status = modelKinship.status;
                if (Status == 200) {
                    getActivity().onBackPressed();
                } else {
                    toast(modelKinship.message.toString());
                }
            }
        } else if (requestCode == WebServicesConst.RES.GET_KINSHIP) {
            if (modelKinship != null) {
                int Status = modelKinship.status;
                if (Status == 200) {
                    if (modelKinship.kinship != null && modelKinship.kinship.size() > kinshipId)
                        setData(modelKinship.kinship.get(kinshipId));
                } else {
                    if (kinshipId == 1) {
                        kinshipId = 0;
                        toast("Please Enter Kinship 1 First.");
                        mainActivity.setHeader("Kinship 1");
                    } else {
                        toast(modelKinship.message.toString());
                    }
                }
            }
        }
    }

    @Override
    public void onPostResponseError(String errorMessage, int requestCode) {

    }
}
