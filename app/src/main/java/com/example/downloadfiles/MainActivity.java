package com.example.downloadfiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.downloadfiles.singlethreaddownload.DownloadFiles;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private String[] urls = {"https://mirrors.tuna.tsinghua.edu.cn/cygwin/x86_64/setup.bz2",
            "https://mirrors.tuna.tsinghua.edu.cn/centos/filelist.gz",
            "https://mirrors.tuna.tsinghua.edu.cn/anaconda/miniconda/Miniconda-3.6.0-Linux-x86.sh"};

    private Context mContext;
    private TextView tv_percent1;
    private ProgressBar progressbar1;
    private ProgressBar progressbar2;
    private ProgressBar progressbar3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        tv_percent1=(TextView)findViewById(R.id.tv_percent);
        progressbar1 = (ProgressBar) findViewById(R.id.progress1);
        progressbar2 = (ProgressBar) findViewById(R.id.progress2);
        progressbar3 = (ProgressBar) findViewById(R.id.progress3);

        Button btDownload1 = (Button) findViewById(R.id.bt_download1);
        Button btDownload2 = (Button) findViewById(R.id.bt_download2);
        Button btDownload3 = (Button) findViewById(R.id.bt_download3);

        btDownload1.setOnClickListener(this);
        btDownload2.setOnClickListener(this);
        btDownload3.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(MainActivity.this,"同意权限申请",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this,"拒接权限申请",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_download1:
                progressbar1.setVisibility(View.VISIBLE);
                try {
                    DownloadFiles.loadFiles(urls[0],progressbar1,tv_percent1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt_download2:
                progressbar2.setVisibility(View.VISIBLE);
                try {
                    DownloadFiles.loadFiles(urls[1],progressbar2,tv_percent1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt_download3:
                progressbar3.setVisibility(View.VISIBLE);
                try {
                    DownloadFiles.loadFiles(urls[2],progressbar3,tv_percent1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
        }
    }
}
