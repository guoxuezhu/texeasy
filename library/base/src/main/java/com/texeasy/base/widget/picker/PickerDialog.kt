package com.texeasy.base.widget.picker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.common.utils.SizeUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.texeasy.base.R
import com.weigan.loopview.LoopView


/**
 ** created by caozhihuang
 */
class PickerDialog(
    var list: List<String>,
    var initialPosition: Int,
    var selectCallback: Action1<Int>
) : BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_picker_dialog, container, false)

        val loopview = view.findViewById<LoopView>(R.id.loop_view)
        loopview.setItems(list)
        val height = SizeUtil.getDeviceHeight(activity) * 0.33
        loopview.layoutParams.height = height.toInt()
        loopview.setInitPosition(initialPosition)

        view.findViewById<ImageView>(R.id.btn_cancel)
            .setOnClickListener { dismissAllowingStateLoss() }
        view.findViewById<ImageView>(R.id.btn_sure).setOnClickListener {
            selectCallback.call(loopview.selectedItem)
        }
        dialog?.setOnShowListener {
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return view
    }

    interface Action1<T> {
        fun call(t: T)
    }
}