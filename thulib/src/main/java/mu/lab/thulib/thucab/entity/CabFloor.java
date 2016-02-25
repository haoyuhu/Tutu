package mu.lab.thulib.thucab.entity;

import mu.lab.thulib.R;

/**
 * Created by coderhuhy on 15/11/15.
 */
public enum CabFloor {
    SecondFloor(R.string.thucab_second_floor_room),
    ThirdFloor(R.string.thucab_third_floor_room);
    int resId;

    CabFloor(int resId) {
        this.resId = resId;
    }
}
