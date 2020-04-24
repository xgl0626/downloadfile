package com.example.downloadfiles.multithreaddownload;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author 徐国林
 * @data 2020/4/24
 * @decription
 */
public class FileUtils {
    // 获得文件长度
    public static long getFileLength(String url) throws IOException {
        long contentLength = 0;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        // 有响应且不为空
        if (response != null && response.isSuccessful()) {
            contentLength = response.body().contentLength();
            response.body().close();
        }
        return contentLength;
    }

    // 得到的是 xxx.xxx,不带斜杠
    public static String getFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

}
