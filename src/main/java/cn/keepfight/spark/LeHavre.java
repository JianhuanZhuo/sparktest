package cn.keepfight.spark;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import static org.graphstream.algorithm.Toolkit.nodePosition;
import static org.graphstream.algorithm.Toolkit.randomNode;

/**
 * Created by tom on 17-4-12.
 */
public class LeHavre {
    ClassLoader loader = this.getClass().getClassLoader();
    public static void main(String args[]) {
        System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        new LeHavre();
    }

    public LeHavre() {
        Graph graph = new MultiGraph("Le Havre");
        String styleFile = loader.getResource("style.css").getFile();
        graph.addAttribute("ui.stylesheet", "url(file://"+styleFile+")");
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");

        try {
//            graph.read(LeHavre.class.getResource("LeHavre.dgs").toURI().toURL().toString());
//            LeHavre.class.getResource("")
            String f = loader.getResource("LeHavre.dgs").getFile();
            System.out.println(f);
            graph.read(f);
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("node count = "+graph.getNodeCount());
        System.out.println("edge count = "+graph.getEdgeCount());

        for(Edge edge: graph.getEachEdge()) {
            if(edge.hasAttribute("isTollway")) {
                edge.addAttribute("ui.class", "tollway");
            } else if(edge.hasAttribute("isTunnel")) {
                edge.addAttribute("ui.class", "tunnel");
            } else if(edge.hasAttribute("isBridge")) {
                edge.addAttribute("ui.class", "bridge");
            }
            // Add this :
            double speedMax = edge.getNumber("speedMax") / 130.0;
            edge.setAttribute("ui.color", speedMax);
        }
        graph.display(false);   // No auto-layout.
        SpriteManager sman = new SpriteManager(graph);
        Sprite s1 = sman.addSprite("S1");
        Sprite s2 = sman.addSprite("S2");
        Node n1 = randomNode(graph);
        Node n2 = randomNode(graph);
        double p1[] = nodePosition(n1);
        double p2[] = nodePosition(n2);
        s1.setPosition(p1[0], p1[1], p1[2]);
        s2.setPosition(p2[0], p2[1], p2[2]);

        graph.addAttribute("ui.screenshot", "/home/tom/share/screenshot.png");
    }
}
