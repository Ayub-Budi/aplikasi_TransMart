package com.a213310009ayubbudisantoso.transmart

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class BebasExpiredFragment : Fragment() {
    private lateinit var kembali: ImageView
    private lateinit var pieChart: PieChart


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bebas_expired, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dataExpired = view.findViewById<CardView>(R.id.data)
        val menarikExpired = view.findViewById<CardView>(R.id.tarik)
        kembali = view.findViewById(R.id.kembali)
        pieChart = view.findViewById(R.id.pieChartDidata)

        val radiusA = 10f // Variabel untuk radius
        val radiusB = 2f // Variabel untuk radius

        drawPieChart(listOf(radiusA, radiusB), listOf(Color.BLUE, Color.RED))

        dataExpired.setOnClickListener {
            findNavController().navigate(R.id.action_bebasExpiredFragment_to_dataExpiredFragment2)
        }
        menarikExpired.setOnClickListener {
            findNavController().navigate(R.id.action_bebasExpiredFragment_to_tarikBarangFragment)
        }

        kembali.setOnClickListener { findNavController().navigate(R.id.action_bebasExpiredFragment_to_homeFragment) }
    }

    private fun drawPieChart(radiusList: List<Float>, colorsList: List<Int>) {
        val entries = ArrayList<PieEntry>()
        radiusList.forEachIndexed { index, radius ->
            if (index == 0) {
                entries.add(PieEntry(radius, "Didata"))
            } else {
                entries.add(PieEntry(radius, "Ditarik"))
            }
        }
        val dataSet = PieDataSet(entries, "Radius")
        dataSet.colors = colorsList

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.invalidate()
    }
}
