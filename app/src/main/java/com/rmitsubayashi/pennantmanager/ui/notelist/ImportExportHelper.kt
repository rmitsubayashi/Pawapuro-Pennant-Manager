package com.rmitsubayashi.pennantmanager.ui.notelist

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import com.rmitsubayashi.pennantmanager.R
import com.rmitsubayashi.pennantmanager.data.model.Note
import com.rmitsubayashi.pennantmanager.data.repository.NoteRepository
import com.rmitsubayashi.pennantmanager.ui.util.TimeUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class ImportExportHelper @Inject constructor(
    private val noteRepository: NoteRepository,
    @ApplicationContext private val context: Context
) {
    suspend fun import(directory: File) : Int{
        val files = directory.listFiles() ?: return 0
        val notes = files.mapNotNull { file ->
            if (!file.isFile) {
                null
            } else {
                val title = file.name.removeSuffix(".txt")
                val content = file.readText()
                Note(title, content, Note.NO_SAVE_FILE_ID, TimeUtil.currentTimestamp())
            }
        }

        for (note in notes) {
            noteRepository.add(note)
        }

        return notes.size
    }

    fun export(notes: List<Note>) {
        val notesDirectory = getNotesDirectory()
        notesDirectory.mkdir()
        for (note in notes) {
            createNoteFile(note, notesDirectory)
        }
    }

    private fun getNotesDirectory(): File {
        val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        return File(downloadsDirectory, context.getString(R.string.app_name))
    }

    private fun createNoteFile(note: Note, directory: File) {
        val file = File(directory, "${note.title}.txt")
        // removing the same file manually (ie from file manager app) before recreating the file
        // doesn't remove it completely, so we need to acknowledge if it exists or it will throw an exception
        val onScanCompletedListener = MediaScannerConnection.OnScanCompletedListener { _, _ ->
            file.writeText(note.content)
            file.createNewFile()
        }
        MediaScannerConnection.scanFile(context, arrayOf(file.toString()), arrayOf("text/plain"), onScanCompletedListener)
    }
}