package com.pixelart.notedock.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.EventListener
import com.pixelart.notedock.R
import com.pixelart.notedock.domain.repository.FolderRepository
import kotlinx.android.synthetic.main.fragment_folder.*
import org.koin.android.ext.android.inject

/**
 * A simple [Fragment] subclass.
 */
class FolderFragment : Fragment() {

    private val folderRepository: FolderRepository by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_folder, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val uid = it.getString("uid")
            uid?.let {
                folderRepository.getFolder(uid, EventListener { folderModel, _ ->
                    folderModel?.let {
                        textViewFolderDescriptionUID.text = it.uid
                        textViewFolderDescriptionName.text = it.name
                        textViewFolderDescriptionNotesCount.text = it.notesCount
                    }
                })
            }
        }
    }
}
