package mu.lab.thulib.thuseat;

/**
 * Implement of seat state
 * Created by coderhuhy on 15/12/8.
 */
public class SeatStateImpl implements SeatState {

    private String area;
    private int occupied;
    private int rest;
    private int total;

    public SeatStateImpl(String area, int occupied, int rest) {
        this.area = area;
        this.occupied = occupied;
        this.rest = rest;
        this.total = rest + occupied;
    }

    @Override
    public String getArea() {
        return area;
    }

    @Override
    public int getOccupied() {
        return occupied;
    }

    @Override
    public int getRest() {
        return rest;
    }

    @Override
    public String toString() {
        return getArea() + "-" + getOccupied() + "-" + getRest();
    }

    @Override
    public State getState() {
        double ratio = (double) occupied / total;
        if (ratio <= State.Idle.getRatio()) {
            return State.Idle;
        } else if (ratio <= State.Well.getRatio()) {
            return State.Well;
        } else {
            return State.Busy;
        }
    }
}
