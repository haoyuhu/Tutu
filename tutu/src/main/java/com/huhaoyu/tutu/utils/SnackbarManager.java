package com.huhaoyu.tutu.utils;

import android.animation.ObjectAnimator;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

/**
 * Snackbar manager
 * Created by coderhuhy on 15/11/25.
 */
public class SnackbarManager {

    public enum Duration {
        Long,
        Short,
        Indefinite,
        Default
    }

    protected int contentId = -1;
    protected String content;
    protected View view;
    protected boolean isFab = false;
    protected int duration = Snackbar.LENGTH_SHORT;
    protected int actionId = -1;
    protected String actionName;
    protected View.OnClickListener listener;
    protected Snackbar.Callback callback;

    public SnackbarManager(View view) {
        this.view = view;
        this.isFab = view instanceof FloatingActionButton || view instanceof FloatingActionsMenu;
    }

    public SnackbarManager setContent(String content) {
        this.content = content;
        return this;
    }

    public SnackbarManager setContent(int contentId) {
        this.contentId = contentId;
        return this;
    }

    public SnackbarManager setDuration(Duration duration) {
        switch (duration) {
            case Long:
                this.duration = Snackbar.LENGTH_LONG;
                break;
            case Short:
                this.duration = Snackbar.LENGTH_SHORT;
                break;
            case Indefinite:
                this.duration = Snackbar.LENGTH_INDEFINITE;
                break;
            default:
                this.duration = Snackbar.LENGTH_SHORT;
        }
        return this;
    }

    public SnackbarManager setAction(String actionName, View.OnClickListener listener) {
        this.actionName = actionName;
        this.listener = listener;
        return this;
    }

    public SnackbarManager setAction(int actionId, View.OnClickListener listener) {
        this.actionId = actionId;
        this.listener = listener;
        return this;
    }

    public SnackbarManager setCallback(Snackbar.Callback callback) {
        this.callback = callback;
        return this;
    }

    public void show() throws SnackbarException {
        if (TextUtils.isEmpty(content) && contentId == -1) {
            throw new SnackbarException("snackbar no content error...");
        }
        Snackbar snackbar = contentId != -1 ?
                Snackbar.make(view, contentId, duration) : Snackbar.make(view, content, duration);
        if (listener != null) {
            if (!TextUtils.isEmpty(actionName)) {
                snackbar.setAction(actionName, listener);
            } else {
                snackbar.setAction(actionId, listener);
            }
        }
        if (callback != null) {
            snackbar.setCallback(callback);
        }
//        if (isFab) {
//            snackbar.setCallback(new Snackbar.Callback() {
//                @Override
//                public void onDismissed(Snackbar snackbar, int event) {
//                    super.onDismissed(snackbar, event);
//                    fabDown(view);
//                }
//            });
//            fabPopup(view);
//        }
        snackbar.show();
    }

    @Deprecated
    private void fabPopup(View fab) {
        final int POPUP_ANIMATION_DURATION = 250;
        final int translate = -80;
        if (isFab) {
            final ObjectAnimator popup = ObjectAnimator.ofFloat(fab, "translationY", 0, translate);
            popup.setDuration(POPUP_ANIMATION_DURATION).setInterpolator(new DecelerateInterpolator());
            popup.start();
        }
    }

    @Deprecated
    private void fabDown(View fab) {
        final int DOWN_ANIMATION_DURATION = 150;
        final int translate = -80;
        if (isFab) {
            final ObjectAnimator down = ObjectAnimator.ofFloat(fab, "translationY", translate, 0);
            down.setDuration(DOWN_ANIMATION_DURATION).setInterpolator(new AccelerateDecelerateInterpolator());
            down.start();
        }
    }

    public static class SnackbarException extends RuntimeException {

        private String details;

        public SnackbarException(String detailMessage) {
            super(detailMessage);
            this.details = detailMessage;
        }

        public String getDetails() {
            return this.details;
        }

    }

}
