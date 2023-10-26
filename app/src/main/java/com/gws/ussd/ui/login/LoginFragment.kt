package com.gws.ussd.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.gws.networking.request.LoginRequest
import com.gws.networking.response.ResourceResponse
import com.gws.ussd.MainActivity
import com.gws.ussd.databinding.FragmentLoginBinding
import com.gws.ussd.ui.splash.SplashActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()
    private val args: LoginFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val server = args.server
        binding.loginButton.setOnClickListener {
            //check if fields are not empty
            if (binding.login.text.isNullOrEmpty() || binding.password.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.errorMessage.visibility = View.GONE
                (requireActivity() as? SplashActivity)?.showLoader()
                hideKeyboardWithView(requireContext(), binding.password)
                server?.let { server ->
                    val LoginRequest = LoginRequest(
                        server.servername,
                        server.dbname,
                        server.username,
                        server.dbpassword,
                        binding.login.text.toString(),
                        binding.password.text.toString()
                    )
                    viewModel.login(LoginRequest)

                }
            }
        }
        subscribe()
    }

    fun hideKeyboardWithView(context: Context, view: View? = null) {
        val imm: InputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE)
                as InputMethodManager

        if (view != null) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
    private fun subscribe() {
        viewModel.login.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ResourceResponse.Loading -> {
                    (requireActivity() as? SplashActivity)?.showLoader()
                }

                is ResourceResponse.Success -> {
                    binding.errorMessage.visibility = View.GONE
                    lifecycleScope.launchWhenResumed {
                        delay(2000)
                        (requireActivity() as? SplashActivity)?.hideLoader()
                        startActivity(Intent(requireActivity(), MainActivity::class.java))
                        requireActivity().finish()
                    }
                }

                is ResourceResponse.Error -> {
                    (requireActivity() as? SplashActivity)?.hideLoader()
                    binding.errorMessage.visibility = View.VISIBLE
                }
            }
        }
    }

}
