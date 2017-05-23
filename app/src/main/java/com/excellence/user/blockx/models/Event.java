package com.excellence.user.blockx.models;

import com.orm.SugarRecord;

/**
 * Created by User on 5/3/2017.
 */
public class Event extends SugarRecord {

    private String eventName;
    private String eventTime;

    public Event(){}

    public Event(String eventTime, String eventName) {
        this.eventTime = eventTime;
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }
}
