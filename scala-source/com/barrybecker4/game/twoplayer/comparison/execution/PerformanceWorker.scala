// Copyright by Barry G. Becker, 2012-2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.execution

import com.barrybecker4.game.twoplayer.common.TwoPlayerController
import com.barrybecker4.game.twoplayer.comparison.model.ResultsModel
import com.barrybecker4.game.twoplayer.comparison.model.config.SearchOptionsConfigList

import javax.swing.SwingWorker
import java.util.LinkedList
import scala.jdk.CollectionConverters._

/**
 * A worker that will run all the computer vs computer games serially in a separate thread.
 * @author Barry Becker
 */
class PerformanceWorker(controller: TwoPlayerController[?, ?], optionsList: SearchOptionsConfigList)
  extends SwingWorker[ResultsModel, Integer] {

  private val resultsBuilder = new PerformanceResultsBuilder(controller, optionsList)
  private val listeners = new LinkedList[PerformanceRunnerListener]()
  private val model = new ResultsModel(optionsList.size())
  private var progress = 0

  def addListener(listener: PerformanceRunnerListener): Unit = {
    listeners.add(listener)
  }

  override def doInBackground(): ResultsModel = {
    val size = model.getSize
    var i = 0
    while (i < size) {
      var j = 0
      while (j < size) {
        val results = resultsBuilder.getResultsForComparison(i, j)
        model.setResults(i, j, results)
        publish(progress)
        progress += 1
        j += 1
      }
      i += 1
    }
    model.normalize()
    model
  }

  override def process(chunks: java.util.List[Integer]): Unit = {}

  override def done(): Unit = {
    try {
      val resultModel = get()
      listeners.asScala.foreach(_.performanceRunsDone(resultModel))
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }
}
