package com.oyj.lottomaker

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.oyj.lottomaker.databinding.FragmentDialogBinding

class SelectNumberDialog: DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private lateinit var binding: FragmentDialogBinding
    private val viewModel: SelectNumberDialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialogBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.view = this

        viewModel.selectNumbers.observe(this) { numberList ->
            var strNumberList = ""
            for(number in numberList) {
                strNumberList += if(numberList.last() == number) {
                    // 마지막 수
                    "$number\n"
                } else {
                    "$number, "
                }

            binding.tvSelectNums.text = strNumberList
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        context?.dialogFragmentResize(this,0.9f, 0.9f)
    }

    fun finish() {
        val result = binding.viewModel?.selectNumbers?.value
        setFragmentResult(RESULT_KEY, bundleOf(BUNDLE_KEY to result))
        this.dismiss()
    }

    private fun Context.dialogFragmentResize(dialogFragment: DialogFragment, width: Float, height: Float) {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        if (Build.VERSION.SDK_INT < 30) {

            val display = windowManager.defaultDisplay
            val size = Point()

            display.getSize(size)

            val window = dialogFragment.dialog?.window

            val x = (size.x * width).toInt()
            val y = (size.y * height).toInt()
            window?.setLayout(x, y)

        } else {

            val rect = windowManager.currentWindowMetrics.bounds

            val window = dialogFragment.dialog?.window

            val x = (rect.width() * width).toInt()
            val y = (rect.height() * height).toInt()

            window?.setLayout(x, y)
        }
    }

    companion object {
        const val RESULT_KEY = "RESULT_KEY"
        const val BUNDLE_KEY = "BUNDLE_KEY"
    }
}