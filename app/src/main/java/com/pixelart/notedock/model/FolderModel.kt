package com.pixelart.notedock.model

class FolderModel {
    var name: String? = null
    var notesCount: String? = null

    override fun toString(): String {
        return "FolderModel(name=$name, notesCount=$notesCount)"
    }
}