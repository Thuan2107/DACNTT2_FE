package com.example.chatapplication.activity
import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.chatapplication.R
import com.example.chatapplication.adapter.TabRequestFriendAdapter
import com.example.chatapplication.app.AppActivity
import com.example.chatapplication.app.AppFragment
import com.example.chatapplication.base.FragmentPagerAdapter
import com.example.chatapplication.databinding.ActivityRequestFriendBinding
import com.example.chatapplication.eventbus.AmountSendEventBus
import com.example.chatapplication.eventbus.RequestEventBus
import com.example.chatapplication.fragment.AllRequestFriendFragment
import com.example.chatapplication.model.entity.Title
import com.gyf.immersionbar.ImmersionBar
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import com.example.chatapplication.fragment.AllSendRequestFragment

class RequestFriendActivity : AppActivity(), TabRequestFriendAdapter.OnTabListener,
    ViewPager.OnPageChangeListener {
    private lateinit var binding: ActivityRequestFriendBinding
    private var tabAdapter: TabRequestFriendAdapter? = null
    private var pagerAdapter: FragmentPagerAdapter<AppFragment<*>>? = null
    private var titleReceived: Title = Title()
    private var titleSend: Title = Title()
    override fun getLayoutView(): View {
        binding = ActivityRequestFriendBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        pagerAdapter = FragmentPagerAdapter(this)
        pagerAdapter!!.addFragment(AllRequestFriendFragment.newInstance(), getString(R.string.received))
        pagerAdapter!!.addFragment(AllSendRequestFragment.newInstance(), getString(R.string.send))
        binding.nsvPager.adapter = pagerAdapter
        binding.nsvPager.addOnPageChangeListener(this)
        binding.nsvPager.offscreenPageLimit = 2
        tabAdapter = TabRequestFriendAdapter(this)
        binding.tabRecyclerView.adapter = tabAdapter
        ImmersionBar.setTitleBar(this, binding.headerTitle)
    }

    override fun initData() {
        titleSend.title = getString(R.string.send)
        titleReceived.title = getString(R.string.received)
        tabAdapter?.addItem(titleReceived)
        tabAdapter?.addItem(titleSend)
        tabAdapter?.setOnTabListener(this)
    }


    /**
     * hứng dữ liệu event bus từ màn hình fragment truyền về
     */
    @SuppressLint("NotifyDataSetChanged")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AmountSendEventBus?) {
        titleSend.amount = event!!.amount
        tabAdapter!!.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageRequestEvent(event: RequestEventBus?) {
        titleReceived.amount = event!!.amount
        tabAdapter!!.notifyDataSetChanged()
    }

    override fun onTabSelected(recyclerView: RecyclerView?, position: Int): Boolean {
        binding.nsvPager.currentItem = position
        return true
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        tabAdapter?.setSelectedPosition(position)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }
}