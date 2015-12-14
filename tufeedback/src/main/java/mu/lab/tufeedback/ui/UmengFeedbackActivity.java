package mu.lab.tufeedback.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.model.Reply;
import com.umeng.fb.model.UserInfo;

import java.util.HashMap;
import java.util.Map;

import mu.lab.tufeedback.R;
import mu.lab.tufeedback.common.FeedbackFactory;
import mu.lab.tufeedback.utils.SystemBarTintManager;
import mu.lab.tufeedback.widget.SwipeRefreshLayout;
import mu.lab.tufeedback.widget.UmengFeedbackAdapter;
import mu.lab.util.Log;

public class UmengFeedbackActivity extends AppCompatActivity {

    static final String LogTag = UmengFeedbackActivity.class.getName();
    Toolbar toolbar;
    ListView conversationList;
    SwipeRefreshLayout swipeRefreshLayout;
    Button sendMessageButton;
    ImageView sendPhotoImage;
    EditText inputBox;
    Spinner switchContactSpinner;
    EditText contactInputBox;
    Button saveContactButton;
    RelativeLayout contactLayout;

    private ArrayAdapter<CharSequence> contactAdapter = null;
    private Contact contact = null;

    private UmengFeedbackAdapter mFeedbackAdapter = null;
    private FeedbackAgent mFeedbackAgent = null;

    protected final void beforeSetViews() {
        UmengFeedbackAdapter.UmengViewCallback callback = new UmengFeedbackAdapter.UmengViewCallback() {
            @Override
            public final void onLoadOldDataSuccess(int dataNum) {
                swipeRefreshLayout.setRefreshing(false);
                if (dataNum >= 1) {
                    Toast.makeText(UmengFeedbackActivity.this, getString(R.string.refresh_success), Toast.LENGTH_SHORT).show();
                    conversationList.setSelection(dataNum - 1);
                } else {
                    Toast.makeText(UmengFeedbackActivity.this, getString(R.string.on_head), Toast.LENGTH_SHORT).show();
                    conversationList.setSelection(0);
                }
                conversationList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
            }
        };
        mFeedbackAdapter = new UmengFeedbackAdapter(this, callback);
    }

    protected final void findView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        conversationList = (ListView) findViewById(R.id.feedback_conversation_listView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.umeng_feedback_swipe_container);
        sendMessageButton = (Button) findViewById(R.id.send_message_button);
        sendPhotoImage = (ImageView) findViewById(R.id.send_photo_imageView);
        inputBox = (EditText) findViewById(R.id.inputBox_editText);
        switchContactSpinner = (Spinner) findViewById(R.id.switchContactSpinner);
        contactInputBox = (EditText) findViewById(R.id.contact_inputBox_editText);
        saveContactButton = (Button) findViewById(R.id.save_contact_button);
        contactLayout = (RelativeLayout) findViewById(R.id.contact_container);
    }

    protected final void setViews() {
        setSwipeLayout();
        setConversationListView();
        setInputBoxEditText();
        setContactInputBoxEditText();
        setSwitchContactSpinner();

        sendMessageButton.setEnabled(false);
        sendMessageButton.setTransformationMethod(null);
        sendMessageButton.setTextColor(getResources().getColor(R.color.TU_GRAY));
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        sendPhotoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFeedbackAdapter.sendPhotoToDev();
                conversationList.setSelection(mFeedbackAdapter.getCount());
            }
        });

        saveContactButton.setEnabled(false);
        saveContactButton.setTransformationMethod(null);
        saveContactButton.setTextColor(getResources().getColor(R.color.TU_GRAY));
        saveContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveContact();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFeedbackAdapter.getPhotoFromAlbum(requestCode, resultCode, data);
    }

    private SwipeRefreshLayout.OnRefreshListener topRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mFeedbackAdapter.loadOldData();
                    conversationList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
                }
            };

    private void setSwipeLayout() {
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.primary_blue,
                R.color.primary_deep_purple,
                R.color.primary_green,
                R.color.primary_brown,
                R.color.primary_red,
                R.color.primary_blue_gray,
                R.color.primary_dark_orange);
        swipeRefreshLayout.setOnRefreshListener(topRefreshListener);
    }

    private void setConversationListView() {
        conversationList.setDivider(null);
        conversationList.setAdapter(mFeedbackAdapter);
        conversationList.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    protected void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    protected final void setToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setTitleTextColor(getResources().getColor(R.color.WHITE));
        toolbar.setBackgroundColor(getResources().getColor(R.color.action_bar_color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.primary_black_transparent);
            SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
            toolbar.setPadding(0, config.getPixelInsetTop(false), 0, config.getPixelInsetBottom());
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }

    private void setInputBoxEditText() {
        inputBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                    return true;
                }
                return false;
            }
        });

        inputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isEmpty = s.toString().isEmpty();
                sendMessageButton.setEnabled(!isEmpty);
                sendMessageButton.setTextColor(isEmpty ? getResources().getColor(R.color.TU_GRAY) : getResources().getColor(R.color.WHITE));
            }
        });
    }

    private void setContactInputBoxEditText() {
        contactInputBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    saveContact();
                    return true;
                }
                return false;
            }
        });

        contactInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isEmpty = s.toString().isEmpty();
                saveContactButton.setEnabled(!isEmpty);
                saveContactButton.setTextColor(isEmpty ? getResources().getColor(R.color.TU_GRAY) : getResources().getColor(R.color.WHITE));
            }
        });
    }

    private enum Contact {
        Phone("phone_number"),
        Email("email"),
        QQ("qq"),
        WechatId("wechat_id");
        String str;

        Contact(String str) {
            this.str = str;
        }

        public String getStr() {
            return this.str;
        }
    }

    private void setSwitchContactSpinner() {
        contactAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.contact_options, R.layout.spinner_layout);
        contactAdapter.setDropDownViewResource(R.layout.setting_spinner_dropdown_item);
        switchContactSpinner.setAdapter(contactAdapter);
        switchContactSpinner.setOnItemSelectedListener(switchContactSpinnerListener);
    }

    private final AdapterView.OnItemSelectedListener switchContactSpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            contact = Contact.values()[pos];
            Log.d(LogTag, contact.getStr());
