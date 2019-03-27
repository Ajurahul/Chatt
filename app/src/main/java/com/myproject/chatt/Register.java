package com.myproject.chatt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity {
    EditText username, password,userid;
    Button registerButton;
    String user, pass,name;
    TextView login;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("users");

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        firebaseAuth = FirebaseAuth.getInstance();
        userid = (EditText)findViewById(R.id.userid);


        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        registerButton = (Button)findViewById(R.id.registerButton);
        login = (TextView)findViewById(R.id.login);

        Firebase.setAndroidContext(this);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int n=0;
                user = username.getText().toString().trim();
                name = userid.getText().toString().trim();
                String url = "https://chatt-96d88.firebaseio.com/users.json";
                pass = password.getText().toString().trim();
                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try{
                            JSONObject obj = new JSONObject(s);
                            if(obj.has(name)){
                                userid.setError("Username already taken");
                                userid.setText("");

                            }

                            else if(user.equals("")){
                                username.setError("can't be blank");
                            }
                            else if(pass.equals("")){
                                password.setError("can't be blank");
                            }


                            else {
                                final ProgressDialog pd = new ProgressDialog(Register.this);
                                pd.setMessage("Loading...");
                                pd.show();
                                firebaseAuth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){

                                            pd.dismiss();
                                            Toast.makeText(Register.this, "registration successful", Toast.LENGTH_LONG).show();
                                            ref.child(name).child("password").setValue(pass);
                                            username.setText("");
                                            password.setText("");
                                            userid.setText("");
                                        }
                                        else{
                                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                                Toast.makeText(Register.this,"User with this email-id already exists",Toast.LENGTH_SHORT).show();
                                                pd.dismiss();
                                                username.setText("");
                                                password.setText("");
                                            }
                                        }
                                    }
                                });




                            }

                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }
                        ,new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("" + volleyError);

                    }
                });
                RequestQueue rQueue = Volley.newRequestQueue(Register.this);
                rQueue.add(request);

            }
        });

    }


}