package com.sensibol.lucidmusic.singstr.gui.app.learn

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.app.learn.academy.TuneTimingAdapter
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentLearnBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LearnFragment : BaseFragment() {

    override val layoutResId = R.layout.fragment_learn
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentLearnBinding::inflate
    override val binding: FragmentLearnBinding get() = super.binding as FragmentLearnBinding
    private var filterCount: Int = 0

    var tagList = mutableListOf<String>()

    val args: LearnFragmentArgs by navArgs()

    @Inject
    internal lateinit var tuneTimingAdapter: TuneTimingAdapter

    override fun onInitView() {

        binding.apply {

            viewPager.isUserInputEnabled = false
            tabLayout.addTab(tabLayout.newTab())
            tabLayout.addTab(tabLayout.newTab())
            tabLayout.addTab(tabLayout.newTab())
            tabLayout.addTab(tabLayout.newTab())
            viewPager.adapter = LearnViewPagerAdapter(this@LearnFragment, tabLayout.tabCount)

            for (i in 0 until tabLayout.tabCount) {
                val tabView: View = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
                tabView.requestLayout()
                ViewCompat.setBackground(tabView, setImageButtonStateNew(requireContext()));
                ViewCompat.setPaddingRelative(tabView, 50, tabView.paddingTop, 50, tabView.paddingBottom)
            }


            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                if (position == 0) {
                    tab.text = "ALL"
                } else if (position == 1) {
                    tab.text = "TUNE"
                } else if (position == 2) {
                    tab.text = "TIMING"
                } else if (position == 3) {
                    tab.text = "FREE"
                }

                viewPager.setCurrentItem(tab.position, true)
            }.attach()

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                    println(" Learn ${tab?.position} onTabReselected")
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    println(" Learn ${tab?.position} onTabUnselected")
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> llFilterText.visibility = GONE
                        1 -> {
                            llFilterText.visibility = VISIBLE
                            println("TUNING")
                            Analytics.logEvent(
                                Analytics.Event.TuningEnhanceEvent()
                            )
                        }

                        2 -> {
                            llFilterText.visibility = VISIBLE
                            Analytics.logEvent(
                                Analytics.Event.TimingEnhanceEvent()
                            )
                            println("TIMING")
                        }
                    }

                }
            })

            tvResetFilter.setOnClickListener {
                filterCount = 0
                viewPager.setCurrentItem(0, true)
            }
            binding.includeHeader.tvMyList.setOnClickListener {
                findNavController().navigate(LearnFragmentDirections.toMyListFragment())
            }

            rvLessonCategories.apply {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        val startMargin = if (0 == parent.getChildAdapterPosition(view)) resources.getDimension(R.dimen.screen_horz_margin) else 0
                        outRect.set(startMargin.toInt(), 0, 0, 0)
                    }
                })
//                adapter = learnAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                isNestedScrollingEnabled = false
            }
        }
    }

    private fun setImageButtonStateNew(mContext: Context): Drawable? {

        val states = StateListDrawable()
        states.addState(intArrayOf(android.R.attr.state_selected), ContextCompat.getDrawable(mContext, R.drawable.shape_dark_grey_corner_radius))
        states.addState(intArrayOf(-android.R.attr.state_selected), ContextCompat.getDrawable(mContext, R.drawable.shape_grey_corner_radius))

        states.addState(intArrayOf(android.R.attr.state_pressed), ContextCompat.getDrawable(mContext, R.drawable.shape_dark_grey_corner_radius))
        states.addState(intArrayOf(-android.R.attr.state_pressed), ContextCompat.getDrawable(mContext, R.drawable.shape_grey_corner_radius))


        states.addState(intArrayOf(android.R.attr.state_focused), ContextCompat.getDrawable(mContext, R.drawable.shape_dark_grey_corner_radius))
        states.addState(intArrayOf(-android.R.attr.state_focused), ContextCompat.getDrawable(mContext, R.drawable.shape_grey_corner_radius))


        states.addState(intArrayOf(android.R.attr.state_checked), ContextCompat.getDrawable(mContext, R.drawable.shape_dark_grey_corner_radius))
        states.addState(intArrayOf(-android.R.attr.state_checked), ContextCompat.getDrawable(mContext, R.drawable.shape_grey_corner_radius))



        return states

    }

}