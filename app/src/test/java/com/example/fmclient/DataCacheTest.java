package com.example.fmclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import Model.Event;
import Model.Person;
import Request.LoginRequest;
import Result.EventIDResult;
import Result.EventsResult;
import Result.LoginResult;
import Result.PersonIDResult;
import Result.PersonsResult;

public class DataCacheTest {
    private DataCache dc;
    private Settings settings;
    private LoginResult logRes;
    private PersonsResult personsRes;
    private EventsResult eventsRes;
    private ServerProxy server;
    private String host = "localhost";
    private String port = "8080";
    private String authtoken = "123";
    private LoginRequest logReq;
    private Map<Person, String> fam;
    private Person person;

    @Before
    public void setUp() {
         server = ServerProxy.initialize();
         dc = DataCache.getInstance();
         server.setHost(host);
         server.setPort(port);
         logReq = new LoginRequest("sheila", "parker");
         logRes = server.login(host, port, logReq);
         personsRes = server.getPeople(host, port, authtoken);
         eventsRes = server.getEvents(host, port, authtoken);
         fam = new HashMap<>();
    }

    @Test
    public void saveDataFail() {
        dc.saveData(null, null, null);
        Assert.assertEquals(dc.getUserPerson().getPersonID(), "");
        Assert.assertNull(dc.getSelectedPerson());
        Assert.assertNull(dc.getSelectedEvent());
        Assert.assertEquals(dc.getEvents().size(), 0);
        Assert.assertEquals(dc.getMaleEvents().size(), 0);
        Assert.assertEquals(dc.getFemaleEvents().size(), 0);
    }

    @Test
    public void saveDataSuccess() {
        Person p1 = new Person("1", "sheila", "joe",
                "smith", "m", "joe_smith", "mother_smith",
                "spouse_smith");
        Person p2 = new Person("2", "sheila", "john",
                "smith", "m", "joe_smith", "mother_smith", "lady_smith");
        Person[] list = new Person[2];
        list[0] = p1;
        list[1] = p2;

        Event e1 = new Event("1", "sheila", "1", 50, 50,
                "United States", "Provo", "birth", 1990);
        Event e2 = new Event("2", "sheila", "1", 5, 5,
                "United States", "Provo", "birth", 1995);
        Event[] temp = new Event[2];
        temp[0] = e1;
        temp[1] = e2;

        eventsRes = new EventsResult(temp, true);
        personsRes = new PersonsResult(list, true);
        dc.saveData(logRes.getPersonID(), personsRes, eventsRes);
        assertNotNull(dc.getUserPerson());
        assertNotNull(dc.getSelectedEvent());
        assertNotNull(dc.getSelectedPerson());
        assertNotNull(dc.getEvents());
        assertNotNull(dc.getMaleEvents());
        assertNotNull(dc.getFemaleEvents());
    }

    @Test
    public void familyRelationshipSuccess() {
        person = dc.getSelectedPerson();
        Person father = dc.getPersonById(person.getFatherID());
        Person mother = dc.getPersonById(person.getMotherID());
        Person spouse = dc.getPersonById(person.getSpouseID());
        assertEquals(fam.get(father), "father");
        assertEquals(fam.get(mother), "mother");
        assertEquals(fam.get(spouse), "spouse");
    }

    @Test
    public void familyRelationshipFail() {
        person = dc.getSelectedPerson();
        assertNull(person.getFatherID());
        Person father = dc.getPersonById(person.getFatherID());
        assertNotEquals(fam.get(father), "father");
    }

    @Test
    public void sortPersonEventFail() {
        ArrayList<Event> events = dc.getEventsByPersonId(person.getPersonID());
        Assert.assertNotNull(events);
        Assert.assertEquals(0, events.size());
    }

