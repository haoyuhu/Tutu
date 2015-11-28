package mu.lab.tufeedback.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umeng.fb.model.Reply;

import java.util.Date;

import mu.lab.tufeedback.R;
import mu.lab.tufeedback.utils.DateTimeUtilities;

/**
 * dev message view holder
 * Created by coderhuhy on 15/9/30.
 */
public class DevMessageHolder extends AbstractMessageHolder {

    TextView messageText;
    TextView timeView;

    public DevMessageHolder(Context context) {
        super(context);
    }

    @Override
    public void showData(Reply reply, Reply nextReply) {
        messageText.setText(reply.content);
        showTimeTag(reply, nextReply);
    }

    @Override
    public View createView(ViewGroup parent) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.umeng_feedback_dev_reply, parent, false);
        findView(itemView);
        return itemView;
    }

    protected void findView(View itemView) {
        messageText = (TextView) itemView.findViewById(R.id.dev_textMsg_textView);
        timeView = (TextView) itemView.findViewById(R.id.message_time_text);
    }

    protected void showTimeTag(Reply reply, Reply nextReply) {
        final int TIME_RANGE = 10 * 60;
        if (nextReply != null && nextReply.created_at - reply.created_at >= TIME_RANGE * 1000) {
            timeView.setVisibility(View.VISIBLE);
            Date replyTime = new Date(nextReply.created_at);
            timeView.setText(DateTimeUtilities.formatToAccurateTime(replyTime, mContext));
        } else {
            timeView.setVisibility(View.GONE);
        }
    }

}
