package com.example.chatapplication.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import com.example.chatapplication.R
import vn.techres.line.app.AppAdapter

class PickerAdapter(context: Context) : AppAdapter<String>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder()
    }

    inner class ViewHolder : AppViewHolder(R.layout.picker_item) {

        private val pickerView: TextView? by lazy { findViewById(R.id.tv_picker_name) }

        override fun onBindView(position: Int) {
            pickerView!!.text = getItem(position)
        }
    }
}
