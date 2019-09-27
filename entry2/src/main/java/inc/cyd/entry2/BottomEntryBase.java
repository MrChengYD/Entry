package inc.cyd.entry2;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;
import java.util.Objects;

import inc.cyd.entry2.entity.EntryMenu;
import inc.cyd.entry2.entity.EntryMenuBase;
import inc.cyd.entry2.interfaces.EntryDashBoardShiftListener;
import inc.cyd.entry2.interfaces.EntryDialogListener;
import inc.cyd.entry2.interfaces.EntryMenuClickListener;
import inc.cyd.entry2.util.AnimateUtil;
import inc.cyd.entry2.util.VirtualkeyUtils;

import static inc.cyd.entry2.util.EntryUtil.findActivity;

public abstract class BottomEntryBase extends LinearLayout implements EntryDialogListener, EntryMenuClickListener {
    public Context context;
    private Handler handler = new Handler();
    public View rootView;
    public View entryView;
    public FrameLayout bottom_entry_dialog_frameLayout;
    public LinearLayout bottom_entry_menu_board;
    public LinearLayout bottom_entry_menus_layout;
    public HorizontalScrollView bottom_entry_menus_scrollView;
    public LinearLayout bottom_entry_menus_scrollView_layout;
    private List< ? extends EntryMenuBase> entryMenus;
    private EntryMenu bottom_entry_menu_switch;
    private EditText bottom_entry_edit;
    private EntryMenu bottom_entry_emoji;
    private LinearLayout bottom_entry_send;
    public LinearLayout bottom_entry_dashboard;
    public LinearLayout sheet_photo_pull_bar;
    public LinearLayout dashboard_view_wrap;

    //自定义的部分
    public boolean takePhoto;
    public boolean pickImage;
    public int maxPhotoNum;
    public boolean recordVoice;
    public Drawable themeColor;

    //常量
    public int keyBoardHeight;
    public int screen_height;
    public int screen_width;

    //接口
    // 1 面板滑动接口
    public EntryDashBoardShiftListener entryDashBoardShiftListener;

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
        initKeyboardHeight();
        initContact();
        // 设置各类接口
        //按钮点击接口
        setEntryMenusListener();
        setBaseListener();

