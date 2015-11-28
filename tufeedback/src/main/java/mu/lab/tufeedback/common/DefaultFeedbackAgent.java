package mu.lab.tufeedback.common;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.SyncListener;
import com.umeng.fb.model.Reply;

import java.util.List;
import java.util.Locale;

import mu.lab.tufeedback.R;
import mu.lab.util.Log;

/**
 * feedback agent
 * Created by coderhuhy on 15/9/26.
 */
public class DefaultFeedbackAgent extends FeedbackAgent {

    private final String LogTag = this.getClass().getCanonicalName();

    Context mContext = null;

    public DefaultFeedbackAgent(Context context) {
        super(context);
        this.mContext = context;
    }

    /**
     * Use this sync function when init in main activity
     * @param activity UmengFeedbackActivity.class or other custom activity
     */
    public void sync(Class activity) {
        SyncListener listener = new DefaultSyncListener(activity);
        getDefaultConversation().sync(listener);
    }

    public void showReplyNotification(List<Reply> list, Class activity) {
        String content, format;
        if(list.size() == 1) {
            format = mContext.getString(R.string.umeng_fb_notification_content_formatter_single_msg);
            content = String.format(Locale.US, format, list.get(0).content);
        } else {
            format = mContext.getString(R.string.umeng_fb_notification_content_formatter_multiple_msg);
            content = String.format(Locale.US, format, list.size());
        }

        try {
            NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            String ticker = mContext.getString(R.string.umeng_fb_notification_ticker_text);
            Intent intent = new Intent(mContext, activity);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            int time = (int) SystemClock.uptimeMillis();
            PendingIntent pendingIntent = PendingIntent
                .getActivity(mContext, time, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            int iconId = mContext.getPackageManager()
                .getPackageInfo(mContext.getPackageName(), 0).applicationInfo.icon;
            NotificationCompat.Builder builder = (new NotificationCompat.Builder(mContext))
                .setSmallIcon(iconId)
                .setContentTitle(ticker)
                .setTicker(ticker)
                .setContentText(content)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
            manager.notify(0, builder.build());
        } catch (Exception error) {
            Log.e(LogTag, error.getMessage(), error);
        }
    }

    protected class DefaultSyncListener implements SyncListener {

        private Class activity = null;

        public DefaultSyncListener(Class activity) {
            this.activity = activity;
        }

        @Override
        public void onReceiveDevReply(List<Reply> list) {
            if (list != null && list.size() >= 1) {
                showReplyNotification(list ,this.activity);
            }
        }

        @Override
        public void onSendUserReply(List<Reply> list) {

        }
    }

}
