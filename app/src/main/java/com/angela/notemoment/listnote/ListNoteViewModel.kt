package com.angela.notemoment.listnote

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.angela.notemoment.LoadApiStatus
import com.angela.notemoment.Logger
import com.angela.notemoment.NoteApplication
import com.angela.notemoment.R
import com.angela.notemoment.data.Box
import com.angela.notemoment.data.ListNoteSorted
import com.angela.notemoment.data.Note
import com.angela.notemoment.data.Result
import com.angela.notemoment.data.source.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class ListNoteViewModel (private val repository: NoteRepository,
                         private val arguments: Box
) : ViewModel() {

    private val _box = MutableLiveData<Box>().apply {
        value = arguments
    }
    val box: LiveData<Box>
        get() = _box


    private val _noteSorted = MutableLiveData<List<ListNoteSorted>>()

    val noteSorted: LiveData<List<ListNoteSorted>>
        get() = _noteSorted


    private val _note = MutableLiveData<List<Note>>()

    val note: LiveData<List<Note>>
        get() = _note

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error


    private var viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")

        getNoteResult()

    }


    private fun getNoteResult() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getNote(_box.value!!.id)

            _note.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    _noteSorted.value = result.data.toListNoteSorted()
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = NoteApplication.instance.getString(R.string.fail)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
        }
    }


}

// sort notes by date
    fun List<Note>?.toListNoteSorted(): List<ListNoteSorted> {

        val sortedNotes = mutableListOf<ListNoteSorted>()
        var tempObj = ListNoteSorted()

        this?.let {

            for (note in it) {

                fun getDateByTime(time: Long): String {
                    val myFormat = "yyyy/MM/dd"
                    val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

                    return sdf.format(time)
                }

                val date = getDateByTime(note.time)
                Logger.i("date=$date")

                if (date != tempObj.date) {
                    Logger.i("create ListNoteSorted")
                    tempObj = ListNoteSorted()
                    tempObj.date = date
                    sortedNotes.add(tempObj)
                }
                tempObj.notes.add(note)
                Logger.i("sortedNotes=$sortedNotes")

            }
        }

        return sortedNotes
    }