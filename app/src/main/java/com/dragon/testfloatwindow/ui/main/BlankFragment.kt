package com.dragon.testfloatwindow.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dragon.testfloatwindow.R
import com.dragon.testfloatwindow.ui.main.service.FloatWindowService
import kotlinx.android.synthetic.main.fragment_blank.*

class BlankFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textView.setOnClickListener {
            val floatWindowService = host as? FloatWindowService
            floatWindowService?.updateWindowSize(500, 900)
            fragmentManager?.beginTransaction()
                ?.replace(R.id.window_content_id, ItemFragment.newInstance(1))?.commit()
        }
    }
}
