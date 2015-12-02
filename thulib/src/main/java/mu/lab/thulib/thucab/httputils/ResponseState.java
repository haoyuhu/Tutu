package mu.lab.thulib.thucab.httputils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mu.lab.thulib.R;

/**
 * Response state
 * Created by coderhuhy on 15/11/12.
 */
public enum ResponseState {

    Success("ok", R.string.thucab_ok),
    ReservationSuccess("操作成功", R.string.thucab_operation_success),
    ActivateFailure("新用户请先激活", R.string.thucab_activate_failure),
    IdFailure("数据库没此账号", R.string.thucab_id_failure),
    PasswordFailure("统一登录认证返回失败", R.string.thucab_pwd_failure),
    JsonFailure("json_failure", R.string.thucab_json_failure),
    DateFailure("日期格式错误", R.string.thucab_date_failure),
    TimeoutFailure("超时", R.string.thucab_timeout_failure),
    ConflictFailure("预约与现有预约冲突", R.string.thucab_conflict_failure),
    NoTicketFailure("VIEWSTATE", R.string.thucab_no_ticket_failure),
    OtherFailure("other_failure", R.string.thucab_other_failure);
    String state;
    int detailId;

    ResponseState(String state, int detailId) {
        this.state = state;
        this.detailId = detailId;
    }

    public String toString() {
        return this.state;
    }

    public int getDetails() {
        return this.detailId;
    }

    public static ResponseState from(String state) {
        for (ResponseState st : ResponseState.values()) {
            Matcher matcher = Pattern.compile(st.toString()).matcher(state);
            if (matcher.find()) {
                return st;
            }
        }
        return OtherFailure;
    }

}
