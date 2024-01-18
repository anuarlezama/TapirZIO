package com.alezama.cart.models

import com.alezama.cart.service.CartService
import sttp.tapir.EndpointIO.annotations.description
import sttp.tapir.Schema
import zio.*
import zio.json.*

import java.util.UUID
import scala.util.Try

type Eff[+A] = RIO[CartService, A]
type ItemId = UUID
given encoder: JsonFieldEncoder[UUID] = JsonFieldEncoder.string.contramap(_.toString())
given decoder: JsonFieldDecoder[UUID] =
  JsonFieldDecoder.string.mapOrFail(str => Try(UUID.fromString(str)).toEither.left.map(_.getMessage))

type UserId = UUID

final case class Item (
  @description("The item's unique identifier") id: ItemId,
  @description("The Item's name") name: String,
  @description("The Item's unit price") price: Double,
  @description("The item's quantity") quantity: Int
  ) {
  self =>
  def withQuantity(quantity: Int): Item = self.copy(quantity = quantity)
}

object Item {
  given jsonCodec: JsonCodec[Item] = DeriveJsonCodec.gen[Item]
  given schema: Schema[Item] = Schema.derived[Item]
}

final case class Items(items: Map[ItemId, Item]) {
  self =>
  def +(item: Item): Items = Items(self.items + (item.id -> item))
  def -(itemId: ItemId): Items = Items(self.items - itemId)
  def take(n: Int): Items = Items(self.items.take(n))
  def updateQuantity(itemId: ItemId, quantity: Int): Items =
    Items(self.items.updatedWith(itemId)(_.map(_.withQuantity(quantity))))
}

object Items {
  val empty = Items(Map.empty)
  implicit val jsonCodec: JsonCodec[Items] = DeriveJsonCodec.gen[Items]
  implicit val schema: Schema[Items] = Schema
    .schemaForMap[ItemId, Item](_.toString())
    .map(items => Some(Items(items)))(_.items)
    .description("Map of item IDs to corresponding items")
}

final case class UpdateItemsRequest(@description("The new item quantity") quantity: Int)
object UpdateItemsRequest {
  given jsonCodec: JsonCodec[UpdateItemsRequest] = DeriveJsonCodec.gen[UpdateItemsRequest]
  given schema: Schema[UpdateItemsRequest] = Schema.derived[UpdateItemsRequest]
}
