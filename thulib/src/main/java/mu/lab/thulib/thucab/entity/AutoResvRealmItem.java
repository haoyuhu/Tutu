package mu.lab.thulib.thucab.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Auto reservation realm item
 * Created by coderhuhy on 15/11/18.
 */
public class AutoResvRealmItem extends RealmObject {

    @PrimaryKey
    private String id;
    private int dayOfWeek;
    private String username;
    private String start;
    private String end;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

}
