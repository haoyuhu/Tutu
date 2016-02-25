package mu.lab.tufeedback.utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

import mu.lab.tufeedback.R;

/**
 * @author coderhuhy on 15/8/23.
 */
public class DateTimeUtilities {

    public static String formatToAccurateTime(Date oldtime, Context context) {
        // TODO: i18n
        SimpleDateFormat dateformat = new SimpleDateFormat(context.getString(R.string.time_format));
        return dateformat.format(oldtime);
    }

}
