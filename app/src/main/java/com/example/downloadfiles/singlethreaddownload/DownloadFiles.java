package com.example.downloadfiles.singlethreaddownload;

import android.os.Environment;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.downloadfiles.multithreaddownload.FileUtils.getFileLength;
import static com.example.downloadfiles.multithreaddownload.FileUtils.getFileName;

/**
 * @author 徐国林
 * @data 2020/4/22
 * @decription
 */
public class DownloadFiles {
    private static boolean isCanceled = false;
    private static boolean isPaused = false;

    public static void loadFiles(String url, final ProgressBar progressBar, final TextView textView) {
        final InputStream[] is = {null};
        final RandomAccessFile[] savedFile = {null};
        final File[] file = {null};
        final long[] downloadLength = {0};
        final String downloadUrl = url;
        final String filename = getFileName(downloadUrl);
        final String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        new Thread(new Runnable() {
        @Override
            public void run() {
                file[0] = new File(directory + filename);
                if (file[0].exists()) {
                    downloadLength[0] = file[0].length();
                }
                try {
                    long contentLength = getFileLength(downloadUrl);
                    if (contentLength == 0) {

                    } else if (contentLength == downloadLength[0])
                    {}

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .addHeader("RANGE", "byte=" + downloadLength[0] + "-")
                            .url(downloadUrl)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response != null) {
                        is[0] = response.body().byteStream();
                        savedFile[0] = new RandomAccessFile(file[0], "rw");
                        savedFile[0].seek(downloadLength[0]);
                        byte[] b = new byte[1024];
                        int total = 0;
                        int len;
                        while ((len = is[0].read(b)) != -1) {
                            if (isCanceled) {
                            } else if (isPaused) {
                            } else {
                                total += len;
                                savedFile[0].write(b, 0, len);
                                int progress = (int) ((total + downloadLength[0]) * 100 / contentLength);
//                                if(progress<=100)handler转移主ui线程更新ui
//                                {textView.setText(progress+"%");}
                                progressBar.setProgress(progress);
                            }
                        }
                        response.body().close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (is[0] != null) {
                            is[0].close();
                        }
                        if (savedFile[0] != null) {
                            savedFile[0].close();
                        }
                        if (isCanceled && file[0] != null) {
                            file[0].delete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
