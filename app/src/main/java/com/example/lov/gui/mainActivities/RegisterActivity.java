package com.example.lov.gui.mainActivities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lov.DB.DataBaseHandler;
import com.example.lov.R;
import com.example.lov.service.GMailSender;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText password, passwordRep, userName, email, emailRep;
    TextView goToLogin;
    Button registerBtn;
    RadioGroup radioGroup;
    RadioButton radioButton1, radioButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long startTime = System.nanoTime();
                registerForAsync(view);
                long endTime = System.nanoTime();
                System.out.print("Normal method executiontime: " + (endTime-startTime));

                //AsycTask for register
                startTime = System.nanoTime();
                new MyAsyncTask(RegisterActivity.this).execute();
                endTime = System.nanoTime();
                System.out.println("AsyncTast method executiontime: " +(endTime-startTime));

                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    public void init() {
        registerBtn = findViewById(R.id.registerBtn);
        userName = findViewById(R.id.userName);
        email = findViewById(R.id.email);
        emailRep = findViewById(R.id.repEmail);
        password = findViewById(R.id.password);
        passwordRep = findViewById(R.id.repPassword);
        goToLogin = findViewById(R.id.textView6);
        radioGroup = findViewById(R.id.radioGroup);
        radioButton1 = findViewById(R.id.radio1);
        radioButton2 = findViewById(R.id.radio2);
        //  dataBaseHandler = new DataBaseHandler(this);
    }
    public void registerForAsync(View view) {
        String user = userName.getText().toString();
        String pass = password.getText().toString();
        String mail = email.getText().toString();

        DataBaseHandler dataBaseHandler = new DataBaseHandler(this);
        String avatar= avatarPath();
        try {
            boolean insert = dataBaseHandler.insertUserIntoDataBase(user, mail, SHA1(pass),avatar,0);
            if (!insert)
                Toast.makeText(getApplicationContext(), "Something went wrong please try again", Toast.LENGTH_LONG).show();
            else {
                Toast.makeText(getApplicationContext(), "Account created you can login now", Toast.LENGTH_SHORT).show();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            GMailSender sender = new GMailSender("yanooooo69@gmail.com", "kapelusz");
            sender.sendMail("This is Subject",
                    "This is Body",
                    "yanooooo69@gmail.com",
                    mail);
        } catch (Exception e) {
            Toast toast = Toast.makeText(this,
                    "Coś poszło nie tak z wysłaniem emaila", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    private String avatarPath() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        if(radioButton==radioButton1)return "drawable/profile1.jpg";
        return "drawable/profile2.jpg";

    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : sha1hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    @Override
    public void onBackPressed(){
    }


    public static class MyAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<RegisterActivity> registerActivityWeakReference;
        private String username;
        private String pass;
        private String mail;
        private String avatar;

        MyAsyncTask(RegisterActivity registerActivity) {
            registerActivityWeakReference = new WeakReference<RegisterActivity>(registerActivity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            RegisterActivity registerActivity = registerActivityWeakReference.get();
            if(registerActivity==null || registerActivity.isFinishing()){
                return;
            }
            username = registerActivity.userName.getText().toString();
            pass = registerActivity.password.getText().toString();
            mail = registerActivity.email.getText().toString();
            avatar="";

            int selectedId = registerActivity.radioGroup.getCheckedRadioButtonId();
            RadioButton radioButton = registerActivity.findViewById(selectedId);
            if(radioButton == registerActivity.findViewById(R.id.radio1)) avatar= "drawable/profile1.jpg";
            else{
                avatar= "drawable/profile2.jpg";
            }
        }

        @Override
        protected Boolean doInBackground(Void... probUser) {
            RegisterActivity registerActivity = registerActivityWeakReference.get();
            if(registerActivity==null || registerActivity.isFinishing()){
                return false;
            }
            publishProgress();
            try {
                GMailSender sender = new GMailSender("yanooooo69@gmail.com", "kapelusz");
                sender.sendMail("This is Subject",
                        "This is Body",
                        "yanooooo69@gmail.com",
                        mail);
                return true;
            } catch (Exception e) {
                Toast toast = Toast.makeText(registerActivity,
                        "Something went wrong with sending email", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate();
            RegisterActivity registerActivity = registerActivityWeakReference.get();
            if(registerActivity==null || registerActivity.isFinishing()){
                return;
            }

            DataBaseHandler databaseHelper= new DataBaseHandler(registerActivity);
            try {
                databaseHelper.insertUserIntoDataBase(username, mail, SHA1(pass),avatar,0);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            RegisterActivity registerActivity = registerActivityWeakReference.get();
            if(registerActivity==null || registerActivity.isFinishing()){
                return;
            }
            if (!success) {
                Toast toast = Toast.makeText(registerActivity,
                        "Cant send an email", Toast.LENGTH_SHORT);
                toast.show();
            } else{
                Toast.makeText(registerActivity, "Account created you can login now", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
