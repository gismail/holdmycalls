package com.smailgourmi.holdmycalls.ui.start.login

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.smailgourmi.holdmycalls.R
import com.smailgourmi.holdmycalls.data.EventObserver
import com.smailgourmi.holdmycalls.databinding.FragmentLoginBinding
import com.smailgourmi.holdmycalls.ui.main.MainActivity
import com.smailgourmi.holdmycalls.util.SharedPreferencesUtil
import com.smailgourmi.holdmycalls.util.forceHideKeyboard
import com.smailgourmi.holdmycalls.util.showSnackBar

class LoginFragment : Fragment() {

    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var viewDataBinding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentLoginBinding.inflate(inflater, container, false)
            .apply { viewmodel = viewModel }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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

        viewModel.isLoggedInEvent.observe(viewLifecycleOwner, EventObserver {
            SharedPreferencesUtil.saveUserID(requireContext(), it.uid)
            navigateToChats()
        })

        viewModel.forgotClicked.observe(viewLifecycleOwner,EventObserver{
            processForgotPWD()
        })

        viewModel.signUpClicked.observe(viewLifecycleOwner,EventObserver{
            navigateToCreateAccount()
        })
    }

    private fun processForgotPWD() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this.requireContext())
        val dialogView: View = layoutInflater.inflate(R.layout.dialog_forgot, null)
        val emailBox: EditText = dialogView.findViewById(R.id.emailBox)
        val emailResult : TextView = dialogView.findViewById(R.id.emailResult)
        builder.setView(dialogView)
        val dialog: AlertDialog = builder.create()
        dialogView.findViewById<Button>(R.id.btnReset)
            .setOnClickListener{
                val userEmail = emailBox.text.toString()
                if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(
                        userEmail
                    ).matches()
                ) {
                    emailResult.text = "Enter your registered email id"
                    emailResult.visibility = View.VISIBLE
                    emailResult.setTextColor(Color.RED)
                    Handler(Looper.getMainLooper()).postDelayed({
                        // Set the visibility of the TextView to GONE after the delay
                        emailResult.visibility = View.GONE
                    }, 3000)
                }else{
                    viewModel.authRepository.resetPasswordwithEmail(userEmail){
                        if(it is com.smailgourmi.holdmycalls.data.Result.Success){
                            emailResult.text = it.data.toString()
                            emailResult.visibility = View.VISIBLE
                            emailResult.setTextColor(Color.GREEN)
                        }else if (it is com.smailgourmi.holdmycalls.data.Result.Error){
                            emailResult.text = it.msg.toString()
                            emailResult.visibility = View.VISIBLE
                            emailResult.setTextColor(Color.RED)
                            Handler(Looper.getMainLooper()).postDelayed({
                                // Set the visibility of the TextView to GONE after the delay
                                emailResult.visibility = View.GONE
                            }, 3000)
                            //view?.showSnackBar(it.msg.toString())
                        }
                    }
                }

            }
        dialogView.findViewById<Button>(R.id.btnCancel)
            .setOnClickListener{
                dialog.dismiss()
            }
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        dialog.show()
    }

    private fun navigateToChats() {
        findNavController().navigate(R.id.action_loginFragment_to_navigation_chats)
    }
    private fun navigateToCreateAccount() {
        findNavController().navigate(R.id.action_startFragment_to_createAccountFragment)
    }
}