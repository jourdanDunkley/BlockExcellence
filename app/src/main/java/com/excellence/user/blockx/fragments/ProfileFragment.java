package com.excellence.user.blockx.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.excellence.user.blockx.R;

import java.io.IOException;

/**
 * Created by User on 2/4/2017.
 */
public class ProfileFragment extends Fragment {
    
    private View view;
    private ImageView imageView;
    private EditText editText;
    private TextView saveButton;
    private SharedPreferences sharedPreferences;
    private String displayName;

    private Bitmap bitmap;
    private TextView memberNameBox;
    private TextView memberPositionBox;
    private String memberName;
    private String memberPosition;
    private Uri fileUri;
    private String uriString;
    private String PREFS_NAME = "userPreferences";
    
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_user_profile, container, false);

        initViews();

        setUpViews();

        return view;
    }

    private void setUpViews() {

        sharedPreferences = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        if(!((displayName = sharedPreferences.getString("DisplayName", "nope")).equals("nope")))
            editText.setText(displayName);
        if(!((memberName = sharedPreferences.getString("FullName", "nope")).equals("nope")))
            memberNameBox.setText(memberName);
        if(!((memberPosition = sharedPreferences.getString("RolePosition", "nope")).equals("nope")))
            memberPositionBox.setText(memberPosition);
        if(!((uriString = sharedPreferences.getString("DisplayPic", "nope")).equals("nope"))) {
            fileUri = Uri.parse(uriString);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), fileUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bitmap);
        }

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/");
                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
                startActivityForResult(chooserIntent, 0);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("DisplayName", displayName);
                editor.putString("DisplayPic", fileUri.toString());
                editor.apply();
                Context context = getActivity();
                CharSequence text = "Changes saved!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });


        displayName = editText.getText().toString().trim();
    }

    private void initViews() {
        imageView = (ImageView)view.findViewById(R.id.avatar);
        editText = (EditText)view.findViewById(R.id.editText2);
        saveButton = (TextView)view.findViewById(R.id.saveButton);
        memberNameBox = (TextView)view.findViewById(R.id.MemberName);
        memberPositionBox = (TextView)view.findViewById(R.id.MemberPosition);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == Activity.RESULT_OK) {
            //Process Image
            fileUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), fileUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                Snackbar.make(view, "An error occurred. Try again later", Snackbar.LENGTH_LONG).show();
            }

        }
    }
}
