package com.gws.ussd.ui.server

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gws.common.utils.UssdNavigation
import com.gws.networking.model.SerEntity
import dagger.hilt.android.AndroidEntryPoint
import com.gws.ussd.databinding.FragmentDetailsBinding

@AndroidEntryPoint
class ServerFragment : Fragment() {


    private lateinit var binding: FragmentDetailsBinding
    private val viewModel by viewModels<ServerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupData()
        binding.loginButton.setOnClickListener {
            //check if all fields are filled
            if (binding.serverName.text.toString().isEmpty() ||
                binding.dbName.text.toString().isEmpty() ||
                binding.userName.text.toString().isEmpty() ||
                binding.dbPassword.text.toString().isEmpty()
            ) {
                Toast.makeText(
                    requireContext(),
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
                ).show()
            }else {
                val serverEntity = SerEntity(
                    servername = binding.serverName.text.toString(),
                    dbname = binding.dbName.text.toString(),
                    username = binding.userName.text.toString(),
                    dbpassword = binding.dbPassword.text.toString()
                )
                viewModel.saveServer(serverEntity)
                val goLogin =
                    ServerFragmentDirections.actionServerFragmentToLoginFragment(
                        server = serverEntity
                    )
                UssdNavigation.navigate(findNavController(), goLogin)
            }
        }
    }

    private fun setupData() {
        binding.serverName.setText("gws.ma")
        binding.dbName.setText("gwsma_owi")
        binding.userName.setText("gwsma_owi")
        binding.dbPassword.setText("2ZGv7qSp92iHbhp")
    }

}
