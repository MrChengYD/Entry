package inc.cyd.entry2.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

public class EntryUtil {
    public static Activity findActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            ContextWrapper wrapper = (ContextWrapper) context;
            return findActivity(wrapper.getBaseContext());
        } else {
            return null;
        }
    }
}
