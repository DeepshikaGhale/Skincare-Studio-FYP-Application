package com.example.fyp_application;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddAdressActivity extends AppCompatActivity {

    private EditText city;
    private EditText locality;
    private EditText faltNo;
    private EditText pincode;
    private EditText landmark;
    private EditText name;
    private EditText mobileNo;
    private EditText alternativeMobileNo;
    private Spinner stateSpinner;
    private Button saveBtn;

    private Dialog loadingDialog;

    private String[] districtList;
    private String selectedDistrict;

    private boolean updateAddress =false;
    private AddressesModel addressesModel;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_adress);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Add a new address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //loading diaglog
        loadingDialog = new Dialog(AddAdressActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialogue);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.input_field));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //loading dialog

        districtList = getResources().getStringArray(R.array.nepal_districts);

        city = findViewById(R.id.city);
        locality = findViewById(R.id.locality);
        faltNo = findViewById(R.id.flat_numberORBuilding_no);
        pincode = findViewById(R.id.pincode);
        landmark = findViewById(R.id.landmark);
        name = findViewById(R.id.name);
        mobileNo = findViewById(R.id.mobile_number);
        alternativeMobileNo = findViewById(R.id.alternate_mobile_number);
        stateSpinner= findViewById(R.id.state_spinner);
        saveBtn = findViewById(R.id.save_btn);

        ArrayAdapter spinnerAdaptor = new ArrayAdapter(this, android.R.layout.simple_spinner_item, districtList);
        spinnerAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        stateSpinner.setAdapter(spinnerAdaptor);

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDistrict = districtList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (getIntent().getStringExtra("INTENT").equals("update_address")){
            updateAddress = true;
            position = getIntent().getIntExtra("index", -1);
            addressesModel = DBqueries.addressesModelList.get(position);

            city.setText(addressesModel.getCity());
            locality.setText(addressesModel.getLocality());
            faltNo.setText(addressesModel.getFaltNo());
            landmark.setText(addressesModel.getLandmark());
            name.setText(addressesModel.getName());
            mobileNo.setText(addressesModel.getMobileNo());
            alternativeMobileNo.setText(addressesModel.getAlternativeMobileNo());
            pincode.setText(addressesModel.getPincode());

            for (int i = 0; i < districtList.length; i++){
                if (districtList[i].equals(addressesModel.getState())) {
                    stateSpinner.setSelection(i);
                }
            }
            saveBtn.setText("Update");
        }else {
            position = DBqueries.addressesModelList.size();
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(city.getText())){
                    if (!TextUtils.isEmpty(locality.getText())){
                        if (!TextUtils.isEmpty(faltNo.getText())){
                            if (!TextUtils.isEmpty(pincode.getText()) && pincode.getText().length() == 6){
                                if (!TextUtils.isEmpty(name.getText())){
                                    if (!TextUtils.isEmpty(mobileNo.getText()) && mobileNo.getText().length() == 10){

                                        loadingDialog.show();
                                        Map<String, Object> addAddress = new HashMap();

                                        addAddress.put("city_"+ String.valueOf(position + 1), city.getText().toString());
                                        addAddress.put("locality_"+ String.valueOf(position + 1), locality.getText().toString());
                                        addAddress.put("flat_no_"+ String.valueOf(position + 1), faltNo.getText().toString());
                                        addAddress.put("pincode_"+ String.valueOf(position + 1), pincode.getText().toString().trim());
                                        addAddress.put("landmark_"+ String.valueOf(position + 1), landmark.getText().toString());
                                        addAddress.put("fullname_" + String.valueOf(position + 1), name.getText().toString());
                                        addAddress.put("mobile_no_" + String.valueOf(position + 1), mobileNo.getText().toString());
                                        addAddress.put("alternate_mobile_no_"+ String.valueOf(position + 1), alternativeMobileNo.getText().toString());
                                        addAddress.put("district_"+ String.valueOf(position + 1), selectedDistrict);

                                        if (!updateAddress) {
                                            addAddress.put("list_size", (long) DBqueries.addressesModelList.size() + 1);
                                            if (getIntent().getStringExtra("INTENT").equals("manage")){
                                                if (DBqueries.addressesModelList.size() == 0){
                                                    addAddress.put("selected_"+ String.valueOf(position + 1), true);
                                                }else {
                                                    addAddress.put("selected_"+ String.valueOf(position + 1), false);
                                                }
                                            }else {
                                                addAddress.put("selected_"+ String.valueOf(position + 1), true);
                                            }

                                            if (DBqueries.addressesModelList.size() > 0) {
                                                addAddress.put("selected_" + (DBqueries.selectedAddress + 1), false);
                                            }
                                        }
                                        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                                                .collection("USER_DATA").document("MY_ADDRESSES").update(addAddress)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            if (!updateAddress) {
                                                                if (DBqueries.addressesModelList.size() > 0) {
                                                                    DBqueries.addressesModelList.get(DBqueries.selectedAddress).setSelected(false);
                                                                }
                                                                DBqueries.addressesModelList.add(new AddressesModel(true, city.getText().toString(), locality.getText().toString(), faltNo.getText().toString(), pincode.getText().toString(), landmark.getText().toString(), name.getText().toString(), mobileNo.getText().toString(), alternativeMobileNo.getText().toString(), selectedDistrict));

                                                                if (getIntent().getStringExtra("INTENT").equals("manage")){
                                                                    if (DBqueries.addressesModelList.size() == 0){
                                                                        DBqueries.selectedAddress = DBqueries.addressesModelList.size() - 1;
                                                                    }
                                                                }else {
                                                                    DBqueries.selectedAddress = DBqueries.addressesModelList.size() - 1;
                                                                }

                                                            }else {
                                                                DBqueries.addressesModelList.set(position,new AddressesModel(true, city.getText().toString(), locality.getText().toString(), faltNo.getText().toString(), pincode.getText().toString(), landmark.getText().toString(), name.getText().toString(), mobileNo.getText().toString(), alternativeMobileNo.getText().toString(), selectedDistrict));
                                                            }

                                                            if (getIntent().getStringExtra("INTENT").equals("deliveryIntent")) {
                                                                Intent intent = new Intent(AddAdressActivity.this, DeliveryActivity.class);
                                                                startActivity(intent);
                                                            }else {
                                                                MyAddressActivity.refreshItem(DBqueries.selectedAddress, DBqueries.addressesModelList.size() - 1);
                                                            }
                                                            finish();
                                                        }else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(AddAdressActivity.this, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                        loadingDialog.dismiss();
                                                    }
                                                });
                                    } else {
                                        mobileNo.requestFocus();
                                        Toast.makeText(AddAdressActivity.this, "Please provide valid mobile number.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    name.requestFocus();
                                }
                            } else {
                                pincode.requestFocus();
                                Toast.makeText(AddAdressActivity.this, "Please provide valid pincode.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            faltNo.requestFocus();
                        }
                    } else {
                        locality.requestFocus();
                    }
                } else{
                    city.requestFocus();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
