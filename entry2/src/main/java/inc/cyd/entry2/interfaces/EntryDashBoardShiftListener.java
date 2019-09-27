package inc.cyd.entry2.interfaces;

/**
 * 功能面板 移动 / 提拉/下滑 动作监听
 *
 * **/
public interface EntryDashBoardShiftListener {
    void start();
    void move(float dashBoardHeight);
    void onBottom();
    void onTop();
}
