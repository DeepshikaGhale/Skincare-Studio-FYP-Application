package com.example.fyp_application;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateInfoFragment extends Fragment {

    public UpdateInfoFragment() {
        // Required empty public constructor
    }

    private CircleImageView circleImageView;
    private Button changePhotoBtn, removeBtn, updateBtn, doneBtn;
    private EditText nameField, emailField, password;
    private Dialog loadingDialog, passwordDialog;
    private String name, email, photo;
    private Uri imageUri;
    private boolean updatePhoto = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_update_info, container, false);

        //loading diaglog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialogue);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.input_field));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //loading dialog

        //password diaglog
        passwordDialog = new Dialog(getContext());
        passwordDialog.setContentView(R.layout.password_confirmation_layout);
        passwordDialog.setCancelable(true);
        passwordDialog.getWindow().getContext().getResources().getColor(R.color.white);
        passwordDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        password = passwordDialog.findViewById(R.id.password_reset);
        doneBtn = passwordDialog.findViewById(R.id.done_btn);

        //password dialog


        circleImageView = view.findViewById(R.id.profile_image_icon);
        changePhotoBtn = view.findViewById(R.id.change_photo_btn);
        removeBtn = view.findViewById(R.id.remoe_photo_btn);
        updateBtn = view.findViewById(R.id.update_btn);
        nameField = view.findViewById(R.id.name);
        emailField = view.findViewById(R.id.email);
//
        name = getArguments().getString("Name");
        email = getArguments().getString("Email");
        photo = getArguments().getString("Profile");

        Glide.with(getContext()).load(photo).into(circleImageView);
        nameField.setText(name);
        emailField.setText(email);

        changePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent, 1);
                    }else {
                        getActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                    }
                }else {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, 1);
                }
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUri = null;
                updatePhoto = true;
                Glide.with(getContext()).load(R.mipmap.profile).into(circleImageView);
            }
        });

        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        emailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailandPassword();
            }
        });

        return view;
    }

    private void checkInputs(){
        if (!TextUtils.isEmpty(emailField.getText())){
            if (!TextUtils.isEmpty(nameField.getText())){
                updateBtn.setEnabled(true);
            }else {
                updateBtn.setEnabled(false);
            }
        }else {
            updateBtn.setEnabled(false);
        }
    }

    private void checkEmailandPassword(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (emailField.getText().toString().trim().equals(email.toLowerCase().trim())){ // same email
            loadingDialog.show();
            updatePhoto(user);
        }else { //update email
            passwordDialog.show();
            doneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadingDialog.show();
                    String userPassword = password.getText().toString();
                    passwordDialog.dismiss();
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(email, userPassword);

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        user.updateEmail(emailField.getText().toString())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            updatePhoto(user);
                                                        }else {
                                                            loadingDialog.dismiss();
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                    }else {
                                        loadingDialog.dismiss();
                                        String error = task.getException().getMessage();
                                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });
        }
    }

    private void updatePhoto(final FirebaseUser user){
        ///updating photo

        if (updatePhoto){

            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile/*" +user.getUid() + ".jpg");
            if (imageUri != null){
                Glide.with(getContext()).asBitmap().load(imageUri).circleCrop().into(new ImageViewTarget<Bitmap>(circleImageView) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        resource.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = storageReference.putBytes(data);
                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()){

                                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()){
                                                imageUri = task.getResult();
                                                DBqueries.profile = task.getResult().toString();
                                                Glide.with(getContext()).load(DBqueries.profile).into(circleImageView);

                                                Map<String, Object> updateData =  new HashMap<>();
                                                updateData.put("email", emailField.getText().toString());
                                                updateData.put("fullname", nameField.getText().toString());
                                                updateData.put("profile",DBqueries.profile);

                                                updateFields(user, updateData);
                                            }else {
                                                loadingDialog.dismiss();
                                                DBqueries.profile = "";
                                                String error = task.getException().getMessage();
                                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }else {
                                    loadingDialog.dismiss();
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        return;
                    }

                    @Override
                    protected void setResource(@Nullable Bitmap resource) {
                        circleImageView.setImageResource(R.mipmap.profile);
                    }
                });
            }else {
                //remove photo
                storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            DBqueries.profile = "";
                            Map<String, Object> updateData =  new HashMap<>();
                            updateData.put("email", emailField.getText().toString());
                            updateData.put("fullname", nameField.getText().toString());
                            updateData.put("profile", "");

                            updateFields(user, updateData);
                        }else {
                            loadingDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }else {
            Map<String, Object> updateData =  new HashMap<>();
            updateData.put("fullname", nameField.getText().toString());
            updateFields(user, updateData);
        }

        ///updating photo
    }

    private void updateFields(FirebaseUser user, Map<String, Object> updateData){

        FirebaseFirestore.getInstance().collection("USERS").document(user.getUid()).update(updateData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            if (updateData.size() > 1){
                                DBqueries.email = emailField.getText().toString().trim();
                                DBqueries.fullName = nameField.getText().toString().trim();
                            }else {
                                DBqueries.fullName = nameField.getText().toString().trim();
                            }
                            Toast.makeText(getContext(), "Successful Updated!", Toast.LENGTH_SHORT).show();
                        }else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1){
            if (requestCode == getActivity().RESULT_OK){
                if (data != null){
                    imageUri = data.getData();
                    updatePhoto = true;
                    Glide.with(getContext()).load(imageUri).into(circleImageView);
                }else {
                    Toast.makeText(getContext(), "Image not found!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 1);
            }else {
                Toast.makeText(getContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
