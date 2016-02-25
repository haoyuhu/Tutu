package mu.lab.thulib.thucab.entity;

import java.util.List;

/**
 * Abstract State
 * Created by coderhuhy on 15/11/24.
 */
public interface AbstractState {

    String getRoomName();

    String getDevId();

    RoomLabKind getKind();

    int getFloorStringId();

    List<ReservationState.TimeRange> getAvailableTimeRanges();

    ReservationState.TimeRange getRange();

}
