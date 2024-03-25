package com.example.chatapplication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.R
import com.example.chatapplication.base.BaseAdapter
import com.example.chatapplication.databinding.HomeNavigationItemBinding
import com.example.chatapplication.model.entity.Title
import com.example.chatapplication.utils.AppUtils.hide
import com.example.chatapplication.utils.AppUtils.show
import vn.techres.line.app.AppAdapter

/**
 * @Author: Phạm Văn Nhân
 * @Date: 28/09/2022
 */
class NavigationAdapter(context: Context) :
    AppAdapter<NavigationAdapter.MenuItem>(context),
    BaseAdapter.OnItemClickListener {

    /** Điểm đến hiện được chọn */
    private var selectedPosition: Int = 0

    /** Thanh điều hướng nhấp vào trình nghe */
    private var listener: OnNavigationListener? = null

    init {
        setOnItemClickListener(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            HomeNavigationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun generateDefaultLayoutManager(context: Context): RecyclerView.LayoutManager {
        return GridLayoutManager(context, getCount(), RecyclerView.VERTICAL, false)
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }

    /**
     * Thiết lập trình nghe thanh điều hướng
     */
    fun setOnNavigationListener(listener: OnNavigationListener?) {
        this.listener = listener
    }

    /**
     * BaseAdapter.OnItemClickListener
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun onItemClick(recyclerView: RecyclerView?, itemView: View?, position: Int) {
        if (listener == null) {
            selectedPosition = position
            notifyDataSetChanged()
            return
        }
        if (listener!!.onNavigationItemSelected(position)) {
            selectedPosition = position
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(val binding: HomeNavigationItemBinding) : AppViewHolder(binding.root) {

        @SuppressLint("SuspiciousIndentation", "SetTextI18n")
        override fun onBindView(position: Int) {
            getItem(position).apply {
                binding.imgIcon.setImageDrawable(getDrawable())


                binding.txtTitle.text = getTitle()!!.title
                binding.imgIcon.isSelected = (selectedPosition == position)
                binding.txtTitle.isSelected = (selectedPosition == position)
            }
        }
    }

    class MenuItem(private val title: Title?, private val drawable: Drawable?) {

        fun getTitle(): Title? {
            return title
        }
        fun getDrawable(): Drawable? {
            return drawable
        }
    }


    interface OnNavigationListener {
        fun onNavigationItemSelected(position: Int): Boolean
    }
}