package com.huhaoyu.tutu.widget;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.huhaoyu.tutu.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Warning Fragment
 * Created by coderhuhy on 15/12/11.
 */
public class TemplateFragment extends DialogFragment implements View.OnClickListener {

    @Bind(R.id.title)
    TextView titleTv;
    @Bind(R.id.content)
    TextView contentTv;
    @Bind(R.id.left_button)
    Button leftButton;
    @Bind(R.id.right_button)
    Button rightButton;

    private TutuFragmentBuilder builder;

    public static TemplateFragment newInstance(TutuFragmentBuilder builder) {
        TemplateFragment fragment = new TemplateFragment();
        fragment.builder = builder;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_template, container);
        ButterKnife.bind(this, view);

        Resources res = getActivity().getResources();
        titleTv.setText(builder.title);
        titleTv.setBackground(res.getDrawable(builder.titleBackground));
        contentTv.setText(builder.content);
        if (builder.hasLeftButton()) {
            leftButton.setVisibility(View.VISIBLE);
            leftButton.setText(builder.leftButtonName);
            leftButton.setOnClickListener(this);
        } else {
            leftButton.setVisibility(View.GONE);
        }
        if (builder.hasRightButton()) {
            rightButton.setVisibility(View.VISIBLE);
            rightButton.setText(builder.rightButtonName);
            rightButton.setOnClickListener(this);
            if (builder.rightButtonBackground != -1) {
                rightButton.setBackground(res.getDrawable(builder.rightButtonBackground));
            }
        } else {
            rightButton.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public static class TutuFragmentBuilder {

        TemplateFragment fragment;
        Context context;

        ButtonClickCallback callback;
        String title;
        String content;
        String leftButtonName;
        String rightButtonName;
        int titleBackground = -1;
        int rightButtonBackground = -1;

        boolean hasLeftButton() {
            return !TextUtils.isEmpty(leftButtonName);
        }

        boolean hasRightButton() {
            return !TextUtils.isEmpty(rightButtonName);
        }

        public TutuFragmentBuilder(Context context) {
            this.context = context;
            fragment = TemplateFragment.newInstance(this);
        }

        public TutuFragmentBuilder title(int id) {
            return title(context.getString(id));
        }

        public TutuFragmentBuilder title(String title) {
            this.title = title;
            return this;
        }

        public TutuFragmentBuilder titleBackground(int drawableId) {
            this.titleBackground = drawableId;
            return this;
        }

        public TutuFragmentBuilder content(int id) {
            return content(context.getString(id));
        }

        public TutuFragmentBuilder content(String content) {
            this.content = content;
            return this;
        }

        public TutuFragmentBuilder leftButton(int id) {
            return leftButton(context.getString(id));
        }

        public TutuFragmentBuilder leftButton(String name) {
            this.leftButtonName = name;
            return this;
        }

        public TutuFragmentBuilder rightButton(int id) {
            return rightButton(context.getString(id));
        }

        public TutuFragmentBuilder rightButton(String name) {
            this.rightButtonName = name;
            return this;
        }

        public TutuFragmentBuilder rightButtonBackground(int drawableId) {
            this.rightButtonBackground = drawableId;
            return this;
        }

        public TutuFragmentBuilder callback(ButtonClickCallback callback) {
            this.callback = callback;
            return this;
        }

        public void show() {
            fragment.show(((Activity) context).getFragmentManager(), this.title);
        }
    }

    void onLeftClicked(View view) {
        if (builder.callback != null) {
            builder.callback.onLeftButtonClicked(view);
        }
        dismiss();
    }

    void onRightClicked(View view) {
        if (builder.callback != null) {
            builder.callback.onRightButtonClicked(view);
        }
        dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_button:
                onLeftClicked(v);
                break;
            case R.id.right_button:
                onRightClicked(v);
                break;
        }
    }

    public interface ButtonClickCallback {

        void onLeftButtonClicked(View view);

        void onRightButtonClicked(View view);

    }
}
