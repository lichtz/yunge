package com.yunge.im.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.yunge.im.R

class IndexHomeFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val aaChartView = view.findViewById<AAChartView>(R.id.aa_chart_view)
        val aaChartModel: AAChartModel = AAChartModel()
            .chartType(AAChartType.Bubble)
            .categories(arrayOf("11-24","11-25","11-26","11-27", "11-28","11-29","11-30"))
            .title("")
            .subtitle("")
            .backgroundColor(Color.TRANSPARENT)
            .dataLabelsEnabled(true)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("号码总数")
                        .data(
                            arrayOf(
                                7,
                                10,
                                10,
                                10,
                                30,
                                20,
                                20,
                            )
                        ),
                    AASeriesElement()
                        .name("已接通")
                        .data(
                            arrayOf(
                                2,
                                3,
                               4,
                                3,
                                2,
                                3,
                                0,
                            )
                        ),
                    AASeriesElement()
                        .name("未接通")
                        .data(
                            arrayOf(
                                6,
                                5,
                                4,
                                3,
                                3,
                                5,
                                1,
                            )
                        ),
                    AASeriesElement()
                        .name("意向")
                        .data(
                            arrayOf(
                                2,
                                2,
                                1,
                                3,
                                3,
                                1,
                                0,
                            )
                        )
                )
            )
        aaChartView.aa_drawChartWithChartModel(aaChartModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_index_layout, container, false)
    }
}