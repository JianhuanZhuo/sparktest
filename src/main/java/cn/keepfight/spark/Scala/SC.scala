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

    sc.addJar("/home/tom/spark-test-1.0-SNAPSHOT.jar")


//    val graph = util.GraphGenerators.logNormalGraph(sc, 15)

    val graph = GraphGenerator.hashGraph(sc, 10, 2).cache()

    graph.vertices.collect().foreach(println)
    println("tcount:"+graph.triplets.count())
    println("vcount:"+graph.vertices.count())
    println("ecount:"+graph.edges.count())
//    graph.edges.foreach(x=>{
//      println(x.dstId)
//    })
//    graph.edges.foreach(println)

    println("ok")

    sc.stop()
  }
}
