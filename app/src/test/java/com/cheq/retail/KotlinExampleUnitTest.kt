package com.cheq.retail

import org.junit.Test

class KotlinExampleUnitTest {

    @Test
    fun test_stringReplacement() {

        val creditScoreStateTemplate = "Woohoo! You're top {{value}} in your area";
        val creditScoreStateValue = 24

        val value =  creditScoreStateTemplate.replace(Regex("top.*\\{\\{.*\\}\\}"), "<span style=\"color: #00C197;\">top $creditScoreStateValue</span>")

        println(value)
    }
}