package com.pixelart.notedock.fragment


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.pixelart.notedock.BR
import com.pixelart.notedock.NavigationRouter
import com.pixelart.notedock.R
import com.pixelart.notedock.adapter.FoldersAdapter
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.dialog.CreateFolderDialog
import com.pixelart.notedock.dialog.FolderDialogSuccessListener
import com.pixelart.notedock.model.FolderModel
import com.pixelart.notedock.viewModel.FABClickedEvent
import com.pixelart.notedock.viewModel.FolderNameTakenEvent
import com.pixelart.notedock.viewModel.FolderViewEvent
import com.pixelart.notedock.viewModel.FoldersViewFragmentViewModel
import kotlinx.android.synthetic.main.fragment_folders_view.*
import org.koin.android.viewmodel.ext.android.viewModel

class FoldersViewFragment : Fragment(), FoldersAdapter.OnFolderClickListener {

    private val foldersViewFragmentViewModel: FoldersViewFragmentViewModel by viewModel()

    //So sorry
    private var name: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_folders_view,
            BR.viewmodel to foldersViewFragmentViewModel
        )
        foldersViewFragmentViewModel.lifecycleOwner = this
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        recyclerViewFolders.layoutManager = LinearLayoutManager(context)
        val folderAdapter = FoldersAdapter(this)
        recyclerViewFolders.adapter = folderAdapter
        observeLiveData(folderAdapter)

        createNotificationChannel()
    }


    private fun observeLiveData(foldersAdapter: FoldersAdapter) {
        foldersViewFragmentViewModel.firebaseTest.observe(this, Observer {
            foldersAdapter.setNewData(it)
        })
        foldersViewFragmentViewModel.newFolderCreated.observe(this, Observer { event ->
            when (event) {
                is FolderViewEvent.Success -> { }
                is FolderViewEvent.Error -> Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
        })

        foldersViewFragmentViewModel.fabClicked.observe(this, Observer {
            when (it) {
                FABClickedEvent.Clicked -> createFolderDialog()
            }
        })

        foldersViewFragmentViewModel.fabClicked.observe(this, Observer {
            when (it) {
                FABClickedEvent.Clicked -> createFolderDialog()
            }
        })

        foldersViewFragmentViewModel.isNameTaken.observe(this, Observer { event ->
            when (event) {
                is FolderNameTakenEvent.Success -> {
                    if (event.taken) {
                        //Toast in weird place, maaybe deal with that
                        Toast.makeText(context, "Folder with this name already exists", Toast.LENGTH_SHORT).show()
                    } else {
                        foldersViewFragmentViewModel.uploadFolderModel(FolderModel(name))
                    }
                }
                is FolderNameTakenEvent.Error -> {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun createFolderDialog() {
        val dialog = CreateFolderDialog(object : FolderDialogSuccessListener {
            override fun onSuccess(folderName: String?) {
                folderName?.let { folderName ->
                    //Checks if name is taken
                    name = folderName
                    foldersViewFragmentViewModel.isNameTaken(folderName)
                }
            }
        })
        fragmentManager?.let {
            dialog.show(it, "CreateFolderDialog")
        }
    }

    override fun onFolderClick(uid: String?) {
        uid?.let { uuid ->
            val action = FoldersViewFragmentDirections.actionFoldersViewFragmentToFolderFragment(uuid)
            val navigationRouter = NavigationRouter(view)
            navigationRouter.openAction(action)
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "Alarm"
            val descriptionText = "Ked potrebujes vstat"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val CHANNEL_ID = "NEVIEM"
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = context?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
            context?.let {
                val builder = NotificationCompat.Builder(it, "NEVIEM")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("NoteDock ta pozdravuje")
                    .setContentText("Cauky mnauky, si sexy inac len tak si chcem povedat")
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText("Cauky mnauky, si sexy inac len tak si chcem povedat, waifu material")
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                notificationManager.notify(0, builder.build())
            }
        }
    }
}

