package com.woolgather.lib.kernel.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.rey.material.widget.ImageButton;

/**
 * Created by poovarasanv on 24/8/17.
 *
 * @author poovarasanv
 * @project MySaloon
 * @on 24/8/17 at 9:49 AM
 */

public class RippleIconButton extends ImageButton {
    public RippleIconButton(Context context) {
        super(context);
    }

    public RippleIconButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RippleIconButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageIcon(IIcon icon,int size,int color) {
        this.setImageDrawable(new IconicsDrawable(getContext(),icon).sizeDp(size).color(color));
    }
}
