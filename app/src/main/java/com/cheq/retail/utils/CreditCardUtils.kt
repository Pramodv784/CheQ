package com.cheq.retail.utils

class CreditCardUtils {

    companion object {
        fun check(ccNumber: String): Boolean {
            try {
                var sum = 0
                var alternate = false
                for (i in ccNumber.length - 1 downTo 0) {
                    var n = ccNumber.substring(i, i + 1).toInt()
                    if (alternate) {
                        n *= 2
                        if (n > 9) {
                            n = n % 10 + 1
                        }
                    }
                    sum += n
                    alternate = !alternate
                }
                return sum % 10 == 0
            } catch (e: Exception) {
                return false
            }
        }
    }

}