package com.example.fmclient;

public class Settings {
    private static Settings instance;
    private static boolean maleEventsOn = true;
    private static boolean femaleEventsOn = true;
    private static boolean motherSideOn = true;
    private static boolean fatherSideOn = true;
    private static boolean spouseSideOn = true;
    private static boolean familySideOn = true;
    private static boolean lifeStoryOn = true;

    private Settings() {

    }

    public synchronized static Settings getInstance() {
        if (instance == null) instance = new Settings();
        return instance;
    }

    public boolean isMaleEventsOn() { return maleEventsOn; }
    public boolean isFamilySideOn() { return familySideOn; }
    public boolean isSpouseSideOn() { return spouseSideOn; }
    public boolean isFemaleEventsOn() { return femaleEventsOn; }
    public boolean isLifeStoryOn() { return lifeStoryOn; }
    public boolean isMotherSideOn() { return motherSideOn; }
    public boolean isFatherSideOn() { return fatherSideOn; }

    public void setMaleEvents(boolean isOn) { maleEventsOn = isOn; }
    public void setFamilySide(boolean isOn) { familySideOn = isOn; }
    public void setSpouseSide(boolean isOn) { spouseSideOn = isOn; }
    public void setFemaleEvents(boolean isOn) { femaleEventsOn = isOn; }
    public void setLifeStory(boolean isOn) { lifeStoryOn = isOn; }
    public void setMotherSide(boolean isOn) { motherSideOn = isOn; }
    public void setFatherSide(boolean isOn) { fatherSideOn = isOn; }
}
