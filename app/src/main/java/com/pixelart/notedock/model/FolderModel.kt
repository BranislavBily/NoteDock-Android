package com.pixelart.notedock.model

class FolderModel {
    var uid: String? = null
    var name: String? = null
    var notesCount: Int? = null

    override fun toString(): String {
        return "FolderModel(uid=$uid, name=$name, notesCount=$notesCount)"
    }
}