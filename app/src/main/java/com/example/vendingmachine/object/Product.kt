package com.example.vendingmachine.`object`

data class Product(val name:String, val description:String, val price: Double, val ratio : Map<Int,Double>, val imageURL: String){
    override fun toString(): String {
        return "name: ${name}\ndesc: ${description}\nprice: ${price}\nratio: ${ratio}\nimageUrl: ${imageURL}"
    }
}