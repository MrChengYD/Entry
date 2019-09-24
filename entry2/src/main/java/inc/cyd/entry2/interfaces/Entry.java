package inc.cyd.entry2.interfaces;

import java.io.File;
import java.io.OutputStream;

public interface Entry {
    /**
     * 获取文字
     * **/
    String getText();

    /**
     * 获取图片路径
      */
    String [] getPicPathes();

    /**
     * 获取图片文件
     */
    File [] getPicFiles();

    /**
     * 获取图片输出流
     * **/
    OutputStream [] getPicOutPutStream();

    /**
     * 获取语音文件路径
     * **/
    String getVoicePath();

    /**
     * 获取语音文件
    * **/
    File getVoiceFile();

    /**
     * 获取语音文件输出流
     * **/
    OutputStream getVoiceOutPutStream();

}
