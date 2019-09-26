package inc.cyd.entry2.interfaces;

import inc.cyd.entry2.entity.EntryMenu;

// 按钮点击监听 泛型接口
public interface EntryMenuClickListener < T extends EntryMenu> {
    void onEntryMenuClick( T   entryMenu);
}
