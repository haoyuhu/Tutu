package mu.lab.thulib.thucab.resvutils;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mu.lab.thulib.thucab.httputils.ResponseState;

/**
 * Error tag creator
 * Created by coderhuhy on 15/12/1.
 */
public class ErrorTagManager {

    private static final String LogTag = ErrorTagManager.class.getCanonicalName();

    public static String from(ResponseState state) {
        String ret = "Login Error #" + state.ordinal() + "-->";
        ret += "Details: " + state.toString();
        return ret;
    }

    public static ResponseState toState(Throwable throwable) {
        String details = throwable.getMessage();
        return toState(details);
    }

    public static ResponseState toState(String error) {
        ResponseState[] list = ResponseState.values();
        int position = ResponseState.OtherFailure.ordinal();
        if (error != null) {
            Matcher matcher = Pattern.compile("#(\\d+)").matcher(error);
            if (matcher.find()) {
                try {
                    position = Integer.valueOf(matcher.group(1));
                } catch (NumberFormatException e) {
                    Log.e(LogTag, e.getMessage(), e);
                }
            }
            if (position < 0 || position >= list.length) {
                position = ResponseState.OtherFailure.ordinal();
            }
        }
        return list[position];
    }

}
