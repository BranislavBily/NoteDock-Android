package com.pixelart.notedock.domain.repository

interface FirebaseIDSRepository {
    fun getCollectionFolders(): String
    fun getCollectionUsers(): String
    fun getFolderName(): String
    fun getFolderNotesCount(): String
    fun getFolderAdded(): String
    fun getCollectionNotes(): String
    fun getNoteTitle(): String
    fun getNoteDescription(): String
    fun getNoteUpdated(): String
    fun getNotePinned(): String
}

class FirebaseIDSImpl: FirebaseIDSRepository {
    override fun getCollectionFolders(): String {
        return "folders"
    }

    override fun getCollectionUsers(): String {
        return "users"
    }

    override fun getFolderName(): String {
        return "name"
    }

    override fun getFolderNotesCount(): String {
        return "notesCount"
    }

    override fun getFolderAdded(): String {
        return "added"
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

    override fun getNoteUpdated(): String {
        return "updated"
    }

    override fun getNotePinned(): String {
        return "pinned"
    }

}