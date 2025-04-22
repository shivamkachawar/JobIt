package com.hackspectra.jobit

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.hackspectra.jobit.R
import com.hackspectra.jobit.viewModel.ResumeViewModel
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.tool.xml.XMLWorkerHelper
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.charset.Charset

class TailoredResumeActivity : AppCompatActivity() {

    private val viewModel: ResumeViewModel by viewModels()

    private lateinit var atsScoreTextView: TextView
    private lateinit var resumeWebView: WebView
    private lateinit var downloadButton: Button

    private var tailoredResumeContent: String? = null
    private var atsScore: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tailored_resume)

        atsScoreTextView = findViewById(R.id.ats_score_text)
        resumeWebView = findViewById(R.id.resume_web_view)
        downloadButton = findViewById(R.id.download_button)

        tailoredResumeContent = intent.getStringExtra("resume_content")
        atsScore = intent.getIntExtra("ats_score", 0)

        if (tailoredResumeContent != null) {
            atsScoreTextView.text = "ATS Score: $atsScore/100"
            displayResumeInWebView(tailoredResumeContent!!)
        }

        downloadButton.setOnClickListener {
            downloadResume()
        }
    }

    private fun displayResumeInWebView(resumeContent: String) {
        val htmlContent = formatToCompactHtml(resumeContent)
        resumeWebView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    private fun downloadResume() {
        val content = tailoredResumeContent ?: return

        try {
            val fileName = "Tailored_Resume_${System.currentTimeMillis()}.pdf"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                }

                val resolver = contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)

                uri?.let { safeUri ->
                    resolver.openOutputStream(safeUri)?.use { outputStream ->
                        generatePDF(outputStream, formatToCompactHtml(content))
                        Toast.makeText(this, "Resume downloaded to Downloads folder", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, fileName)

                FileOutputStream(file).use { outputStream ->
                    generatePDF(outputStream, formatToCompactHtml(content))
                    Toast.makeText(this, "Resume downloaded to Downloads folder", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error downloading resume: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun generatePDF(outputStream: OutputStream, content: String) {
        try {
            val document = Document()
            PdfWriter.getInstance(document, outputStream)
            document.open()

            val htmlInput = content.byteInputStream()
            XMLWorkerHelper.getInstance().parseXHtml(
                PdfWriter.getInstance(document, outputStream),
                document,
                htmlInput,
                null,
                Charset.forName("UTF-8")
            )

            document.close()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    private fun formatToCompactHtml(content: String): String {
        return """
            <html>
            <head>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        font-size: 10px;
                        line-height: 1.4;
                        margin: 20px;
                    }
                    h1, h2 {
                        font-size: 12px;
                        margin-bottom: 4px;
                    }
                    .section {
                        margin-bottom: 10px;
                    }
                    ul {
                        padding-left: 16px;
                        list-style-type: disc;
                        margin: 4px 0;
                    }
                    ul ul {
                        padding-left: 16px;
                        list-style-type: circle;
                    }
                    li {
                        margin-bottom: 4px;
                    }
                </style>
            </head>
            <body>
                $content
            </body>
            </html>
        """.trimIndent()
    }
}