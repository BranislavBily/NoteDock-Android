package com.pixelart.notedock.domain.repository

interface FirebaseIDSRepository {
    fun getCollectionFolders(): String
    fun getFolderName(): String
    fun getFolderNotesCount(): String
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
}