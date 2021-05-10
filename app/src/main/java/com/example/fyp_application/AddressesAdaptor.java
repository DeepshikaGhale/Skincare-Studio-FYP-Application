package com.example.fyp_application;

import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.fyp_application.DeliveryActivity.SELECT_ADDRESS;
import static com.example.fyp_application.MyAccountFragment.MANAGE_ADDRESS;
import static com.example.fyp_application.MyAddressActivity.refreshItem;


public class AddressesAdaptor extends RecyclerView.Adapter<AddressesAdaptor.ViewHolder> {

    private List<AddressesModel> addressesModelList;
    private int MODE;
    private int preSelectedPosition;
    private boolean refresh = false;
    private Dialog loadingDialog;

    public AddressesAdaptor(List<AddressesModel> addressesModelList, int MODE, Dialog loadingDialog) {
        this.addressesModelList = addressesModelList;
        this.MODE = MODE;
        preSelectedPosition = DBqueries.selectedAddress;
        this.loadingDialog = loadingDialog;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.addresses_item_layout, parent, false);
         return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String city = addressesModelList.get(position).getCity();
        String locality = addressesModelList.get(position).getLocality();
        String flatNo = addressesModelList.get(position).getFaltNo();
        String pincode = addressesModelList.get(position).getPincode();
        String landmark = addressesModelList.get(position).getLandmark();
        String name = addressesModelList.get(position).getName();
        String mobileNo = addressesModelList.get(position).getMobileNo();
        String alternateMobileNo = addressesModelList.get(position).getAlternativeMobileNo();
        String districtSpinner = addressesModelList.get(position).getState();
        boolean selected = addressesModelList.get(position).getSelected();

        holder.setData(name, city, pincode, selected, position, mobileNo, alternateMobileNo, flatNo, locality, districtSpinner, landmark);
    }

    @Override
    public int getItemCount() {
        return addressesModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView fullName;
        private TextView address;
        private TextView pincode;
        private ImageView icon;
        private LinearLayout option_container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            pincode = itemView.findViewById(R.id.pincode);
            icon = itemView.findViewById(R.id.icon_view);
            option_container = itemView.findViewById(R.id.option_container);
        }

        private void setData(String username, String city, String userPincode, final Boolean selected, final int position, String mobileNo, String alternateMobileNo, String flatNo, String locality, String district, String landmark){
            mobileNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getMobileNo();
            if (alternateMobileNo.equals("")) {
                fullName.setText(username + " - " + mobileNo);
            }
            else {
                fullName.setText(username + " - " + mobileNo + " OR " + alternateMobileNo);
            }

            if (landmark.equals("")){
                address.setText(flatNo + " - " + locality + " - " + city + " - " + district);
            }else{
                address.setText(flatNo + " - " + locality + " - " + landmark + " - " + city + " - " + district);
            }
            pincode.setText(userPincode);


            if (MODE == SELECT_ADDRESS){
                icon.setImageResource(R.mipmap.done);
                if (selected){
                    icon.setVisibility(View.VISIBLE);
                    preSelectedPosition = position;
                }
                else {
                    icon.setVisibility(View.GONE);
                }
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (preSelectedPosition != position) {
                            addressesModelList.get(position).setSelected(true);
                            addressesModelList.get(preSelectedPosition).setSelected(false);
                            refreshItem(preSelectedPosition, position);
                            preSelectedPosition = position;
                            DBqueries.selectedAddress = position;
                        }
                    }
                });
            }else if (MODE == MANAGE_ADDRESS) {
                option_container.setVisibility(View.GONE);
                option_container.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //edit address
                        Intent addAddressesIntent = new Intent(itemView.getContext(), AddAdressActivity.class);
                        addAddressesIntent.putExtra("INTENT", "update_address");
                        addAddressesIntent.putExtra("index", position);
                        itemView.getContext().startActivity(addAddressesIntent);
                        refresh = false;
                    }
                });
                option_container.getChildAt(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //remove address
                        loadingDialog.show();
                        Map<String, Object> addresses = new HashMap<>();
                        int x = 0;
                        int selected = -1;
                        for (int i = 0; i < addressesModelList.size() +1; i++){
                            if (i != position){
                                x++;
                                addresses.put("city_"+x, addressesModelList.get(i).getCity());
                                addresses.put("locality_"+x, addressesModelList.get(i).getLocality());
                                addresses.put("flat_no_"+x, addressesModelList.get(i).getFaltNo());
                                addresses.put("pincode_"+x, addressesModelList.get(i).getPincode());
                                addresses.put("landmark_"+x, addressesModelList.get(i).getLandmark());
                                addresses.put("fullname_"+x, addressesModelList.get(i).getName());
                                addresses.put("mobile_no_"+x, addressesModelList.get(i).getMobileNo());
                                addresses.put("alternate_mobile_no_"+x, addressesModelList.get(i).getAlternativeMobileNo());
                                addresses.put("district_"+x, addressesModelList.get(i).getState());
                                if (addressesModelList.get(position).getSelected()){
                                    if (position - 1 >= 0){
                                        if (x == position){
                                            addresses.put("selected_"+ x, true);
                                            selected = x;
                                        }else {
                                            addresses.put("selected_"+x, addressesModelList.get(i).getSelected());
                                        }
                                    }else {
                                        if (x == 1){
                                            addresses.put("selected_" +x, true);
                                            selected = x;
                                        }else {
                                            addresses.put("selected_"+x, addressesModelList.get(i).getSelected());
                                        }
                                    }
                                }else {
                                    addresses.put("selected_"+x, addressesModelList.get(i).getSelected());
                                    if (addressesModelList.get(i).getSelected()){
                                        selected = x;
                                    }
                                }
                            }
                        }
                        addresses.put("list_size", x);

                        final int finalSelected = selected;
                        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                                .document("MY_ADDRESSES").set(addresses).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    DBqueries.addressesModelList.remove(position);
                                    if (finalSelected != -1) {
                                        DBqueries.selectedAddress = finalSelected - 1;
                                        DBqueries.addressesModelList.get(finalSelected - 1).setSelected(true);
                                    }else if (DBqueries.addressesModelList.size() == 0){
                                        DBqueries.selectedAddress = -1;
                                    }
                                    notifyDataSetChanged();
                                }else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(itemView.getContext(), "Error", Toast.LENGTH_SHORT).show();
                                }
                                loadingDialog.dismiss();
                            }
                        });

                        refresh = false;

                    }
                });

                icon.setImageResource(R.mipmap.vertical_dots);
                icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        option_container.setVisibility(View.VISIBLE);
                        if (refresh){
                            refreshItem(preSelectedPosition, preSelectedPosition);
                        }
                        else {
                            refresh = true;
                        }
                        preSelectedPosition = position;
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refreshItem(preSelectedPosition, preSelectedPosition);
                        preSelectedPosition = -1;
                    }
                });
            }
        }
    }
}
