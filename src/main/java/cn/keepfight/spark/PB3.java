package cn.keepfight.spark;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.Graphs;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.keepfight.gs.PBA2.generateSF;

/**
 * Created by tom on 17-4-12.
 */
public class PB3 {
//    static final long seed = System.currentTimeMillis();
    static final long seed = System.currentTimeMillis();
    static int x;
    static int threadNum = 3;
    public static void main(String[] args) throws InterruptedException {
        SingleGraph internalGraph = new SingleGraph("Tourial 1");
        Graph graph = Graphs.synchronizedGraph(internalGraph);


        // // The graph being a factory for node
        graph.setStrict(false);
        graph.setAutoCreate(true);

        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");

        List<List<Node>> hostList = Collections.synchronizedList(new ArrayList<List<Node>>());


        int d = 1;
        int n = 10000;
        x = threadNum;
        for (int i = 0; i < threadNum; i++) {
            hostList.add(new ArrayList<>());
        }

        String[] color = {"red", "yellow", "green", "blue", "black", "pink", "cyan1"};
        String style = "node{size:4px;fill-color:red;}" + "edge{size: 0px;}";
        for (int i = 0; i < color.length; i++) {
            style += "node.marke" + i + "{fill-color:" + color[i] + ";}";
        }

        graph.addAttribute("ui.stylesheet", style);

        generateSF(threadNum, n, d, graph);
//        graph.getNodeSet().forEach(node->node.addAttribute("ui.label", node.getId()));
//        graph.getNodeSet().stream()
//                .collect(Collectors.groupingBy(node->node.getAttribute("ui.class"), Collectors.counting()))
//                .forEach((k,v)-> System.out.println(k+" counting: "+v));

        System.out.println("generate ok!");

        // display the graph
//        graph.display();


        for (int i = 0; i < 100; i++) {
            int count = 0;
            for (int k= 0; k< graph.getNodeCount() ; k++) {
                Node p = graph.getNode(k);
                count += PBA2.sampleAndSwap(p, graph);
            }
            if (count < n * 0.01) {
                System.out.println("out!");
                break;
            }
        }
//        graph.getNodeSet().forEach(node->node.addAttribute("n",1));
//        compact(graph, false);
//        graph.forEach(GroupManager::putGroup);
//
//        System.out.println("group exchange!");
//
////        new Scanner(System.in).next();
//        graph.forEach(node -> {
//            node.setAttribute("ui.label", node.getId()+" : "+node.getAttribute("n"));
//        });
//
//        Thread.sleep(3000);
//
//        graph.getNodeSet().stream()
//                .collect(Collectors.groupingBy(node->node.getAttribute("ui.class"), Collectors.summingInt(node->node.getAttribute("n"))))
//                .forEach((k,v)-> System.out.println(k+" counting: "+v));
//        for (int i=0; i<20; i++) {
//            graph.forEach(node -> {
//                if (swapWithN(node, graph)) compact(graph, true);
//            });
//        }
//        graph.getNodeSet().stream()
//                .collect(Collectors.groupingBy(node->node.getAttribute("ui.class"), Collectors.summingInt(node->node.getAttribute("n"))))
//                .forEach((k,v)-> System.out.println(k+" counting: "+v));
//        System.out.println("over?");

        // display the graph
//        graph.display();
    }


