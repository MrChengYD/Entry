package inc.cyd.entry2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import inc.cyd.entry2.entity.EntryMenu;
import inc.cyd.entry2.interfaces.EntryDashBoardShiftListener;
import inc.cyd.entry2.interfaces.EntryMenuTouchListener;

public class BottomEntry extends BottomEntryBase {

    public BottomEntry(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAttribute(AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.BottomEntry);
        if (null != typedArray) {
            takePhoto = typedArray.getBoolean(R.styleable.BottomEntry_takePhoto, true);
            pickImage = typedArray.getBoolean(R.styleable.BottomEntry_pickImage, true);
            recordVoice = typedArray.getBoolean(R.styleable.BottomEntry_recordVoice, true);
            maxPhotoNum = typedArray.getInt(R.styleable.BottomEntry_maxPhotoNum, 9);
            if (maxPhotoNum < 1) {
                maxPhotoNum = 1;
            } else if (maxPhotoNum > 9) {
                maxPhotoNum = 9;
            }
            themeColor = typedArray.getDrawable(R.styleable.BottomEntry_themeColor);

            //回收TypedArray，以便后面重用。在调用这个函数后，你就不能再使用这个TypedArray。
            typedArray.recycle();
        }
    }

    /**
     * 禁止被重写
     **/
    @Override
    public final List<EntryMenu> getEntryMenus() {
        //获取默认的按钮
        List<EntryMenu> retEntryMenus = initMyMenus();
        //获取自定义的按钮
        List<EntryMenu> customMenus = customMenu();
        if (null != customMenus) {
            customMenus.removeAll(Collections.singleton(null));
            if (customMenus.size() > 0) {
                retEntryMenus.addAll(customMenus);
            }
        }
        return retEntryMenus;
    }

    private final int sheet_photo_maxHeight = 200;
    /**
     * 禁止被重写
     **/
    @SuppressLint("ClickableViewAccessibility")
    public final List<EntryMenu> initMyMenus() {
        List<EntryMenu> myMenus = new ArrayList<>();
        //添加menu
        //主按钮
        EntryMenu mainMenu = new EntryMenu(context, null);
        mainMenu.customMenuResource(R.drawable.icon_menu, R.drawable.icon_menu_un);
        mainMenu.customMenuFunView(null , customMainSheet(mainMenu.getClickAbleView()));
        myMenus.add(mainMenu);
        //拍照
        if (takePhoto) {
            EntryMenu takePhotoMenu = new EntryMenu(context, null);
            takePhotoMenu.customMenuResource(R.drawable.icon_camera, R.drawable.icon_camera_un);

            myMenus.add(takePhotoMenu);
        }



        if (pickImage) {
            EntryMenu picImageMenu = new EntryMenu(context, null);
            View picImageBottomSheetView = LayoutInflater.from(context).inflate(R.layout.sheet_photo, null);
            picImageMenu.customMenuResource(R.drawable.icon_photo, R.drawable.icon_photo_un);
            picImageMenu.customMenuFunView(null , picImageBottomSheetView);
            if (null == sheet_photo_title_layoutParam) {
                sheet_photo_title_layoutParam = new LinearLayout.LayoutParams(picImageBottomSheetView.findViewById(R.id.sheet_photo_title_layout).getLayoutParams());
            }
            picImageMenu.setEntryDashBoardShiftListener(new EntryDashBoardShiftListener() {
                @Override
                public void start() {

                }

                @Override
                public void move(float dashBoardHeight) {
                    sheetPhotoTitleMove(picImageBottomSheetView , (int) dashBoardHeight);
                }

                @Override
                public void onBottom() {
                    sheet_photo_title_layoutParam.height = 0;
                }


                @Override
                public void onTop() {
                    sheet_photo_title_layoutParam.height = sheet_photo_maxHeight;
                }
            });

            myMenus.add(picImageMenu);
        }
        if (recordVoice) {
            EntryMenu recordVoiceMenu = new EntryMenu(context, null);
            View recordVoiceDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_voice, null);
            recordVoiceMenu.customMenuResource(R.drawable.icon_mic, R.drawable.icon_mic_un);
            recordVoiceMenu.customMenuFunView(recordVoiceDialogView , null);
            recordVoiceMenu.setEntryMenuTouchListener(new EntryMenuTouchListener() {
                @Override
                public void start() {
                    entryDialogShow();
                }

                @Override
                public void end() {
                    entryDialogDisMiss();
                }

                @Override
                public void move(float positionX, float positionY) {

                }
            });
            myMenus.add(recordVoiceMenu);
        }

        return myMenus;
    }

    private LinearLayout.LayoutParams sheet_photo_title_layoutParam;
    /**
     * 拉伸 面板
     * <p>
     * 面板拉伸后， 超过了entryView的范围，导致当中的view无法设置onTouch（onClick）监听
     **/
    // sheet_photo中的各种组件
    private LinearLayout sheet_photo_title_layout;
    private ImageView sheet_photo_close;
    private TextView sheet_photo_main_title;
    private TextView sheet_photo_side_title;



    protected void sheetPhotoTitleMove (View sheet_view ,  int dashBoardHeight){
        // 变动 title的布局
        sheet_photo_title_layout = sheet_view.findViewById(R.id.sheet_photo_title_layout);
        if (null == sheet_photo_title_layoutParam) {
            sheet_photo_title_layoutParam = new LinearLayout.LayoutParams(sheet_photo_title_layout.getLayoutParams());
        }






        if (sheet_photo_title_layoutParam.height < 0 || (dashBoardHeight - keyBoardHeight ) / 7 < 0) {
            sheet_photo_title_layoutParam.height = 0;
        } else if (sheet_photo_title_layoutParam.height > sheet_photo_maxHeight || (dashBoardHeight - keyBoardHeight ) / 7 > sheet_photo_maxHeight) {
            sheet_photo_title_layoutParam.height = sheet_photo_maxHeight;
        } else {
            sheet_photo_title_layoutParam.height = (dashBoardHeight - keyBoardHeight ) / 7;
        }
        sheet_photo_title_layout.setLayoutParams(sheet_photo_title_layoutParam);

    }
    /**
     *
     * 可以被重写的部分
     *  1 menu_main的bottomSheetView
     *  2 在写好的按钮之后，添加的自定义的按钮
     * **/

    /** 可以自定义main_sheet的view以及各种监听 **/
    public View customMainSheet (View menu_icon_view){
        return LayoutInflater.from(context).inflate(R.layout.sheet_main_dialog, null);
    }
    //自定义的按钮
    @SuppressLint("ClickableViewAccessibility")
    public List<EntryMenu> customMenu () {
        List<EntryMenu> customMenus = new ArrayList<>();
        //在增加一个menu
        EntryMenu custom1 = new EntryMenu(context, null);
        custom1.customMenuResource(R.drawable.icon_menu_def, R.drawable.icon_menu_def_un);

        customMenus.add(custom1);
        EntryMenu custom2 = new EntryMenu(context, null);
        custom2.customMenuResource(R.drawable.icon_menu_def, R.drawable.icon_menu_def_un);
        customMenus.add(custom2);
        return customMenus;
    }


}