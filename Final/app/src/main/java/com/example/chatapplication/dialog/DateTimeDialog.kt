package com.example.chatapplication.dialog

import com.example.chatapplication.base.BaseDialog
import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.R
import com.example.chatapplication.action.AnimAction
import com.example.chatapplication.adapter.PickerAdapter
import com.example.chatapplication.databinding.DateTimeDialogBinding
import com.example.chatapplication.manager.PickerLayoutManager
import com.hjq.toast.ToastUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DateTimeDialog {
    @SuppressLint("SetTextI18n")
    class Builder @JvmOverloads constructor(
        context: Context,
        inputDay: Int,
        inputMonth: Int,
        inputYear: Int,
        inputHour: Int,
        inputMinute: Int,
        private val minDateTime: String,
        private val maxDateTime: String,
        private val startYear: Int = Calendar.getInstance(Locale.getDefault())[Calendar.YEAR] - 100,
        private val endYear: Int = Calendar.getInstance(Locale.getDefault())[Calendar.YEAR] + 100,
        private val messageErrorMin: String,
        private val messageErrorMax: String
    ) : BaseDialog.Builder<Builder>(context), Runnable,
        PickerLayoutManager.OnPickerListener {
        private var binding: DateTimeDialogBinding =
            DateTimeDialogBinding.inflate(LayoutInflater.from(context))

        private val yearManager: PickerLayoutManager
        private val monthManager: PickerLayoutManager
        private val dayManager: PickerLayoutManager
        private val hourManager: PickerLayoutManager
        private val minuteManager: PickerLayoutManager
        private val yearAdapter: PickerAdapter
        private val monthAdapter: PickerAdapter
        private val dayAdapter: PickerAdapter
        private val hourAdapter: PickerAdapter
        private val minuteAdapter: PickerAdapter
        private var listener: OnDateTimeDialogListener? = null

        fun setListener(listener: OnDateTimeDialogListener): Builder = apply {
            this.listener = listener
        }

        init {
            setCancelable(false)
            setAnimStyle(AnimAction.ANIM_BOTTOM)
            setGravity(Gravity.BOTTOM)
            setWidth(context.resources.displayMetrics.widthPixels * 9 / 10)
            setContentView(binding.root)

            //=============Khởi tạo các adapter============//
            yearAdapter = PickerAdapter(context)
            monthAdapter = PickerAdapter(context)
            dayAdapter = PickerAdapter(context)
            hourAdapter = PickerAdapter(context)
            minuteAdapter = PickerAdapter(context)
            //=============================================//
            //=====Tạo dữ liệu ngày/tháng/năm/giờ/phút=====//
            // Năm
            val yearData = ArrayList<String>(10)
            for (i in startYear..endYear) {
                yearData.add(i.toString())
            }
            // Tháng
            val monthData = ArrayList<String>(12)
            for (i in 1..12) {
                if (i.toString().length < 2) {
                    monthData.add("0$i")
                } else {
                    monthData.add(i.toString())
                }
            }
            val calendar = Calendar.getInstance(Locale.getDefault())
            val day = calendar.getActualMaximum(Calendar.DATE)
            // Ngày
            val dayData = ArrayList<String>(day)
            for (i in 1..day) {
                if (i.toString().length < 2) {
                    dayData.add("0$i")
                } else {
                    dayData.add(i.toString())
                }
            }
            // Giờ
            val hourData = ArrayList<String>(24)
            for (i in 0..23) {
                if (i.toString().length < 2) {
                    hourData.add("0$i")
                } else {
                    hourData.add(i.toString())
                }
            }
            // Phút
            val minuteData = ArrayList<String>(60)
            for (i in 0..59) {
                if (i.toString().length < 2) {
                    minuteData.add("0$i")
                } else {
                    minuteData.add(i.toString())
                }
            }
            //=============================================//
            //==============Set Data Adapter===============//
            yearAdapter.setData(yearData)
            monthAdapter.setData(monthData)
            dayAdapter.setData(dayData)
            hourAdapter.setData(hourData)
            minuteAdapter.setData(minuteData)
            binding.yearView.adapter = yearAdapter
            binding.monthView.adapter = monthAdapter
            binding.dayView.adapter = dayAdapter
            binding.hourView.adapter = hourAdapter
            binding.minuteView.adapter = minuteAdapter
            //=============================================//
            //========Khởi tạo PickerLayoutManager=========//
            yearManager = PickerLayoutManager.Builder(context).build()
            monthManager = PickerLayoutManager.Builder(context).build()
            dayManager = PickerLayoutManager.Builder(context).build()
            hourManager = PickerLayoutManager.Builder(context).build()
            minuteManager = PickerLayoutManager.Builder(context).build()
            binding.yearView.layoutManager = yearManager
            binding.monthView.layoutManager = monthManager
            binding.dayView.layoutManager = dayManager
            binding.hourView.layoutManager = hourManager
            binding.minuteView.layoutManager = minuteManager
            //=============================================//
            setDay(inputDay)
            setMonth(inputMonth + 1)
            setYear(inputYear)
            setHour(inputHour)
            setMinute(inputMinute)
            //==========Hiển thị ngày giờ lên view=========//
            val textDay = if (inputDay.toString().length < 2) {
                "0$inputDay"
            } else {
                inputDay.toString()
            }
            val textMonth = if ((inputMonth + 1).toString().length < 2) {
                "0${inputMonth + 1}"
            } else {
                (inputMonth + 1).toString()
            }
            val textHour = if (inputHour.toString().length < 2) {
                "0$inputHour"
            } else {
                inputHour.toString()
            }
            val textMinute = if (inputMinute.toString().length < 2) {
                "0$inputMinute"
            } else {
                inputMinute.toString()
            }
            binding.tvDateTime.text = "$textDay/$textMonth/$inputYear lúc $textHour:$textMinute"
            //=============================================//
            yearManager.setOnPickerListener(this)
            monthManager.setOnPickerListener(this)
            dayManager.setOnPickerListener(this)
            hourManager.setOnPickerListener(this)
            minuteManager.setOnPickerListener(this)
            setOnClickListener(
                binding.btnDone,
                binding.ivBackDialog,
                binding.ivCloseDialog,
            )
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
                    if (i.toString().length < 2) {
                        dayData.add("0$i")
                    } else {
                        dayData.add(i.toString())
                    }
                }
                dayAdapter.setData(dayData)
            }
        }

        /**
         * [PickerLayoutManager.OnPickerListener]
         *
         * @param recyclerView              RecyclerView
         */
        @SuppressLint("SetTextI18n")
        override fun onPicked(recyclerView: RecyclerView, position: Int) {
            val day = dayAdapter.getItem(dayManager.getPickedPosition())
            val month = monthAdapter.getItem(monthManager.getPickedPosition())
            val year = yearAdapter.getItem(yearManager.getPickedPosition())
            val hour = hourAdapter.getItem(hourManager.getPickedPosition())
            val minute = minuteAdapter.getItem(minuteManager.getPickedPosition())
            binding.tvDateTime.text = "$day/$month/$year lúc $hour:$minute"
            refreshMonthMaximumDay()
        }

        /**
         * Làm mới số ngày tối đa mỗi tháng
         */
        private fun refreshMonthMaximumDay() {
            binding.yearView.removeCallbacks(this)
            binding.yearView.post(this)
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

        private fun setHour(hour: Int): Builder = apply {
            var index = hour
            if (index < 0) {
                index = 0
            } else if (index > hourAdapter.getCount() - 1) {
                index = hourAdapter.getCount() - 1
            }
            binding.hourView.scrollToPosition(index)
            refreshMonthMaximumDay()
        }

        private fun setMinute(minute: Int): Builder = apply {
            var index = minute
            if (index < 0) {
                index = 0
            } else if (index > minuteAdapter.getCount() - 1) {
                index = minuteAdapter.getCount() - 1
            }
            binding.minuteView.scrollToPosition(index)
            refreshMonthMaximumDay()
        }

        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        override fun onClick(view: View) {
            when (view.id) {
                R.id.btnDone -> {
                    val day = dayAdapter.getItem(dayManager.getPickedPosition()).toInt()
                    val month = monthAdapter.getItem(monthManager.getPickedPosition()).toInt()
                    val year = yearAdapter.getItem(yearManager.getPickedPosition()).toInt()
                    val hour = hourAdapter.getItem(hourManager.getPickedPosition()).toInt()
                    val minute = minuteAdapter.getItem(minuteManager.getPickedPosition()).toInt()
                    val dateTime1 = "$day/$month/$year $hour:$minute"
                    val dateTime2 = "$day/$month/$year"
                    val dateTimeResultCheckMin = SimpleDateFormat("dd/MM/yyyy HH:mm").parse(dateTime1)
                    val dateTimeResultCheckMax = SimpleDateFormat("dd/MM/yyyy").parse(dateTime2)
                    val dateTimeMin = SimpleDateFormat("dd/MM/yyyy HH:mm").parse(minDateTime)
                    val dateTimeMax = SimpleDateFormat("dd/MM/yyyy").parse(maxDateTime)
                    if (dateTimeResultCheckMin!!.before(dateTimeMin)) {
                        ToastUtils.show(messageErrorMin)
                    } else if (dateTimeResultCheckMax!!.after(dateTimeMax)) {
                        ToastUtils.show(messageErrorMax)
                    } else {
                        dismiss()
                        listener?.onSelectedDateTime(year, month, day, hour, minute)
                    }
                }

                R.id.ivBackDialog, R.id.ivCloseDialog -> {
                    dismiss()
                    listener?.onCancel()
                }
            }
        }
    }
    interface OnDateTimeDialogListener {
        /**
         * Gọi sau khi chọn ngày giờ
         */
        fun onSelectedDateTime(year: Int, month: Int, day: Int, hour: Int, minute: Int)

        fun onCancel()
    }
}