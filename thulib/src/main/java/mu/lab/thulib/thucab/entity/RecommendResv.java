package mu.lab.thulib.thucab.entity;

import android.os.Parcelable;

/**
 * Recommand reservation record
 * Created by coderhuhy on 15/11/18.
 */
public abstract class RecommendResv implements Parcelable {

    long MAX_PRIORITY = Long.MAX_VALUE;

    public abstract String getRoomName();

    public abstract String getDevId();

    public abstract String getStart();

    public abstract String getEnd();

    public abstract String getDevKind();

    public abstract String getLabId();

    public abstract int getFloorStringId();

    public abstract long getPriority();

}
