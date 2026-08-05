// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.set

import com.barrybecker4.game.common.board.Board
import com.barrybecker4.game.common.Move
import java.util.{ArrayList, LinkedList, List}

class SetBoard(private val initialNumCardsShown_ : Int) extends Board[Move] {
  import SetBoard.*

  private var deck_ : List[Card] = scala.compiletime.uninitialized
  private var numCardsShown_ : Int = scala.compiletime.uninitialized

  initializeData()

  override def reset(): Unit = initializeData()

  override def copy(): SetBoard = {
    val b = SetBoard(initialNumCardsShown_)
    b.numCardsShown_ = this.numCardsShown_
    b
  }

  def initializeData(): Unit = {
    deck_ = Deck()
    numCardsShown_ = initialNumCardsShown_
  }

  def getDeck: List[Card] = deck_

  def getNumCardsShowing: Int = numCardsShown_

  def addCards(num: Int): Unit = {
    for (i <- 0 until num) {
      if (hasCardsToAdd) numCardsShown_ += 1
    }
  }

  def removeCard(): Unit = {
    if (canRemoveCards) numCardsShown_ -= 1
  }

  def canRemoveCards: Boolean = numCardsShown_ > 3

  def hasCardsToAdd: Boolean = numCardsShown_ < deck_.size && numCardsShown_ < MAX_CARDS_BEFORE_SET

  def removeCards(cards: List[Card]): Unit = {
    deck_.removeAll(cards)
    numCardsShown_ -= 3
  }

  private def getCardsOnBoard: List[Card] = {
    val cardsOnBoard = LinkedList[Card]()
    for (i <- 0 until getNumCardsShowing) {
      cardsOnBoard.add(getDeck.get(i))
    }
    cardsOnBoard
  }

  def getSetsOnBoard: List[Card] = Card.getSets(getCardsOnBoard)

  def getSelectedCards: List[Card] = {
    val selected = ArrayList[Card]()
    for (i <- 0 until getNumCardsShowing) {
      val card = getDeck.get(i)
      if (card.isSelected) selected.add(card)
    }
    selected
  }

  def getNumSetsOnBoard: Int = Card.numSets(getCardsOnBoard)

  def isDone: Boolean = !Card.hasSet(deck_)

  override def makeMove(move: Move): Boolean = 
    throw UnsupportedOperationException("make move not supported for set.")

  override def undoMove(): Move = 
    throw UnsupportedOperationException("undo move not supported for set.")
  
  override def makeInternalMove(move: Move): Boolean = 
    throw UnsupportedOperationException("makeInternalMove not supported for set.")
  
  override def undoInternalMove(move: Move): Unit = 
    throw UnsupportedOperationException("undoInternalMove not supported for set.")
  
  override def getMaxNumMoves: Int = 0
}

object SetBoard {
  private val MAX_CARDS_BEFORE_SET = 20
}
