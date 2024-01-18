package com.alezama.cart.server

import com.alezama.cart.endpoints.Endpoints
import com.alezama.cart.models.{Eff, Items}
import com.alezama.cart.service.{CartService, CartServiceLive}
import zio.ZIOAppDefault
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import zio.*
import zio.interop.catz.*
import sttp.tapir.ztapir.{RichZEndpoint, ZServerEndpoint}

object TapirServer extends ZIOAppDefault with Endpoints {
  val initializeCartLogic = initilizeCart.zServerLogic {
    userId =>
      ZIO.logSpan("initializeCart") {
        for {
          _ <- ZIO.logInfo("Initialize cart")
          cart <- ZIO.service[CartService]
          _ <- cart.initialize(userId)
        } yield ()
      } @@ ZIOAspect.annotated("userId", userId.toString)
  }

  val addItemsLogic = addItem.zServerLogic {
    case (userId, allItems, item) =>
      ZIO.logSpan("addItem") {
        for {
          _ <- ZIO.logInfo("Adding item to cart")
          cart <- ZIO.service[CartService]
          items0 <- cart.addItem(userId, item)
          items = allItems match
            case Some(true) => items0
            case _ => Items.empty + item
        } yield items
      } @@ZIOAspect.annotated("userId", userId.toString)
  }

  val removeItemLogic = removeIem.zServerLogic {
    case (userId, itemId) =>
      ZIO.logSpan("removeItem") {
        for {
          _ <- ZIO.logInfo("Removing item from cart")
          cart <- ZIO.service[CartService]
          items <- cart.removeItem(userId, itemId)
        } yield items
      } @@ ZIOAspect.annotated("userId" -> userId.toString, "itemId" -> itemId.toString)
  }

  val updateItemLogic: ZServerEndpoint[CartService, Any] = updateItem.zServerLogic {
    case (userId, itemId, updateItemRequest) =>
      ZIO.logSpan("updateItem") {
        for {
          _ <- ZIO.logInfo("Updating item")
          cart <- ZIO.service[CartService]
          items <- cart.updateItemQuantity(userId, itemId, updateItemRequest.quantity)
        } yield items
      } @@ ZIOAspect.annotated("userId" -> userId.toString, "itemId" -> itemId.toString)
  }

  val getCartContentsLogic = getCartContents.zServerLogic {
    case (userId, limit) =>
      ZIO.logSpan("getCartContents") {
        for {
          _ <- ZIO.logInfo("Getting cart contents")
          cart <- ZIO.service[CartService]
          items <- cart.getContents(userId)
        } yield limit.fold(items)(items.take)
      } @@ ZIOAspect.annotated("userId" -> userId.toString)
  }

  val routes: HttpRoutes[Eff] =
    ZHttp4sServerInterpreter()
      .from(
        List(initializeCartLogic,
          addItemsLogic,
          removeItemLogic,
          updateItemLogic,
          getCartContentsLogic
        )
      ).toRoutes

  override val run =
    BlazeServerBuilder[Eff]
      .bindHttp(8080, "localhost")
      .withHttpApp(routes.orNotFound)
      .serve
      .compile
      .drain
      .provideLayer(CartServiceLive.layer)
}
