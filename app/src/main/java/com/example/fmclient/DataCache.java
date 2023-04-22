package com.example.fmclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Model.Event;
import Model.Person;
import Model.User;
import Result.EventsResult;
import Result.PersonsResult;

public class DataCache {
    private static DataCache instance;
    private static final String LOGOUT_KEY = "logout";
    private static final String SETTING_KEY = "setting";
    public synchronized static DataCache getInstance() {
        if (instance == null) instance = new DataCache();
        return instance;
    }

    private User user;
    private Person userPerson = new Person("","","","","","","","");
    private Event event;
    private Event selectedEvent;
    private Person selectedPerson;
    private Set<Person> userPeople = new HashSet<Person>();
    private Set<Event> events = new HashSet<Event>();
    private float[] colors;
    private Map<String, Float> color = new HashMap<String, Float>();
    private Set<Event> maleEvents = new HashSet<Event>();
    private Set<Event> femaleEvents = new HashSet<Event>();

    private Map<String, Person> personById = new HashMap<String, Person>();
    private Map<String, Event> eventById = new HashMap<String, Event>();
    private Map<String, ArrayList<Event>> eventByPersonId = new HashMap<>();
    private Map<String, Event> eventsByEventId = new HashMap<>();
    private Map<String, Person> childById = new HashMap<String, Person>();

    public void saveData(String personID, PersonsResult people, EventsResult events) {
        if (!(personID == null || people == null || events == null)) {
            if (!(people.getData().length == 0 || events.getData().length == 0)) {
                saveUserPeople(people);
                saveUserEvents(events);
                userPerson = personById.get(personID);
                selectedEvent = eventByPersonId.get(personID).get(0);
                selectedPerson = personById.get(personID);
            }
        }
    }

    public void saveUserPeople(PersonsResult people) {
        List<Person> list = Arrays.asList(people.getData());
        ArrayList<Person> listOfPeople = new ArrayList<Person>();

        // add all the persons from the PersonsResult to listOfPeople array
        listOfPeople.addAll(list);

        for (Person p : listOfPeople) {
            if (p.getFatherID() != null) childById.put(p.getFatherID(), p);
            if (p.getMotherID() != null) childById.put(p.getMotherID(), p);
            personById.put(p.getPersonID(), p);
            userPeople.add(p);
        }
    }

    public void saveUserEvents(EventsResult events) {
        List<Event> list = Arrays.asList(events.getData());
        ArrayList<Event> listOfEvents = new ArrayList<Event>();

        // add all the persons from the PersonsResult to listOfPeople array
        listOfEvents.addAll(list);


        for (Event e : listOfEvents) {
            // EVENTS BY PERSON ID (Multiple events per person id)
            if (eventByPersonId.get(e.getPersonID()) != null) {
                eventByPersonId.get(e.getPersonID()).add(e);
            } else {
                ArrayList<Event> temp = new ArrayList<>();
                temp.add(e);
                eventByPersonId.put(e.getPersonID(), temp);
            }

            if (e.getEventID() != null) {
                eventById.put(e.getEventID(), e);
            }

            // EVENTS BY EVENT ID (One event per event id)
            eventsByEventId.putIfAbsent(e.getEventID(), e);

            this.events.add(e);
        }
    }

    public ArrayList<Person> searchPeople(String name) {
        String text = name.toLowerCase();
        ArrayList<Person> temp = new ArrayList<>();
        for (Person p : userPeople) {
            if (p.getFirstName().toLowerCase().contains(text) ||
                    p.getLastName().toLowerCase().contains(text))
                temp.add(p);
        }
        return temp;
    }

    public ArrayList<Event> searchEvents(String name) {
        String text = name.toLowerCase();
        ArrayList<Event> temp = new ArrayList<>();
        for (Event e : events) {
            if (e.getCity().toLowerCase().contains(text) || e.getCountry().toLowerCase().contains(text)
            || e.getEventType().toLowerCase().contains(text) || String.valueOf(e.getYear()).contains(text))
                temp.add(e);
        }

        return temp;
    }

    public Person getSelectedPerson() { return selectedPerson; }
    public Event getSelectedEvent() { return selectedEvent; }
    public Person getUserPerson() { return userPerson; }

    public Person getPersonById(String personID) { return personById.get(personID); }
    public ArrayList<Event> getEventsByPersonId(String personID) {
        ArrayList<Event> temp = new ArrayList<>();

        temp = eventByPersonId.get(personID);

        return temp;
    }

    public Event getEventByEventId(String eventID) {
        return eventsByEventId.get(eventID);
    }

    public void categorizeEventsByGender() {
        for (Event e: this.events) {
            Person p = getPersonById(e.getPersonID());
            if (p.getGender().toLowerCase().contains("m")) maleEvents.add(e);
            else if (p.getGender().toLowerCase().contains("f")) femaleEvents.add(e);
        }
    }

    public Set<Event> getMaleEvents() { return maleEvents; }
    public Set<Event> getFemaleEvents() { return femaleEvents; }
    public Person getChildById(String personID) { return childById.get(personID); }
    public Set<Event> getEvents() { return events; }

    public void setSelectedEvents(Set<Event> events) { this.events = events; }
    public void setSelectedEvent(Event event) { this.event = event; }

}
