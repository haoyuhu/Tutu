package mu.lab.thulib.thucab.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Reservation state
 * Created by coderhuhy on 15/11/12.
 */
public class JsonReservationState {

    private String roomName;
    private String devId;
    private String kindId;
    private String labId;
    private String openStart;
    private String openEnd;
    @SerializedName("ts")
    private List<JsonStateItem> states;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getKindId() {
        return kindId;
    }

    public void setKindId(String kindId) {
        this.kindId = kindId;
    }

    public String getLabId() {
        return labId;
    }

    public void setLabId(String labId) {
        this.labId = labId;
    }

    public String getOpenStart() {
        return openStart;
    }

    public void setOpenStart(String openStart) {
        this.openStart = openStart;
    }

    public String getOpenEnd() {
        return openEnd;
    }

    public void setOpenEnd(String openEnd) {
        this.openEnd = openEnd;
    }

    public List<JsonStateItem> getStates() {
        return states;
    }

    public void setStates(List<JsonStateItem> states) {
        this.states = states;
    }

    public static class JsonStateItem {

        private static final String LogTag = JsonStateItem.class.getSimpleName();

        private String start;
        private String end;
        private String state;
        private String owner;
        private String id;

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

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }

}
