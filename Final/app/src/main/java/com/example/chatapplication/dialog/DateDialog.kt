package com.example.chatapplication.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.R
import com.example.chatapplication.adapter.PickerAdapter
import com.example.chatapplication.base.BaseDialog
import com.example.chatapplication.databinding.DateDialogBinding
import com.example.chatapplication.manager.PickerLayoutManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DateDialog {
    class Builder @JvmOverloads constructor(
        context: Context,
        inputDay: Int,
        inputMonth: Int,
        inputYear: Int,
        private val startYear: Int = Calendar.getInstance(Locale.getDefault())[Calendar.YEAR] - 100,
        endYear: Int = Calendar.getInstance(Locale.getDefault())[Calendar.YEAR]
    ) : CommonDialog.Builder<Builder>(context), Runnable, PickerLayoutManager.OnPickerListener {
        private var binding: DateDialogBinding =
            DateDialogBinding.inflate(LayoutInflater.from(context))

        private val yearManager: PickerLayoutManager
        private val monthManager: PickerLayoutManager
        private val dayManager: PickerLayoutManager
        private val yearAdapter: PickerAdapter
        private val monthAdapter: PickerAdapter
        private val dayAdapter: PickerAdapter
        private var listener: OnDateDialogListener? = null

        init {
            setCustomView(binding.root)
            setTitle(R.string.time_title)
            yearAdapter = PickerAdapter(context)
            monthAdapter = PickerAdapter(context)
            dayAdapter = PickerAdapter(context)

            // Năm
            val yearData = ArrayList<String>(10)
            for (i in startYear..endYear) {
                yearData.add(i.toString())
            }
            // Tháng
            val monthData = ArrayList<String>(12)
            for (i in 1..12) {
                monthData.add(i.toString())
            }
            val calendar = Calendar.getInstance(Locale.getDefault())
            val day = calendar.getActualMaximum(Calendar.DATE)
            // Ngày
            val dayData = ArrayList<String>(day)
            for (i in 1..day) {
                dayData.add(i.toString())
            }
            yearAdapter.setData(yearData)
            monthAdapter.setData(monthData)
            dayAdapter.setData(dayData)
            yearManager = PickerLayoutManager.Builder(context)
                .build()
            monthManager = PickerLayoutManager.Builder(context)
                .build()
            dayManager = PickerLayoutManager.Builder(context)
                .build()
            binding.yearView.layoutManager = yearManager
            binding.monthView.layoutManager = monthManager
            binding.dayView.layoutManager = dayManager
            binding.yearView.adapter = yearAdapter
            binding.monthView.adapter = monthAdapter
            binding.dayView.adapter = dayAdapter
            setDay(inputDay)
            setMonth(inputMonth + 1)
            setYear(inputYear)
            yearManager.setOnPickerListener(this)
            monthManager.setOnPickerListener(this)
        }

        fun setListener(listener: OnDateDialogListener): Builder = apply {
            this.listener = listener
        }

        /**
         * Không chọn ngày
         */
        fun setIgnoreDay(): Builder = apply {
            binding.dayView.visibility = View.GONE
        }

        fun setDate(date: Long): Builder = apply {
            if (date > 0) {
                setDate(SimpleDateFormat("ddMMyy", Locale.getDefault()).format(Date(date)))
            }
        }

        private fun setDate(date: String): Builder = apply {
            if (date.matches(Regex("\\d{8}"))) {
                // 20190519
                setDay(date.substring(6, 8))
                setMonth(date.substring(4, 6))
                setYear(date.substring(0, 4))
            } else if (date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                // 2019-05-19
                setYear(date.substring(0, 4))
                setMonth(date.substring(5, 7))
                setDay(date.substring(8, 10))
            }
        }

        private fun setYear(year: String): Builder = apply {
            return setYear(year.toInt())
        }

        private fun setYear(year: Int): Builder = apply {
            var index = year - startYear
            if (index < 0) {
                index = 0
            } else if (index > yearAdapter.getCount() - 1) {
                index = yearAdapter.getCount() - 1
            }
            binding.yearView.scrollToPosition(index)
            refreshMonthMaximumDay()
        }

        private fun setMonth(month: String): Builder = apply {
            setMonth(month.toInt())
        }

        private fun setMonth(month: Int): Builder = apply {
            var index = month - 1
            if (index < 0) {
                index = 0
            } else if (index > monthAdapter.getCount() - 1) {
                index = monthAdapter.getCount() - 1
            }
            binding.monthView.scrollToPosition(index)
            refreshMonthMaximumDay()
        }

        private fun setDay(day: String): Builder = apply {
            setDay(day.toInt())
        }

        private fun setDay(day: Int): Builder = apply {
            var index = day - 1
            if (index < 0) {
                index = 0
            } else if (index > dayAdapter.getCount() - 1) {
                index = dayAdapter.getCount() - 1
            }
            binding.dayView.scrollToPosition(index)
            refreshMonthMaximumDay()
        }

        override fun onClick(view: View) {
            when (view.id) {
                R.id.tv_ui_confirm -> {
                    autoDismiss()
                    listener!!.onSelected(
                        getDialog()!!, startYear + yearManager.getPickedPosition(),
                        monthManager.getPickedPosition() + 1, dayManager.getPickedPosition() + 1
                    )
                }

                R.id.tv_ui_cancel -> {
                    autoDismiss()
                }
            }
        }

        /**
         * [PickerLayoutManager.OnPickerListener]
         *
         * @param recyclerView              RecyclerView
         */
        override fun onPicked(recyclerView: RecyclerView, position: Int) {
            refreshMonthMaximumDay()
        }

        override fun run() {
            // Nhận số ngày tối đa trong tháng này
            val calendar = Calendar.getInstance(Locale.getDefault())
            calendar[startYear + yearManager.getPickedPosition(), monthManager.getPickedPosition()] =
                1
            val day = calendar.getActualMaximum(Calendar.DATE)
            if (dayAdapter.getCount() != day) {
                val dayData = ArrayList<String>(day)
                for (i in 1..day) {
                    dayData.add(i.toString())
                }
                dayAdapter.setData(dayData)
            }
        }

        /**
         * Làm mới số ngày tối đa mỗi tháng
         */
        private fun refreshMonthMaximumDay() {
            binding.yearView.removeCallbacks(this)
            binding.yearView.post(this)
        }
    }
    interface OnDateDialogListener {

        /**
         * Gọi sau khi chọn ngày
         */
        fun onSelected(dialog: BaseDialog, year: Int, month: Int, day: Int)
    }
}