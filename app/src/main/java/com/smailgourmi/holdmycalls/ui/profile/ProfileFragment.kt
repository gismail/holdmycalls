package com.smailgourmi.holdmycalls.ui.profile

import android.app.Activity
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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.smailgourmi.holdmycalls.App
import com.smailgourmi.holdmycalls.R
import com.smailgourmi.holdmycalls.data.EventObserver
import com.smailgourmi.holdmycalls.data.model.ChatWithContactInfo
import com.smailgourmi.holdmycalls.databinding.FragmentProfileBinding
import com.smailgourmi.holdmycalls.ui.chat.ChatFragment
import com.smailgourmi.holdmycalls.ui.main.MainActivity
import com.smailgourmi.holdmycalls.util.convertTwoUserIDs
import com.smailgourmi.holdmycalls.util.forceHideKeyboard
import com.smailgourmi.holdmycalls.util.resizeImage
import com.smailgourmi.holdmycalls.util.showSnackBar


class ProfileFragment : Fragment() {

    companion object {
        const val ARGS_KEY_USER_ID = "bundle_user_id"
    }

    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(App.myUserID, requireArguments().getString(ARGS_KEY_USER_ID)!!)
    }
    private lateinit var viewDataBinding: FragmentProfileBinding

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle the selected image, you can get the URI from the result data
            val selectedImageUri = result.data?.data.let { uri ->
                if (uri != null) {
                    resizeImage(requireContext(), uri,48,48).let {
                        viewModel.changeContactImage(it)
                    }
                }
                // Do something with the selected image URI
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentProfileBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = viewModel
                lifecycleOwner = viewLifecycleOwner
            }
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupObservers() {
        viewModel.dataLoading.observe(viewLifecycleOwner,
            EventObserver { (activity as MainActivity).showGlobalProgressBar(it) })

        viewModel.snackBarText.observe(viewLifecycleOwner,
            EventObserver { text ->
                view?.showSnackBar(text)
                view?.forceHideKeyboard()
            })

        viewModel.editDisplayNameEvent.observe(viewLifecycleOwner,
            EventObserver { showEditTextDialog("Display Name:") })

        viewModel.editStatusEvent.observe(viewLifecycleOwner,
            EventObserver{showEditTextDialog("Status:")})

        viewModel.editImageEvent.observe(viewLifecycleOwner,
            EventObserver { startSelectImageIntent() })

        viewModel.selectedSendSMS.observe(viewLifecycleOwner,
            EventObserver{navigateToChat(it)})
    }

    private fun navigateToChat(chatWithContactInfo: ChatWithContactInfo) {
        val bundle = bundleOf(
            ChatFragment.ARGS_KEY_USER_ID to App.myUserID,
            ChatFragment.ARGS_KEY_USER_CONTACT_ID to chatWithContactInfo.mContactInfo.contactID,
            ChatFragment.ARGS_KEY_CHAT_ID to chatWithContactInfo.mChat.info.id
        )
        findNavController().navigate(R.id.action_navigation_chats_to_chatFragment, bundle)
    }

    private fun showEditTextDialog(title:String) {
        val input = EditText(requireActivity() as Context)
        AlertDialog.Builder(requireActivity()).apply {
            setTitle(title)
            setView(input)
            setPositiveButton("Ok") { _, _ ->
                val textInput = input.text.toString()
                if (!textInput.isBlank() && textInput.length <= 40) {
                    when(title){
                        "Display Name:" -> viewModel.changeDisplayName(textInput)
                        "Status:"->viewModel.changeStatus(textInput)
                    }

                }
            }
            setNegativeButton("Cancel") { _, _ -> }
            show()
        }
    }
    private fun startSelectImageIntent() {
        val selectImageIntent = Intent(Intent.ACTION_GET_CONTENT)
        selectImageIntent.type = "image/*"
        selectImageLauncher.launch(selectImageIntent)
    }
}