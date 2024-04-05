package com.a213310009ayubbudisantoso.transmart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController

class ListFragment : Fragment() {

    private lateinit var kembali: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kembali = view.findViewById(R.id.kembali)

        kembali.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_homeFragment)
        }
    }

}