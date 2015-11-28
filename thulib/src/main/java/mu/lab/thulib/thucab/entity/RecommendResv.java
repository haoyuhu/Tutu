package mu.lab.thulib.thucab.entity;

/**
 * Recommand reservation record
 * Created by coderhuhy on 15/11/18.
 */
public interface RecommendResv {

    long MAX_PRIORITY = Long.MAX_VALUE;

    String getRoomName();

    String getDevId();

    String getStart();

    String getEnd();

    String getDevKind();

    String getLabId();

    int getFloorStringId();

    long getPriority();

}
