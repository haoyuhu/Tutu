package mu.lab.thulib.thucab.entity;

/**
 * Room kind entity
 * Created by coderhuhy on 15/11/12.
 */
public enum RoomLabKind {

    HumanitiesLibSecFloorSingle("10310", "10319"), HumanitiesLibThirdFloorSingle("10312", "10321"),
    HumanitiesLibSecFourMem("10314", "10323"), HumanitiesLibSecEightMem("1371211", "10323"),
    YifuSingle("10334", "10336");
    String room;
    String lab;

    RoomLabKind(String room, String lab) {
        this.room = room;
        this.lab = lab;
    }

    public String getRoom() {
        return this.room;
    }

    public String getLab() {
        return this.lab;
    }

    public static RoomLabKind fromRoomLab(String room, String lab) throws NoRoomLabKindException {
        for (RoomLabKind k : RoomLabKind.values()) {
            if (room != null && lab != null
                && k.getRoom().equals(room) && k.getLab().equals(lab)) {
                return k;
            }
        }
        throw new NoRoomLabKindException(room);
    }

    public int getFloorStringId() {
        switch (this) {
            case HumanitiesLibSecFloorSingle:
                return CabFloor.SecondFloor.resId;
            case HumanitiesLibThirdFloorSingle:
                return CabFloor.ThirdFloor.resId;
            default:
                return -1;
        }
    }

    public static class NoRoomLabKindException extends Exception {

        private final String details;

        public NoRoomLabKindException(String details) {
            super(details);
            this.details = details;
        }

        public String getDetails() {
            return "no this room or lab kind: " + this.details;
        }

    }

}