    @Test
    public void sortPersonEventSuccess() {
        ArrayList<Event> events = dc.getEventsByPersonId(person.getPersonID());
        Assert.assertNull(events);

        Person person = new Person("0002", "yirang", "yirang",
                "lim", "male", null, null, null);

        EventIDResult e1 = new EventIDResult("0001", "hi", "no", person.getPersonID(),
                "10.0f", "10.0f", "Korea", "Provo", "1999", true);
        EventIDResult e2 = new EventIDResult("0001", "maybe", "hia", person.getPersonID(),
                "10.0f", "10.0f", "Korea", "Provo", "1999", true);

        PersonIDResult personIDResult = new PersonIDResult(person.getAssociatedUsername(),
                person.getPersonID(), person.getFirstName(), person.getLastName(),
                person.getGender(), person.getFatherID(), person.getMotherID(),
                person.getSpouseID(), true);

        dc.saveData(person.getPersonID(), personsRes, eventsRes);
        ArrayList<Event> temp = dc.getEventsByPersonId(person.getPersonID());
        Assert.assertEquals(temp.get(0).getYear(), temp.get(1).getYear());
    }

    @Test
    public void filterEventsSuccess() {
        Set<Event> selectedEvents = new HashSet<>();
        settings.setMaleEvents(true);
        settings.setFemaleEvents(true);
        if (settings.isMaleEventsOn() && settings.isFemaleEventsOn()) {
            selectedEvents.addAll(dc.getMaleEvents());
            selectedEvents.addAll(dc.getFemaleEvents());
            dc.setSelectedEvents(selectedEvents);
        }
        assertNotNull(dc.getEvents());
        selectedEvents.clear();
        settings.setMaleEvents(true);
        settings.setFemaleEvents(false);
        if (settings.isMaleEventsOn() && !settings.isFemaleEventsOn()) {
            selectedEvents.addAll(dc.getMaleEvents());
            dc.setSelectedEvents(selectedEvents);
        }
        selectedEvents.clear();
        settings.setMaleEvents(false);
        settings.setFemaleEvents(true);
        if (settings.isFemaleEventsOn() && !settings.isMaleEventsOn()) {
            selectedEvents.addAll(dc.getFemaleEvents());
            dc.setSelectedEvents(selectedEvents);
        }
        selectedEvents.clear();
        settings.setMaleEvents(false);
        settings.setFemaleEvents(false);
        if (!(settings.isMaleEventsOn() && settings.isFemaleEventsOn())) {
            dc.setSelectedEvents(selectedEvents);
        }
        Assert.assertNotNull(selectedEvents);
        Assert.assertEquals(0, selectedEvents.size());
        selectedEvents.clear();
        dc.setSelectedEvents(dc.getEvents());
    }

    @Test
    public void filterEventsFail(){
        Set<Event> selectedEvents = new HashSet<>();
        settings.setMaleEvents(true);
        settings.setFemaleEvents(true);
        if (settings.isMaleEventsOn() && settings.isFemaleEventsOn()) {
            selectedEvents.addAll(dc.getFemaleEvents());
            dc.setSelectedEvents(selectedEvents);
        }

        selectedEvents.clear();
        settings.setMaleEvents(true);
        settings.setFemaleEvents(false);
        if (settings.isMaleEventsOn() && !settings.isFemaleEventsOn()) {
            dc.setSelectedEvents(selectedEvents);
        }

        selectedEvents.clear();
        settings.setMaleEvents(false);
        settings.setFemaleEvents(true);
        if (settings.isFemaleEventsOn() && !settings.isMaleEventsOn()) {
            selectedEvents.addAll(dc.getMaleEvents());
            selectedEvents.addAll(dc.getFemaleEvents());
            dc.setSelectedEvents(selectedEvents);
        }

        selectedEvents.clear();
        settings.setMaleEvents(false);
        settings.setFemaleEvents(false);
        if (!(settings.isMaleEventsOn() && settings.isFemaleEventsOn())) {
            selectedEvents.addAll(dc.getMaleEvents());
            dc.setSelectedEvents(selectedEvents);
        }
        Assert.assertNotNull(selectedEvents);
        assertNotEquals(0, selectedEvents.size());
        selectedEvents.clear();
        dc.setSelectedEvents(dc.getEvents());
    }

    @Test
    public void findPeopleSuccess() {
        assertNotNull(dc.getPersonById("sheila_parker"));
    }

    @Test
    public void findPeopleFail() {
        assertNull(dc.getPersonById("none"));
    }

    @Test
    public void findEventsSuccess() {
        assertNotNull(dc.getEventByEventId("birthday"));
    }

    @Test
    public void findEventsFail() {
        assertNull(dc.getEventByEventId("yo"));
    }
}