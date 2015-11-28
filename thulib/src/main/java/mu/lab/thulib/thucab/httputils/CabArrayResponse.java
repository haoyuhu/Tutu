package mu.lab.thulib.thucab.httputils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Cab array response
 * Created by coderhuhy on 15/11/12.
 */
public class CabArrayResponse extends CabJsonResponse {

    private final static String LogTag = CabArrayResponse.class.getSimpleName();

    private JSONArray data;

    public CabArrayResponse(String resp) {
        super(resp);
        try {
            JSONObject object = new JSONObject(resp);
            this.data = object.optJSONArray("data");
        } catch (JSONException e) {
            Log.e(LogTag, e.getMessage(), e);
            this.data = null;
        }
    }

    public JSONArray getData() {
        return this.data;
    }

}
