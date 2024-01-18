package com.alezama.cart.doc

import sttp.apispec.openapi.circe.yaml.*
import com.alezama.cart.endpoints.Endpoints
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import zio.*

object DocConsolePrinter extends ZIOAppDefault with Endpoints {

  val docs = OpenAPIDocsInterpreter().toOpenAPI(
    List(initilizeCart, addItem, removeIem, updateItem, getCartContents),
    "Shopping cart",
    "0.1.0"
  )

  override val run =
    Console.printLine(s"OpenAPI docs:\n${docs.toYaml}")
}
