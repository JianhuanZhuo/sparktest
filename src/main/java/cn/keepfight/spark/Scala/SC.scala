package cn.keepfight.spark.Scala

import org.apache.spark.SparkContext
import org.apache.spark.graphx._

/**
  * Created by tom on 17-5-3.
  */
object SC {

  val isremote = true

  def x: Unit ={
    val sc =
      if (isremote) new SparkContext("spark://10.10.6.30:7077", "SStest")
      else new SparkContext("local[4]", "SStest")



//    val graph = util.GraphGenerators.logNormalGraph(sc, 15)

    val graph = GraphGenerator.hashGraph(sc, 1000, 1).cache()

//    graph.triplets.foreach(println)
    graph.edges.foreach(println)

    println("ok")
  }
}
