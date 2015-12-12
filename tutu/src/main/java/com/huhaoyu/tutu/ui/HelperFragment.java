package com.huhaoyu.tutu.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huhaoyu.tutu.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Helper fragment
 * Created by coderhuhy on 15/12/11.
 */
public class HelperFragment extends DialogFragment implements View.OnClickListener {

    private static final String LogTag = HelperFragment.class.getCanonicalName();


    @Bind(R.id.confirm_button)
    Button confirmButton;
    @Bind(R.id.faq_container)
    LinearLayout faqContainer;

    private Context context;
    private String[] questions;
    private String[] answers;
    private List<View> views = new ArrayList<>();

    public static HelperFragment newInstance(Context context) {
        HelperFragment fragment = new HelperFragment();
        fragment.context = context;
        return fragment;
    }

    public void show() {
        show(((Activity) context).getFragmentManager(), LogTag);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_helper, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Resources res = getActivity().getResources();
        questions = res.getStringArray(R.array.tutu_helper_questions);
        answers = res.getStringArray(R.array.tutu_helper_answers);

        faqContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(context);
        for (int i = 0; i != questions.length; ++i) {
            View v = inflater.inflate(R.layout.helper_faq_item, faqContainer, false);
            TextView questionTv = (TextView) v.findViewById(R.id.question_tv);
            TextView answerTv = (TextView) v.findViewById(R.id.answer_tv);
            questionTv.setText(questions[i]);
            answerTv.setText(answers[i]);
            faqContainer.addView(v);
        }

        confirmButton.setOnClickListener(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_button:
                dismiss();
                break;
        }
    }
}
