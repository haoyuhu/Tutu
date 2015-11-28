package mu.lab.thulib;

import io.realm.annotations.RealmModule;
import mu.lab.thulib.thucab.entity.AutoResvRealmItem;
import mu.lab.thulib.thucab.entity.RealmReservationRecord;

/**
 * @author guangchen.
 */
@RealmModule(library = true, classes = {RealmReservationRecord.class, AutoResvRealmItem.class})
public class ThuLibRealmModule {
}
