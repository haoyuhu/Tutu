package mu.lab.tufeedback.common;

import android.content.Context;

import com.umeng.fb.model.Conversation;

/**
 * feedback agent factory
 * Created by coderhuhy on 15/9/26.
 */
public class FeedbackFactory {

    protected static DefaultFeedbackAgent mAgent = null;

    /**
     * Create default feedback agent
     * @param context Main context
     */
    public static void init(Context context) {
        mAgent = new DefaultFeedbackAgent(context);
    }

    /**
     * @return Return default feedback agent
     */
    public static DefaultFeedbackAgent getAgent() {
        return mAgent;
    }

    /**
     * @return Return umeng feedback conversation
     */
    public static Conversation getConversation() {
        return mAgent.getDefaultConversation();
    }

}
