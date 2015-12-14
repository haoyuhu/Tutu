package mu.lab.tufeedback.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.umeng.fb.SyncListener;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Reply;

import java.util.List;
import java.util.UUID;

import mu.lab.tufeedback.common.FeedbackFactory;
import mu.lab.tufeedback.utils.UmengImageUtils;
import mu.lab.util.Log;

/**
 * umeng feedback adapter
 * Created by coderhuhy on 15/9/24.
 */
public class UmengFeedbackAdapter extends BaseAdapter {

    private final String LogTag = getClass().getCanonicalName();

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_USER = 0;
    private static final int VIEW_TYPE_DEV = 1;
    private static final int REQUEST_CODE = 1;
    private static final int mLoadDataNum = 10; // default
    private int mCurrentMsgCount = 10;

    private Context mContext;
    private UmengViewCallback mCallback;
    private Conversation mConversation;
    private Handler mImageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            sendMsgToDev((String) msg.obj, Reply.CONTENT_TYPE_IMAGE_REPLY);
        }
    };
    private Conversation.OnChangeListener mListener = new Conversation.OnChangeListener() {
        @Override
        public void onChange() {
            notifyDataSetChanged();
        }
    };

    public UmengFeedbackAdapter(Context context, UmengViewCallback callback) {
        mContext = context;
        mCallback = callback;
        mConversation = FeedbackFactory.getConversation();
        mConversation.setOnChangeListener(mListener);
    }

    @Override
    public int getCount() {
        int totalCount = mConversation.getReplyList().size();
        if (totalCount < mCurrentMsgCount || totalCount >= mCurrentMsgCount && totalCount <= mLoadDataNum) {
            mCurrentMsgCount = totalCount;
        }
        return mCurrentMsgCount;
    }

    private int getCurrentPosition(int position) {
        int totalCount = mConversation.getReplyList().size();
        if (totalCount < mCurrentMsgCount) {
            mCurrentMsgCount = totalCount;
        }
        return totalCount - mCurrentMsgCount + position;
    }

    @Override
    public Object getItem(int position) {
        position = getCurrentPosition(position);
        return mConversation.getReplyList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return getCurrentPosition(position);
    }


    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        position = getCurrentPosition(position);
        Reply reply = mConversation.getReplyList().get(position);
        switch (reply.type) {
            case Reply.TYPE_DEV_REPLY:
                return VIEW_TYPE_DEV;
            case Reply.TYPE_USER_REPLY:
                return VIEW_TYPE_USER;
            default:
                return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        position = getCurrentPosition(position);
        Reply reply = mConversation.getReplyList().get(position);
        Reply nextReply = null;
        if ((position + 1) < mConversation.getReplyList().size()) {
            nextReply = mConversation.getReplyList().get(position + 1);
        }
        AbstractMessageHolder holder = null;

        if (convertView == null) {
            switch (reply.type) {
                case Reply.TYPE_DEV_REPLY:
                    holder = new DevMessageHolder(mContext);
                    break;
                case Reply.TYPE_NEW_FEEDBACK:
                case Reply.TYPE_USER_REPLY:
                    holder = new UserMessageHolder(mContext);
                    break;
                default:
                    holder = new UserMessageHolder(mContext);
            }
            convertView = holder.createView(parent);
            convertView.setTag(holder);
        } else {
            holder = (AbstractMessageHolder) convertView.getTag();
        }

        holder.showData(reply, nextReply);

        return convertView;
    }

    public void loadOldData() {
        int loadDataNum = mLoadDataNum;
        int totalCount = mConversation.getReplyList().size();
        if (loadDataNum + mCurrentMsgCount >= totalCount) {
            loadDataNum = totalCount - mCurrentMsgCount;
        }
        mCurrentMsgCount += loadDataNum;
        notifyDataSetChanged();
        mCallback.onLoadOldDataSuccess(loadDataNum);
    }

    public void sendPhotoToDev() {
        Intent intent = new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE);
    }

    public void getPhotoFromAlbum(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1 && requestCode == REQUEST_CODE && data != null) {
            Log.e(LogTag, "data.getDataString -- " + data.getDataString());
            if (UmengImageUtils.isImage(mContext, data.getData())) {
                UmengImageUtils.saveReplyImage(mContext, data.getData(), "R" + UUID.randomUUID().toString(), mImageHandler);
            }
        }
    }

    public void sendMsgToDev(String replyMsg, String type) {
        if (type.equals(Reply.CONTENT_TYPE_TEXT_REPLY)) {
            mConversation.addUserReply(replyMsg);
        } else if (type.equals(Reply.CONTENT_TYPE_IMAGE_REPLY)) {
            mConversation.addUserReply("", replyMsg, "image_reply", -1.0F);
        } else if (type.equals(Reply.CONTENT_TYPE_AUDIO_REPLY)) {
            // TODO: 15/10/4 audio reply
        }
        mCurrentMsgCount++;
        syncToUmeng();
    }

    public void syncToUmeng() {
        mConversation.sync(new SyncListener() {

            @Override
            public void onSendUserReply(List<Reply> replyList) {
                Log.d(LogTag, "onSendUserReply");
                if (replyList == null || replyList.size() < 1) {
                    Log.d(LogTag, "No user reply");
                } else {
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onReceiveDevReply(List<Reply> replyList) {
                Log.d(LogTag, "onReceiveDevReply");
                if (replyList == null || replyList.size() < 1) {
                    Log.d(LogTag, "No dev reply");
                } else {
                    notifyDataSetChanged();
                }
            }
        });
    }

    public void clear() {
        mCallback = null;
        mContext = null;
    }

    public interface UmengViewCallback {

        void onLoadOldDataSuccess(int dataNum);

    }

}
