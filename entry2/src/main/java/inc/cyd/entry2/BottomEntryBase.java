package inc.cyd.entry2;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

import inc.cyd.entry2.entity.EntryMenu;
import inc.cyd.entry2.entity.EntryMenuBase;
import inc.cyd.entry2.util.VirtualkeyUtils;

import static inc.cyd.entry2.util.EntryUtil.findActivity;

public abstract class BottomEntryBase extends LinearLayout implements EntryMenu.OnEntryMenuClickListener, EntryMenuBase.EntryMenuDialogListener {
    public Context context;
    private Handler handler = new Handler();
    public View rootView;
    public View entryView;
    public FrameLayout bottom_entry_dialog_frameLayout;
    public LinearLayout bottom_entry_menu_board;
    public LinearLayout bottom_entry_menus_layout;
    public HorizontalScrollView bottom_entry_menus_scrollView;
    public LinearLayout bottom_entry_menus_scrollView_layout;
    private List<EntryMenu> entryMenus;
    private EntryMenu bottom_entry_menu_switch;
    private EditText bottom_entry_edit;
    private EntryMenu bottom_entry_emoji;
    private LinearLayout bottom_entry_send;
    public LinearLayout bottom_entry_dashboard;


    //常量
    public int keyBoardHeight;
    public int screen_height;
    public int screen_width;
    public BottomEntryBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setAttribute(attrs);
        rootView = getRootView();


