package com.example.liput;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private LinearLayout fileList;
    private File filesDir;

    ActivityResultLauncher<String[]> filePicker = registerForActivityResult(
        new ActivityResultContracts.OpenMultipleDocuments(),
        uris -> {
            for (Uri uri : uris) {
                try {
                    String fileName = getFileName(uri);
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    File outFile = new File(filesDir, fileName);
                    FileOutputStream outputStream = new FileOutputStream(outFile);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = Objects.requireNonNull(inputStream).read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                    inputStream.close();
                    outputStream.close();
                    addFileCard(fileName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fileList = findViewById(R.id.file_list);
        filesDir = getFilesDir();
        findViewById(R.id.import_button).setOnClickListener(v -> filePicker.launch(new String[]{"text/html"}));
        for (File file : filesDir.listFiles()) {
            addFileCard(file.getName());
        }
    }

    private void addFileCard(String fileName) {
        View card = getLayoutInflater().inflate(R.layout.file_card, null);
        ((TextView) card.findViewById(R.id.file_title)).setText(fileName.replaceAll("\.html|\.mht", ""));
        card.findViewById(R.id.open_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewerActivity.class);
            intent.putExtra("file", fileName);
            startActivity(intent);
        });
        card.findViewById(R.id.delete_button).setOnClickListener(v -> {
            File f = new File(filesDir, fileName);
            if (f.exists()) f.delete();
            fileList.removeView(card);
        });
        fileList.addView(card);
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            assert result != null;
            int cut = result.lastIndexOf('/');
            if (cut != -1) result = result.substring(cut + 1);
        }
        return result;
    }
}
