package com.hadassah.azrieli.lev_isha.utility;

import java.util.Date;

/**
 * Created by Avihu Harush on 06/05/2017
 * E-Mail: tchvu3@gmail.com
 */

class ChecklistEntry {

    /*
    * This class relevancy is still under investigation
    * */

    public enum types {
        SINGLE_LINE, MULTI_LINE, CHECK_BOX
    }

    private String name;
    private String field;
    private boolean done;
    private types type;

    public ChecklistEntry(String name, String field) {
        this.name = name;
        this.field = field;
        done = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getfield() {
        return field;
    }

    public void setfield(String field) {
        this.field = field;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void toggleDone() {
        done = !done;
    }
}
