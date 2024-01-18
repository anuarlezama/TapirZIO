package com.alezama.cart.service

import com.alezama.cart.models.{Item, ItemId, Items, UserId}
import zio.{Ref, UIO, ULayer, ZLayer}

trait CartService {
  def initialize(userId: UserId): UIO[Unit]
  def addItem(userId: UserId, item: Item): UIO[Items]
  def removeItem(userId: UserId, itemId: ItemId): UIO[Items]
  def updateItemQuantity(userId: UserId, itemId: ItemId, quantity: Int): UIO[Items]
  def getContents(userId: UserId): UIO[Items]
  
}

final case class CartServiceLive(carts: Ref[Map[UserId, Items]]) extends CartService {
  self =>
  override def initialize(userId: UserId): UIO[Unit] = self.carts.update(_ + (userId -> Items.empty))
  def addItem(userId: UserId, item: Item): UIO[Items] = self.updateCartsWith(userId)(_ + item)
  def removeItem(userId: UserId, itemId: ItemId): UIO[Items] = self.updateCartsWith(userId)(_ - itemId)

  override def updateItemQuantity(userId: UserId, itemId: ItemId, quantity: Int): UIO[Items] =
    self.updateCartsWith(userId)(_.updateQuantity(itemId, quantity))
    
  def getContents(userId: UserId): UIO[Items] = self.carts.get.map(_.getOrElse(userId, Items.empty))
  
  private def updateCartsWith(userId: UserId)(f: Items => Items): UIO[Items] =
    self.carts.updateAndGet(_.updatedWith(userId)(_.map(f))).map(_.getOrElse(userId, Items.empty))
}

object CartServiceLive {
  val layer: ULayer[CartService] = {
    ZLayer {
      Ref.make(Map.empty[UserId, Items]).map(CartServiceLive(_))
    }
  }
}