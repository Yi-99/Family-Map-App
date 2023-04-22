package com.example.fmclient.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fmclient.DataCache;
import com.example.fmclient.R;
import com.example.fmclient.Settings;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import Model.Event;
import Model.Person;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap map;
    private DataCache dc;
    private ArrayList<Polyline> fatherSide;
    private ArrayList<Polyline> motherSide;
    private Polyline lifeEvent;
    private Polyline eventLine;
    private Polyline spouseLine;
    private Settings settings;
    private float[] colors;
    private TextView infoView;
    private ImageView iconView;
    private Event selectedEvent;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.clear();
        map.setOnMapLoadedCallback(this);

        if (getArguments() != null) {
            boolean isEventActivityDone = getArguments().getBoolean("boolean");
            boolean isSettingsActivityDone = getArguments().getBoolean("SETTINGS");
            String selectedEventID = getArguments().getString("EVENTID");

            selectedEvent = dc.getEventByEventId(selectedEventID);
            Person selectedPerson = dc.getPersonById(selectedEvent.getPersonID());
            LatLng loc = new LatLng(selectedEvent.getLatitude(), selectedEvent.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLng(loc));

            // Make markers according to the settings
            makeMarkers();

            if (isEventActivityDone || isSettingsActivityDone) {
                if (selectedPerson.getGender().contains("m")) iconView.setImageResource(R.drawable.man_icon);
                else iconView.setImageResource(R.drawable.woman_icon);
                // Display event info
                displayEvent(selectedPerson, selectedEvent);
                // Make lines according to the settings
                drawLines(selectedPerson, selectedEvent);
            } else iconView.setImageResource(R.drawable.android_icon);
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        map.animateCamera(CameraUpdateFactory.newLatLng(sydney));
            map.setOnMarkerClickListener(this);

        } else throw new Error("Did not select an event");
    }

    private void displayEvent(Person p, Event e) {
        if (p.getGender().toLowerCase().equals("m")) iconView.setImageResource(R.drawable.man_icon);
        else if (p.getGender().toLowerCase().equals("f")) iconView.setImageResource(R.drawable.woman_icon);

        String info = p.getFirstName() + " " + p.getLastName() + "\n" + e.getEventType() + ": " +
                e.getCity() + " " + e.getCountry() + " (" + e.getYear() +  ")";
        infoView.setText(info);
        infoView.setOnClickListener(v -> updateToPersonActivity(getActivity(), "PERSONID", p.getPersonID()));
    }

    private void updateToPersonActivity(Context c, String key, String personID) {
        Intent intent = new Intent(c, PersonActivity.class);
        intent.putExtra(key, personID);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        c.startActivity(intent);
    }

    private void makeMarkers() {
        Set<Event> events = new HashSet<>();
        float color;
        Random random = new Random();
        int birth = 3; // light green
        int death = 0; // light blue
        int marriage = 4; // pink
        int others = 5; // orange

        Person person = dc.getPersonById(selectedEvent.getPersonID());

        // categorize events by gender
        dc.categorizeEventsByGender();

        if (settings.isMaleEventsOn()) events.addAll(dc.getMaleEvents());
        if (settings.isFemaleEventsOn()) events.addAll(dc.getFemaleEvents());
        if (!settings.isMotherSideOn()) {
            for (Event e : events) {
                if (e.getPersonID().equals(person.getPersonID()) || e.getPersonID().equals(person.getSpouseID()))
                    events.add(e);
            }
        }
        if (!settings.isFatherSideOn()) {
            for (Event e : events) {
                if (e.getPersonID().equals(person.getPersonID()) || e.getPersonID().equals(person.getSpouseID()))
                    events.add(e);
            }
        }

        dc.setSelectedEvents(events);
        for (Event e: events) {
            LatLng loc = new LatLng(e.getLatitude(), e.getLongitude());

            if (e.getEventType().toLowerCase().contains("birth")) color = colors[birth];
            else if (e.getEventType().toLowerCase().contains("death")) color = colors[death];
            else if (e.getEventType().toLowerCase().contains("marriage")) color = colors[marriage];
            else color = colors[others];

            Marker marker = map.addMarker(new MarkerOptions().position(loc).icon(BitmapDescriptorFactory.defaultMarker(color)));
            // when the marker is clicked it will be referencing info from e
            marker.setTag(e);
        }
    }

    private void drawLines(Person p, Event e) {
        // clear lines that were on the screen before
        clearLines();
        if (settings.isMaleEventsOn() && p.getGender().contains("m") || settings.isFemaleEventsOn()
        && p.getGender().contains("f")) {
            if (settings.isFamilySideOn()) {
                createFamilyLines(p, e);
            }
            if (settings.isLifeStoryOn()) {
                createLifeStoryLines(p.getPersonID());
            }
            if (settings.isSpouseSideOn()) {
                createSpouseLines(p, e);
            }
        }
    }

    private void clearLines() {
        if(eventLine != null) eventLine.remove();
        if(fatherSide != null) removeLine(fatherSide);
        if(motherSide != null) removeLine(motherSide);
        if(spouseLine != null) spouseLine.remove();
    }

    private void removeLine(ArrayList<Polyline> lines) {
        for (Polyline line: lines) {
            line.remove();
        }
    }

    private void createLineBetweenChildAndParent(Event event, Event parentEvent, String parent, float w) {
        Polyline line = map.addPolyline(new PolylineOptions()
                .add(new LatLng(event.getLatitude(), event.getLongitude()))
                .add(new LatLng(parentEvent.getLatitude(), parentEvent.getLongitude()))
                .color(Color.BLUE).width(w));
        if (parent.equals("father")) fatherSide.add(line);
        else motherSide.add(line);
    }

    private void createParentLine(Person p, Event e, float w) {
        float newW = w - 5f;
        if (w <= 2f) newW = 2;
        // father's side
        if (p.getPersonID() != null && settings.isMaleEventsOn() && settings.isFatherSideOn()) {
            ArrayList<Event> fatherSideEvents = dc.getEventsByPersonId(p.getFatherID());
            Person father = dc.getPersonById(p.getFatherID());
            if (father != null && fatherSideEvents != null) {
                Event fatherBirth = fatherSideEvents.get(0);
                createLineBetweenChildAndParent(e, fatherBirth, "father", newW);
                createParentLine(father, fatherBirth, newW);
            }
        }
        // mother's side
        if (p.getPersonID() != null && settings.isFemaleEventsOn() && settings.isMotherSideOn()) {
            ArrayList<Event> motherSideEvents = dc.getEventsByPersonId(p.getMotherID());
            Person mother = dc.getPersonById(p.getMotherID());
            if (mother != null && motherSideEvents != null) {
                // Get the last event in the list of events for the mother to get the event that has happened longer ago
                Event motherBirth = motherSideEvents.get(motherSideEvents.size()-1);
                createLineBetweenChildAndParent(e, motherBirth, "mother", newW);
                createParentLine(mother, motherBirth, newW);
            }
        }
    }

    private void createFamilyLines(Person p, Event e) {
        fatherSide = new ArrayList<>();
        motherSide = new ArrayList<>();
        float width = 20.0f;

        if(p.getFatherID() != null && settings.isFatherSideOn() && settings.isMaleEventsOn()) {
            Person father = dc.getPersonById(p.getFatherID());
            ArrayList<Event> fatherEvents = dc.getEventsByPersonId(father.getPersonID());
            Event fatherBirth = fatherEvents.get(0);
//            ArrayList<Event> childBirth = dc.get
            createLineBetweenChildAndParent(e, fatherBirth, "father", width);
            createParentLine(father, fatherBirth, width-3f);
        }
        if(p.getMotherID() != null && settings.isMotherSideOn() && settings.isFemaleEventsOn()) {
            Person mother = dc.getPersonById(p.getMotherID());
            ArrayList<Event> motherEvents = dc.getEventsByPersonId(mother.getPersonID());
            Event motherBirth = motherEvents.get(0);
            createLineBetweenChildAndParent(e, motherBirth, "mother", width);
            createParentLine(mother, motherBirth, width-3f);
        }
    }

    private void createLifeStoryLines(String personID) {
        if (lifeEvent != null) lifeEvent.remove();
        ArrayList<Event> lifeEvents = dc.getEventsByPersonId(personID);
        ArrayList<LatLng> lifeLocations = new ArrayList<>();

        if (lifeEvents != null) {
            for (int i = 0; i < lifeEvents.size(); i++) {
                lifeLocations.add(new LatLng(lifeEvents.get(i).getLatitude(), lifeEvents.get(i).getLongitude()));
            }
            // green color
        }   lifeEvent = map.addPolyline(new PolylineOptions().addAll(lifeLocations).color(0xff81C784));
    }

    private void createSpouseLines(Person p, Event e) {
        if (p.getSpouseID() != null && !Objects.equals(p.getSpouseID(), "")) {
            Person spouse = dc.getPersonById(p.getSpouseID());
            if (settings.isFemaleEventsOn() && spouse.getGender().contains("f")) {
                Event spouseBirth = dc.getEventsByPersonId(p.getSpouseID()).get(0);
                // orange color
                spouseLine = map.addPolyline(new PolylineOptions().add(new LatLng(e.getLatitude(), e.getLongitude()))
                        .add(new LatLng(spouseBirth.getLatitude(), spouseBirth.getLongitude())).color(0xffF9A825));
            } else if (settings.isMaleEventsOn() && spouse.getGender().contains("m")) {
                Event spouseBirth = dc.getEventsByPersonId(p.getSpouseID()).get(0);
                // orange color
                spouseLine = map.addPolyline(new PolylineOptions().add(new LatLng(e.getLatitude(), e.getLongitude()))
                        .add(new LatLng(spouseBirth.getLatitude(), spouseBirth.getLongitude())).color(0xffF9A825));
            }
        }
    }

    @SuppressLint("ResourceType")
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        assert getArguments() != null;
        boolean isEventActivity = getArguments().getBoolean("boolean");
        if (isEventActivity) {
            ((EventActivity) getActivity()).getSupportActionBar().setTitle("Family Map: Event");
        } else {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.menu, menu);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.search_button:
                Toast.makeText(getActivity().getApplicationContext(), "Search Clicked!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            case R.id.settings_button:
                Toast.makeText(getActivity().getApplicationContext(), "Settings Clicked!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        View view = layoutInflater.inflate(R.layout.fragment_map, container, false);

        setHasOptionsMenu(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dc = DataCache.getInstance();
        infoView = view.findViewById(R.id.infoView);
        iconView = view.findViewById(R.id.iconView);
        settings = Settings.getInstance();
        // 10 different colors
        colors = new float[]{
                BitmapDescriptorFactory.HUE_AZURE,
        BitmapDescriptorFactory.HUE_BLUE,
        BitmapDescriptorFactory.HUE_CYAN,
        BitmapDescriptorFactory.HUE_GREEN,
        BitmapDescriptorFactory.HUE_MAGENTA,
        BitmapDescriptorFactory.HUE_ORANGE,
        BitmapDescriptorFactory.HUE_RED,
        BitmapDescriptorFactory.HUE_ROSE,
        BitmapDescriptorFactory.HUE_VIOLET,
        BitmapDescriptorFactory.HUE_YELLOW};

        return view;
    }

    @Override
    public void onMapLoaded() {

    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        // when a marker is selected, get the person whose event it is
        Event selectedEvent = (Event) marker.getTag();
        Person selectedPerson = dc.getPersonById(selectedEvent.getPersonID());

        displayEvent(selectedPerson, selectedEvent);

        drawLines(selectedPerson, selectedEvent);
        dc.setSelectedEvent(selectedEvent);

        return false;
    }
}