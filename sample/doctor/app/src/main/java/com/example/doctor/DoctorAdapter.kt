package com.example.doctor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.BaseAdapter

class DoctorAdapter(private val context: Context, private val doctors: List<Doctor>) : BaseAdapter() {

    override fun getCount(): Int = doctors.size

    override fun getItem(position: Int): Any = doctors[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_doctor, parent, false)

        val doctor = doctors[position]

        val imgDoctor: ImageView = view.findViewById(R.id.imgDoctor)
        val txtName: TextView = view.findViewById(R.id.txtDoctorName)
        val txtSpecialty: TextView = view.findViewById(R.id.txtDoctorSpecialty)

        imgDoctor.setImageResource(doctor.imageResId)
        txtName.text = doctor.name
        txtSpecialty.text = doctor.specialty

        return view
    }
}
