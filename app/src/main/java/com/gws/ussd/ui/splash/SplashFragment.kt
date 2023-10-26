package com.gws.ussd.ui.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.gws.common.utils.UssdNavigation
import com.gws.networking.providers.CurrentUserProvider
import com.gws.ussd.MainActivity
import com.gws.ussd.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.delay
import timber.log.Timber

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider
    private var remoteConfig: FirebaseRemoteConfig? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 10
        }
        remoteConfig?.setConfigSettingsAsync(configSettings)
        fetchRemoteConfig()

    }

    private fun fetchRemoteConfig() {
        remoteConfig?.fetchAndActivate()
            ?.addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val remoteConfigData = remoteConfig?.getString("allowApp")
                    if(remoteConfigData.toString() == "true"){
                        lifecycleScope.launchWhenResumed {
                            delay(2000)
                            if (currentUserProvider.currentUser() == null) {
                                val goLogin =
                                    SplashFragmentDirections.actionSplashFragmentToServerFragment()
                                UssdNavigation.navigate(findNavController(), goLogin)
                            } else {
                                startActivity(Intent(requireActivity(), MainActivity::class.java))
                                requireActivity().finish()
                            }
                        }
                    }else
                        Toast.makeText(
                            requireContext(),
                            "Trials version is expired, Contact Developer for more information",
                            Toast.LENGTH_SHORT
                        ).show()
                } else {
                    Toast.makeText(
                        requireContext(), "Une erreur est survenue",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

}
