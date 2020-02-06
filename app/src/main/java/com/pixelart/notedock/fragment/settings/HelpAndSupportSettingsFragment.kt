package com.pixelart.notedock.fragment.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pixelart.notedock.BR
import com.pixelart.notedock.R
import com.pixelart.notedock.adapter.settings.SettingsAdapter
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.domain.livedata.observer.EventObserver
import com.pixelart.notedock.ext.showAsSnackBar
import com.pixelart.notedock.model.SettingsModel
import com.pixelart.notedock.viewModel.settings.HelpAndSupportViewModel
import kotlinx.android.synthetic.main.fragment_help_and_support_settings.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


class HelpAndSupportSettingsFragment : Fragment(), SettingsAdapter.OnSettingsClickListener {

    private val helpAndSupportViewModel: HelpAndSupportViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_help_and_support_settings,
            BR.viewmodel to helpAndSupportViewModel
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
                this
            )
        recycler_view_help_and_support.layoutManager = LinearLayoutManager(context)
        recycler_view_help_and_support.adapter = settingsAdapter
        observeLiveData()
    }

    private fun createSettings(): ArrayList<SettingsModel> {
        val settings = ArrayList<SettingsModel>()
        settings.add(SettingsModel(R.drawable.ic_bug, getString(R.string.send_bug_report)))
        settings.add(SettingsModel(R.drawable.ic_feedback, getString(R.string.send_feedback)))
        return settings
    }

    private fun observeLiveData() {
        helpAndSupportViewModel.onBackClicked.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })
    }

    override fun onSettingClick(setting: SettingsModel) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_EMAIL, Array(1) { "branislav.bily@gmail.com"})
        intent.type = "message/rfc822"
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        when(setting.title) {
            getString(R.string.send_bug_report) -> {
                intent.putExtra(Intent.EXTRA_SUBJECT, "NoteDock: Bug report")
            }
            getString(R.string.send_feedback) -> {
                intent.putExtra(Intent.EXTRA_SUBJECT, "NoteDock: Feedback")
            }
        }
        try {
            startActivity(Intent.createChooser(intent, "Send mail..."))
        } catch (e: ActivityNotFoundException) {
            view?.let { R.string.no_email_client_installed.showAsSnackBar(it) }
        }
    }
}
