package mu.lab.tufeedback.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.umeng.fb.model.Reply;

import java.util.Date;

import mu.lab.tufeedback.R;
import mu.lab.tufeedback.utils.DateTimeUtilities;
import mu.lab.tufeedback.utils.LoadImageThread;
import mu.lab.tufeedback.utils.UmengImageUtils;

/**
 * user message view holder
 * Created by coderhuhy on 15/9/30.
 */
public class UserMessageHolder extends AbstractMessageHolder {

    TextView messageText;
    ImageView photoMessageImage;
    ImageView errorImage;
    ImageView photoErrorImage;
    ProgressBar sendingMessageProgress;
    ProgressBar sendingPhotoProgress;
    TextView timeView;

    public UserMessageHolder(Context context) {
        super(context);
    }

    protected void findView(View itemView) {
        messageText = (TextView) itemView.findViewById(R.id.user_textMsg_textView);
        photoMessageImage = (ImageView) itemView.findViewById(R.id.photo_imageView);
        errorImage = (ImageView) itemView.findViewById(R.id.msg_error_imageView);
        photoErrorImage = (ImageView) itemView.findViewById(R.id.photo_msg_error_imageView);
        sendingMessageProgress = (ProgressBar) itemView.findViewById(R.id.msg_progressBar);
        sendingPhotoProgress = (ProgressBar) itemView.findViewById(R.id.photo_msg_progressBar);
        timeView = (TextView) itemView.findViewById(R.id.message_time_text);
    }

    @Override
    public View createView(ViewGroup parent) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.umeng_feedback_user_reply, parent, false);
        findView(itemView);
        return itemView;
    }

    @Override
    public void showData(Reply reply, Reply nextReply) {

        switch (reply.content_type) {
            case Reply.CONTENT_TYPE_TEXT_REPLY:
                photoMessageImage.setVisibility(View.GONE);
                messageText.setVisibility(View.VISIBLE);
                messageText.setText(reply.content);
                break;
            case Reply.CONTENT_TYPE_IMAGE_REPLY:
                photoMessageImage.setVisibility(View.VISIBLE);
                messageText.setVisibility(View.GONE);
                String path = UmengImageUtils.getImagePathWithName(mContext, reply.reply_id);
                int size = UmengImageUtils.getPhotoSize(mContext);
                new LoadImageThread(path, photoMessageImage, size).run();
                break;
            case Reply.CONTENT_TYPE_AUDIO_REPLY:
                break;
        }

        switch (reply.status) {
            case Reply.STATUS_NOT_SENT:
                if (messageText.getVisibility() == View.GONE) {
                    photoErrorImage.setVisibility(View.VISIBLE);
                    errorImage.setVisibility(View.GONE);
                } else {
                    photoErrorImage.setVisibility(View.GONE);
                    errorImage.setVisibility(View.VISIBLE);
                }
                sendingMessageProgress.setVisibility(View.GONE);
                sendingPhotoProgress.setVisibility(View.GONE);
                break;
            case Reply.STATUS_SENDING:

            case Reply.STATUS_WILL_SENT:
                errorImage.setVisibility(View.GONE);
                photoErrorImage.setVisibility(View.GONE);
                if (messageText.getVisibility() == View.GONE) {
                    sendingMessageProgress.setVisibility(View.GONE);
                    sendingPhotoProgress.setVisibility(View.VISIBLE);
                } else {
                    sendingMessageProgress.setVisibility(View.VISIBLE);
                    sendingPhotoProgress.setVisibility(View.GONE);
                }
                break;
            case Reply.STATUS_SENT:
                errorImage.setVisibility(View.GONE);
                photoErrorImage.setVisibility(View.GONE);
                sendingMessageProgress.setVisibility(View.GONE);
                sendingPhotoProgress.setVisibility(View.GONE);
                break;
            default:
        }

        showTimeTag(reply, nextReply);

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
