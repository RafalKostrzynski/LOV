package com.example.lov;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    DataBaseHelper dataBaseHelper;
    EditText password,passwordRep, userName, email, emailRep;
    TextView goToLogin;
    Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

        goToLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(RegisterActivity.this,  LoginActivity.class));
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register(view);
            }
        });
    }

    public void init() {
        registerBtn = findViewById(R.id.registerBtn);
        userName =findViewById(R.id.userName);
        email =findViewById(R.id.email);
        emailRep =findViewById(R.id.repEmail);
        password =findViewById(R.id.password);
        passwordRep =findViewById(R.id.repPassword);
        goToLogin = findViewById(R.id.textView6);
        dataBaseHelper = new DataBaseHelper(this);
    }

    public void register(View view) {
        String user = userName.getText().toString();
        String pass = password.getText().toString();
        String passRep = passwordRep.getText().toString();
        String mail = email.getText().toString();
        String mailRep = emailRep.getText().toString();

        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(mail);
        if(user.equals("")||pass.equals("")||passRep.equals("")||mail.equals("")||mailRep.equals(""))
            Toast.makeText(getApplicationContext(),"Fields are empty",Toast.LENGTH_SHORT).show();
        else if(user.length()<6)Toast.makeText(getApplicationContext(),"Username is too short",Toast.LENGTH_SHORT).show();
        else if(!matcher.matches())Toast.makeText(getApplicationContext(),"Its not a real email",Toast.LENGTH_SHORT).show();
        else if(pass.length()<6 )Toast.makeText(getApplicationContext(),"Password is too short",Toast.LENGTH_SHORT).show();
        else if(user.equals(pass))Toast.makeText(getApplicationContext(),"Username and password cant be the same",Toast.LENGTH_SHORT).show();
        else if(pass.equals(passRep) && mail.equals(mailRep)){
            try {
                Boolean checkUser= dataBaseHelper.checkUserName(user);
                if(checkUser) {
                    Boolean checkEmail = dataBaseHelper.checkEmail(mail);
                    if(checkEmail){
                        Boolean insert = dataBaseHelper.insertIntoDataBase(user, mail, generateHash(pass));
                        if (!insert)
                            Toast.makeText(getApplicationContext(), "Something went wrong please try again", Toast.LENGTH_LONG).show();
                        else {
                            Toast.makeText(getApplicationContext(), "Account created you can login now", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        }
                    }else Toast.makeText(getApplicationContext(), "This email Already exists", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getApplicationContext(), "This username Already exists", Toast.LENGTH_SHORT).show();
            }catch (NoSuchAlgorithmException e){
                Toast.makeText(getApplicationContext(),"Something went wrong please try again",Toast.LENGTH_LONG).show();
            }
        }
    }

    private String generateHash(String passwordToHash)throws NoSuchAlgorithmException{
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        String generatedPassword = null;
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(salt);
        byte[] bytes = md.digest(passwordToHash.getBytes());
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        generatedPassword = sb.toString();

        return generatedPassword;
    }


}
