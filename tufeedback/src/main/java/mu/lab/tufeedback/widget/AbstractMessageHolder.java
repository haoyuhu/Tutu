package mu.lab.tufeedback.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.fb.model.Reply;


/**
 * abstract message view holder
 * Created by coderhuhy on 15/9/30.
 */
public abstract class AbstractMessageHolder {

    protected Context mContext = null;

    public AbstractMessageHolder(Context context) {
        this.mContext = context;
    }

    abstract public View createView(ViewGroup parent);

    abstract public void showData(Reply reply, Reply nextReply);

}
