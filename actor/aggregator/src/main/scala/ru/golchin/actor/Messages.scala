package ru.golchin.actor

case class Request(query: String, limit: Int)

case class Response(results: Seq[String])

case class AggregateResponse(results: Map[String, Seq[String]])

