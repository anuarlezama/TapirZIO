package com.alezama.cart.client

import com.alezama.cart.endpoints.Endpoints
import com.alezama.cart.models.{Item, UpdateItemsRequest}
import sttp.client3.UriContext
import sttp.client3.httpclient.zio.HttpClientZioBackend
import sttp.tapir.client.sttp.SttpClientInterpreter
import zio.*

import java.util.UUID

object TapirClient extends ZIOAppDefault with Endpoints {

  val run =
    for {
      backend <- HttpClientZioBackend()
      uri = Some(uri"http://localhost:8080")
      initializeCartClient = SttpClientInterpreter().toClient(initilizeCart, uri, backend)
      addItemClient = SttpClientInterpreter().toClient(addItem, uri, backend)
      removeItemClient = SttpClientInterpreter().toClient(removeIem, uri, backend)
      updateItemClient = SttpClientInterpreter().toClient(updateItem, uri, backend)
      getCartContentClient = SttpClientInterpreter().toClient(getCartContents, uri, backend)
      userId <- ZIO.succeed(UUID.randomUUID())
      itemId1 <- ZIO.succeed(UUID.randomUUID())
      itemId2 <- ZIO.succeed(UUID.randomUUID())
      _ <- initializeCartClient(userId)
      _ <- addItemClient(userId, None, Item(itemId1, "test-item-1", 10.0, 10)).debug("add Item1 result")
      _ <- addItemClient(userId, Some(true), Item(itemId2, "test-item-2", 20.0, 20)).debug("add Item2 result")
      _ <- removeItemClient((userId, itemId2)).debug("removeItem result")
      _ <- updateItemClient((userId, itemId1, UpdateItemsRequest(35))).debug("updateItem result")
      _ <- addItemClient(userId, Some(true), Item(itemId2, "test-item-2", 20.0, 20)).debug("add Item2 result")
      _ <- getCartContentClient((userId, Some(1))).debug("getCartContents 1 result")
      _ <- getCartContentClient((userId, None)).debug("getCartContents unbounded result")
    } yield()
}
