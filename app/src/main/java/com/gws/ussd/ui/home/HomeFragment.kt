package com.gws.ussd.ui.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
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
import com.gws.networking.providers.CurrentUserProvider
import com.gws.networking.response.ResourceResponse
import com.gws.ussd.MainActivity
import com.gws.ussd.databinding.FragmentHomeBinding
import com.gws.ussd.service.UssdBackgroundService
import com.romellfudi.ussdlibrary.USSDApi
import com.romellfudi.ussdlibrary.USSDController
import com.romellfudi.ussdlibrary.contains
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

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

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
            if (verifyAccessibilityAccess()) {
                viewModel.getUssdList(getNumberOfSimCards(requireContext()))
            }
        }
    }

    private fun runFullProcess() {
        scheduleButtonClick()
        //check if list is not empty then runUSSDWithCodeList else exit method
        if (ussdList.isNotEmpty()) {
            return
        } else
            if (verifyAccessibilityAccess()) {
                viewModel.getUssdList(getNumberOfSimCards(requireContext()))
            }

        runUSSDWithCodeList()
    }

    fun getNumberOfSimCards(context: Context): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            val subscriptionManager =
                requireActivity().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return 0
            }
            val activeSubscriptions: List<SubscriptionInfo> =
                subscriptionManager.activeSubscriptionInfoList

            return activeSubscriptions.size
        } else {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return if (telephonyManager.simState == TelephonyManager.SIM_STATE_READY) 1 else 0
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
                    Thread.sleep(1000) // Sleep for 1 seconds
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                currentCodeIndex++
                sendNextCode()
            }
        } else {
            // All codes in the list have been processed
            ussdList.removeAt(currentIndex)
            ussdListAdapter.setUssdList(ussdList)
        }
    }

    fun runUSSDWithCodeList() {
        if (currentIndex < ussdList.size) {
            currentCodeIndex = 0
            val ussd = ussdList[currentIndex]
            codeList = ussd.duplicateSteps()

            try {
                Thread.sleep(3000) // Sleep for 3 seconds
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

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
        } else ussdList.clear()
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
        if (verifyAccessibilityAccess()) {
            viewModel.getUssdList(getNumberOfSimCards(requireContext()))
        }
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
        currentUserProvider.currentUser()?.let {
            handler.postDelayed({
                binding.valider.performClick()
                scheduleButtonClick()
            }, (it.refresh.toInt()*1000).toLong()) // 1000 milliseconds = 1 second
        }
    }

    fun verifyAccessibilityAccess(): Boolean =
        isAccessibilityServicesEnable(requireContext()).also {
            if (!it) openSettingsAccessibility(requireActivity())
        }

    fun isAccessibilityServicesEnable(context: Context): Boolean {
        (context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager)?.apply {
            installedAccessibilityServiceList.forEach { service ->
                if (service.id.contains(context.packageName) &&
                    Settings.Secure.getInt(
                        context.applicationContext.contentResolver,
                        Settings.Secure.ACCESSIBILITY_ENABLED
                    ) == 1
                )
                    Settings.Secure.getString(
                        context.applicationContext.contentResolver,
                        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                    )?.let {
                        if (it.split(':').contains(service.id)) return true
                    }
            }
        }
        return false
    }

    private fun openSettingsAccessibility(activity: Activity) =
        with(android.app.AlertDialog.Builder(activity)) {
            setTitle("USSD Accessibility permission")
            setMessage("You must enable accessibility permissions for the app Ussd")
            setCancelable(true)
            setNeutralButton("ok") { _, _ ->
                activity.startActivityForResult(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), 1)
            }
            create().show()
        }
}
