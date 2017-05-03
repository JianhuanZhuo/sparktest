package cn.keepfight.spark.Scala


import org.apache.spark.SparkContext
import org.apache.spark.graphx.{Edge, Graph, VertexId}
import org.apache.spark.rdd.RDD

import scala.annotation.tailrec
import scala.util.Random

/**
  * Created by tom on 17-5-3.
  */
object GraphGenerator {

  val SEED = 100;

//  def testGraph(numVertices: Int){
//
//        val vertices: RDD[(VertexId, Long)] = sc.parallelize(0 until numVertices).map {
//          src => (src, 1)
//        }
//        val edges = vertices.flatMap { case (src, degree) =>
//          generateRandomEdges(src.toInt, degree.toInt, numVertices, seed = (src))
//        }
//          Graph(vertices, edges)
//  }

  def hashGraph(sc:SparkContext, numVertices: Int, degree: Int):Graph[Int, Int]={

    /**
      * val s: Int = i / d
      * val e: Int = generateEdge(i) / (2 * d)
      */
    val edges: RDD[(VertexId, VertexId)] = sc.parallelize(0 until numVertices*degree-1)
      .map(eid => (eid, getSeed(eid)))
    Graph.fromEdgeTuples(edges, 1)
  }

  def generateRandomEdges(
                           src: Int,
                           numEdges: Int,
                           maxVertexId: Int,
                           seed: Long): Array[Edge[Int]] = {
    val rand =  new Random(seed)
    Array.fill(numEdges) { Edge[Int](src, rand.nextInt(maxVertexId), 1) }
  }


  /**
    * get scale-free edge by index
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
