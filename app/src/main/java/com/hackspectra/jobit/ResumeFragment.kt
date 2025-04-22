package com.hackspectra.jobit

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.hackspectra.jobit.view.TailoredResumeFragment
import com.hackspectra.jobit.viewModel.ResumeViewModel

class ResumeFragment : Fragment() {
    // Use activityViewModels delegate to share the ViewModel with TailoredResumeFragment
    private val viewModel: ResumeViewModel by activityViewModels()

    private lateinit var selectResumeButton: Button
    private lateinit var resumeNameTextView: TextView
    private lateinit var jobDescriptionEditText: EditText
    private lateinit var generateButton: Button

    private var resumeUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_resume, container, false)

        selectResumeButton = view.findViewById(R.id.select_resume_button)
        resumeNameTextView = view.findViewById(R.id.resume_name_text)
        jobDescriptionEditText = view.findViewById(R.id.job_description_edit_text)
        generateButton = view.findViewById(R.id.generate_button)

        selectResumeButton.setOnClickListener { selectResume() }
        generateButton.setOnClickListener { generateTailoredResume() }

        setupObservers()

        return view
    }

    private fun setupObservers() {
        viewModel.processingState.observe(viewLifecycleOwner) { state ->
            if (state.isLoading) {
                // Show loading indicator
                generateButton.isEnabled = false
                generateButton.text = "Processing..."
                // You could also add a progress indicator here
                view?.findViewById<View>(R.id.progress_indicator)?.visibility = View.VISIBLE
            } else {
                generateButton.isEnabled = true
                generateButton.text = "Generate Tailored Resume"
                view?.findViewById<View>(R.id.progress_indicator)?.visibility = View.GONE

                state.error?.let { error ->
                    Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                }

                if (state.tailoredResume != null) {
                    val intent = Intent(requireContext(), TailoredResumeActivity::class.java).apply {
                        putExtra("resume_content", state.tailoredResume)
                        putExtra("ats_score", state.atsScore)
                    }
                    startActivity(intent)
                }

            }
        }
    }

    private fun selectResume() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }
        startActivityForResult(intent, REQUEST_PDF_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_PDF_PICK && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                resumeUri = uri

                // Get file name
                val fileName = getFileNameFromUri(uri)
                resumeNameTextView.text = fileName
            }
        }
    }

    private fun getFileNameFromUri(uri: Uri): String {
        var fileName = "Selected File"

        context?.contentResolver?.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex)
                }
            }
        }

        return fileName
    }

    private fun generateTailoredResume() {
        val resumeUri = this.resumeUri
        if (resumeUri == null) {
            Toast.makeText(context, "Please select a resume first", Toast.LENGTH_SHORT).show()
            return
        }

        val jobDescription = jobDescriptionEditText.text.toString()
        if (jobDescription.isEmpty()) {
            Toast.makeText(context, "Please enter a job description", Toast.LENGTH_SHORT).show()
            return
        }

        // Process the resume through ViewModel
        context?.let { context ->
            viewModel.processResume(context, resumeUri, jobDescription)
        }
    }

    companion object {
        private const val REQUEST_PDF_PICK = 1
    }
}