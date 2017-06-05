package com.fangdd.esf;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lijiang on 5/24/17.
 */
public class HttpUtil {
    private static int BUFFER_SIZE = 1024;

    public static void saveToFile(String destUrl, String fileName) throws Exception {
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        HttpURLConnection httpUrl = null;

        try {
            URL url = new URL(destUrl);
            byte[] buf = new byte[BUFFER_SIZE];
            int size;

            httpUrl = (HttpURLConnection) url.openConnection();
//            httpUrl.setConnectTimeout(500);
//            httpUrl.setRequestMethod("GET");
            httpUrl.connect();

            bis = new BufferedInputStream(httpUrl.getInputStream());
            fos = new FileOutputStream(fileName);

            while ((size = bis.read(buf)) != -1)
                fos.write(buf, 0, size);

        } catch (Exception e) {
            throw e;
        } finally {
            if (fos != null) {
                fos.close();
            }
            if (bis != null) {
                bis.close();
            }
            if (httpUrl != null) {
                httpUrl.disconnect();
            }
        }
    }
}
