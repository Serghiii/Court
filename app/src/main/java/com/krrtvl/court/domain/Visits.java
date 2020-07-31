package com.krrtvl.court.domain;

import java.sql.Time;
import java.util.Date;

public class Visits {
    private Long id;
    private Date date;
    private Time btime;
    private Time etime;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getBtime() {
        return btime;
    }

    public void setBtime(Time btime) {
        this.btime = btime;
    }

    public Time getEtime() {
        return etime;
    }

    public void setEtime(Time etime) {
        this.etime = etime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Visits visits = (Visits) o;
        return id.equals(visits.id);
    }
}
