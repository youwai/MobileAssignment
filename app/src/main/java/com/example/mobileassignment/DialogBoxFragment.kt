package com.example.mobileassignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.mobileassignment.databinding.DialogBoxBinding

class DialogBoxFragment (private val selectedRecord: Materials) : DialogFragment() {
    private lateinit var binding: DialogBoxBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_box, container, false)

        binding.selectedMaterials = selectedRecord

        binding.okBtn.setOnClickListener {
            dismiss()
        }

        return binding.root
    }
}