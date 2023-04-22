package com.example.fmclient.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

import com.example.fmclient.DataCache;
import com.example.fmclient.R;

import Model.Event;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener {

    private FragmentManager fm = getSupportFragmentManager();
    private boolean isDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment main = fm.findFragmentById(R.id.mainActivity);
        boolean isLoggedOut = getIntent().getBooleanExtra("logout", false);
        isDone = getIntent().getBooleanExtra("SETTINGS", false);
        if(isDone) whenDone();
        else {
            if (main == null || isLoggedOut) {
                main = createLoginFragment();
                fm.beginTransaction()
                        .add(R.id.mainActivity, main)
                        .commit();
            } else {
                if (main instanceof LoginFragment) ((LoginFragment) main).registerListener((LoginFragment.Listener) this);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        String type = "MainActivity";
        Log.i(type, "onSavedInstanceState");
    }

    private Fragment createLoginFragment() {
        LoginFragment lf = new LoginFragment();
        lf.registerListener((LoginFragment.Listener) this);
        return lf;
    }

    @Override
    public void whenDone() {
        DataCache data = DataCache.getInstance();
        FragmentManager fm = this.getSupportFragmentManager();
        Fragment mapFrag = new MapFragment();
        Bundle bundle = new Bundle();
        Event selectedEvent = data.getSelectedEvent();
        bundle.putString("EVENTID", selectedEvent.getEventID());
        bundle.putBoolean("SETTINGS", isDone);
        mapFrag.setArguments(bundle);
        fm.beginTransaction()
                .replace(R.id.mainActivity, mapFrag)
                .commit();
    }
}