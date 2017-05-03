package cn.keepfight.spark.Scala

import java.util.Random

import org.apache.avro.TestAnnotation
import org.apache.spark._
import org.apache.spark.rdd._
import org.apache.spark.SparkContext._
import org.apache.spark.graphx._
import org.apache.spark.graphx.lib.ShortestPaths

import scala.annotation.tailrec

/**
  * Created by tom on 17-4-20.
  */
class SCTest {
  val SEED = 100;
  def test() {
    val sc = new SparkContext("spark://10.10.6.30:7077", "SStest");
//    sc.setLocalProperty("spark.jars", "/home/tom/idea/sparktest/target/spark-test-1.0-SNAPSHOT.jar")
//    println(sc.textFile("/home/tom/share/goods_sale_row_44.csv").count);

    val myVertices = sc.makeRDD(Array((1L, "Ann"), (2L, "Bill"),
      (3L, "Charles"), (4L, "Diane"), (5L, "Went to gym this morning")))

    val myEdges = sc.makeRDD(Array(Edge(1L, 2L, "is-friends-with"),
      Edge(2L, 3L, "is-friends-with"), Edge(3L, 4L, "is-friends-with"),
      Edge(4L, 5L, "Likes-status"), Edge(3L, 5L, "Wrote-status")))

    val myGraph = Graph(myVertices, myEdges)

//    val x =myGraph.vertices.collect
//    x.foreach(println)
//
//    // return type of triplet is an RDD of EdgeTriplet[VD, ED], which is a subclass of Edge[VD] that also contains
//    // references to the source and destination vertices associated with the edge.
//    val triple = myGraph.triplets.collect
//
//    triple.foreach(println)
//
//    val line : String = "=====" * 2
//
////    val x = s"pi is ${"balbla" * 3}"
//
//    println("------------------------------------")

    val pa = Array(3L)
    val ca = ShortestPaths.run(myGraph, pa).cache
      ca.vertices.foreach(println)

    println("------------------------------------")



//    val ts =myGraph.mapTriplets((t=>(t.attr, t.attr == "is-friends-with" && t.attr.contains("a")))
//      :(EdgeTriplet[String, String]=>Tuple2[String, Boolean])
//    )
//    val ts2 = ts.triplets
//    ts2.cache()
//    val ts3 = ts2.foreach(println)

//    val inDeg: RDD[(VertexId, Int)] = myGraph.aggregateMessages[Int](_.sendToSrc(1), _ + _)
////    inDeg.foreach(println)
//    inDeg.collect().foreach(println)
  }
  def generate(): Unit ={
    println(getSe(1))
  }

  //  @tailrec
//  def getSe(i:Int):Int={
//    val it = i*2+1
//    while (it % 2 != 0)
//      it = new Random(it + seed).nextInt(it)
//    return i
//  }

  /**
    * get  by index
    * @param i
    * @return
    */
  def getSeed(i : Int):Int={
    getSe(i*2+1)
  }

  @tailrec
  final def getSe(it : Int):Int={
    if (it % 2 == 0) it
    else getSe(new Random(it + SEED).nextInt(it))
  }

}
