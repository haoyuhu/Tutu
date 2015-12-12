package mu.lab.thulib.thuseat;


/**
 * Seat state
 * Created by coderhuhy on 15/12/8.
 */
public interface SeatState {

    enum State {
        Busy(1.0), Well(0.8), Idle(0.4);
        double ratio;

        State(double ratio) {
            this.ratio = ratio;
        }

        public double getRatio() {
            return ratio;
        }
    }

    String getArea();

    int getOccupied();

    int getRest();

    State getState();

    String toString();

}
