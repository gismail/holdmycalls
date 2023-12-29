package com.smailgourmi.holdmycalls.ui.call

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telecom.DisconnectCause
import android.telecom.TelecomManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.smailgourmi.holdmycalls.App
import com.smailgourmi.holdmycalls.data.EventObserver
import com.smailgourmi.holdmycalls.databinding.FragmentCallBinding
import com.smailgourmi.holdmycalls.sms.SmsReceiverService
import com.smailgourmi.holdmycalls.ui.main.SMS_PERMISSION_REQUEST
import com.smailgourmi.holdmycalls.util.isPhoneValid

class CallFragment: Fragment() {

    companion object {
        const val ARGS_KEY_USER_ID = "bundle_user_id"
        const val ARGS_KEY_USER_CONTACT_ID = "bundle_user_contact_id"
    }
    private val viewModel: CallViewModel by viewModels {
        CallViewModelFactory(requireArguments().getString(ARGS_KEY_USER_ID)!!, requireArguments().getString(ARGS_KEY_USER_CONTACT_ID)!!)
    }
    private lateinit var viewDataBinding: FragmentCallBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentCallBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = viewModel
                lifecycleOwner = viewLifecycleOwner
            }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.selectedCall.observe(viewLifecycleOwner,EventObserver{
            makeCall()
        })
    }

    private fun makeCall() {
        val telecomManager = activity?.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        val phoneNumber = viewModel.userContact.value?.phoneNumber ?: ""
        if(isPhoneValid(phoneNumber)){
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$phoneNumber")

            if (ActivityCompat.checkSelfPermission(
                    this.requireContext(),
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                this.activity?.let {
                    val REQUEST_CODE_PERMISSION_CALL_PHONE = 3
                    ActivityCompat.requestPermissions(
                        it,
                        arrayOf(Manifest.permission.CALL_PHONE),
                        REQUEST_CODE_PERMISSION_CALL_PHONE
                    )
                }
            }

            val uri = Uri.fromParts("tel", phoneNumber, null)
            val extras = Bundle()
            telecomManager.placeCall(uri,extras)
        }

    }

    private fun endCall(){
        val viewModel = ViewModelProvider(this).get(CallViewModel::class.java)
        val connection = viewModel.outgoingConnection.value
        if(connection != null) {
            connection.setDisconnected(DisconnectCause.LOCAL as DisconnectCause)
        }
    }
}