package com.smailgourmi.holdmycalls.ui.contacts

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.smailgourmi.holdmycalls.App
import com.smailgourmi.holdmycalls.R
import com.smailgourmi.holdmycalls.data.EventObserver
import com.smailgourmi.holdmycalls.databinding.FragmentUsersBinding
import com.smailgourmi.holdmycalls.ui.profile.ProfileFragment

const val READ_CONTACTS_PERMISSION_REQUEST = 2
class ContactsFragment : Fragment(){

    private val activityViewModel: ContactsViewModel by activityViewModels() { UsersViewModelFactory(App.myUserID,this) }
    private lateinit var viewDataBinding: FragmentUsersBinding
    private lateinit var listAdapter: ContactsListAdapter
    private var contactPicker = ContactPicker(this)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding =
            FragmentUsersBinding.inflate(inflater, container, false).apply {
                viewmodel = activityViewModel
                lifecycleOwner = viewLifecycleOwner
            }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListAdapter()
        setupObservers()
        requestContactPermission()
    }

    private fun requestContactPermission() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (!isGranted) {
                    // Handle the case where permission is denied
                    // You may show a message or take appropriate action

                }else{
                    setupAddContact()
                }
            }
        // Permission is not granted, request it
        requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
    }

    private fun setupAddContact() {

        viewDataBinding.floatingActionButton.setOnClickListener{
            openContactsPicker()
        }
    }
    private fun openContactsPicker() {
        contactPicker.openContactsPicker()
    }
    private fun setupListAdapter() {
        listAdapter = ContactsListAdapter(activityViewModel)
        viewDataBinding.usersRecyclerView.adapter = listAdapter
    }
    private fun setupObservers() {
        activityViewModel.selectedContact.observe(viewLifecycleOwner, EventObserver { navigateToProfile(it.contactID) })
    }

    private fun navigateToProfile(userID: String) {
        val bundle = bundleOf(ProfileFragment.ARGS_KEY_USER_ID to userID)
        findNavController().navigate(R.id.action_navigation_users_to_profileFragment, bundle)
    }

    override fun onPause() {
        super.onPause()
        activityViewModel.selectedContact.removeObservers(viewLifecycleOwner)
    }


}