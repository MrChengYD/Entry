package inc.cyd.entry2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import inc.cyd.entry2.entity.EntryMenu;

public class BottomEntry extends BottomEntryBase{
    private boolean takePhoto;
    private boolean pickImage;
    private int maxPhotoNum;
    private boolean recordVoice;
    private Drawable themeColor;
    public BottomEntry(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAttribute(AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet , R.styleable.BottomEntry);
        if(null != typedArray){
            takePhoto = typedArray.getBoolean(R.styleable.BottomEntry_takePhoto , true);
            pickImage = typedArray.getBoolean(R.styleable.BottomEntry_pickImage , true);
            recordVoice = typedArray.getBoolean(R.styleable.BottomEntry_recordVoice , true);
            maxPhotoNum = typedArray.getInt(R.styleable.BottomEntry_maxPhotoNum , 9);
            if(maxPhotoNum < 1) {
                maxPhotoNum = 1;
            }else if(maxPhotoNum > 9){
                maxPhotoNum = 9;
            }
            themeColor = typedArray.getDrawable(R.styleable.BottomEntry_themeColor);

            //回收TypedArray，以便后面重用。在调用这个函数后，你就不能再使用这个TypedArray。
            typedArray.recycle();
        }
    }

    /** 禁止被重写 **/
    @Override
    public final List<EntryMenu> getEntryMenus() {
        //获取默认的按钮
        List<EntryMenu> retEntryMenus = initMyMenus();
        //获取自定义的按钮
        List<EntryMenu> customMenus = customMenu();
        if(null != customMenus){
            customMenus.removeAll(Collections.singleton(null));
            if(customMenus.size() > 0){
                retEntryMenus.addAll(customMenus);
            }
        }




        return retEntryMenus;
    }
    /** 禁止被重写 **/
    @SuppressLint("ClickableViewAccessibility")
    public final List<EntryMenu> initMyMenus(){
        List<EntryMenu> myMenus = new ArrayList<>();
        //添加menu
        //主按钮
        EntryMenu mainMenu = new EntryMenu(context , null);
        mainMenu.initResource(R.drawable.icon_menu , R.drawable.icon_menu_un , null , customMainSheet(mainMenu.getSetListenerView()));
        myMenus.add(mainMenu);
        //拍照
        if(takePhoto){
            EntryMenu takePhotoMenu = new EntryMenu(context , null);
            takePhotoMenu.initResource(R.drawable.icon_camera , R.drawable.icon_camera_un , null , null);

            myMenus.add(takePhotoMenu);
        }
        if(pickImage){
            EntryMenu picImageMenu = new EntryMenu(context , null);
            View picImageBottomSheetView = LayoutInflater.from(context).inflate(R.layout.sheet_photo , null);
            picImageMenu.initResource(R.drawable.icon_photo , R.drawable.icon_photo_un , null , picImageBottomSheetView);
            //设置对应的监听
            LinearLayout sheet_photo_pull = picImageBottomSheetView.findViewById(R.id.sheet_photo_pull);
            sheet_photo_pull.setOnTouchListener(((view, motionEvent) -> {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_MOVE : {
                        //监听点击坐标
                        float position_y = motionEvent.getY();
                        pullDashBoard((int) position_y);

                        break;
                    }
                }
                return false;
            }));
            myMenus.add(picImageMenu);
        }
        if(recordVoice){
            EntryMenu recordVoiceMenu = new EntryMenu(context , null);
            View recordVoiceDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_voice , null);
            recordVoiceMenu.initResource(R.drawable.icon_mic , R.drawable.icon_mic_un , recordVoiceDialogView , null);
            recordVoiceMenu.getSetListenerView().setOnTouchListener((view, motionEvent) -> {
                //当子类布局滑动时，父类不拦截事件
                recordVoiceMenu.getSetListenerView().getParent().requestDisallowInterceptTouchEvent(true);
                switch (motionEvent.getAction()){

                    case MotionEvent.ACTION_DOWN : {
                        recordVoiceMenu.entryDialogShow();
                        break;
                    }
                    case MotionEvent.ACTION_UP :{
                        recordVoiceMenu.entryDialogDismiss();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE:{
                        if(!recordVoiceMenu.getSetListenerView().isClickable()){
                            break;
                        }
                        float position_x = motionEvent.getX();//监听点击坐标
                        float position_y = motionEvent.getY();
                        Log.i("aaa" , "输出坐标 : X -> " + position_x + " , Y -> " + position_y);
                        break;
                    }
                }
                return false;
            });
            myMenus.add(recordVoiceMenu);
        }

        return myMenus;
    }

    private FrameLayout.LayoutParams dashBoardLayoutParam ;
    /** 拉伸 面板
     *
     * 面板拉伸后， 超过了entryView的范围，导致当中的view无法设置onTouch（onClick）监听
     *
     * **/
    protected void pullDashBoard(int position_y){

        // 拉伸 bottom_entry_dashboard
        if(null == dashBoardLayoutParam){
            dashBoardLayoutParam = new FrameLayout.LayoutParams(bottom_entry_dashboard.getLayoutParams());
        }
        Log.i("aaa" , "输出 dashBoard 的高度 : " + dashBoardLayoutParam.height);
        //Log.i("aaa" , "输出 bottom_entry_menu_board 的高度 : " + bottom_entry_menu_board.getHeight());//141
        Log.i("aaa" , "输出 screen_height 的高度 : " + screen_height);
        if(dashBoardLayoutParam.height - position_y < keyBoardHeight || dashBoardLayoutParam.height < keyBoardHeight){
            Log.i("aaa" , "1");
            dashBoardLayoutParam.height = keyBoardHeight;
        }else if(dashBoardLayoutParam.height - position_y >= screen_height - 200 || dashBoardLayoutParam.height >= screen_height - 200){
            Log.i("aaa" , "2");
            dashBoardLayoutParam.height = screen_height - 200;
        }else{
            Log.i("aaa" , "3");
            dashBoardLayoutParam.height = dashBoardLayoutParam.height  - position_y ;
        }
        dashBoardLayoutParam.topMargin = keyBoardHeight + bottom_entry_menu_board.getHeight() - dashBoardLayoutParam.height;
        bottom_entry_dashboard.setLayoutParams(dashBoardLayoutParam);

    }
    /**
     *
     * 可以被重写的部分
     *  1 menu_main的bottomSheetView
     *  2 在写好的按钮之后，添加的自定义的按钮
     * **/

    /** 可以自定义main_sheet的view以及各种监听 **/
    public View customMainSheet(View menu_icon_view){
        return LayoutInflater.from(context).inflate(R.layout.sheet_main_dialog , null);
    }
    //自定义的按钮
    public  List<EntryMenu> customMenu(){
        List<EntryMenu> customMenus = new ArrayList<>();
        //在增加一个menu
        EntryMenu custom1 = new EntryMenu(context , null);
        custom1.initResource(R.drawable.icon_menu_def , R.drawable.icon_menu_def_un,null , null);
        customMenus.add(custom1);
        EntryMenu custom2 = new EntryMenu(context , null);
        custom2.initResource(R.drawable.icon_menu_def , R.drawable.icon_menu_def_un,null , null);
        customMenus.add(custom2);
        return customMenus;
    }


}
