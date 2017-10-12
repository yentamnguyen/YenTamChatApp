/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
/*
 * @author: Trương Nguyễn Yên Tâm - AT110541 - AT11E
 */
package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity {

    Boolean loginModeActive = false;

    EditText reEnterPasswordEditText;

    //Chuyển sang giao diện UserListActivity sau khi log in thành công
    public void redirectIfLoggedIn() {

        if (ParseUser.getCurrentUser() != null) {

            Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
            startActivity(intent);

        }

    }

    public void toggleLoginMode(View view) {

        Button loginSignupButton = (Button) findViewById(R.id.loginSignupButton);

        reEnterPasswordEditText = (EditText) findViewById(R.id.repasswordEditText);

        TextView toggleLoginModeTextView = (TextView) findViewById(R.id.toggleLoginModeTextView);

        if (loginModeActive) {

            loginModeActive = false;
            loginSignupButton.setText("Sign Up");
            toggleLoginModeTextView.setText("Or, log in");
            reEnterPasswordEditText.setVisibility(View.VISIBLE);


        } else {

            loginModeActive = true;
            loginSignupButton.setText("Log In");
            toggleLoginModeTextView.setText("Or, sign up");
            reEnterPasswordEditText.setVisibility(View.INVISIBLE);

        }

    }

    public void signupLogin(View view) {

        Button loginSignupButton = (Button) findViewById(R.id.loginSignupButton);

        loginSignupButton.setActivated(false);

        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);

        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        //Nếu đang ở trạng thái Login (loginModeActive = true)
        if (loginModeActive) {

            ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {

                    if (e == null) {

                        Log.i("Info", "user logged in");

                        //Chuyển sang giao diện UserListActivity sau khi log in thành công
                        redirectIfLoggedIn();


                    } else {

                        String message = e.getMessage();

                        if (message.toLowerCase().contains("java")) {

                            message = e.getMessage().substring(e.getMessage().indexOf(" "));

                        }

                        //Hiện thông báo lỗi
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

                    }

                }
            });

        //Nếu đang ở trạng thái Sign up (LoginModeActive = false) và người dùng đã nhập dữ liệu vào ô reEnterPasswordEditText
        } else if (passwordEditText.getText().toString().equals(reEnterPasswordEditText.getText().toString())) {

            //Tạo một đối tượng Parse lưu trên Parse Server
            ParseUser user = new ParseUser();

            //Ghi thuộc tính Username
            user.setUsername(usernameEditText.getText().toString());

            //Ghi thuộc tính Password
            user.setPassword(passwordEditText.getText().toString());

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    //Nếu không có lỗi
                    if (e == null) {

                        Log.i("Info", "user signed up");

                        //Chuyển sang giao diện UserListActivity sau khi log in thành công
                        redirectIfLoggedIn();

                    } else {

                        String message = e.getMessage();

                        if (message.toLowerCase().contains("java")) {

                            message = e.getMessage().substring(e.getMessage().indexOf(" "));

                        }

                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                }
            });

        //Nếu Password và phần nhập lại Password không khớp
        } else

        {
            Toast.makeText(MainActivity.this, "Password and Re-enter password did not match!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("YenTamChatApp Login");

        redirectIfLoggedIn();

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

}