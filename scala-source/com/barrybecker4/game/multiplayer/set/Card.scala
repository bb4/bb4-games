/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set

import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*
import scala.util.boundary
import scala.util.boundary.break

class Card(colorVal: Card.AttributeValue, shapeVal: Card.AttributeValue, numberVal: Card.AttributeValue, textureVal: Card.AttributeValue) {
  import Card.*

  private val attributes = Array.ofDim[AttributeValue](Attribute.values.length)
  attributes(Attribute.COLOR.ordinal) = colorVal
  attributes(Attribute.SHAPE.ordinal) = shapeVal
  attributes(Attribute.NUMBER.ordinal) = numberVal
  attributes(Attribute.TEXTURE.ordinal) = textureVal

  private var isHighlighted_ = false
  private var isSelected_ = false

  def valueOfAttribute(a: Attribute): AttributeValue = attributes(a.ordinal)

  def color(): AttributeValue = attributes(Attribute.COLOR.ordinal)
  def shape(): AttributeValue = attributes(Attribute.SHAPE.ordinal)
  def number(): AttributeValue = attributes(Attribute.NUMBER.ordinal)
  def texture(): AttributeValue = attributes(Attribute.TEXTURE.ordinal)

  override def toString: String = {
    val buf = StringBuilder()
    for (i <- 0 until attributes.length) {
      val value = attributes(i)
      buf.append(Attribute.values.apply(i)).append(":").append(value).append("  ")
    }
    buf.toString
  }

  def isSelected: Boolean = isSelected_
  def setSelected(selected: Boolean): Unit = isSelected_ = selected

  def isHighlighted: Boolean = isHighlighted_
  def setHighlighted(highlighted: Boolean): Unit = isHighlighted_ = highlighted

  def toggleSelect(): Unit = isSelected_ = !isSelected_

  override def equals(card: Any): Boolean = card match {
    case c: Card =>
      c.color() == color() && c.number() == number() && c.texture() == texture() && c.shape() == shape()
    case _ => false
  }

  override def hashCode(): Int = 
    color().ordinal * 1000 + number().ordinal * 100 + texture().ordinal * 10 + shape().ordinal
}

object Card {
  enum AttributeValue extends java.lang.Enum[AttributeValue] {
    case FIRST, SECOND, THIRD
  }

  enum Attribute extends java.lang.Enum[Attribute] {
    case COLOR, SHAPE, NUMBER, TEXTURE
  }

  def isSet(cards: java.util.List[Card]): Boolean = {
    if (cards == null || cards.size != 3) return false

    Attribute.values.forall { attribute =>
      val val0 = cards.get(0).valueOfAttribute(attribute)
      val val1 = cards.get(1).valueOfAttribute(attribute)
      val val2 = cards.get(2).valueOfAttribute(attribute)
      val allSame = (val0 == val1) && (val1 == val2)
      val allDifferent = (val0 != val1) && (val1 != val2) && (val0 != val2)
      allSame || allDifferent
    }
  }

  def hasSet(cards: java.util.List[Card]): Boolean = 
    getSetsInternal(cards, true).size / 3 > 0

  def numSets(cards: java.util.List[Card]): Int = 
    getSetsInternal(cards, false).size / 3

  def getSets(cards: java.util.List[Card]): java.util.List[Card] = 
    getSetsInternal(cards, false)

  private def getSetsInternal(cards: java.util.List[Card], terminateEarly: Boolean): java.util.List[Card] = boundary {
    val sets = java.util.LinkedList[Card]()
    if (cards == null || cards.size < 3) break(sets)

    for (i <- 0 until cards.size) {
      val candidate = java.util.LinkedList[Card]()
      candidate.add(cards.get(i))
      for (j <- i + 1 until cards.size) {
        candidate.add(cards.get(j))
        for (k <- j + 1 until cards.size) {
          candidate.add(cards.get(k))
          if (isSet(candidate)) {
            sets.addAll(candidate)
            if (terminateEarly) break(sets)
          }
          candidate.remove(2)
        }
        candidate.remove(1)
      }
    }
    sets
  }
}
