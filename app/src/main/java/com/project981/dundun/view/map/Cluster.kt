package com.project981.dundun.view.map

import com.project981.dundun.model.dto.MarkerDTO
import kotlin.math.sqrt

class Cluster(private var v: List<MarkerDTO>) {
    private var tempV = ArrayList<MarkerDTO>()
    private val parent = ArrayList<Int>()
    private val label = ArrayList<Int>()

    private val dist = arrayListOf<ArrayList<Double>>()

    init {
        for (i in v.indices) {
            val temp = arrayListOf<Double>()
            for (j in v.indices) {
                if (i == j) {
                    temp.add(0.0)
                } else {
                    val x1: Double = v[i].lng
                    val x2: Double = v[j].lng
                    val y1: Double = v[i].lat
                    val y2: Double = v[j].lat
                    temp.add(sqrt(pow(x1 - x2) + pow(y1 - y2)));
                }
            }
            dist.add(temp)
        }
    }

    private fun find(x: Int): Int {
        if (parent[x] == x) {
            return x;
        }
        val y = find(parent[x]);
        parent[x] = y
        return y
    }

    private fun check(x: Int, y: Int, d: Double): Boolean {
        if (x == y) return false

        return dist[x][y] <= d
    }

    private fun pow(a: Double): Double {
        return a * a;
    }

    private fun merge(x: Int, y: Int) {
        val a = find(x)
        val b = find(y)

        parent[b] = a
    }

    fun clustering(d: Double): List<MarkerDTO> {
        parent.clear()
        label.clear()
        tempV = arrayListOf()
        for (i in v.indices) {
            parent.add(i)
            label.add(0)
            tempV.add(v[i].copyObj())
        }

        for (i in v.indices) {
            var Cnt = 0
            for (j in v.indices) {
                if (check(i, j, d)) {
                    Cnt++
                }
            }
            if (Cnt >= 1) {
                label[i] = 1
                for (j in v.indices) {
                    if (label[j] == 1) {
                        if (check(i, j, d)) {
                            merge(i, j)
                        }
                    } else {
                        if (check(i, j, d)) {
                            label[j] = 2
                            merge(i, j)
                        }
                    }
                }
            } else {
                label[i] = 3
            }
        }
        for (i in v.indices) {
            find(i)
        }

        val m = mutableMapOf<Int, MarkerDTO>()
        for (i in v.indices) {
            if (m[parent[i]] == null) {
                m[parent[i]] = tempV[i].copyObj()
            } else {
                requireNotNull(m[parent[i]]).also {
                    it.noticeList.addAll(tempV[i].noticeList)
                    it.lat += tempV[i].lat
                    it.lng += tempV[i].lng
                    it.count++
                }
            }
        }
        val ans = mutableListOf<MarkerDTO>()
        for (item in m) {
            ans.add(item.value)
        }

        return ans
    }
}