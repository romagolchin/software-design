package ru.golchin.model

object Currency {
  val exchangeRates: Map[String, Map[String, Double]] = Map(
    "USD" -> Map("EUR" -> 0.875, "RUR" -> 70),
    "EUR" -> Map("RUR" -> 80)
  )

  def conversionRate(currencyFrom: String, currencyTo: String): Double = {
    if (currencyTo == currencyFrom)
      1.0
    else {
      val maybeMap = exchangeRates.get(currencyTo)
      if (maybeMap.isDefined && maybeMap.get.get(currencyFrom).isDefined) {
        1.0 / exchangeRates(currencyTo)(currencyFrom)
      } else {
        exchangeRates(currencyFrom)(currencyTo)
      }
    }
  }

  def convert(costFrom: Cost, currencyTo: String): Cost =
    Cost(costFrom.value * conversionRate(costFrom.currency, currencyTo), currencyTo)
}


