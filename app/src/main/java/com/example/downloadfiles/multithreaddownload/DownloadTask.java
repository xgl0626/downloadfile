package com.example.downloadfiles.multithreaddownload;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.downloadfiles.multithreaddownload.FileUtils.getFileLength;
import static com.example.downloadfiles.multithreaddownload.FileUtils.getFileName;

/**
 * @author 徐国林
 * @data 2020/4/23
 * @decription
 */
public class DownloadTask {
    private long lastPosition;
    private Context context;
    public static boolean isPaused;
    public static boolean isCanceled;
    public static boolean isDownloading;
    public final int THREAD_NUM = 5;
    private int flag = 3;

    public DownloadTask(Context context) {
        this.context = context;
    }

    //多线程下载
    public void download(final TaskDao taskDao, final String url, final int thread, final long startIndex, final long enIndex) {
        final InputStream[] is = {null};
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (taskDao.getLastPoint(getFileName(url), thread) != -1) {
                    lastPosition = taskDao.getLastPoint(getFileName(url), thread);
                }
                if (lastPosition == enIndex + 1) {
                    return;
                } else {
                    lastPosition = startIndex;
                }
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .addHeader("RANGE", "byte=" + lastPosition + "-" + enIndex)
                        .url(url)
                        .build();
                File file = null;
                RandomAccessFile accessFile = null;
                try {
                    Response response = client.newCall(request).execute();
                    if (response != null && response.isSuccessful()) {
                        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + getFileName(url));
                        accessFile = new RandomAccessFile(file, "rw");
                        accessFile.seek(lastPosition);
                        Task task = new Task();
                        task.task = getFileName(url);
                        task.thread = thread;
                        task.position = -1;
                        taskDao.addPoint(task);

                        is[0] = response.body().byteStream();
                        byte[] b = new byte[1024 * 1024];
                        int len;
                        int total = 0;
                        while ((len = is[0].read(b)) != -1) {
                            if (!isPaused && !isCanceled) {
                                accessFile.write(b, 0, len);
                                total += len;
                                task.position = total + lastPosition;
                                taskDao.savePoint(task);
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
                        if (accessFile != null) {
                            accessFile.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //startLoad
    public void fileDownload(final TaskDao taskDao, final String url, final ProgressBar progressBar, final Button paused, final Button cancel) {
        //每次下载标志位设置为false
        isPaused = false;
        isCanceled = false;
        if (!isDownloading) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    isDownloading = true;
                    RandomAccessFile accessFile = null;
                    try {
                        final String fileName = getFileName(url);
                        long fileLength = getFileLength(url);
                        long partLength = fileLength / THREAD_NUM;
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + fileName);
                        accessFile = new RandomAccessFile(file, "rw");
                        accessFile.setLength(fileLength);//提前设置好空间
                        for (int thread = 0; thread < THREAD_NUM; thread++) {
                            long startIndex = thread * partLength;
                            long endIndex = (thread + 1) * partLength - 1;
                            if (thread == THREAD_NUM - 1) {
                                endIndex = fileLength - 1;
                            }
                            download(taskDao, url, thread, startIndex, endIndex);
                        }
                        while (true) {
                            long totalProgress = 0;
                            for (int i = 0; i < THREAD_NUM; i++) {
                                totalProgress += taskDao.getLastPoint(getFileName(url), i) - i * partLength;
                            }
                            int progress = (int) (totalProgress * 100 / fileLength);
                            progressBar.setProgress(progress);
                            if (totalProgress == fileLength) {
                                progressBar.setProgress(100);
                                taskDao.delete(getFileName(url));
                                Log.d("tag", "success");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("tag", "fail");
                    } finally {
                        try {
                            if (accessFile != null) {
                                accessFile.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

    }

    // 暂停下载,利用单偶数模拟点击一下暂停，在点击一下又开始
    public void pauseDownload() {
        if (flag % 2 == 0)
            isPaused = false;
        else
            isPaused = true;
        flag++;
        isDownloading = false;
    }

    // 取消下载
    public void canceledDownload(TaskDao taskDao, String url) {
        isCanceled = true;
        isDownloading = false;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + getFileName(url));
        if (file.exists()) {
            file.delete();
        }
        taskDao.delete(getFileName(url));
    }
}
