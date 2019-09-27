package inc.cyd.entry2.util;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

public class AnimateUtil {
    public static ValueAnimator createValueAnimator_X(final View view , int x_start , int x_end){
        ValueAnimator valueAnimator = ValueAnimator.ofInt(x_start, x_end);
        valueAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = value;
            view.setLayoutParams(layoutParams);
        });
        return valueAnimator;
    }
    public static ValueAnimator createValueAnimator_Y(final View view , int y_start , int y_end){
        ValueAnimator valueAnimator = ValueAnimator.ofInt(y_start, y_end);
        valueAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = value;
            view.setLayoutParams(layoutParams);
        });
        return valueAnimator;
    }
}
