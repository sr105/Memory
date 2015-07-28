package org.tbadg.memory;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SliderPreference extends Preference implements OnSeekBarChangeListener {
//    private final String TAG = "SliderPreference";

    private final static int MAX_SLIDER_VALUE = 100;
    public final static int DEFAULT_VALUE = 50;

    private int value = DEFAULT_VALUE;


    public SliderPreference(Context context) {
        super(context);
        setWidgetLayoutResource(R.layout.slider_preference);
    }

    public SliderPreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.preferenceStyle);
        setWidgetLayoutResource(R.layout.slider_preference);
    }

    public SliderPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWidgetLayoutResource(R.layout.slider_preference);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        SeekBar sb = (SeekBar) view.findViewById(R.id.slider);
        sb.setMax(MAX_SLIDER_VALUE);
        sb.setProgress(value);
        sb.setOnSeekBarChangeListener(this);
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            value = progress;
            persistInt(value);
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }


    @Override
    protected Object onGetDefaultValue(TypedArray ta, int index) {
        int def = ta.getInt(index, DEFAULT_VALUE);
        if (def < 0)
            def = 0;
        if (def > MAX_SLIDER_VALUE)
            def = MAX_SLIDER_VALUE;

        return def;
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        value = (defaultValue != null) ? (Integer) defaultValue : DEFAULT_VALUE;
        if (!restoreValue) {
            persistInt(value);
        } else {
            value = getPersistedInt(value);
        }
    }
}
