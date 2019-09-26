package inc.cyd.entry2.entity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;


public class EntryMenu extends EntryMenuBase{

    public EntryMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void customMenuResource(int turnOn_Resource, int turnOff_Resource) {
        this.turnOn_Resource = turnOn_Resource;
        this.turnOff_Resource = turnOff_Resource;
        switchState(true);
    }

    @Override
    public void customMenuFunView(View dialogView, View bottomSheetView) {
        this.dialogView = dialogView;
        this.bottomSheetView = bottomSheetView;
    }

    @Override
    protected void setMenuClickEvent() {
        clickAbleView.setOnClickListener(view -> {
            if(null != entryMenuClickListener){
                entryMenuClickListener.onEntryMenuClick(this);
            }
        });
    }


}
