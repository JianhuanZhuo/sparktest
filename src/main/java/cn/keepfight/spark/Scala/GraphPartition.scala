package cn.keepfight.spark.Scala

import org.apache.spark.SparkContext
import org.apache.spark.graphx.{EdgeContext, Graph}

/**
  * functions of map
  * mapVertices         map: (graphx.VertexId, VD) => VD2
  * mapTriplets         map: EdgeTriplet[VD, ED] => ED2
  * mapTriplets         map: EdgeTriplet[VD, ED] => ED2,
  *                     tripletFields: TripletFields
  * mapTriplets         map: (PartitionID, Iterator[EdgeTriplet[VD, ED]]) => Iterator[ED2],
  *                     tripletFields: TripletFields
  * mapEdges            map: (PartitionID, Iterator[Edge[ED]]) => Iterator[ED2]
  * mapEdges            map: Edge[ED] => ED2
  * : Graph[VD, ED2]
  *
  *
  * functions of aggregate
  * aggregateMessages   sendMsg: EdgeContext[VD, ED, A] => Unit,
  *                     mergeMsg: (A, A) => A,
  *                     tripletFields: TripletFields = TripletFields.All
  * : VertexRDD[A]
  *
  * can update continuously each vertex based only on information obtained from neighboring edges and vertices.
  *
  * Created by tom on 17-5-4.
  */
object GraphPartition {

  // getDegree
  def jbj(graph: Graph[(Int, Int), Int]): Graph[(Int, Int), Int] ={



    // return graph
    graph
  }

  /**
    * find the greatest distance from ancestor node.
    * return each vertex labeled with the farthest distance from an ancestors.
    */
  def example(graph : Graph[(Int, Int), Int]):Graph[(Int, Int), Int]={

    def sendMsg(ec: EdgeContext[(Int, Int), Int, Int]):Unit={
      //send to distance element with attribute of source
      // so, src element will add and send distance to the dist element
      ec.sendToDst(ec.srcAttr._2+1)
      ec.sendToSrc(1)
    }

    def mergeMsg(a:Int, b:Int): Int ={
      math.max(a, b)
//      a+b
    }

    def echo(graph: Graph[(Int, Int), Int]):Unit={
      graph.triplets.collect().foreach(println)
    }

    def propagateEdgeCount(g : Graph[(Int, Int), Int]):Graph[(Int, Int), Int]={
      // generate new set of vertices
      val verts = g.aggregateMessages[Int](sendMsg, mergeMsg)

      println("verts")
      verts.collect().foreach(println)
      //generate updated version of graph containing the new info.
      val verts_2 =  verts.mapValues((0,_))
      val vert_arr = verts_2.map(_._1).collect()
      val g2 = Graph(verts_2, g.edges.filter(e=>vert_arr.contains(e.dstId)&&vert_arr.contains(e.srcId)&&e.dstId!=e.srcId))

      println("g")
      echo(g)
      println("g2")
      echo(g2)

      // join means collect both information of two graphs for the same vertex
      // those information are stored in x._2, vertexID is stored in x._1
      val check = g2.vertices.join(g.vertices)
            //
            .map(x=>x._2._1._2 - x._2._2._2)
            .reduce(_+_)

      //continue recursion if graph has changed
      if (check>0) propagateEdgeCount(g2)
      else g
    }

    propagateEdgeCount(graph)
  }
}
