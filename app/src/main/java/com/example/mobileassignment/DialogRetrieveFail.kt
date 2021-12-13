package com.example.mobileassignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.mobileassignment.databinding.FragmentDialogRetrieveFailBinding

class DialogRetrieveFail: DialogFragment() {

    private lateinit var binding: FragmentDialogRetrieveFailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_retrieve_fail, container, false)

        //Close the dialog with button
        binding.retrieveFailButton.setOnClickListener{
            dismiss()
        }

        return binding.root
    }
}