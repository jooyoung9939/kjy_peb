package com.example.madcamp_week2_kjy_peb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.madcamp_week2_kjy_peb.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var currentFragment: Fragment? = null
    private lateinit var id: String
    private lateinit var  token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        id = intent.getStringExtra("id") ?: ""

        token = intent.getStringExtra("token") ?: ""

        // ViewModelProvider를 사용하여 ViewModel 인스턴스 생성

        // 페이지 데이터 로드
        val list = listOf(SecondActivity.newInstance(id, token), ChatListFragment.newInstance(id, token))

        // 아답터 생성 및 연결
        val pagerAdapter = FragmentPagerAdapter(list, this)
        binding.viewPager.adapter = pagerAdapter

        // 탭 메뉴 제목 설정
        val titles = listOf("Main", "Chatting")

        // 탭 레이아웃과 뷰페이저 연결
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()

        // 현재 활성화된 프래그먼트 추적
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentFragment = list[position]
            }
        })
    }

    class FragmentPagerAdapter(
        private val fragmentList: List<Fragment>,
        fragmentActivity: AppCompatActivity
    ) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount() = fragmentList.size
        override fun createFragment(position: Int) = fragmentList[position]
    }
}
