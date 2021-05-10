package com.example.fyp_application;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.example.fyp_application.DeliveryActivity.SELECT_ADDRESS;


public class MyAddressActivity extends AppCompatActivity {

    private int previousAddress;
    private LinearLayout addNewAddressesBtn;
    private TextView addressesSaved;
    private RecyclerView myAddressRecyclerView;
    private static AddressesAdaptor addressesAdaptor;
    private Button deliverHereBtn;

    private Dialog loadingDialog;
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("My Address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //loading diaglog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialogue);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.input_field));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                addressesSaved.setText(String.valueOf(DBqueries.addressesModelList.size())+ " saved addresses");
            }
        });
        //loading dialog

        previousAddress = DBqueries.selectedAddress;

        deliverHereBtn = findViewById(R.id.deliver_here_btn);
        myAddressRecyclerView = findViewById(R.id.addresses_rv);
        addNewAddressesBtn = findViewById(R.id.add_new_address_btn);
        addressesSaved = findViewById(R.id.address_saved);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        myAddressRecyclerView.setLayoutManager(layoutManager);


        mode = getIntent().getIntExtra("MODE",-1);
        if (mode == SELECT_ADDRESS){
            deliverHereBtn.setVisibility(View.VISIBLE);
        }
        else {
            deliverHereBtn.setVisibility(View.GONE);
        }
        deliverHereBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DBqueries.selectedAddress != previousAddress){
                    final int previousAddressIndex = previousAddress;
                    loadingDialog.show();
                    Map<String, Object> updateSelection = new HashMap<>();
                    updateSelection.put("selected_"+String.valueOf(previousAddress+1), false);
                    updateSelection.put("selected_"+String.valueOf(DBqueries.selectedAddress+1),true);

                    previousAddress = DBqueries.selectedAddress;

                    FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                            .document("MY_ADDRESSES").update(updateSelection).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                finish();
                            } else {
                                previousAddress = previousAddressIndex;
                                String error = task.getException().getMessage();
                                Toast.makeText(MyAddressActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });
                }else {
                    finish();
                }
            }
        });

        addressesAdaptor = new AddressesAdaptor(DBqueries.addressesModelList, mode, loadingDialog);
        myAddressRecyclerView.setAdapter(addressesAdaptor);
        ((SimpleItemAnimator)myAddressRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        addressesAdaptor.notifyDataSetChanged(); // refreshes whole list

        addNewAddressesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addAddressesIntent = new Intent(MyAddressActivity.this, AddAdressActivity.class);
                if (mode != SELECT_ADDRESS){
                    addAddressesIntent.putExtra("INTENT", "manage");
                }else {
                    addAddressesIntent.putExtra("INTENT", "null");
                }
                startActivity(addAddressesIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        addressesSaved.setText(String.valueOf(DBqueries.addressesModelList.size())+ " saved addresses");
    }

    public static void refreshItem(int deselect, int select){
        addressesAdaptor.notifyItemChanged(deselect); //refreshes the "passed" postion of the layout
        addressesAdaptor.notifyItemChanged(select);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            if (mode == SELECT_ADDRESS) {
                if (DBqueries.selectedAddress != previousAddress) {
                    DBqueries.addressesModelList.get(DBqueries.selectedAddress).setSelected(false);
                    DBqueries.addressesModelList.get(previousAddress).setSelected(true);
                    DBqueries.selectedAddress = previousAddress;
                }
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mode == SELECT_ADDRESS) {
            if (DBqueries.selectedAddress != previousAddress) {
                DBqueries.addressesModelList.get(DBqueries.selectedAddress).setSelected(false);
                DBqueries.addressesModelList.get(previousAddress).setSelected(true);
                DBqueries.selectedAddress = previousAddress;
            }
        }
            super.onBackPressed();
        }


}


