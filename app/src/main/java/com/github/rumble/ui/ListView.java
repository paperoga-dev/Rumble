package com.github.rumble.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

public class ListView extends android.widget.ListView {
    public interface OnUpdateListener {
        void onUpdate();
    }

    private OnUpdateListener onUpdateListener;

    public ListView(Context context) {
        super(context);

        init(context);
    }

    public ListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public ListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    public ListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context);
    }

    private void init(Context context) {
        this.onUpdateListener = null;

        setDivider(new ColorDrawable(Color.RED));
        setDividerHeight(4);

        ProgressBar pb = new ProgressBar(context);
        pb.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (onUpdateListener != null)
                    onUpdateListener.onUpdate();
            }
        });

        addFooterView(pb);
    }

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }
}
