package ru.golchin.model

object Currency extends Enumeration {
  type Currency = Value
  val USD, EUR, RUR = Value
  val exchangeRates: Map[Currency, Map[Currency, Double]] = Map(
    USD -> Map(EUR -> 0.875, RUR -> 70),
    EUR -> Map(RUR -> 80)
  )

  def conversionRate(currencyFrom: Currency, currencyTo: Currency): Double = {
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

  def convert(costFrom: Cost, currencyTo: Currency): Cost =
    Cost(costFrom.value * conversionRate(costFrom.currency, currencyTo), currencyTo)
}


