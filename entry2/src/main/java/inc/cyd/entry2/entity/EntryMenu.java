package inc.cyd.entry2.entity;

import android.content.Context;
import android.util.AttributeSet;


public class EntryMenu extends EntryMenuBase{


    public OnEntryMenuClickListener onEntryMenuClickListener;
    public EntryMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setClickListener() {
        entry_menu_layout.setOnClickListener(v -> {
            if(null != onEntryMenuClickListener){
                onEntryMenuClickListener.onEntryMenu(this);
            }
        });
    }


    public void setOnEntryMenuClickListener(OnEntryMenuClickListener onEntryMenuClickListener){
        this.onEntryMenuClickListener = onEntryMenuClickListener;
    }








    public interface OnEntryMenuClickListener{
        void onEntryMenu(EntryMenu entryMenu);
    }


}
