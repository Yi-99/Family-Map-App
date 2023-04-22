package com.example.fmclient.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.fmclient.R;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Family Map: Event");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = this.getSupportFragmentManager();
        Fragment frag = new MapFragment();

        Intent intent = getIntent();
        Bundle arguments = new Bundle();
        arguments.putString("EVENTID", intent.getStringExtra("EVENTID"));
        arguments.putBoolean("boolean", intent.getBooleanExtra("boolean", true));
        frag.setArguments(arguments);
        fm.beginTransaction().replace(R.id.eventActivity, frag).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("SETTINGS", true);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}