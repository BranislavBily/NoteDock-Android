package com.pixelart.notedock.domain.repository

interface FirebaseIDSRepository {
    fun getCollectionFolders(): String
    fun getFolderName(): String
    fun getFolderNotesCount(): String
    fun getCollectionNotes(): String
    fun getNoteTitle(): String
    fun getNoteDescription(): String
}

class FirebaseIDSImpl: FirebaseIDSRepository {
    override fun getCollectionFolders(): String {
        return "folders"
    }

    override fun getFolderName(): String {
        return "name"
    }

    override fun getFolderNotesCount(): String {
        return "notesCount"
    }

    override fun getCollectionNotes(): String {
        return "notes"
    }

    override fun getNoteTitle(): String {
        return "title"
    }

    override fun getNoteDescription(): String {
        return "description"
    }
}