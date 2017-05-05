package cn.keepfight.spark.Scala


import java.util.Random

import org.apache.spark.SparkContext
import org.apache.spark.graphx.util.GraphGenerators
import org.apache.spark.graphx.{Edge, Graph, VertexId}
import org.apache.spark.rdd.RDD

import scala.annotation.tailrec

/**
  * Created by tom on 17-5-3.
  */
object GraphGenerator {

  val SEED = 100L

  def hashGraph(sc:SparkContext, numVertices: Int, degree: Int, label : Int):Graph[(Int, Int), Int]={

    /**
      * val s: Int = i / d
      * val e: Int = generateEdge(i) / (2 * d)
      */
    val edges: RDD[(VertexId, VertexId)] = sc.parallelize(0 until numVertices*degree)
      .map(eid => (eid/degree, getSeed(eid)/(2*degree)))
    val resGraph = Graph.fromEdgeTuples(edges, (1,1))

    // let VD._1 be label
    resGraph.mapVertices((id, attr)=>{
      ((id%label).toInt, attr._2)
    })
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
