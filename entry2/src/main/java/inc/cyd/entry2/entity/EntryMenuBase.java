package inc.cyd.entry2.entity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import inc.cyd.entry2.R;
import inc.cyd.entry2.interfaces.EntryDashBoardPullListener;
import inc.cyd.entry2.interfaces.EntryDialogListener;
import inc.cyd.entry2.interfaces.EntryMenuClickListener;
import inc.cyd.entry2.interfaces.EntryMenuTouchListener;

public abstract class EntryMenuBase extends LinearLayout{

    private Context context;
    private View menuView;
    public LinearLayout clickAbleView;
    private ImageView menuImageView;

    protected int turnOn_Resource;
    protected int turnOff_Resource;
    protected View dialogView = null;
    protected View bottomSheetView = null;

    //接口
    //按钮点击接口
    public EntryMenuClickListener entryMenuClickListener = null;
    protected EntryMenuTouchListener entryMenuTouchListener = null;
    //dialog接口
    private EntryDialogListener entryDialogListener = null;
    //面板移动接口
    private EntryDashBoardPullListener entryDashBoardPullListener = null;

    public EntryMenuBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        menuView =  LayoutInflater.from(context).inflate(R.layout.peace_menu_layout , this , true);
        initMenu();
        setMenuClickEvent();
        setMenuTouchEvent();
    }
    private final void initMenu(){
        initMenuBaseView();
        customMenuResource(R.drawable.icon_menu_def , R.drawable.icon_menu_def_un);
        customMenuFunView(null , null);
    }
    private final void initMenuBaseView(){
        menuImageView = menuView.findViewById(R.id.entry_menu);
        clickAbleView = menuView.findViewById(R.id.clickAbleView);
    }
    public abstract void customMenuResource(int turnOn_Resource , int turnOff_Resource);
    public abstract void customMenuFunView(View dialogView , View bottomSheetView);




    //设置 按钮点击监听
    public void setEntryMenuClickListener(EntryMenuClickListener entryMenuClickListener){
        this.entryMenuClickListener = entryMenuClickListener;
    }

    public void setEntryMenuTouchListener(EntryMenuTouchListener entryMenuTouchListener) {
        this.entryMenuTouchListener = entryMenuTouchListener;
    }

    //设置 dialog监听
    public void setEntryDialogListener(EntryDialogListener entryDialogListener){
        this.entryDialogListener = entryDialogListener;
    }
    public void setEntryDashBoardPullListener(EntryDashBoardPullListener entryDashBoardPullListener) {
        this.entryDashBoardPullListener = entryDashBoardPullListener;
    }

    public EntryDashBoardPullListener getEntryDashBoardPullListener() {
        return entryDashBoardPullListener;
    }

    public void entryDialogShow(){
        if(null != this.entryDialogListener){
            this.entryDialogListener.entryDialogShow();
        }
    }
    public void entryDialogDismiss(){
        if(null != this.entryDialogListener){
            this.entryDialogListener.entryDialogDisMiss();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setMenuTouchEvent(){
        if(null != entryMenuTouchListener){
            clickAbleView.setOnTouchListener(((view, motionEvent) -> {
                clickAbleView.getParent().requestDisallowInterceptTouchEvent(true);
                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN: {
                        entryMenuTouchListener.start();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        entryMenuTouchListener.end();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        if (!clickAbleView.isClickable()) {
                            break;
                        }
                        entryMenuTouchListener.move(motionEvent.getX() , motionEvent.getY());
                        break;
                    }
                }

                return false;
            }));
        }
    }
    protected abstract void setMenuClickEvent();
    //获取可以设置监听的view
    public LinearLayout getClickAbleView() {
        return clickAbleView;
    }

    public View getDialogView() {
        return dialogView;
    }

    public View getBottomSheetView() {
        return bottomSheetView;
    }
    /** 对外的方法 **/


    public void switchState(boolean state){
        menuImageView.setImageResource(state ? turnOn_Resource : turnOff_Resource);
    }






    public void setMenuClickAble(boolean clickAble){
        clickAbleView.setClickable(clickAble);
        switchState(clickAble);
    }

}
