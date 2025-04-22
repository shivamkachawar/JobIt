package com.hackspectra.jobit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class GeneralFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_general, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = GeneralFragment().apply {
            arguments = Bundle().apply {
                // You can pass parameters here if needed in the future
            }
        }
    }
}