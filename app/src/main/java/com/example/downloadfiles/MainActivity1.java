package com.example.downloadfiles;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.downloadfiles.multithreaddownload.DownloadTask;
import com.example.downloadfiles.multithreaddownload.TaskDao;

/**
 * @author 徐国林
 * @data 2020/4/22
 * @decription
 */
public class MainActivity1 extends AppCompatActivity implements View.OnClickListener {
    private String fileUrl = "https://mirrors.tuna.tsinghua.edu.cn/mysql/downloads/Win32/Perl-5.00502-mswin32-1.1-x86.zip";

    private Context mContext;
    // 设置数据库连接为全局变量。所有线程共用一个数据库连接。也不会close掉。
    // 所有线程用了就close的话，可能A线程在close的时候，B线程又想打开连接进行读写。
    // 不频繁开关连接，性能更好。等到生命周期结束才自动close
    private TaskDao taskDao;
    private ProgressBar progressbar;
    private Button btPause;
    private Button btCancel;
    private DownloadTask downloadTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        mContext = this;
        // 一进应用就创建数据库
        taskDao = new TaskDao(mContext, "tasks.db", 1);
        downloadTask=new DownloadTask(mContext);
        progressbar = (ProgressBar) findViewById(R.id.progress);
        Button btDownload1 = (Button) findViewById(R.id.bt_download);
        btPause = (Button) findViewById(R.id.bt_pause);
        btCancel = (Button) findViewById(R.id.bt_cancel);

        btDownload1.setOnClickListener(this);
        btPause.setOnClickListener(this);
        btCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_download:
                btPause.setVisibility(View.VISIBLE);
                btCancel.setVisibility(View.VISIBLE);
                progressbar.setVisibility(View.VISIBLE);
                downloadTask.fileDownload(taskDao,fileUrl, progressbar, btPause, btCancel);
                Toast.makeText(mContext, "正在下载...", Toast.LENGTH_SHORT).show();
                break;

            case R.id.bt_pause:
                downloadTask.pauseDownload();
                break;
            case R.id.bt_cancel:
                btPause.setVisibility(View.INVISIBLE);
                btCancel.setVisibility(View.INVISIBLE);
                progressbar.setVisibility(View.INVISIBLE);
                downloadTask.canceledDownload(taskDao,fileUrl);
                Toast.makeText(mContext, "下载取消，删除文件...", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
    }
}
