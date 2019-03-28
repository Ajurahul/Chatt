package com.myproject.chatt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    TextView registerUser;
    EditText username, password;
    Button loginButton;
    String user, pass;
    private CheckBox checkBox;
    private FirebaseAuth firebaseAuth;
    public static final String SHARED_PREFS = "sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        Firebase.setAndroidContext(this);
        setContentView(R.layout.login);
            loadData();
            registerUser = (TextView)findViewById(R.id.register);
            username = (EditText)findViewById(R.id.username);
            password = (EditText)findViewById(R.id.password);
            loginButton = (Button)findViewById(R.id.loginButton);
            checkBox = (CheckBox)findViewById(R.id.checkBoxRememberMe);
            registerUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Login.this, Register.class));
                }
            });

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user = username.getText().toString().trim();
                    pass = password.getText().toString().trim();

                    if(user.equals("")){
                        username.setError("can't be blank");
                    }
                    else if(pass.equals("")){
                        password.setError("can't be blank");
                    }
                    else{
                        String url = "https://chatt-96d88.firebaseio.com/users.json";
                        final ProgressDialog pd = new ProgressDialog(Login.this);
                        pd.setMessage("Loading...");
                        pd.show();
                        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                            @Override
                            public void onResponse(String s) {
                                if (s.equals("null")) {
                                    Toast.makeText(Login.this, "user not found", Toast.LENGTH_LONG).show();
                                    pd.dismiss();
                                } else {
                                    try {
                                        JSONObject obj = new JSONObject(s);

                                        if (!obj.has(user)) {
                                            Toast.makeText(Login.this, "user not found", Toast.LENGTH_LONG).show();
                                            username.setText("");
                                            password.setText("");
                                        } else if (obj.getJSONObject(user).getString("password").equals(pass)) {
                                            UserDetails.username = user;
                                            UserDetails.password = pass;
                                            startActivity(new Intent(Login.this, MainActivity.class));
                                            if(checkBox.isChecked()){
                                                saveUserData(user,pass);
                                            }
                                            username.setText("");
                                            password.setText("");
                                        } else {
                                            Toast.makeText(Login.this, "incorrect password", Toast.LENGTH_LONG).show();
                                            password.setText("");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }


                                pd.dismiss();
                            }
                        },new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                System.out.println("" + volleyError);
                                pd.dismiss();
                            }
                        });


                        RequestQueue rQueue = Volley.newRequestQueue(Login.this);
                        rQueue.add(request);





                    }





                }
            });


    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        String User1=sharedPreferences.getString("Username","");
        String Pass=sharedPreferences.getString("Password","");
            if(User1.isEmpty()){

            }
            else {
                UserDetails.username = User1;
                UserDetails.password = Pass;
                finish();
                startActivity(new Intent(Login.this, MainActivity.class));
            }

    }

    private void saveUserData(String user, String pass) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Username", user);
        editor.putString("Password", pass);
        editor.commit();
    }

}