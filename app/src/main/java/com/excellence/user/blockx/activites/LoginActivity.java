package com.excellence.user.blockx.activites;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.excellence.user.blockx.R;
import com.excellence.user.blockx.util.AppConfig;
import com.excellence.user.blockx.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by User on 2/11/2017.
 */
public class LoginActivity extends Activity {
    private Button loginButton;
    private EditText hallName;
    private EditText password;
    private SessionManager sessionManager;
    private ProgressDialog pDialog;
    private String loginName;
    private String loginPassword;
    private SharedPreferences sharedPreferences;
    private String PREFS_NAME = "userPreferences";


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        initViews();
        
        setUpViews();
    }

    private void initViews() {
        hallName = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        loginButton = (Button)findViewById(R.id.btnLogin);
        sessionManager = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
    }

    private void setUpViews() {
        if(sessionManager.isLoggedIn()){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                loginName = hallName.getText().toString().trim();
                loginPassword = password.getText().toString().trim();

                // Check for empty data in the form
                if (!loginName.isEmpty() && !loginPassword.isEmpty()) {
                    // login user
                    checkLogin(loginName, loginPassword);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });
    }

    private void checkLogin(final String loginName, final String loginPassword) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        sessionManager.setLogin(true);

                        // Now store the user in SQLite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String hallname = user.getString("hallname");
                        String rolePosition = user.getString("position");
                        editor.putString("FullName", name);
                        editor.putString("HallName", hallname);
                        editor.putString("RolePosition", rolePosition);
                        editor.commit();

                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("hallname", loginName);
                params.put("password", loginPassword);

                return params;
            }

        };

        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //Adding our request to the queue
        requestQueue.add(strReq);
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


}
