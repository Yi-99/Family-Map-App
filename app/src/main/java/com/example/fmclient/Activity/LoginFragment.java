package com.example.fmclient.Activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.fmclient.R;
import com.example.fmclient.Tasks.LoginTask;
import com.example.fmclient.Tasks.RegisterTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Request.LoginRequest;
import Request.RegisterRequest;

public class LoginFragment extends Fragment {

    private EditText host;
    private EditText port;
    private EditText username;
    private EditText password;
    private EditText email;
    private EditText firstName;
    private EditText lastName;
    private RadioButton female;
    private RadioButton male;
    private Button loginButton;
    private Button registerButton;
    private boolean isGender = false;

    private RegisterRequest regRequest = new RegisterRequest("", "", "", "", "", "");
    private LoginRequest logRequest = new LoginRequest("", "");

    private TextWatcher watcher;
    private Listener listener;

    public interface Listener {
        void whenDone();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        watcher = new Enabler();
        host = view.findViewById(R.id.serverHostInput);
        port = view.findViewById(R.id.serverPortInput);
        username = view.findViewById(R.id.usernameInput);
        password = view.findViewById(R.id.passwordInput);
        email = view.findViewById(R.id.emailInput);
        firstName = view.findViewById(R.id.firstNameInput);
        lastName = view.findViewById(R.id.lastNameInput);
        female = view.findViewById(R.id.femaleButton);
        male = view.findViewById(R.id.maleButton);
        loginButton = view.findViewById(R.id.loginButton);
        registerButton = view.findViewById(R.id.registerButton);

        host.addTextChangedListener(watcher);
        port.addTextChangedListener(watcher);
        username.addTextChangedListener(watcher);
        password.addTextChangedListener(watcher);
        firstName.addTextChangedListener(watcher);
        lastName.addTextChangedListener(watcher);
        email.addTextChangedListener(watcher);
        loginButton.addTextChangedListener(watcher);
        registerButton.addTextChangedListener(watcher);

        validate();
        loginButton.setEnabled(true);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    logRequest.setUsername(username.getText().toString());
                    logRequest.setPassword(password.getText().toString());

                    Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message m) {
                            Bundle bundle = m.getData();

                            if (bundle.getBoolean("success")) {
                                Toast.makeText(view.getContext(), bundle.getString("firstname") + " " + bundle.getString("lastname"),
                                        Toast.LENGTH_SHORT).show();
                                listener.whenDone();
                            }
                            else
                            {
                                Toast.makeText(view.getContext(), "Login Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    };

                    LoginTask login = new LoginTask(uiThreadMessageHandler, logRequest, host.getText().toString(), port.getText().toString());
                    ExecutorService exec = Executors.newSingleThreadExecutor();
                    exec.submit(login);
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    regRequest.setUsername(username.getText().toString());
                    regRequest.setPassword(password.getText().toString());
                    regRequest.setEmail(email.getText().toString());
                    regRequest.setFirstName(firstName.getText().toString());
                    regRequest.setLastName(lastName.getText().toString());

                    Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message m) {
                            Bundle bundle = m.getData();

                            if (bundle.getBoolean("success")) {
                                Toast.makeText(view.getContext(), bundle.getString("firstname") + " " + bundle.getString("lastname"),
                                        Toast.LENGTH_SHORT).show();
                                listener.whenDone();
                            }
                            else
                            {
                                Toast.makeText(view.getContext(), "Register Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    };

                    RegisterTask register = new RegisterTask(uiThreadMessageHandler, regRequest, host.getText().toString(), port.getText().toString());
                    ExecutorService exec = Executors.newSingleThreadExecutor();
                    exec.submit(register);
                }
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regRequest.setGender("f");
                isGender = true;
                validate();
            }
        });

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regRequest.setGender("m");
                isGender = true;
                validate();
            }
        });

//        loginButton.setEnabled(false);
//        registerButton.setEnabled(false);

        return view;
    }
    private class Enabler implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            validate();
        }

        @Override
        public void afterTextChanged(Editable s) {}

    }

    private void validate() {
        // enable the register button when the fields that are necessary are filled
        registerButton.setEnabled(!(TextUtils.isEmpty(host.getText()) || TextUtils.isEmpty(port.getText()) || TextUtils.isEmpty(username.getText()) ||
                TextUtils.isEmpty(password.getText()) || TextUtils.isEmpty(firstName.getText()) || TextUtils.isEmpty(lastName.getText()) ||
                TextUtils.isEmpty(email.getText()) || !isGender));

        loginButton.setEnabled(!(TextUtils.isEmpty(host.getText()) || TextUtils.isEmpty(port.getText()) || TextUtils.isEmpty(username.getText())
                || TextUtils.isEmpty(password.getText())));
    }

    public void registerListener(Listener listener) { this.listener = listener; }
}