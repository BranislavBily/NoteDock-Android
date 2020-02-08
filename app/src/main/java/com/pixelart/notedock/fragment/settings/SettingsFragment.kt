package com.pixelart.notedock.fragment.settings


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pixelart.notedock.BR
import com.pixelart.notedock.NavigationRouter

import com.pixelart.notedock.R
import com.pixelart.notedock.activity.LoginActivity
import com.pixelart.notedock.adapter.settings.SettingsAdapter
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.domain.livedata.observer.EventObserver
import com.pixelart.notedock.ext.showAsSnackBar
import com.pixelart.notedock.model.SettingsModel
import com.pixelart.notedock.viewModel.settings.SettingsFragmentViewModel
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.android.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment(), SettingsAdapter.OnSettingsClickListener {

    private val settingFragmentViewModel: SettingsFragmentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_settings,
            BR.viewmodel to settingFragmentViewModel
        )
        setHasOptionsMenu(true)
        settingFragmentViewModel.lifecycleOwner = this
        return dataBinding.root
    }

    override fun onStart() {
        super.onStart()

        observeLiveData()
        val settingsAdapter =
            SettingsAdapter(
                createSettings(),
                this
            )
        recyclerViewSettings.layoutManager = LinearLayoutManager(context)
        recyclerViewSettings.adapter = settingsAdapter
    }

    private fun observeLiveData() {

        settingFragmentViewModel.onBackClicked.observe(this, EventObserver {
            findNavController().popBackStack()
        })

        settingFragmentViewModel.signedOut.observe(this, EventObserver {
            context?.let {context ->
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        })
    }

    override fun onSettingClick(setting: SettingsModel) {
        when(setting.title) {
            getString(R.string.account) -> NavigationRouter(view).settingsToAccount()
            getString(R.string.changePassword) -> NavigationRouter(view).settingsToChangePassword()
            getString(R.string.rateUs) ->  view?.let { R.string.not_on_appstore.showAsSnackBar(it) }
            getString(R.string.helpAndSupport) -> NavigationRouter(view).settingsToHelpAndSupport()
            else -> settingFragmentViewModel.logOut()
        }
    }

    private fun createSettings(): ArrayList<SettingsModel> {
        val settings = ArrayList<SettingsModel>()
        settings.add(SettingsModel(R.drawable.ic_account, getString(R.string.account)))
        settings.add(SettingsModel(R.drawable.ic_password, getString(R.string.changePassword)))
        settings.add(SettingsModel(R.drawable.ic_rateus, getString(R.string.rateUs)))
        settings.add(SettingsModel(R.drawable.ic_help, getString(R.string.helpAndSupport)))
        settings.add(SettingsModel(R.drawable.ic_logout, getString(R.string.log_out)))
        return settings
    }
}