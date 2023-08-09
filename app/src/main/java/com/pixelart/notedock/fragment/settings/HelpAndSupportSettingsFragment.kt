package com.pixelart.notedock.fragment.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.pixelart.notedock.BR
import com.pixelart.notedock.R
import com.pixelart.notedock.adapter.settings.SettingsAdapter
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.databinding.FragmentHelpAndSupportSettingsBinding
import com.pixelart.notedock.domain.livedata.observer.EventObserver
import com.pixelart.notedock.ext.openLoginActivity
import com.pixelart.notedock.ext.openMailApp
import com.pixelart.notedock.model.SettingsModel
import com.pixelart.notedock.viewModel.settings.HelpAndSupportViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class HelpAndSupportSettingsFragment : Fragment(), SettingsAdapter.OnSettingsClickListener {

    private val helpAndSupportViewModel: HelpAndSupportViewModel by viewModel()

    private lateinit var dataBinding: FragmentHelpAndSupportSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        dataBinding = setupDataBinding(
            R.layout.fragment_help_and_support_settings,
            BR.viewmodel to helpAndSupportViewModel,
        )
        setHasOptionsMenu(true)
        helpAndSupportViewModel.lifecycleOwner = this
        return dataBinding.root
    }

    override fun onStart() {
        super.onStart()

        val settingsAdapter =
            SettingsAdapter(
                createSettings(),
                this,
            )
        dataBinding.recyclerViewHelpAndSupport.layoutManager = LinearLayoutManager(context)
        dataBinding.recyclerViewHelpAndSupport.adapter = settingsAdapter
        observeLiveData()
    }

    override fun onResume() {
        super.onResume()

        FirebaseAuth.getInstance().currentUser?.let { user ->
            user.reload()
                .addOnFailureListener { error ->
                    if (error is FirebaseNetworkException) {
                        // All is well
                    } else {
                        openLoginActivity()
                    }
                }
        }
    }

    private fun createSettings(): ArrayList<SettingsModel> {
        val settings = ArrayList<SettingsModel>()
        settings.add(SettingsModel(R.drawable.ic_bug, getString(R.string.send_bug_report)))
        settings.add(SettingsModel(R.drawable.ic_feedback, getString(R.string.send_feedback)))
        return settings
    }

    private fun observeLiveData() {
        helpAndSupportViewModel.onBackClicked.observe(
            viewLifecycleOwner,
            EventObserver {
                findNavController().popBackStack()
            },
        )
    }

    override fun onSettingClick(setting: SettingsModel) {
        when (setting.title) {
            getString(R.string.send_bug_report) -> {
                activity?.openMailApp("branislav.bily@gmail.com", "NoteDock: Send bug report")
            }

            getString(R.string.send_feedback) -> {
                activity?.openMailApp("branislav.bily@gmail.com", "NoteDock: Send feedback")
            }
        }
    }
}
