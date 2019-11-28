package com.pixelart.notedock.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.pixelart.notedock.R
import com.pixelart.notedock.dataBinding.setupDataBinding
import com.pixelart.notedock.dialog.DeleteNoteDialog
import com.pixelart.notedock.dialog.NoteDialogDeleteSuccessListener
import com.pixelart.notedock.viewModel.DeleteButtonClickEvent
import com.pixelart.notedock.viewModel.NoteFragmentViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class NoteFragment : Fragment() {

    private val noteFragmentViewModel: NoteFragmentViewModel by viewModel()

    private val args: NoteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = setupDataBinding<ViewDataBinding>(
            R.layout.fragment_note,
            BR.viewmodel to noteFragmentViewModel
        )
        noteFragmentViewModel.lifecycleOwner = this
        return dataBinding.root
    }

    override fun onResume() {
        super.onResume()

        observeLiveData()
    }

    private fun observeLiveData() {
        noteFragmentViewModel.loadNotes(args.folderUUID, args.noteUUID)

        noteFragmentViewModel.deleteButtonClicked.observe(this, Observer { event ->
            when(event) {
                DeleteButtonClickEvent.Clicked -> createDeleteNoteDialog()
            }
        })
    }

    private fun createDeleteNoteDialog() {
        fragmentManager?.let { fragmentManager ->
            val dialog = DeleteNoteDialog(object : NoteDialogDeleteSuccessListener {
                override fun onDelete() {
                    // vymaz
                    Toast.makeText(context, "Mazem", Toast.LENGTH_SHORT).show()
                }
            })
            dialog.show(fragmentManager, "Delete note dialog")
        }
    }
}
