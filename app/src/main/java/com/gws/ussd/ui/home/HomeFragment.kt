package com.gws.ussd.ui.home

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.auth.api.credentials.Credential
import com.gws.common.utils.UssdVerticalItemDecoration
import com.gws.local_models.models.Ussd
import com.gws.local_models.models.duplicateSteps
import com.gws.networking.response.ResourceResponse
import com.gws.ussd.MainActivity
import com.gws.ussd.databinding.FragmentHomeBinding
import com.gws.ussd.service.UssdBackgroundService
import com.romellfudi.ussdlibrary.USSDApi
import com.romellfudi.ussdlibrary.USSDController
import dagger.hilt.android.AndroidEntryPoint
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import javax.inject.Inject
import kotlin.math.roundToInt


@AndroidEntryPoint
class HomeFragment : Fragment() {

    @Inject
    lateinit var ussdApi: USSDApi
    var ussdList: MutableList<Ussd> = mutableListOf()

    var result = ""
    var finish = false
    var currentCodeIndex = 0
    var currentIndex = 0
    var codeList: List<String> = emptyList()
    val map = hashMapOf(
        "KEY_LOGIN" to listOf("espere", "waiting", "loading", "esperando"),
        "KEY_ERROR" to listOf("problema", "problem", "error", "null")
    )

    private lateinit var binding: FragmentHomeBinding
    private val viewModel by activityViewModels<HomeViewModel>()
    private lateinit var serviceIntent: Intent
    private val handler = Handler()


    private val ussdListAdapter: UssdListAdapter by lazy {
        UssdListAdapter()
    }
    private val phoneNumberUtil: PhoneNumberUtil by lazy {
        PhoneNumberUtil.createInstance(requireContext())
    }

    private val hintPhoneNumberLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            if (result != null && result.data != null) {
                val data = result.data
                val credential: Credential? = data?.getParcelableExtra(Credential.EXTRA_KEY)
                val retrievedPhoneNumber = credential?.id
                val phoneNumberProto = phoneNumberUtil.parse(
                    retrievedPhoneNumber,
                    "MA"
                )
                val retrievedPhoneNumberFormatted = phoneNumberUtil.format(
                    phoneNumberProto,
                    PhoneNumberUtil.PhoneNumberFormat.NATIONAL
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        serviceIntent = Intent(requireContext(), UssdBackgroundService::class.java)
        viewModel.getUssdList()
        // Permission denied; request the permission
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_PHONE_NUMBERS
            )
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            )
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_PHONE_STATE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            showPermissionRationaleDialog(requireContext())
        }
        setupMoviesList()

        binding.valider.setOnClickListener {
            scheduleButtonClick()
            runUSSDWithCodeList()
        }
        binding.refresh.setOnClickListener {
            viewModel.getUssdList()
        }
    }

    fun sendNextCode() {
        if (currentCodeIndex < codeList.size) {
            val code = codeList[currentCodeIndex]
            ussdApi.send(code) { response ->
                result += "\n-\n$response"
                finish = false
                // Sleep for a moment (optional)
                try {
                    Thread.sleep(300) // Sleep for 5 seconds
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                currentCodeIndex++
                sendNextCode()
            }
        } else {
            // All codes in the list have been processed
        }
    }

    fun runUSSDWithCodeList() {
        if (currentIndex < ussdList.size) {
            currentCodeIndex = 0
            val ussd = ussdList[currentIndex]
            codeList = ussd.duplicateSteps()

            ussdApi.callUSSDInvoke(
                requireActivity(),
                ussd.ussd ?: "",
                0,
                map,
                object : USSDController.CallbackInvoke {
                    override fun responseInvoke(message: String) {
                        result += "\n-\n$message"
                        sendNextCode()
                    }

                    override fun over(message: String) {
                        result += "\n-\n$message"
                        when {
                            finish -> {
                                ussd.reponceussd = message
                                ussd.etat = "1"
                                viewModel?.updateList(ussd)
                                currentIndex++
                                runUSSDWithCodeList()
                            }

                            else -> {
                                ussd.reponceussd = message
                                ussd.etat = "0"
                                viewModel?.updateList(ussd)
                                currentIndex++
                                runUSSDWithCodeList()
                            }
                        }
                    }
                })
        }
    }


    private fun showPermissionRationaleDialog(context: Context) {
        val dialogBuilder = AlertDialog.Builder(context)

        dialogBuilder.setTitle("Autorisation requise")
        dialogBuilder.setMessage("Pour vous offrir le meilleur service, nous avons besoin d'accéder à votre numéro de téléphone. Veuillez accorder la permission.")
        dialogBuilder.setPositiveButton("OK") { dialog: DialogInterface, _: Int ->

            // User has previously denied the permission without explanation, provide an option to open settings

            ActivityCompat.requestPermissions(
                requireActivity(), // Cast the context to an Activity
                arrayOf(
                    Manifest.permission.READ_PHONE_NUMBERS,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_PHONE_STATE
                ),
                123
            )
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
            // Handle the case where the user clicks "Cancel" (optional)
            dialog.dismiss()
        }

        val dialog = dialogBuilder.create()
        dialog.show()
    }


    private fun subscribe() {
        viewModel.ussdList.observe(viewLifecycleOwner) {
            when (it) {
                is ResourceResponse.Loading -> {
//                    (requireActivity() as? MainActivity)?.showLoader()
                }

                is ResourceResponse.Error -> {
                    (requireActivity() as? MainActivity)?.hideLoader()
                }

                is ResourceResponse.Success -> {
                    (requireActivity() as? MainActivity)?.hideLoader()
                    ussdListAdapter.setUssdList(it.data ?: emptyList())
                    ussdList.clear()
                    ussdList.addAll(it.data ?: emptyList())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        subscribe()
    }

    private fun setupMoviesList() {
        val itemSpacing =
            resources.getDimension(com.gws.ussd.ui_core.R.dimen.movie_item_spacing)
        val itemDecoration = UssdVerticalItemDecoration(1, itemSpacing.roundToInt())
        val recyclerViewLayoutManager = GridLayoutManager(
            requireContext(),
            1
        )
        binding.ussdRecycler.apply {
            clipToPadding = false
            clipChildren = false
            adapter = ussdListAdapter
            layoutManager = recyclerViewLayoutManager
            setHasFixedSize(true)
            if (itemDecorationCount == 0) {
                addItemDecoration(itemDecoration)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearList()
        handler.removeCallbacksAndMessages(null)
    }

    private fun scheduleButtonClick() {
        handler.postDelayed({
            binding.valider.performClick()
            scheduleButtonClick()
        }, 600_000) // 600,000 milliseconds = 10 minutes
    }

}
