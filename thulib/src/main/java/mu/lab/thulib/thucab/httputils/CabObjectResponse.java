package mu.lab.thulib.thucab.httputils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Json object response
 * Created by coderhuhy on 15/11/11.
 */
public class CabObjectResponse extends CabJsonResponse {

    private final static String LogTag = CabObjectResponse.class.getSimpleName();

    private JSONObject data;

    public CabObjectResponse(String resp) {
        super(resp);
        try {
            JSONObject object = new JSONObject(resp);
            this.data = object.optJSONObject("data");
        } catch (JSONException e) {
            Log.e(LogTag, e.getMessage(), e);
            this.data = null;
        }
    }

    public JSONObject getData() {
        return data;
    }

}
