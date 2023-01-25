package com.example.airconditionerproject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileServiceFragment extends Fragment {
    EditText etUsername, etEmail, etPhone, etAddress, etPassword;
    CircleImageView profileCircleImageView;
    Button btnUpdate, btnChangePhoto;
    ProgressBar progressBar;
    String pilihan;
    String photo = "0";
    static final int REQUEST_TAKE_PHOTO = 1;
    final int CODE_GALLERY_REQUEST = 999;
    String rPilihan[] = {"Take a photo", "From album"};
    public final String APP_TAG = "ACku";
    Bitmap bitmap = null;
    public String photoFileName = "photo.jpg";
    File photoFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_service, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileCircleImageView = view.findViewById(R.id.imageProfile);
        etUsername = view.findViewById(R.id.etUsername);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etAddress = view.findViewById(R.id.etAddress);
        etPassword = view.findViewById(R.id.etPassword);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnChangePhoto = view.findViewById(R.id.btnChangePhotoProfile);
        progressBar = view.findViewById(R.id.progressBarProfile);

        Session session = new Session(getActivity());
        String photo = session.getPicture();
        String checkAddress = session.getAddress();
        etUsername.setText(session.getUsername());
        etEmail.setText(session.getEmail());
        etPhone.setText(session.getPhone());

        if(photo.equals("0")){
            profileCircleImageView.setImageResource(R.drawable.default_photo_profile);
        } else {
            String imageUrl = "http://tkjbpnup.com/kelompok_5/acku/upload/" + session.getPicture();
            Glide.with(getActivity())
                    .load(imageUrl)
                    .into(profileCircleImageView);
        }

        if(checkAddress.equals("0")){
            etAddress.setHint("Alamat kamu belum diatur");
        } else {
            etAddress.setText(session.getAddress());
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUser();
            }
        });

        btnChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bitmap != null){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage("Do you want to take photo again?");
                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            takePhote();
                        }
                    });

                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    takePhote();
                }
            }
        });
    }


    private void takePhote()
    {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Select");
        builder.setItems(rPilihan, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pilihan = String.valueOf(rPilihan[i]);
                if(pilihan.equals("Take a photo")){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    photoFile = getPhotoFileUri(photoFileName);

                    String authorities = getActivity().getPackageName() + ".fileprovider";
                    Uri fileProvider = FileProvider.getUriForFile(getActivity(), authorities, photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

                    if(intent.resolveActivity(getActivity().getPackageManager()) != null){
                        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                    }
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CODE_GALLERY_REQUEST);
                }
            }
        });
        builder.show();

    }

    //permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == CODE_GALLERY_REQUEST){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Image"), CODE_GALLERY_REQUEST);
            }else{
                Toast.makeText(getActivity(), "You don't have permission to access gallery!", Toast.LENGTH_LONG).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_TAKE_PHOTO){
            if(resultCode == Activity.RESULT_OK){
                bitmap = decodeSampledBitmapFromFile(String.valueOf(photoFile), 1000, 700);
                uploadImage();
                profileCircleImageView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(getActivity(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        } else {
            if(data != null){
                Uri photoUri = data.getData();
                bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
                } catch (IOException e){
                    e.printStackTrace();
                }
                profileCircleImageView.setImageBitmap(bitmap);
            }
        }
    }

    public File getPhotoFileUri(String fileName){
        if(isExternalStorageAvailable()){
            File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
            if(!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(APP_TAG, "Failed to create directory");
            }

            File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
            return file;
        }
        return null;
    }

    private boolean isExternalStorageAvailable(){
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if(height > reqHeight){
            inSampleSize = Math.round((float) height / (float) reqHeight);
        }
        int expectedWidth = width / inSampleSize;
        if(expectedWidth > reqWidth){
            inSampleSize = Math.round((float) width / (float) reqWidth);
        }

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }


    private void uploadImage()
    {
        Session session = new Session(getActivity());

        String picture = getStringImage(bitmap);
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = "http://tkjbpnup.com/kelompok_5/acku/user/uploadImage";
        RequestParams requestParams = new RequestParams();
        requestParams.put("id_user", session.getIdUser());
        requestParams.put("picture", picture);

        asyncHttpClient.post(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.getString("msg");
                    String picture = jsonObject.getString("picture");
                    if(status){
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        session.setPicture(picture);
                    } else {
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void updateUser()
    {
        Session session = new Session(getActivity());
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if(username.isEmpty()){
            etUsername.setError("username is required!");
            etUsername.requestFocus();
            return;
        }
        if(email.isEmpty()){
            etEmail.setError("email is required!");
            etEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("please provide valid email!");
            etEmail.requestFocus();
            return;
        }
        if(phone.isEmpty()){
            etPhone.setError("phone number is required");
            etPhone.requestFocus();
            return;
        }
        if(address.isEmpty()){
            etAddress.setError("address is required");
            etAddress.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        btnUpdate.setEnabled(false);

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String url = "http://tkjbpnup.com/kelompok_5/acku/user/updateData";
        RequestParams requestParams = new RequestParams();
        requestParams.put("id_user", session.getIdUser());
        requestParams.put("ses_username", session.getUsername());
        requestParams.put("ses_email", session.getEmail());
        requestParams.put("ses_phone", session.getPhone());
        requestParams.put("username", username);
        requestParams.put("email", email);
        requestParams.put("phone", phone);
        requestParams.put("password", password);
        requestParams.put("address", address);

        asyncHttpClient.post(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressBar.setVisibility(View.GONE);
                btnUpdate.setEnabled(true);
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.getString("msg");
                    if(status){
                        etUsername.setText(username);
                        etEmail.setText(email);
                        etPhone.setText(phone);
                        etAddress.setText(address);

                        session.setUsername(username);
                        session.setEmail(email);
                        session.setPhone(phone);
                        session.setAddress(address);
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}