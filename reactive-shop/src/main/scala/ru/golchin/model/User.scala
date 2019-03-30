package ru.golchin.model

import ru.golchin.model.Currency.Currency

case class User(login: String, firstName: String, lastName: String, currency: Currency)