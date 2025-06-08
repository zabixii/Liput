package com.example.liput

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var fileList: LinearLayout
    private lateinit var filesDirPath: File

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        uris?.forEach { uri ->
            val fileName = getFileName(uri)
            val inputStream = contentResolver.openInputStream(uri)
            val outFile = File(filesDirPath, fileName)
            val outputStream = FileOutputStream(outFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            addFileCard(fileName)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fileList = findViewById(R.id.file_list)
        filesDirPath = filesDir

        findViewById<MaterialButton>(R.id.import_button).setOnClickListener {
            filePickerLauncher.launch(arrayOf("text/html"))
        }

        filesDirPath.listFiles()?.forEach {
            addFileCard(it.name)
        }
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path?.substringAfterLast('/')
        }
        return result ?: "unknown.html"
    }

    private fun addFileCard(fileName: String) {
        val card = LayoutInflater.from(this).inflate(R.layout.file_card, null)
        card.findViewById<TextView>(R.id.file_title).text = fileName.removeSuffix(".html").removeSuffix(".mht")
        card.findViewById<MaterialButton>(R.id.open_button).setOnClickListener {
            val intent = Intent(this, ViewerActivity::class.java)
            intent.putExtra("file", fileName)
            startActivity(intent)
        }
        card.findViewById<MaterialButton>(R.id.delete_button).setOnClickListener {
            File(filesDirPath, fileName).delete()
            fileList.removeView(card)
        }
        fileList.addView(card)
    }
}