    static boolean swapWithN(Node p, Graph graph){
        Node partner;
//        System.out.println("try node of "+p.getId());
        int num = p.getAttribute("n");
//        Stream<Node> s = p.getEdgeSet().stream().map(e->e.getOpposite(p));
//        Set<Node> nodes = s.filter(n->n!=p).filter(n->n.getAttribute("n").equals(num)).collect(Collectors.toSet());

        Set<Node> nodes = new HashSet<>();
        Iterator<Node> it = p.getNeighborNodeIterator();
        while (it.hasNext()) {
            Node node = it.next();
            if (!graph.getNodeSet().contains(node)){
                try {
                    Thread.currentThread().wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (node.getAttribute("n").equals(p.getAttribute("n")))
            nodes.add(node);
        }
        System.out.println("2try node of "+p.getId() + "("+p.getAttribute("n")+") with :"+ nodes.stream().map(node->node.getId()+"_"+node.getAttribute("n")).collect(Collectors.joining("; ")));

        nodes.stream().filter(node->!graph.getNodeSet().contains(node)).forEach(x->{
            System.out.println(x.getId()+" -> is not contains!");
        });

        partner = findPartnerWithN(p, nodes);
        if (partner!=null && !graph.getNodeSet().contains(partner)){
            System.out.println("god! it's a ghost node!" +graph.getNodeSet().contains(partner));
            try {
                Thread.currentThread().wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (partner==null){
            nodes = GroupManager.getSamples(p);
            System.out.println("try node of "+p.getId() + " with :"+ nodes.stream().map(node->node.getId()+node.getAttribute("n")).collect(Collectors.joining(";")));
            partner = findPartnerWithN(p, nodes);
        }

        // 执行交换
        if (partner!=null) {
            // 简单交换
            if (!partner.getAttribute("n").equals(p.getAttribute("n"))){
                System.out.println("god! it's not the same nodes");
            }
            synchronized (p) {
                Set<Node> nos = GroupManager.removeAll(p, partner);
//                graph.getNodeSet().stream()
//                        .collect(Collectors.groupingBy(node->node.getAttribute("ui.class"), Collectors.summingInt(node->node.getAttribute("n"))))
//                        .forEach((k,v)-> System.out.println(k+" before counting: "+v));
                List<String> listStr = graph.getNodeSet().stream()
                        .map(x->x.getId()+" - "+x.getAttribute("ui.class")+" -- "+x.getAttribute("n"))
                        .collect(Collectors.toList());

                String c1 = p.getAttribute("ui.class");
                String c2 = partner.getAttribute("ui.class");
                p.setAttribute("ui.class", c2);
                partner.setAttribute("ui.class", c1);
                System.out.println("swap in "+Thread.currentThread().getName()+":"
                        +p.getId()+"("+p.getAttribute("ui.class")+"|"+p.getAttribute("n")+") <==> "
                        +partner.getId()+"("+partner.getAttribute("ui.class")+"|"+partner.getAttribute("n")+")");

                nos.forEach(GroupManager::putGroup);

//                graph.getNodeSet().stream()
//                        .collect(Collectors.groupingBy(node->node.getAttribute("ui.class"), Collectors.summingInt(node->node.getAttribute("n"))))
//                        .forEach((k,v)-> System.out.println(k+" after counting: "+v));

                List<String> listStr2 = graph.getNodeSet().stream()
                        .map(x->x.getId()+" - "+x.getAttribute("ui.class")+" -- "+x.getAttribute("n"))
                        .collect(Collectors.toList());
                graph.getNodeSet().stream()
                        .collect(Collectors.groupingBy(node->node.getAttribute("ui.class"), Collectors.summingInt(node->node.getAttribute("n"))))
                        .forEach((k,v)->{
                            if (v!=40){
                                for (int i = 0; i < listStr2.size()+3; i++) {
                                    try {
                                        String r = listStr.get(i);
                                        String y = listStr2.get(i);
                                        if (!r.equals(y)){
                                            System.out.println(r+" -> "+y);
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                                System.exit(0);
                            }
                        });
            }
            return true;
        }
        return false;
    }

    static void compact(Graph g, boolean c){
        // must compact by for i, iterator will get troubles
        for (int j=0; j<g.getNodeCount(); j++) {
            Node x = g.getNode(j);

            for (int i = 0; i < x.getDegree(); i++) {
                Node y = x.getEdge(i).getOpposite(x);
                if (y == x) {
                    continue;
                }

                if (y.getAttribute("ui.class").equals(x.getAttribute("ui.class"))) {
                    y.getNeighborNodeIterator().forEachRemaining(w -> {
                        try {
                            if (x != w) g.addEdge(x.getId() + "--" + w.getId(), x, w);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    Set<Node> nos = null;
                    if (c) nos = GroupManager.removeAll(y, x);
                    int n = x.getAttribute("n");
                    int n2 = y.getAttribute("n");
                    n+=n2;
                    x.setAttribute("n", n);
                    double size = 4+ Math.sqrt(n);
                    x.addAttribute("ui.style", "size:"+size+"px;");
                    x.setAttribute("ui.label", x.getId()+" : "+x.getAttribute("n"));
                    i--;

                    g.removeNode(y);
                    if (c) {
                        nos.remove(y);
                        nos.forEach(GroupManager::putGroup);
                    }
                }
            }
        }
    }


    static Node findPartnerWithN(Node p, Set<Node> nebos){
        int highest = 0;
        Node bestPartner = null;

        for (Node q : nebos) {
            if (p.getAttribute("ui.class").equals(q.getAttribute("ui.class")))
                continue;
            int dpp = getDp(p, p);
            int dqq = getDp(q, q);
            int oldE = dpp+dqq;
            int dpq = getDp(p, q)-1;
            int dqp = getDp(q, p)-1;
            int newE = dpq+dqp;

            if (newE>oldE && newE>highest) {
                bestPartner = q;
                highest = newE;

//                System.out.println(p.getId()+" => oldE("+oldE+") = dpp("+dpp+")+dqq("+dqq+")");
//                System.out.println(q.getId()+" => newE("+newE+") = dpq("+dpq+")+dqp("+dqp+")");
            }
        }
//        if (Objects.nonNull(bestPartner))System.out.println("bestPartner:"+bestPartner.getId()+" with:"+highest);

        return bestPartner;
    }

    static int getDp(Node p, Node c){
        int res = 0;
        Set<Node> resNodes = new HashSet<>();
        Stream<Node> s = p.getEdgeSet().stream().map(e->e.getOpposite(p));
        for (Node node : s.collect(Collectors.toSet())){
            String color = node.getAttribute("ui.class");
            if (color.equals(c.getAttribute("ui.class")) && resNodes.add(node)) {
                res+=(int)node.getAttribute("n");
            }
        }
        return res;
    }
}
