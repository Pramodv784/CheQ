package com.cheq.retail.utils

enum class ProductStatus(val status: String){
    /**
     * If we can't find the Product status
     */
    ProductStatusDefault("0"),

    /**
     * Unsecured without bill
     */
    ProductStatusOne("1"),

    /**
     *
     */
    ProductStatusTwo("2"),

    /**
     * secured with bill
     */
    ProductStatusThree("3"),

    /**
     * secured without bill
     */
    ProductStatusFour("4"),

    /**
     * fully paid
     */
    ProductStatusFive("5")

}