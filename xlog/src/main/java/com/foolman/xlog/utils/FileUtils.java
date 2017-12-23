package com.foolman.xlog.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    /**
     * 根据文件路径获取文件
     */
    public static File getFile(final String filePath) {
        return filePath == null ? null : new File(filePath);
    }

    /**
     * 判断文件是否存在
     */
    public static boolean isFileExists(final String filePath) {
        return isFileExists(getFile(filePath));
    }

    /**
     * 判断文件是否存在
     */
    public static boolean isFileExists(final File file) {
        return file != null && file.exists();
    }

    /**
     * 将src1 & src2的内容合并到dest文件内
     */
    public static void mergerFile(File src1, File src2, File dest) throws IOException {

        if (dest.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dest.delete();
        }
        FileOutputStream out = new FileOutputStream(dest);
        byte[] temp = new byte[1024 * 10];

        if (src1 != null && src1.exists()) {
            writeData(src1, out, temp);
        }

        if (src2 != null && src2.exists()) {
            writeData(src2, out, temp);
        }

        out.close();
    }

    private static void writeData(File src2, FileOutputStream out, byte[] temp) throws IOException {
        long total;
        FileInputStream in;
        total = src2.length();
        in = new FileInputStream(src2);
        long count = 0;
        while (count < total) {
            int size = in.read(temp);
            if (size != -1) {
                out.write(temp, 0, size);
                count += size;
            }
        }
        in.close();
    }

    /**
     * Delete the file tree.
     *
     * @param file
     * @return true if file does not exist or delete successfully. false if there is an Exception during operation or sdcard is not mounted.
     */
    public static boolean deleteFile(File file) {
//		if(SystemUtils.isSDCardMounted()) {
        if (!file.exists()) {
            return true;
        }
        try {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
//		} else return false;
    }
}
