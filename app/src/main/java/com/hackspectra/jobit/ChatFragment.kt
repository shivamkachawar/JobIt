package com.hackspectra.jobit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.kommunicate.KmConversationBuilder
import io.kommunicate.Kommunicate
import io.kommunicate.callbacks.KmCallback

class ChatFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Kommunicate with your App ID
        Kommunicate.init(requireContext(), "34fb377a951c81837cbf44465048a19bf")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        // Start loading the chatbot
        launchKommunicateChat(view)

        return view
    }

    private fun launchKommunicateChat(view: View) {
        // Show loading animation while the chatbot is loading
        val progressBar = view.findViewById<ProgressBar>(R.id.loadingSpinner)

        // Hide the chat placeholder and progress bar after the chat is initialized
        KmConversationBuilder(requireContext())
            .createConversation(object : KmCallback {
                override fun onSuccess(conversationId: Any?) {
                    // Hide the loading spinner and placeholder text
                    progressBar.visibility = View.GONE

                    // Open the conversation (chat)
                    Kommunicate.openConversation(requireContext())
                }

                override fun onFailure(error: Any?) {
                    // Log the error and show a Toast message if the chat fails
                    Log.e("KommunicateChat", "Chat launch failed: $error")
                    Toast.makeText(requireContext(), "Chat failed to launch", Toast.LENGTH_SHORT).show()

                    // Hide the loading spinner on failure
                    progressBar.visibility = View.GONE
                }
            })
    }
}