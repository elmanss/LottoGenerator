package me.elmanss.melate.ui

import android.util.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.elmanss.melate.data.FavoritoQueries
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random


class MainPresenter(private val random: ThreadLocalRandom, private val queries: FavoritoQueries) {

    private val bombo = listOf(
        1,
        2,
        3,
        4,
        5,
        6,
        7,
        8,
        9,
        10,
        11,
        12,
        13,
        14,
        15,
        16,
        17,
        18,
        19,
        20,
        21,
        22,
        23,
        24,
        25,
        26,
        27,
        28,
        29,
        30,
        31,
        32,
        33,
        34,
        35,
        36,
        37,
        38,
        39,
        40,
        41,
        42,
        43,
        44,
        45,
        46,
        47,
        48,
        49,
        50,
        51,
        52,
        53,
        54,
        55,
        56
    )

    private fun shuffleBombo(): List<Int> {
        return bombo.shuffled(Random.apply { random })
        //return bombo.shuffled()
    }

    fun getRangeFromBombo(): List<Int> {
        return shuffleBombo().subList(0, 6).sorted()
    }

    fun multiSorteo(): List<List<Int>> {
        val sorteos = ArrayList<List<Int>>()
        for (i in 0 until 30) {
            sorteos.add(getRangeFromBombo())
        }
        return sorteos
    }

    fun saveToFavs(sorteo: String): Disposable {
        return Single.fromCallable {
            queries.insertFav(sorteo)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.i("MainPresenter", "Favorito agregado con exito")
            }, {
                Log.e("MainPresenter", "Error al agregar favorito con exito", it)
            })
    }
}