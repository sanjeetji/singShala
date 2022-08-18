package com.sensibol.lucidmusic.singstr.gui.app.learn.mylist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentMyListBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyListFragment : BaseFragment() {

    override val layoutResId = R.layout.fragment_my_list

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentMyListBinding::inflate

    override val binding: FragmentMyListBinding get() = super.binding as FragmentMyListBinding

    private val myListViewModel: MyListViewModel by viewModels()

    @Inject
    lateinit var myListAdapter: MyListAdapter

    override fun onInitView() {
        myListViewModel.getMyLesson()
        binding.apply {
            ivPlaylistAdd.setOnClickListener {
                findNavController().popBackStack()
            }

            rvImproveTuning.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = myListAdapter
            }

            myListAdapter.onTuneClickListener = {
                findNavController().navigate(MyListFragmentDirections.toLearnDetailFragment(it.id))
            }


            myListAdapter.onRemoveClickListener = {
                myListViewModel.removeFromMyList(it.id)
            }


        }

        myListViewModel.apply {
            failure(failure, ::handleFailure)
            observe(myLesson, { myListAdapter.collections = it })
            observe(removeFromMyList, ::RemoveFromList)
        }
    }

    fun RemoveFromList(b: Boolean) {
        if (b) {
            myListViewModel.getMyLesson()
        }
    }

}