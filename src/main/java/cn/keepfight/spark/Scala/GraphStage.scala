package cn.keepfight.spark.Scala

import org.apache.spark.graphx.Graph
import org.graphstream.graph.Node
import org.graphstream.graph.implementations.SingleGraph

/**
  * Created by tom on 17-5-4.
  */
class GraphStage(graph: Graph[(Int, Int), Int]) {

  val internalGraph = new SingleGraph("Tourial 1")
  internalGraph.setStrict(false)
  internalGraph.setAutoCreate(true)
  internalGraph.addAttribute("ui.quality")
  internalGraph.addAttribute("ui.antialias")


  val color = Array("red", "yellow", "green", "blue", "black", "pink", "cyan1")
  var style: String = "node{size:4px;fill-color:red;}" + "edge{size: 0px;}"
  var i = 0
  while (i < color.length) {
      style += "node.marke" + i + "{fill-color:" + color(i) + ";}"
      i += 1;
  }
  internalGraph.addAttribute("ui.stylesheet", style)


  def display(){
    graph.edges.collect().foreach(edge=>{
      // add edges
      internalGraph.addEdge("e"+edge.srcId+"->e"+edge.dstId,"v"+edge.srcId,"v"+edge.dstId)

      // add labels

      print("")
    })

    graph.vertices.collect().foreach(v=>{
//      val node = internalGraph.getNode("v" + v._1)
//      if (node.isInstanceOf[Node]) {
//        node.asInstanceOf[Node].addAttribute("ui.label", "marke"+v._2._1)
//      }
      internalGraph.getNode("v" + v._1).asInstanceOf[Node].addAttribute("ui.class", "marke"+(v._1%6))
//      internalGraph.getNode("v" + v._1).asInstanceOf[Node].addAttribute("ui.label", "v"+v._1+":"+v._2._2)
      internalGraph.getNode("v" + v._1).asInstanceOf[Node].addAttribute("ui.style", "size:"+Math.sqrt(v._2._2/10)+"px;")
      //x.addAttribute("ui.style", "size:"+Math.sqrt(x.getNumber("uix.label"))+"px;");

      print("")
    })

    internalGraph.display()
  }
}
