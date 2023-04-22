package com.example.fmclient.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import Model.Person;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fmclient.DataCache;
import com.example.fmclient.R;

import java.util.ArrayList;

import Model.Event;

public class SearchActivity extends AppCompatActivity {
    private DataCache dc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Family Map: Search");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.search_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        dc = DataCache.getInstance();

        EditText search = findViewById(R.id.searchViewText);
        ImageView icon = findViewById(R.id.iconView);
        icon.setImageResource(R.drawable.search_icon);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<Person> people = dc.searchPeople(s.toString());
                ArrayList<Event> events = dc.searchEvents(s.toString());
                SearchAdapter adapter = new SearchAdapter(people, events);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // home == MainActivity
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchView> {
        private ArrayList<Person> people;
        private ArrayList<Event> events;

        public SearchAdapter(ArrayList<Person> people, ArrayList<Event> events) {
            this.people = people;
            this.events = events;
        }

        @Override
        public int getItemViewType(int position) {
            // 0 is PERSON TYPE 1 is EVENT TYPE
            return position < people.size() ? 0 : 1;
        }

        @NonNull
        @Override
        public SearchView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v;

            // PERSON TYPE
            if (viewType == 0) v = getLayoutInflater().inflate(R.layout.family, parent, false);
            else v = getLayoutInflater().inflate(R.layout.life_event, parent, false);

            return new SearchView(v, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchView holder, int position) {
            if (position < people.size()) holder.bind(people.get(position));
            else holder.bind(events.get(position - people.size()));
        }

        @Override
        public int getItemCount() {
            return people.size() + events.size();
        }
    }

    private class SearchView extends RecyclerView.ViewHolder implements View.OnClickListener {

        private DataCache dc;
        private int viewType;
        private ImageView iconView;
        private TextView text1;
        private TextView text2;
        private Person p;
        private Event e;

        public SearchView(@NonNull View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            p = null;
            e = null;
            dc = DataCache.getInstance();

            itemView.setOnClickListener(this);
            iconView = itemView.findViewById(R.id.iconView);
            // Person Type
            if (viewType == 0) {
                text1 = itemView.findViewById(R.id.person_name);
            }
            // Event Type
            else {
                text1 = itemView.findViewById(R.id.life_event_name);
                text2 = itemView.findViewById(R.id.user_name);
            }
        }

        @SuppressLint("SetTextI18n")
        public void bind(Person person) {
            this.p = person;
            if (person.getGender().contains("m")) iconView.setImageResource(R.drawable.man_icon);
            else iconView.setImageResource(R.drawable.woman_icon);
            text1.setText(p.getFirstName() + " " + p.getLastName());
        }

        @SuppressLint("SetTextI18n")
        public void bind(Event event) {
            e = event;
            Person person = dc.getPersonById(e.getPersonID());
            iconView.setImageResource(R.drawable.location_icon);
            text1.setText(e.getEventType() + ": " +
                    e.getCity() + " " + e.getCountry() + " (" + e.getYear() +  ")");
            text2.setText(person.getFirstName() + " " + person.getLastName());
        }

        @Override
        public void onClick(View v) {
            // if view type is person type
            if (viewType == 0) moveToPersonActivity(SearchActivity.this, "PERSONID", p.getPersonID());
            else moveToEventActivity(SearchActivity.this, "EVENTID", e.getEventID());
        }

        private void moveToPersonActivity(Context c, String key, String personID) {
            Intent intent = new Intent();
            intent.putExtra(key, personID);
            intent.setClass(c, PersonActivity.class);
            c.startActivity(intent);
        }

        private void moveToEventActivity(Context c, String key, String eventID) {
            dc.setSelectedEvent(dc.getEventByEventId(eventID));
            Intent intent = new Intent();
            intent.putExtra(key, eventID);
            intent.putExtra("boolean", true);
            intent.setClass(c, EventActivity.class);
            c.startActivity(intent);
        }
    }
}