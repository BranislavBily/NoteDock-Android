package com.pixelart.notedock.model

class NoteModel {
    var uuid: String? = null
    var noteTitle: String? = null
    var noteDescription: String? = null
    var marked: Boolean? = null

    constructor()

    constructor(noteTitle: String?, noteDescription: String?) {
        this.noteTitle = noteTitle
        this.noteDescription = noteDescription
    }

    constructor(uid: String?, noteTitle: String?, noteDescription: String?) {
        this.uuid = uid
        this.noteTitle = noteTitle
        this.noteDescription = noteDescription
    }

    override fun toString(): String {
        return "NoteModel(uuid=$uuid, noteTitle=$noteTitle, noteDescription=$noteDescription, pinned=$marked)"
    }
}