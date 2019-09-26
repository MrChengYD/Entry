package inc.cyd.entry2.interfaces;

/**
 * 功能面板提拉动作监听
 *
 * **/
public interface EntryDashBoardPullListener {
    void start();
    void move(float positionY);
    void onBottom();
    void onMiddle();
    void onTop();
}
