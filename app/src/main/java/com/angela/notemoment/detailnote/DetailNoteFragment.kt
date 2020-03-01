package com.angela.notemoment.detailnote

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.angela.notemoment.Logger
import com.angela.notemoment.NavigationDirections
import com.angela.notemoment.R
import com.angela.notemoment.addnote.AddNoteFragmentDirections
import com.angela.notemoment.databinding.FragmentDetailNoteBinding
import com.angela.notemoment.databinding.FragmentListNoteBinding
import com.angela.notemoment.ext.getVmFactory
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.android.synthetic.main.fragment_detail_note.*
import java.io.IOException
import java.util.*


class DetailNoteFragment : Fragment() {
    private val viewModel by viewModels<DetailNoteViewModel> {
        getVmFactory (DetailNoteFragmentArgs.fromBundle(requireArguments()).NoteKey,
            DetailNoteFragmentArgs.fromBundle(requireArguments()).BoxKey) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentDetailNoteBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_detail_note, container, false)
        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        binding.noteButtonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.navigateToAddNote.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalAddNoteFragment())
                viewModel.onAddNoteNavigated()
            }
        })


        //set date picker
        val cal = Calendar.getInstance()

        val myFormat = "yyyy/MM/dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.selectDate.text = sdf.format(viewModel.note.value?.time)

        binding.selectTime.text = SimpleDateFormat("HH:mm").format(viewModel.note.value?.time)

        viewModel.onChangeNoteTime(cal.timeInMillis)


        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            viewModel.onChangeNoteTime(cal.timeInMillis)

            binding.selectDate.text = sdf.format(cal.time)
        }

        binding.selectDate.setOnClickListener {
            DatePickerDialog(requireContext(), dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }


        val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            viewModel.onChangeNoteTime(cal.timeInMillis)
            binding.selectTime.text = String.format("%02d:%02d", hourOfDay, minute)
        }

        binding.selectTime.setOnClickListener {
            TimePickerDialog(requireContext(), 3, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(
                Calendar.MINUTE)
                , true).show()
        }



        // spinner listener
        val spinner = binding.changeBox

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                viewModel.changeBoxPosition(pos)
                Logger.i("pos = $pos")
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }

        }

//                val defaultBox = viewModel.box.value?.title?:""
//                val spinnerAdap = spinner.adapter as ArrayAdapter<*>
//                    val spinnerPosition = spinnerAdap.getPosition(defaultBox)
//                    spinner.setSelection(spinnerPosition)



        //upload photo
        binding.detailNoteImage.setOnClickListener { launchGallery() }



        return binding.root
    }


    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 12)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12 && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }
//            viewModel.photoUrl.value = data.data.toString()
            viewModel.note.value?.images = data.data.toString()

            try {
                var filePath = data.data
                Glide.with(this).load(filePath).centerCrop()
                    .into(detail_note_image)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}