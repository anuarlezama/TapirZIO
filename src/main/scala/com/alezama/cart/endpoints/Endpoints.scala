package com.alezama.cart.endpoints

import com.alezama.cart.models.{Item, Items, UpdateItemsRequest}
import sttp.model.StatusCode
import sttp.tapir.json.zio.jsonBody
import sttp.tapir.ztapir.*

import java.util.UUID

trait Endpoints {
  val userId = path[UUID]("userId").description("The unique identifier of a user")
  val itemId = path[UUID]("itemId").description("The unique identifier of an item")
  val limit = query[Option[Int]]("limit").description("The maximum number of items to obtain")
  val xAllItems = header[Option[Boolean]]("X-ALL-ITEMS")
    .description("Flag to indicate whether to return all items or just the new one")
  
  val initilizeCart =
    endpoint.post.in("cart" / userId)
      .out(statusCode(StatusCode.NoContent))
      .description("Initialize a user's cart")
    
  val addItem =
    endpoint.post
      .in("cart"/ userId / "item")
      .in(xAllItems)
      .in(jsonBody[Item].description("The item to be added"))
      .out(jsonBody[Items].description("The operation result"))
      .description("Add an item to a user's cart")
  
  val removeIem =
    endpoint.delete
      .in("cart" / userId / "item" /  itemId)
      .out(jsonBody[Items].description("The cart items after removal"))
      .description("Remove an item from a user's cart")
    
  val updateItem =
    endpoint.put
      .in("cart"/ userId / "item"/itemId)
      .in(jsonBody[UpdateItemsRequest].description("The request object"))
      .out(jsonBody[Items].description("The cart items after updating"))
      .description("Update an item")
    
  val getCartContents =
    endpoint.get
      .in("cart"/ userId)
      .in(limit)
      .out(jsonBody[Items].description("The cart items"))
      .description("Gets the contents of a user's cart")
}
