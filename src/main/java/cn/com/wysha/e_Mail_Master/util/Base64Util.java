package cn.com.wysha.e_mail_master.util;

import java.io.*;
import java.nio.file.Files;
import java.util.Base64;

public class Base64Util {
    public static String fileToBase64(String path) {
        File file = new File(path);
        byte[] byteArray;
        try {
            byteArray = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String(Base64.getEncoder().encode(byteArray));
    }
}
