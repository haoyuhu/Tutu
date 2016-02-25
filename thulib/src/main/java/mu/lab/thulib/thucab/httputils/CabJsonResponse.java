package mu.lab.thulib.thucab.httputils;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Cab json response
 * Created by coderhuhy on 15/11/12.
 */
public abstract class CabJsonResponse {

    private final static int SUCCESS_CODE = 1;
    private int code;
    private RequestAction action;
    private ResponseState state;

    public CabJsonResponse(String resp) {
        try {
            JSONObject object = new JSONObject(resp);
            this.code = object.optInt("ret", 0);
            this.action = RequestAction.from(object.optString("act", ""));
            this.state = ResponseState.from(object.optString("msg", ""));
        } catch (JSONException e) {
            this.code = 0;
            this.action = RequestAction.notFound;
            this.state = ResponseState.JsonFailure;
        }
    }

    public int getCode() {
        return code;
    }

    public boolean isRequestSuccess() {
        return this.code == SUCCESS_CODE;
    }

    public int getStateDetails() {
        return this.state.getDetails();
    }

    public String getStateDetails(Context context) {
        return context.getString(getStateDetails());
    }

    public RequestAction getAction() {
        return action;
    }

    public ResponseState getState() {
        return state;
    }

}
