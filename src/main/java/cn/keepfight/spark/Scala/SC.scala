package cn.keepfight.spark.Scala

import java.io._

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx.Graph


/**
  * Created by tom on 17-5-3.
  */
object SC {

  val isremote = true

  def x(){
    val conf = new SparkConf()
                    .set("spark.executor.memory","6g")
                    .setMaster("spark://10.10.6.30:7077")
                    .setAppName("SStest1")
    val sc =
      if (isremote) new SparkContext(conf)
      else new SparkContext("local[1]", "SStest")
    sc.addJar("/home/tom/spark-test-1.0-SNAPSHOT.jar")


    // generate graph by using hash random seed
    // specify label
    // spark not need to ?? specify tactic to divide E[] to hosts
    val graph = GraphGenerator.hashGraph(sc, 10000000, 2, 4).cache()

    /************************************ /
    val g2 = graph.mapVertices((id, vd)=>Data.data.apply(id.toInt))
    new GraphStage(g2).display()
    // ***********************************/

//
////    echo(graph)
//    GraphPartition.example(graph)
//
//    // sampleAndSwap
//
//    // compact graph with attribute n which indicate number of nodes
//

    /************************************************************************************************
      *
      */
    System.setOut(new PrintStream(new FileOutputStream("/home/tom/huhu.tx")))
//    println("begin")
    graph.edges
      .map(edge=>(edge.dstId, 1))
      .reduceByKey(_+_)
//      .reduce((x,y)=>if(x._2>y._2) x else y)
      .sortBy(f=>f._2, ascending = false)
      .map(ss=>ss._2)
      .collect
      .foreach(println)
//    println(max)
//    println("end")

    sc.stop()


    // ************************************************************************************************/
  }

  def echo(graph: Graph[(Int, Int), Int]):Unit={
    graph.triplets.collect().foreach(println)
  }
}



