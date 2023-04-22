package com.example.fmclient.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fmclient.DataCache;
import com.example.fmclient.R;
import com.example.fmclient.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Model.Event;
import Model.Person;

public class PersonActivity extends AppCompatActivity {

    private Person person;
    private Map<Person, String> fam = new HashMap<>();
    private DataCache dc;
    private ArrayList<Event> lifeEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Family Map: Person");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        dc = DataCache.getInstance();
        Settings settings = Settings.getInstance();
        String personID = getIntent().getStringExtra("PERSONID");
        person = dc.getPersonById(personID);

        TextView firstName = findViewById(R.id.firstNameView);
        firstName.setText(person.getFirstName());
        TextView lastName = findViewById(R.id.lastNameView);
        lastName.setText(person.getLastName());
        TextView gender = findViewById(R.id.genderView);
        gender.setText(person.getGender());

        getFamilyRelationships(person);

        // check for settings
        if (settings.isMaleEventsOn() && person.getGender().contains("m") || settings.isFemaleEventsOn()
                && person.getGender().contains("f")) {
            lifeEvents = dc.getEventsByPersonId(person.getPersonID());
        } else lifeEvents = new ArrayList<>();

        ExpandableListView elv = findViewById(R.id.expandableListView);
        elv.setAdapter(new ExpandableListAdapter());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int LIFE_EVENTS_POS = 0;
        private static final int FAMILY_POS = 1;

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch(groupPosition) {
                case LIFE_EVENTS_POS:
                    return lifeEvents.size();
                case FAMILY_POS:
                    return fam.size();
                default:
                    throw new IllegalArgumentException("Unknown group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_list, parent, false);
            }

            TextView title = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case LIFE_EVENTS_POS:
                    title.setText("Life Events");
                    break;
                case FAMILY_POS:
                    title.setText("Family");
                    break;
                default:
                    throw new IllegalArgumentException("Unknown group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch(groupPosition) {
                case LIFE_EVENTS_POS:
                    itemView = getLayoutInflater().inflate(R.layout.life_event, parent, false);
                    initializeLifeEventView(itemView, childPosition);
                    break;
                case FAMILY_POS:
                    itemView = getLayoutInflater().inflate(R.layout.family, parent, false);
                    initializeFamilyView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        @SuppressLint("SetTextI18n")
        private void initializeLifeEventView(View lifeEventView, int childPosition) {
            Event event = lifeEvents.get(childPosition);

            if (event != null) {
                // where the location icon will be
                ImageView iconView = lifeEventView.findViewById(R.id.iconView);
                iconView.setImageResource(R.drawable.location_icon);

                TextView eventNameView = lifeEventView.findViewById(R.id.life_event_name);
                eventNameView.setText(event.getEventType() + ": " + event.getCity() + ", "
                + event.getCountry() + "(" + event.getYear() + ")");
                TextView userNameView = lifeEventView.findViewById(R.id.user_name);
                userNameView.setText(person.getFirstName() + " " + person.getLastName());

                lifeEventView.setOnClickListener(view -> moveToEventActivity(PersonActivity.this, "EVENTID",
                        event.getEventID()));
            }
        }

        private void moveToEventActivity(Context c, String key, String eventID) {
            dc.setSelectedEvent(dc.getEventByEventId(eventID));
            Intent intent = new Intent(c, EventActivity.class);
            intent.putExtra(key, eventID);
            intent.putExtra("boolean", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(intent);
        }

        @SuppressLint("SetTextI18n")
        private void initializeFamilyView(View familyView, int childPosition) {
            // convert map into an arraylist
            ArrayList<Person> temp = new ArrayList<>();
            ArrayList<String> relationship = new ArrayList<>();
            for (Map.Entry<Person, String> entry : fam.entrySet()) {
                temp.add(entry.getKey());
                relationship.add(entry.getValue());
            }

            Person p = temp.get(childPosition);
            String rel = relationship.get(childPosition);

            ImageView iconView = familyView.findViewById(R.id.iconView);
            if (p.getGender().contains("m")) iconView.setImageResource(R.drawable.man_icon);
            else iconView.setImageResource(R.drawable.woman_icon);

            TextView personNameView = familyView.findViewById(R.id.person_name);
            personNameView.setText(p.getFirstName() + " " + p.getLastName());
            TextView relationshipView = familyView.findViewById(R.id.relationship);
            relationshipView.setText(rel);

            familyView.setOnClickListener(view -> updatePersonActivity(PersonActivity.this, "PERSONID",
                    p.getPersonID()));
        }

        private void updatePersonActivity(Context c, String key, String personID) {
            Intent intent = new Intent(c, PersonActivity.class);
            intent.putExtra(key, personID);
            c.startActivity(intent);
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void onGroupExpanded(int groupPosition) {

        }

        @Override
        public void onGroupCollapsed(int groupPosition) {

        }

        @Override
        public long getCombinedChildId(long groupId, long childId) {
            return 0;
        }

        @Override
        public long getCombinedGroupId(long groupId) {
            return 0;
        }
    }

    private void getFamilyRelationships(Person p) {
        if (p.getFatherID() != null) {
            Person father = dc.getPersonById(p.getFatherID());
            fam.put(father, "father");
        }
        if (p.getMotherID() != null) {
            Person mother = dc.getPersonById(p.getMotherID());
            fam.put(mother, "mother");
        }
        if (p.getSpouseID() != null) {
            Person spouse = dc.getPersonById(p.getSpouseID());
            fam.put(spouse, "spouse");
        }
        if (dc.getChildById(p.getPersonID()) != null) {
            Person child = dc.getChildById(p.getPersonID());
            fam.put(child, "child");
        }
    }
}