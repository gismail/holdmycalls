package com.smailgourmi.holdmycalls.ui.settings

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.smailgourmi.holdmycalls.App
import com.smailgourmi.holdmycalls.R
import com.smailgourmi.holdmycalls.data.EventObserver
import com.smailgourmi.holdmycalls.data.db.repository.AuthRepository
import com.smailgourmi.holdmycalls.data.db.repository.DatabaseRepository
import com.smailgourmi.holdmycalls.data.db.repository.StorageRepository
import com.smailgourmi.holdmycalls.databinding.FragmentSettingsBinding
import com.smailgourmi.holdmycalls.util.SharedPreferencesUtil
import com.smailgourmi.holdmycalls.util.convertFileToByteArray
import com.smailgourmi.holdmycalls.util.resizeImage


class SettingsFragment : Fragment() {
    private val dbRepository: DatabaseRepository = DatabaseRepository()
    private val storageRepository: StorageRepository = StorageRepository()
    private val authRepository: AuthRepository = AuthRepository()
    private val viewModel: SettingsViewModel by viewModels { SettingsViewModelFactory(App.myUserID,dbRepository,storageRepository,authRepository) }

    private lateinit var viewDataBinding: FragmentSettingsBinding
    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle the selected image, you can get the URI from the result data
            val selectedImageUri = result.data?.data.let { uri ->
                if (uri != null) {
                    resizeImage(requireContext(), uri,48,48).let {
                        viewModel.changeUserImage(it)
                    }
                }
                // Do something with the selected image URI
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentSettingsBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = viewModel
                lifecycleOwner = this@SettingsFragment.viewLifecycleOwner
            }
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun setupObservers() {
        viewModel.editStatusEvent.observe(viewLifecycleOwner, EventObserver { showEditStatusDialog() })
        viewModel.editImageEvent.observe(viewLifecycleOwner, EventObserver { startSelectImageIntent() })
        viewModel.logoutEvent.observe(viewLifecycleOwner, EventObserver {
            SharedPreferencesUtil.removeUserID(requireContext())
            navigateToStart()
        })
    }

    private fun showEditStatusDialog() {
        val input = EditText(requireActivity() as Context)
        AlertDialog.Builder(requireActivity()).apply {
            setTitle("Status:")
            setView(input)
            setPositiveButton("Ok") { _, _ ->
                val textInput = input.text.toString()
                if (!textInput.isBlank() && textInput.length <= 40) {
                    viewModel.changeUserStatus(textInput)
                }
            }
            setNegativeButton("Cancel") { _, _ -> }
            show()
        }
    }

    private fun startSelectImageIntent() {
        val selectImageIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        selectImageLauncher.launch(selectImageIntent)
    }

    private fun navigateToStart() {
        findNavController().navigate(R.id.action_navigation_settings_to_startFragment)
    }
}