        //设置布局
        //布局文件
        entryView = LayoutInflater.from(context).inflate(R.layout.layout_bottom_entry, this , true);
        initBaseComponent();
        setBaseComponent();
        setEntryMenus();
        setEntryMenusListener();
        setBaseListener();
        initKeyboardHeight();
        initContact();

    }


    //常量 bottom_entry_menus_layout 的宽度
    protected int menus_layout_width;
    private void initContact(){
        bottom_entry_menus_layout.post(() ->{
            menus_layout_width = bottom_entry_menus_layout.getWidth();
            if(menus_layout_width <= 0 || menus_layout_width >= 650){
                menus_layout_width = 650;
                RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(bottom_entry_menus_layout.getLayoutParams());
                rl.width = menus_layout_width;
                bottom_entry_menus_layout.setLayoutParams(rl);
            }

        });

    }

    private void initBaseComponent(){
        bottom_entry_dialog_frameLayout = entryView.findViewById(R.id.bottom_entry_dialog_frameLayout);
        bottom_entry_menu_board = entryView.findViewById(R.id.bottom_entry_menu_board);
        bottom_entry_menus_layout = entryView.findViewById(R.id.bottom_entry_menus_layout);
        bottom_entry_menus_scrollView = entryView.findViewById(R.id.bottom_entry_menus_scrollView);
        bottom_entry_menus_scrollView_layout = entryView.findViewById(R.id.bottom_entry_menus_scrollView_layout);
        bottom_entry_menu_switch = entryView.findViewById(R.id.bottom_entry_menu_switch);
        bottom_entry_edit = entryView.findViewById(R.id.bottom_entry_edit);
        bottom_entry_emoji = entryView.findViewById(R.id.bottom_entry_emoji);
        bottom_entry_send = entryView.findViewById(R.id.bottom_entry_send);
        bottom_entry_dashboard = entryView.findViewById(R.id.bottom_entry_dashboard);
    }
    private void setBaseComponent(){
        bottom_entry_dialog_frameLayout.setVisibility(GONE);

        bottom_entry_menu_switch.initResource( R.drawable.icon_contract , R.drawable.icon_spread  , null , null);
        bottom_entry_emoji.initResource( R.drawable.icon_emoji , R.drawable.icon_emoji_un , null , null);
        bottom_entry_menu_switch.setOnEntryMenuClickListener(this);
    }

    private void initKeyboardHeight(){
        //获取虚拟键盘的高度
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() ->{
            Rect rect = new Rect();
            findActivity(context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            //获取屏幕的高度
            screen_height = findActivity(context).getWindow().getDecorView().getRootView().getHeight();

            int virtualHeight = VirtualkeyUtils.getNavigationBarHeight(findActivity(context));
            if(screen_height - rect.bottom - virtualHeight != 0){
                keyBoardHeight = screen_height - rect.bottom - virtualHeight;
                if(keyBoardHeight > 100){
                    setDashBoardLayoutParam();
                }

            }
        });

    }
    private void setDashBoardLayoutParam(){
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(bottom_entry_dashboard.getLayoutParams());
        fl.height = keyBoardHeight;
        fl.topMargin = bottom_entry_menu_board.getHeight();
        bottom_entry_dashboard.setLayoutParams(fl);
    }
    private void setEntryMenus(){
        entryMenus = getEntryMenus();
        if(null != entryMenus){
            entryMenus.forEach(item -> bottom_entry_menus_scrollView_layout.addView(item));
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    private void setEntryMenusListener(){
        rootView.setOnTouchListener(((view, motionEvent) -> bottom_entry_dashboard.dispatchTouchEvent(motionEvent)));

        if(null != entryMenus){

            entryMenus.forEach(item -> item.setOnEntryMenuClickListener(this));
        }
    }
    private void setBaseListener(){

    }

    private void dashBoardShow(View view){
        bottom_entry_dashboard.setVisibility(VISIBLE);
        bottom_entry_dashboard.removeAllViews();
        ViewGroup viewParent = (ViewGroup) view.getParent();
        if(null != viewParent){
            viewParent.removeView(view);
        }
        bottom_entry_dashboard.addView(view);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(view.getLayoutParams());
        ll.height = keyBoardHeight;
        view.setLayoutParams(ll);
    }
    private void setDialogView(View view){

        bottom_entry_dashboard.setVisibility(GONE);
        bottom_entry_dialog_frameLayout.removeAllViews();
        ViewGroup viewParent = (ViewGroup) view.getParent();
        if(null != viewParent){
            viewParent.removeView(view);
        }
        bottom_entry_dialog_frameLayout.addView(view);
    }

    @Override
    public void onEntryMenu(EntryMenu entryMenu) {
        if(bottom_entry_menu_switch == entryMenu){
            //切换
            toggleSpread();
        }else if(bottom_entry_emoji == entryMenu){
            //表情
        }else{
            if(null != entryMenu.getDialogView()){
                entryMenu.setEntryMenuDialogListener(this);
                setDialogView(entryMenu.getDialogView());
            }else if(null != entryMenu.getBottomSheetView()){
                dashBoardShow(entryMenu.getBottomSheetView());
            }else{
                bottom_entry_dialog_frameLayout.setVisibility(GONE);
                bottom_entry_dashboard.setVisibility(GONE);
            }
        }





        //entryMenus.forEach(item -> item.setMenuClickAble(false));
        //entryMenu.setMenuClickAble(true);
        //manageMenuClickable(entryMenu);


    }
    protected boolean spread = true;
    protected void toggleSpread(){
        new Thread(() -> {
            if(spread){
                //进行收缩
                handler.post(contractMenuRunnable);
            }else{
                handler.post(spreadMenuRunnable);
            }
        }).start();

    }

    protected AlphaAnimation alphaAnimation;
    protected static final long duration = 400L;
    Runnable contractMenuRunnable = this::contract;
    Runnable spreadMenuRunnable = this::spread;
    protected void contract(){
        bottom_entry_menu_switch.setMenuState(!spread);
        if(null == bottom_entry_menus_layout){
            return;
        }
        if(null != alphaAnimation){
            alphaAnimation.cancel();
        }
        alphaAnimation = new AlphaAnimation(1f, 0.0f);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        //收缩
        ValueAnimator valueAnimator = createValueAnimator(bottom_entry_menus_layout , menus_layout_width , 0);
        valueAnimator.setDuration(duration);
        valueAnimator.start();
        spread = false;
    }
    protected void spread(){
        bottom_entry_menu_switch.setMenuState(!spread);
        if(null == bottom_entry_menus_layout){
            return;
        }
        if(null != alphaAnimation){
            alphaAnimation.cancel();
        }
        alphaAnimation = new AlphaAnimation(0.0f, 1f);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        //收缩
        ValueAnimator valueAnimator = createValueAnimator(bottom_entry_menus_layout , 0 , menus_layout_width);
        valueAnimator.setDuration(duration);
        valueAnimator.start();
        spread = true;
    }
    protected ValueAnimator createValueAnimator(final View view , int x_start , int x_end){
        ValueAnimator valueAnimator = ValueAnimator.ofInt(x_start, x_end);
        valueAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = value;
            view.setLayoutParams(layoutParams);
            view.bringToFront();
        });
        return valueAnimator;
    }
    /**
     * 管理menu可点击的方法
     * 影响因素
     * 1 是否是第一个menu
     * 2 是否有 bottomSheetView 或者 dialogView 处于显示状态
     * 3 editText中是否有文字
     *
     *
     * **/
    private boolean dialogShow = false;
    private boolean bottomSheetShow = false;
    protected void manageMenuClickable(EntryMenu entryMenu){
        dialogShow = bottom_entry_dialog_frameLayout.getVisibility() == VISIBLE;
        bottomSheetShow = bottom_entry_dashboard.getVisibility() == VISIBLE;


    }
    protected void setMenusArrClickableButFirst(boolean clickable){

    }
    protected void setMenuArrClickableButFirstAndCustom(EntryMenu entryMenu , boolean clickable){

    }

    public abstract void setAttribute(AttributeSet attributeSet);

    public abstract List<EntryMenu> getEntryMenus();

    public View getRootView(){
         return findActivity(context).getWindow().getDecorView().findViewById(android.R.id.content);
    }



    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void show() {
        bottom_entry_dialog_frameLayout.setVisibility(VISIBLE);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void dismiss() {
        bottom_entry_dialog_frameLayout.setVisibility(GONE);
    }
}
