package com.oyj.lottomaker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.oyj.lottomaker.SelectNumberDialog.Companion.BUNDLE_KEY
import com.oyj.lottomaker.SelectNumberDialog.Companion.RESULT_KEY
import com.oyj.lottomaker.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        // 뷰모델 바인딩도 안하고 쓰려고했네
        binding.viewModel = viewModel
        binding.view = this

        // viewLifecycleOwner 라이프 사이클 때문에 사용된다. createView ~ destroyView 정리할 것.
        viewModel.arrWinningNumberList.observe(viewLifecycleOwner) { arrNumberList ->
            var strNumberList = ""
            for(numberList in arrNumberList) {
                for(number in numberList) {
                    strNumberList += if(numberList.last() == number) {
                        // 마지막 수
                        "$number\n"
                    } else {
                        "$number, "
                    }
                }
            }

            binding.tvNumbers.text = strNumberList
        }

        viewModel.winningNumberList.observe(viewLifecycleOwner) { numberList ->
            var strNumberList = ""
            for(number in numberList) {
                strNumberList += if(numberList.last() == number) {
                    // 마지막 수
                    "$number\n"
                } else {
                    "$number, "
                }
                binding.tvNumbers.text = strNumberList
            }
        }

        viewModel.initDatabase()

        setFragmentResultListener(RESULT_KEY) { requestKey, bundle ->
            val result = bundle.getIntegerArrayList(BUNDLE_KEY)
            viewModel.createWinningNumberPart(result!!)
        }

        return binding.root
    }

    fun showSelectNumberDialog() {
        SelectNumberDialog().show(requireFragmentManager(),"dialog")
    }
}