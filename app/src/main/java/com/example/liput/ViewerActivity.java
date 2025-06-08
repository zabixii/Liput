package com.example.liput;

import android.os.Bundle;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;

public class ViewerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webView = new WebView(this);
        setContentView(webView);
        String fileName = getIntent().getStringExtra("file");
        File file = new File(getFilesDir(), fileName);
        webView.loadUrl("file://" + file.getAbsolutePath());
    }
}
