package inc.cyd.entry2.entity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import inc.cyd.entry2.R;

public abstract class EntryMenuBase extends LinearLayout {

    private Context context;
    public View menuView;
    public LinearLayout entry_menu_layout;
    private ImageView menuImageView;

    private int menu_icon_clickable;
    private int menu_icon_unClickable;
    private View dialogView = null;
    private View bottomSheetView = null;
    private boolean show_pull_bar;
    private EntryMenuDialogListener entryMenuDialogListener;
    public EntryMenuBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        menuView =  LayoutInflater.from(context).inflate(R.layout.peace_menu_layout , this , true);
        initMenu();
        setClickListener();
    }
    public void initMenu(){
        menuImageView = menuView.findViewById(R.id.entry_menu);
        entry_menu_layout = menuView.findViewById(R.id.entry_menu_layout);
        //默认值
        menu_icon_clickable = R.drawable.icon_menu_def;
        menu_icon_unClickable = R.drawable.icon_menu_def_un;

    }
    public void initResource(int menu_icon_clickable , int menu_icon_unClickable , View dialogView  , boolean show_pull_bar , View bottomSheetView){
        this.menu_icon_clickable = menu_icon_clickable;
        this.menu_icon_unClickable = menu_icon_unClickable;
        this.dialogView = dialogView;
        this.bottomSheetView = bottomSheetView;
        this.show_pull_bar = show_pull_bar;
        //设置默认显示的值
        menuImageView.setImageResource(menu_icon_clickable);
    }

    public void setEntryMenuDialogListener(EntryMenuDialogListener entryMenuDialogListener){
        this.entryMenuDialogListener = entryMenuDialogListener;
    }

    public void entryDialogShow(){
        if(null != this.entryMenuDialogListener){
            this.entryMenuDialogListener.show();
        }
    }
    public void entryDialogDismiss(){
        if(null != this.entryMenuDialogListener){
            this.entryMenuDialogListener.dismiss();
        }
    }

    public interface EntryMenuDialogListener {
        void show();
        void dismiss();
    }
    //获取可以设置监听的view
    public View getSetListenerView(){
        return entry_menu_layout;
    }
    public View getDialogView() {
        return dialogView;
    }

    public View getBottomSheetView() {
        return bottomSheetView;
    }

    public boolean isShowPullBar(){return show_pull_bar;}

    public void setMenuState(boolean state){
        menuImageView.setImageResource(state ? menu_icon_clickable : menu_icon_unClickable);
    }

    public abstract void setClickListener();


    /** 对外的方法 **/

    public void setMenuClickAble(boolean clickAble){
        entry_menu_layout.setClickable(clickAble);
        setMenuState(clickAble);
    }

}
