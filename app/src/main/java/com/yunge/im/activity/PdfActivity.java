package com.yunge.im.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yunge.im.R;

public class PdfActivity extends AppCompatActivity {
    WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ys_zc);
        Intent intent = getIntent();
        String acc = intent.getStringExtra("acc");
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView title = findViewById(R.id.title);
        if ("1".equals(acc)){
            title.setText("隐私政策");
        }else if ("2".equals(acc)){
            title.setText("保密协议");
        }
        initView();

        //加载本地文件
        preView(acc);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        mWebView = findViewById(R.id.webView);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
    }

//    /**
//     * 下载pdf文件到本地
//     *
//     * @param url 文件url
//     */
//    private void download(String url) {
//        DownloadUtil.download(url, getCacheDir() + "/temp.pdf",
//                new DownloadUtil.OnDownloadListener() {
//                    @Override
//                    public void onDownloadSuccess(final String path) {
//                        Log.d("MainActivity", "onDownloadSuccess: " + path);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                preView(path);
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onDownloading(int progress) {
//                        Log.d("MainActivity", "onDownloading: " + progress);
//                    }
//
//                    @Override
//                    public void onDownloadFailed(String msg) {
//                        Log.d("MainActivity", "onDownloadFailed: " + msg);
//                    }
//                });
//    }

    /**
     * 预览pdf
     *
     * @param pdfUrl url或者本地文件路径
     */
    private void preView(String pdfUrl) {
        //1.只使用pdf.js渲染功能，自定义预览UI界面
        try {
            mWebView.loadUrl("file:///android_asset/index.html?type=" + pdfUrl);

        }catch (Throwable e){

        }
        //2.使用mozilla官方demo加载在线pdf
//        mWebView.loadUrl("http://mozilla.github.io/pdf.js/web/viewer.html?file=" + pdfUrl);
        //3.pdf.js放到本地
//        mWebView.loadUrl("file:///android_asset/pdfjs/web/viewer.html?file=" + pdfUrl);
        //4.使用谷歌文档服务
//        mWebView.loadUrl("http://docs.google.com/gviewembedded=true&url=" + pdfUrl);
    }
}