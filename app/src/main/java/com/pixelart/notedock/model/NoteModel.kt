package com.pixelart.notedock.model

class NoteModel {
    var uid: String? = null
    var noteTitle: String? = null
    var noteDescription: String? = null

    constructor()

    constructor(noteTitle: String?, noteDescription: String?) {
        this.noteTitle = noteTitle
        this.noteDescription = noteDescription
    }

    override fun toString(): String {
        return "NoteModel(uid=$uid, noteTitle=$noteTitle, noteDescription=$noteDescription)"
    }


}