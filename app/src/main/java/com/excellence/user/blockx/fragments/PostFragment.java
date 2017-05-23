package com.excellence.user.blockx.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.excellence.user.blockx.R;
import com.excellence.user.blockx.util.AppConfig;
import com.excellence.user.blockx.util.MultipartUtility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class PostFragment extends Fragment {

    /**
     * Here we have declaration of variables
     */
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private Bitmap vidThumbBitmap;
    private Uri fileUri;
    private Uri userFileUri;
    private ImageView imageView;
    private ImageView imageView2;
    private ImageView userImageView;
    private TextView textView;
    private TextView postView;
    private EditText edText;
    private static final String TAG = "PostFragment";
    private String realPath;
    private String memberPicRealPath;
    public static final String KEY_IMAGE = "image";
    public static final String KEY_TEXT = "text";
    public static final String UPLOADVID_URL = "http://192.168.1.3/uploadvid.php";
    private SharedPreferences sharedPreferences;
    private String PREFS_NAME = "userPreferences";
    private TextView fullName;
    private String displayName;
    private String displayPosition;
    private String uriString;
    private TextView rolePosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        /**
         * Initialization of views that will be used in this Activity
         */
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.frag_post, container, false);
        LinearLayout photosvideos = (LinearLayout)rootView.findViewById(R.id.photos_videos);
        imageView = (ImageView)rootView.findViewById(R.id.picView);
        textView = (TextView)rootView.findViewById(R.id.textView2);
        imageView2=(ImageView)rootView.findViewById(R.id.imageView2);
        userImageView =(ImageView)rootView.findViewById(R.id.imageView4);
        postView = (TextView)rootView.findViewById(R.id.imageButton);
        edText = (EditText)rootView.findViewById(R.id.editText);
        fullName = (TextView)rootView.findViewById(R.id.fullName2);
        rolePosition = (TextView)rootView.findViewById(R.id.textView5);

        sharedPreferences = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        if(!((displayName = sharedPreferences.getString("DisplayName", "nope")).equals("nope")))
            fullName.setText(displayName);
        if(!((displayPosition = sharedPreferences.getString("RolePosition", "nope")).equals("nope")))
            rolePosition.setText(displayPosition);
        if(!((uriString = sharedPreferences.getString("DisplayPic", "nope")).equals("nope"))) {
            userFileUri = Uri.parse(uriString);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), userFileUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            userImageView.setImageBitmap(bitmap);
        }
        memberPicRealPath = getImagePath(getActivity(), userFileUri);
        /**
         * Adding listener to EditText to determine when
         * it has input and activate the "POST" button
         * accordingly
         */
        edText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String fieldText = edText.getText().toString();

                /**
                 * The button is activated and clickable if there is text
                 * in the field and is deactivated if the text field is
                 * empty and there is no image in the imageview
                 */
                if(!fieldText.matches("")){

                    postView.setTextColor(Color.WHITE);
                    postView.setClickable(true);
                } else if (fieldText.matches("") && imageView.getDrawable() == null) {
                    postView.setTextColor(Color.parseColor("#80ffffff"));
                    postView.setClickable(false);
                }
            }
        });

        /**
         * OnclickListener placed on the photos/videos layout which will
         * prompt the user to select a file from his/her gallery
         */
        photosvideos.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        /**
         * OnClickListener placed on postView button. on click, upload of data commences.
         */
        postView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getActivity();
                String fileType;
                if(fileUri!=null) {
                    fileType = getMimeType(context, fileUri);
                }else{
                    fileType = null;
                }
                if(fileType == "jpg" || fileType == "jpeg" || fileType == "png" || fileType == "bmp"
                        || fileType == "bpg"){
                    uploadImage();
                }else if(fileType == "mp4" || fileType == "wmv" || fileType == "gif" || fileType == "avi"
                        || fileType == "mpg" || fileType == "flv" || fileType == "mpeg"){
                    uploadVideo();
                }else if(fileType == null){
                    uploadText();
                }
            }
        });
        postView.setClickable(false);
        return rootView;
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * Displays photo or thumbnail of video in the photos/videos layout
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            fileUri = data.getData();
            Context context = getActivity();
            String fileType = getMimeType(context, fileUri);
            imageView2.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            postView.setTextColor(Color.WHITE);
            postView.setClickable(true);
            try {
                if(fileType == "jpg" || fileType == "jpeg" || fileType == "png" || fileType == "bmp"
                        || fileType == "bpg"){
                    realPath = getImagePath(context, fileUri);
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), fileUri);
                    imageView.setImageBitmap(bitmap);
                }else if(fileType == "mp4" || fileType == "wmv" || fileType == "gif" || fileType == "avi"
                        || fileType == "mpg" || fileType == "flv" || fileType == "mpeg"){
                    realPath = getPath(context, fileUri);
                    vidThumbBitmap = ThumbnailUtils.createVideoThumbnail(realPath,
                            MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                    imageView.setImageBitmap(vidThumbBitmap);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param context
     * @param uri
     * @return
     * Obtains the type of file that is being added to the photos/videos layout
     */
    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    /**
     *
     * @param context
     * @param uri
     * @return
     * Obtains the path of a video file we are attempting to upload
     */
    public String getPath(Context context, Uri uri) {
        String path ="";

        if(ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {



            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);

        }else {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = context.getContentResolver().query(
                    android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            cursor.close();
        }
        return path;
    }

    public String getImagePath(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    /**
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     * Deals with permissions pertaining to the app
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 2909: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "Granted");
                } else {
                    Log.e("Permission", "Denied");
                }
                return;
            }
        }
    }

    /**
     *
     * @param bmp
     * @return
     * Provides an encoded string fit for upload to a server
     */
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    /**
     * Uploads post
     */
    public void uploadImage(){

        final String text;
        if(edText.getText().toString().trim().length()>0) {
            text = edText.getText().toString().trim();
        }else {
            text = "n/a";
        }
        final String userImage;
        final String image = getStringImage(bitmap);
        class UploadImage extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getActivity(),"Please wait...","uploading",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getActivity(),s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                StringBuilder sb = new StringBuilder();
                try{
                    MultipartUtility multipartUtility = new MultipartUtility(AppConfig.URL_UPLOAD, "UTF-8");
                    multipartUtility.addFilePart("imageFile", new File(realPath));
                    multipartUtility.addFilePart("profilePic", new File(memberPicRealPath));
                    multipartUtility.addFormField("text", text);
                    multipartUtility.addFormField("position", displayPosition);
                    multipartUtility.addFormField("displayName", displayName);
                    List<String> response = multipartUtility.finish();
                    for(String line : response){
                        sb.append(line);
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
                return sb.toString();



            }
        }
        UploadImage u = new UploadImage();
        u.execute();
    }

    private void uploadVideo() {

        final String text;
        if(edText.getText().toString().trim().length()>0) {
            text = edText.getText().toString().trim();
        }else {
            text = "n/a";
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        vidThumbBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        final String sentThumb = Base64.encodeToString(byteArray, Base64.DEFAULT);
        class UploadVideo extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(getActivity(), "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
            }

            @Override
            protected String doInBackground(Void... params) {
                StringBuilder sb = new StringBuilder();
                try{
                    MultipartUtility multipartUtility = new MultipartUtility(AppConfig.URL_UPLOAD, "UTF-8");
                    multipartUtility.addFilePart("videoFile", new File(realPath));
                    multipartUtility.addFilePart("profilePic", new File(memberPicRealPath));
                    multipartUtility.addFormField("text", text);
                    multipartUtility.addFormField("position", displayPosition);
                    multipartUtility.addFormField("displayName", displayName);
                    multipartUtility.addFormField("thumbNail", sentThumb);
                    List<String> response = multipartUtility.finish();
                    for(String line : response){
                        sb.append(line);
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
                return sb.toString();
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }

    private void uploadText() {
        final String text;
        text = edText.getText().toString().trim();
        class UploadText extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getActivity(),"Please wait...","uploading",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getActivity(),s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                StringBuilder sb = new StringBuilder();
                try{
                    MultipartUtility multipartUtility = new MultipartUtility(AppConfig.URL_UPLOAD, "UTF-8");
                    multipartUtility.addFilePart("profilePic", new File(memberPicRealPath));
                    multipartUtility.addFormField("text", text);
                    multipartUtility.addFormField("position", displayPosition);
                    multipartUtility.addFormField("displayName", displayName);
                    List<String> response = multipartUtility.finish();
                    for(String line : response){
                        sb.append(line);
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
                return sb.toString();
            }
        }
        UploadText u = new UploadText();
        u.execute();
    }

}
