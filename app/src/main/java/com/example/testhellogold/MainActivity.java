package com.example.testhellogold;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

//import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
//import java.util.Set;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class MainActivity extends AppCompatActivity {



    private String secretKey;
    EditText emailText,passwordText;
    Button registerBtn;
    CheckBox tncCheckBox;



    public boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isValidPassword(String pwd) {
        return passwordText.getText().toString().length() >= 8;
    }

    public boolean isTNCClicked() {
        return tncCheckBox.isChecked();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        secretKey = App.getContext().getResources().getString(R.string.secret);
        emailText = (EditText)findViewById(R.id.emailInput);
        passwordText = (EditText)findViewById(R.id.pwdInput);
        registerBtn = (Button)findViewById(R.id.registerButton);
        tncCheckBox = (CheckBox)findViewById(R.id.tncBox);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidEmail(emailText.getText().toString())
                        && isValidPassword(passwordText.getText().toString())
                        && isTNCClicked())
                {
                    try {
                        postRequest(App.getContext().getString(R.string.register_api));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                    Toast.makeText(MainActivity.this,"Please complete the form.",Toast.LENGTH_LONG).show();
            }
        });

    }


    void postRequest(String postUrl) throws IOException {

        OkHttpClient client = new OkHttpClient();

        final String emailTemp = emailText.getText().toString();
        String uuid = UUID.randomUUID().toString();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("email", emailTemp)
                .addFormDataPart("uuid", uuid)
                .addFormDataPart("data", encrypt(passwordText.getText().toString(),secretKey))
                .addFormDataPart("tnc", tncCheckBox.isChecked()+"")
                .build();


        Request request = new Request.Builder()
                .url(postUrl)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String result = response.body().string();

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject resultJson = new JSONObject(result);

                            if(resultJson.getString("result").equals("ok")) {
                                JSONObject dataJson = new JSONObject(resultJson.getString("data"));
                                Auth.user = new Auth.User(
                                        emailTemp,
                                        dataJson.getString("api_token"),
                                        dataJson.getString("account_number"),
                                        dataJson.getString("public_key"),
                                        dataJson.getString("api_key"));
                                if(Auth.user != null)
                                    startActivity(new Intent(MainActivity.this,LandingActivity.class));
                                else{
                                    //Bob do something
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("UNAUTH",e.getMessage());
                            Auth.user = null;
                        }
                    }
                });
            }
        });

    }


    public String encrypt(String strToEncrypt, String secret)
    {
        try
        {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[64];
            random.nextBytes(salt);

            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt, 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return java.util.Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
            }
            else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                return android.util.Base64.encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")), android.util.Base64.DEFAULT);
            }
        }
        catch (Exception e)
        {
            Log.d("ENCRYPT FAILED",e.toString());
        }
        return null;
    }


}