//            Toast.makeText(UmengFeedbackActivity.this, String.format(getString(R.string.contact_switch_to), contactAdapter.getItem(pos)), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private void saveContact() {
        String contactType = contact.getStr();
        if (contactType != null) {
            Log.d(LogTag, "contact update tasks start.");

            String contactInfo = contactInputBox.getText().toString();
            contactInputBox.getText().clear();
            Log.d(LogTag, String.format("contact type: %s, contact information: %s.", contactType, contactInfo));

            UserInfo userInfo = mFeedbackAgent.getUserInfo();
            if (userInfo == null) {
                userInfo = new UserInfo();
            }
            Map<String, String> contactStore = userInfo.getContact();
            if (contactStore == null) {
                contactStore = new HashMap<>();
            }
            String currentContact = contactStore.get(contactType);

            //update feedback contactInfo
            if (currentContact == null || !currentContact.equals(contactInfo)) {
                contactStore.put(contactType, contactInfo);
                userInfo.setContact(contactStore);
                mFeedbackAgent.setUserInfo(userInfo);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mFeedbackAgent.updateUserInfo();
                    }
                }).start();
            }
        }
        contactLayout.setVisibility(View.GONE);
        Toast.makeText(UmengFeedbackActivity.this, getString(R.string.update_contact), Toast.LENGTH_SHORT).show();
    }

    private void sendMessage() {
        String replyMessage = inputBox.getText().toString();
        inputBox.getText().clear();
        if (!TextUtils.isEmpty(replyMessage)) {
            mFeedbackAdapter.sendMsgToDev(replyMessage, Reply.CONTENT_TYPE_TEXT_REPLY);
            conversationList.setSelection(mFeedbackAdapter.getCount());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umeng_feedback);
        findView();
        mFeedbackAgent = FeedbackFactory.getAgent();

        setToolbar();
        beforeSetViews();
        setViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFeedbackAdapter.syncToUmeng();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFeedbackAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_umeng_feedback, menu);
        return true;
    }

    @Override
    public void finish() {
        hideInputPad();
        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            mFeedbackAdapter.syncToUmeng();
            Toast.makeText(UmengFeedbackActivity.this, getString(R.string.synchronization_success), Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_set_contact) {
            if (contactLayout.getVisibility() == View.GONE) {
                contactLayout.setVisibility(View.VISIBLE);
            } else {
                contactLayout.setVisibility(View.GONE);
            }
        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void hideInputPad() {
        InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(inputBox.getWindowToken(), 0);
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            final View currentFocus = getCurrentFocus();
            if (isShouldHideInput(currentFocus, event)) {
                hideInputPad();
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText || v instanceof Button)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            return !(event.getRawX() > left && event.getRawX() < right
                    && event.getRawY() > top && event.getRawY() < bottom);
        }
        return false;
    }
}
