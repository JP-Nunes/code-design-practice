package br.com.study.codedesignpractice

import org.junit.jupiter.api.Test

class FoolingAroundWithLists {

    @Test
    fun `trying somethings using arrayLists`() {
        val arrayList = arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        arrayList.add(5, 11)
        println(arrayList.joinToString())

        println(arrayList.contains(8))
        println(arrayList.contains(88))

        arrayList.remove(11)
        println(arrayList.joinToString())
    }

    @Test
    fun `trying somethings using linkedLists`() {
        val linkedList = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    }
}