        /**
         * 设置面板滑动接口
         * **/
        setDashBoardPullListener();
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
        sheet_photo_pull_bar = entryView.findViewById(R.id.sheet_photo_pull_bar);
        dashboard_view_wrap = entryView.findViewById(R.id.dashboard_view_wrap);
    }
    private void setBaseComponent(){
        bottom_entry_dialog_frameLayout.setVisibility(GONE);

        bottom_entry_menu_switch.customMenuResource( R.drawable.icon_contract , R.drawable.icon_spread);
        bottom_entry_emoji.customMenuResource( R.drawable.icon_emoji , R.drawable.icon_emoji_un);
        bottom_entry_menu_switch.setEntryMenuClickListener(this);
    }

    private void initKeyboardHeight(){
        //获取虚拟键盘的高度
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() ->{
            Rect rect = new Rect();
            findActivity(context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            //获取屏幕的高度
            screen_height = findActivity(context).getWindow().getDecorView().getRootView().getHeight();
            screen_width = findActivity(context).getWindow().getDecorView().getRootView().getWidth();

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
        //rootView.setOnTouchListener(((view, motionEvent) -> bottom_entry_dashboard.dispatchTouchEvent(motionEvent)));
        if(null != entryMenus){
            entryMenus.forEach(item -> item.setEntryMenuClickListener(this));
        }
    }

    /** 给整个布局设置触摸事件 **/

    @SuppressLint("ClickableViewAccessibility")
    private void setBaseListener(){
        rootView.setOnTouchListener((view, motionEvent) -> {
            bottom_entry_edit.clearFocus();
            return false;
        });
        //给整个view设置触摸事件
        entryView.setOnTouchListener((view, motionEvent) -> {
            entryView.setFocusable(true);
            entryView.setFocusableInTouchMode(true);
            entryView.requestFocus();
            return false;
        });

        bottom_entry_edit.setOnFocusChangeListener((view , b ) -> {
            if(!b){
                // 关闭小键盘
                closeKeyBoard(view);
                // 如果当前的 菜单 处于 收缩状态
                if(!spread){
                    toggleSpread();
                }
            }else{
                switchKeyBoardMode(bottom_entry_dashboard.getVisibility() == GONE);

                if(spread){
                    toggleSpread();
                }

            }
        });
    }


    //触发面板移动动画的空间距离参数
    private final int DASHBOARD_ANIMATE_SPACE = 300;
    @SuppressLint("ClickableViewAccessibility")
    private void setDashBoardPullListener(){
        //滑动监听 ， 然后通过自定义的接口传递面板的状态
        sheet_photo_pull_bar.setOnTouchListener((view, motionEvent) -> {
            if(null != this.entryDashBoardShiftListener){
                //sheet_photo_pull_bar 可见
                sheet_photo_pull_bar.setVisibility(VISIBLE);
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        /** 传递当前状态 **/
                        entryDashBoardShiftListener.start();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        /** 根据抬起手指时 所在的位置  判断状态 并通过接口传递 **/
                        if (null == dashBoardLayoutParam) {
                            dashBoardLayoutParam = new FrameLayout.LayoutParams(bottom_entry_dashboard.getLayoutParams());
                        }


                        //输出 手指抬起时 ， 是向上移动还是向下移动
                        // up  Y < 0 ; down Y > 0
                        /**   当手指抬起时 , 如果此时view高度介于最高和最低之间 ，(留出空间范围为 DASHBOARD_ANIMATE_SPACE = 300 ) , 开启移动的动画， 参数为 motionEvent.getY()  : up  Y < 0 ; down Y > 0**/

                        if(dashBoardLayoutParam.height > keyBoardHeight + DASHBOARD_ANIMATE_SPACE && dashBoardLayoutParam.height < screen_height - DASHBOARD_ANIMATE_SPACE){
                            //开启动画
                            dashBoardAnimate(motionEvent.getY());

                        }else{
                            //触发动画的距离不足
                            if (dashBoardLayoutParam.height >= screen_height - DASHBOARD_ANIMATE_SPACE) {
                                //设置为最大高度
                                dashBoardLayoutParam.height = screen_height - 200;


                                dashBoardLayoutParam.topMargin = 0;
                                //更新接口状态
                                entryDashBoardShiftListener.onTop();
                            } else{
                                dashBoardLayoutParam.height = keyBoardHeight;
                                dashBoardLayoutParam.topMargin = bottom_entry_menu_board.getHeight();

                                //更新接口状态
                                entryDashBoardShiftListener.onBottom();
                            }
                            bottom_entry_dashboard.setLayoutParams(dashBoardLayoutParam);
                        }


                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        // motionEvent.getY()  获取到点击的坐标
                        /** 执行拖动 **/
                        pullDashBoard(motionEvent.getY());

                        break;
                    }
                }
            }else{
                // 没有滑动监听 sheet_photo_pull_bar 不可见
                sheet_photo_pull_bar.setVisibility(GONE);
            }

            return false;
        });

    }
    private FrameLayout.LayoutParams dashBoardLayoutParam;

    //拖动面板
    private void pullDashBoard ( float positionY){
        if (null == dashBoardLayoutParam) {
            dashBoardLayoutParam = new FrameLayout.LayoutParams(bottom_entry_dashboard.getLayoutParams());
        }
        if (dashBoardLayoutParam.height - positionY < keyBoardHeight || dashBoardLayoutParam.height < keyBoardHeight) {
            dashBoardLayoutParam.height = keyBoardHeight;

            dashBoardLayoutParam.topMargin = bottom_entry_menu_board.getHeight();
            bottom_entry_dashboard.setLayoutParams(dashBoardLayoutParam);
            entryDashBoardShiftListener.onBottom();
        } else if (dashBoardLayoutParam.height - positionY > screen_height - 200 || dashBoardLayoutParam.height > screen_height - 200) {
            dashBoardLayoutParam.height = screen_height - 200;

            dashBoardLayoutParam.topMargin = 0;
            bottom_entry_dashboard.setLayoutParams(dashBoardLayoutParam);
            entryDashBoardShiftListener.onTop();
        } else {
            dashBoardLayoutParam.height = dashBoardLayoutParam.height - (int) positionY;

            dashBoardLayoutParam.topMargin = keyBoardHeight + bottom_entry_menu_board.getHeight() - dashBoardLayoutParam.height;
            bottom_entry_dashboard.setLayoutParams(dashBoardLayoutParam);
            /** 通过接口传递状态 并 将手指的纵坐标通过接口传递 **/
            entryDashBoardShiftListener.move(dashBoardLayoutParam.height);
        }


    }

    //全局变量 面板移动动画
    private ValueAnimator valueAnimator_up;
    private ValueAnimator valueAnimator_down;

    // 面板移动动画 参数 float spaceY
    private void dashBoardAnimate(float spaceY){
        if (null == dashBoardLayoutParam) {
            dashBoardLayoutParam = new FrameLayout.LayoutParams(bottom_entry_dashboard.getLayoutParams());
        }
        //up
        if(spaceY < 0){
            if(spaceY > -50) spaceY = -50 ;
            if(valueAnimator_up != null){
                valueAnimator_up.cancel();
            }
            valueAnimator_up = null;

            valueAnimator_up = AnimateUtil.createValueAnimator_Y(bottom_entry_dashboard , dashBoardLayoutParam.height , screen_height - 200);
            valueAnimator_up.setDuration(((screen_height - 200 - dashBoardLayoutParam.height) / (int) - spaceY) * 20);
            // 动画更新接口
            valueAnimator_up.addUpdateListener(valueAnimator -> {
                // 将最新高度 更新至接口信息
                if(null != entryDashBoardShiftListener){
                    dashBoardLayoutParam.topMargin =
                            (int) valueAnimator.getAnimatedValue() - (keyBoardHeight + bottom_entry_menu_board.getHeight()) > 0 ?
                                0
                                    :
                                (int) valueAnimator.getAnimatedValue() - (keyBoardHeight + bottom_entry_menu_board.getHeight())
                    ;
                    bottom_entry_dashboard.setLayoutParams(dashBoardLayoutParam);

                    entryDashBoardShiftListener.move((int) valueAnimator.getAnimatedValue());
                }
            });
            valueAnimator_up.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if(null != entryDashBoardShiftListener){
                        dashBoardLayoutParam.topMargin = 0;
                        entryDashBoardShiftListener.onTop();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            valueAnimator_up.start();
        }else{
            //down
            if(spaceY < 50) spaceY = 50;
            if(valueAnimator_down != null){
                valueAnimator_down.cancel();
            }
            valueAnimator_down = null;

            valueAnimator_down = AnimateUtil.createValueAnimator_Y(bottom_entry_dashboard , dashBoardLayoutParam.height , keyBoardHeight);
            valueAnimator_down.setDuration( ((dashBoardLayoutParam.height - keyBoardHeight) / (int) spaceY) * 20 );
            valueAnimator_down.addUpdateListener(valueAnimator -> {
                // 将最新高度 更新至接口信息
                if(null != entryDashBoardShiftListener){
                    dashBoardLayoutParam.topMargin =
                            (int) valueAnimator.getAnimatedValue() - (keyBoardHeight + bottom_entry_menu_board.getHeight()) > 0 ?
                                    0
                                    :
                                    (keyBoardHeight + bottom_entry_menu_board.getHeight()) - (int) valueAnimator.getAnimatedValue()
                    ;

                    bottom_entry_dashboard.setLayoutParams(dashBoardLayoutParam);


                    entryDashBoardShiftListener.move((int) valueAnimator.getAnimatedValue());
                }
            });
            valueAnimator_down.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if(null != entryDashBoardShiftListener){
                        dashBoardLayoutParam.topMargin = bottom_entry_menu_board.getHeight();
                        entryDashBoardShiftListener.onBottom();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            valueAnimator_down.start();
        }
    }


    private void setDashBoard(View view){
        showDashBoard(true);
        dashboard_view_wrap.removeAllViews();
        ViewGroup viewParent = (ViewGroup) view.getParent();
        if(null != viewParent){
            viewParent.removeView(view);
        }
        dashboard_view_wrap.addView(view);
        view.getParent().requestDisallowInterceptTouchEvent(true);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(view.getLayoutParams());
        ll.height = keyBoardHeight;
        ll.width = screen_width;
        view.setLayoutParams(ll);
    }
    private void setDialogView(View view){
        bottom_entry_dialog_frameLayout.removeAllViews();
        ViewGroup viewParent = (ViewGroup) view.getParent();
        if(null != viewParent){
            viewParent.removeView(view);
        }
        bottom_entry_dialog_frameLayout.addView(view);
    }

    private void showDashBoard(boolean is_show){
        bottom_entry_dashboard.setVisibility(is_show ? VISIBLE : GONE);
        switchKeyBoardMode(!is_show);
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
    Runnable contractMenuRunnable = this::contractMenuLayout;
    Runnable spreadMenuRunnable = this::spreadMenuLayout;
    protected void contractMenuLayout(){
        bottom_entry_menu_switch.switchState(!spread);
        if(null == bottom_entry_menus_layout){
            return;
        }
        if(null != alphaAnimation){
            alphaAnimation.cancel();
        }
        alphaAnimation = new AlphaAnimation(1f, 0.0f);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        bottom_entry_menus_layout.startAnimation(alphaAnimation);
        //收缩
        ValueAnimator valueAnimator = createValueAnimator(bottom_entry_menus_layout , menus_layout_width , 0);
        valueAnimator.setDuration(duration);
        valueAnimator.start();
        spread = false;
    }
    protected void spreadMenuLayout(){
        bottom_entry_menu_switch.switchState(!spread);
        if(null == bottom_entry_menus_layout){
            return;
        }
        if(null != alphaAnimation){
            alphaAnimation.cancel();
        }
        alphaAnimation = new AlphaAnimation(0.0f, 1f);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        bottom_entry_menus_layout.startAnimation(alphaAnimation);
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



    /** 子类需要重写的方法 **/
    public abstract void setAttribute(AttributeSet attributeSet);

    public abstract List<EntryMenu> getEntryMenus();

    public View getRootView(){
         return findActivity(context).getWindow().getDecorView().findViewById(android.R.id.content);
    }



    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void entryDialogShow() {
        bottom_entry_dialog_frameLayout.setVisibility(VISIBLE);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void entryDialogDisMiss() {
        bottom_entry_dialog_frameLayout.setVisibility(GONE);
    }

    @Override
    public void onEntryMenuClick(EntryMenu entryMenu) {
        //任何按钮 点击时 editText 失去焦点
        bottom_entry_edit.clearFocus();

        if(bottom_entry_menu_switch == entryMenu){
            //切换
            toggleSpread();
        }else if(bottom_entry_emoji == entryMenu){
            //表情
        }else{
            if(null != entryMenu.getDialogView()){
                entryMenu.setEntryDialogListener(this);

                setDialogView(entryMenu.getDialogView());
            }else if(null != entryMenu.getBottomSheetView()){
                if(null != entryMenu.getEntryDashBoardShiftListener()){
                    sheet_photo_pull_bar.setVisibility(VISIBLE);
                    this.entryDashBoardShiftListener = entryMenu.getEntryDashBoardShiftListener();
                }else{
                    sheet_photo_pull_bar.setVisibility(GONE);
                }
                // setDashBoard 方法中 调用 是否显示面板 以及设置键盘模式的方法
                setDashBoard(entryMenu.getBottomSheetView());
            }else{
                showDashBoard(false);

            }
        }
    }

    private void closeKeyBoard(View v){
        /*关闭小键盘*/
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void switchKeyBoardMode(boolean resize){
        findActivity(context).getWindow().setSoftInputMode(resize ? WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE : WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }
}
