
package cn.keepfight.spark;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.Graphs;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.Replayable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PBA2 {

	static final long seed = 100;

	static Set<Node> cutNode = ConcurrentHashMap.newKeySet();
	static String lock2 = "xx";

	static int x;
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
		int n = 1000;
		int threadNum = 5;
		x = threadNum;
		for (int i = 0; i < threadNum; i++) {
			hostList.add(new ArrayList<>());
		}

		String[] color = { "red", "yellow", "green", "blue", "black", "pink", "cyan1" };
		String style = "node{size:4px;fill-color:red;}" + "edge{size: 0px;}";
        for (int i = 0; i < color.length; i++) {
//			style += "node.marke" + i + "{size:4px;fill-color:" + color[i] + ";}";
            style += "node.marke" + i + "{fill-color:" + color[i] + ";}";
        }
        style += "edge.ok{size: 10px;fill-color: #92F;}";
        style += "edge.error{size: 10px;fill-color: blue;}";
        graph.addAttribute("ui.stylesheet", style);


        generateSF(threadNum, n, d, graph);


//		new Scanner(System.in).next();

		System.out.println("waken!");

		// display the graph
		graph.display();
		x = threadNum;
//		for (int j = 0; j < threadNum; j++ ) {
//			int block = n / (threadNum - (n % threadNum == 0 ? 0 : 1));
//			final int start = j * block;
//			final int end = Math.min((j + 1) * block, n);
//			new Thread(()->{

				for (int i = 0; i < 100; i++) {
					int count = 0;
					for (int k= 0; k< graph.getNodeCount() ; k++) {
						Node p = graph.getNode(k);
						count += sampleAndSwap(p, graph);
					}
					if (count < n * 0.01) {
						break;
					}
				}
//				synchronized (lock2) {
//					if (--x==0) {
//						lock2.notify();
//					}
//				}
				System.out.println(Thread.currentThread().getName()+" finish!");
//			}).start();
//		}
//		synchronized (lock2) {
//			lock2.wait(10000);
//			for(ThreadInfo info :ManagementFactory.getThreadMXBean().dumpAllThreads(true, true)){
//				for (MonitorInfo mi : info.getLockedMonitors()){
//					System.out.println(info.getLockName()+" -- "+info.getThreadId()+" >> "+mi.getClassName());
//				}
//			}
//			lock2.wait();
//		}

		System.out.println("sort ok!");

		graph.forEach(p->
			p.getNeighborNodeIterator().forEachRemaining(nebo->{
				if (!p.getAttribute("ui.class").equals(nebo.getAttribute("ui.class"))){
					cutNode.add(p);
				}
			})
		);

		System.out.println("sort ok2!");
//		for (int i=0; i<30; i++)
//		for (Node node : cutNode) {
//			swapCut(node, cutNode);
//		}
//		System.out.println("sort ok3!");


//		for (int i = 0; i < 20; i++){
//			for (Node p : graph){
//				sampleAndSwaps(p, graph);
//			}
//		}
//		System.out.println("swap over");

		Replayable.Controller controller = internalGraph.getReplayController();

//		SingleGraph g3 = new SingleGraph("miaomiao");
//		controller.addSink(g3);
//		controller.replay();



		SingleGraph g3 = internalGraph;

        compact(g3);

//		for (int j=0; j<g3.getNodeCount(); j++){
//			g3.getNode(j);
//		}
		System.out.println("sort ok8!");

//		Iterator<Node> iterator = x.getNeighborNodeIterator();
//		while (iterator.hasNext()){
//			Node nebo = iterator.next();
//			if (nebo.getAttribute("ui.class").equals(x.getAttribute("ui.class"))){
//				nebo.getNeighborNodeIterator().forEachRemaining(w->{
//					try {
//						if (x!=w) g3.addEdge(x.getId()+"--"+w.getId(), x, w);
//					}catch (Exception e){
//						e.printStackTrace();
//					}
//				});
//				g3.removeNode(nebo);
//			}
//		}
	}


	static void compact2(Graph g){
	    for (int j=0; j<g.getNodeCount(); j++) {

        }
    }


	static void compact(Graph g){

		for (int j=0; j<g.getNodeCount(); j++) {
			Node x = g.getNode(j);
			x.addAttribute("uix.label", 10);

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
					g.removeNode(y);
					System.out.println("remove " + y + " : " + y.getAttribute("ui.class"));
					x.setAttribute("uix.label", x.getNumber("uix.label") + 1);
                    x.setAttribute("ui.label", ""+x.getNumber("uix.label"));
					x.addAttribute("ui.style", "size:"+Math.sqrt(x.getNumber("uix.label"))+"px;");
					i--;
				}
			}

		}
	}

//	static void swapCut(Node p, Set<Node> nodes){
//		Node partner;
//		partner = findPartner(p, p.getNeighborNodeIterator(), 1);
//		if (partner==null) {
//			partner = findPartner(p, nodes.iterator(), 1);
//		}
//
//		if (partner!=null){
//			synchronized (nodes) {
//				String c1 = p.getAttribute("ui.class");
//				String c2 = partner.getAttribute("ui.class");
//				//xx
//				p.setAttribute("ui.class", c2);
//				partner.setAttribute("ui.class", c1);
//	 				System.out.println("swap:"+p.getId()+" <==> "+partner.getId());
//			}
//		}
//	}
//
//	static void swapCuts(Node p, Set<Node> nodes){
//		Node partner;
//		partner = findPartner(p, p.getNeighborNodeIterator(), 1);
//		if (partner==null) {
//			partner = findPartner(p, nodes.iterator(), 1);
//		}
//
//		if (partner!=null){
//			synchronized (nodes) {
//				String c1 = p.getAttribute("ui.class");
//				String c2 = partner.getAttribute("ui.class");
//				//xx
//				p.setAttribute("ui.class", c2);
//				partner.setAttribute("ui.class", c1);
//				System.out.println("swap:"+p.getId()+" <==> "+partner.getId());
//			}
//		}
//	}



	static int sampleAndSwap(Node p, Graph graph){

		int res = 0;

		double tr = p.getAttribute("t");
		double delta = 0.03;
		Node partner;
//		System.out.println("lock!");
		Stream<Node> s = p.getEdgeSet().stream().map(e->e.getOpposite(p));
		Set<Node> iterator = s.collect(Collectors.toSet());

		partner = findPartner(p, iterator, tr);
		if (partner==null) {
			partner = findPartner(p, new HashSet<>(Toolkit.randomNodeSet(graph, (int)(graph.getNodeCount()*0.01), new Random())), tr);
		}

		// 执行交换
		if (partner!=null) {
			// 简单交换
			synchronized (graph) {
				String c1 = p.getAttribute("ui.class");
				String c2 = partner.getAttribute("ui.class");
				p.setAttribute("ui.class", c2);
				partner.setAttribute("ui.class", c1);
	 				System.out.println("swap in "+Thread.currentThread().getName()+":"+p.getId()+" <==> "+partner.getId());
			}
			res = 1;
		}else {
//				System.out.println("no swap!");
		}

		p.setAttribute("t", (tr - delta)<1?1:(tr - delta));
		return res;
	}

	static  void sampleAndSwaps(Node p, Graph graph){

		Node[] partner;
		partner = findParters(p);
//		if (partner==null) {
//			partner = findPartner(p, Toolkit.randomNodeSet(graph, 10, new Random(System.currentTimeMillis())).iterator(), tr);
//		}

		// 执行交换
		if (partner[0]!=null) {
			// 简单交换
			synchronized (graph) {
				String c1 = p.getAttribute("ui.class");
				String c2 = partner[1].getAttribute("ui.class");
				p.setAttribute("ui.class", c2);
				partner[0].setAttribute("ui.class", c2);
				partner[1].setAttribute("ui.class", c1);
				partner[2].setAttribute("ui.class", c1);
//	 				System.out.println("swap:"+p.getId()+" <==> "+partner.getId());
			}
		}else {
//				System.out.println("no swap!");
		}

	}

//	static void sampleAndSwap_G(Graph graph) throws InterruptedException{
//		double t0 = 3;
//		double delta = 0.001;
//		//开始划图
//		for (Node p : graph) {
//			double tr = t0;
//			Node partner;
//			partner = findPartner(p, p.getNeighborNodeIterator(), tr);
//			if (partner==null) {
//				partner = findPartner(p, Toolkit.randomNodeSet(graph, 10, new Random(System.currentTimeMillis())).iterator(), tr);
//			}
//
//			// 执行交换
//			if (partner!=null) {
//				// 简单交换
//				synchronized (graph) {
//					String c1 = p.getAttribute("ui.class");
//					String c2 = partner.getAttribute("ui.class");
//					p.setAttribute("ui.class", c2);
//					partner.setAttribute("ui.class", c1);
////					System.out.println("swap:"+p.getId()+" <==> "+partner.getId());
//				}
//			}else {
////				System.out.println("no swap!");
//			}
//
//			tr = tr - delta;
//			if (tr<1) {
//				tr = 1;
//			}
//
////			Thread.sleep(10);
//		}
//	}


	static Node findPartner(Node p, Set<Node> nebos, double tr){
		int highest = 0;
		Node bestPartner = null;

		for (Node q : nebos) {
			if (p.getAttribute("ui.class").equals(q.getAttribute("ui.class")))
				continue;
			int dpp = getDp(p, p);
			int dqq = getDp(q, q);
			int oldE = dpp+dqq;
			int dpq = getDp(p, q);
			int dqp = getDp(q, p);
			int newE = dpq+dqp;

			if ((newE*tr)>oldE && newE>highest) {
				bestPartner = q;
				highest = newE;
			}
		}

		return bestPartner;
	}

	static Node[] findParters(Node p){
		int highest = 0;
		Node[] bestPartner = new Node[3];
		Iterator<Node> iterator = p.getNeighborNodeIterator();
		while(iterator.hasNext()){
			Node p2 = iterator.next();
			if (!p.getAttribute("ui.class").equals(p2.getAttribute("ui.class"))){
				continue;
			}

			Iterator<Node> target = p2.getNeighborNodeIterator();
			while (target.hasNext()){
				Node t = target.next();
				if (t==p || p.getAttribute("ui.class").equals(t.getAttribute("ui.class"))) continue;

				Iterator<Node> target2 = t.getNeighborNodeIterator();
				while (target2.hasNext()){
					Node t2 = target2.next();

					if (t2==p || t2==p2 || !t2.getAttribute("ui.class").equals(t.getAttribute("ui.class"))) continue;


					int dpp = getDp(p, p);
					int dpp2 = getDp(p2, p2);
					int dtt = getDp(t, t);
					int dtt2 = getDp(t2, t);
					int oldE = dpp+dpp2+dtt+dtt2;

					int dpt = getDp(p, t);
					int dpt2 = getDp(p2, t);
					int dtp = getDp(t, p);
					int dtp2 = getDp(t2, p);
					int newE = dpt+dpt2+dtp+dtp2;

					if (newE>oldE && newE>highest) {
						bestPartner[0] = p2;
						bestPartner[1] = t;
						bestPartner[2] = t2;
						highest = newE;
					}
				}

			}
		}

		return bestPartner;
	}

	// return the number of p's neighbors that have color c
	static Set<Node> getNp(Node p, Node c){
		Set<Node> resNodes = new HashSet<>();
		Stream<Node> s = p.getEdgeSet().stream().map(e->e.getOpposite(p));
		for (Node node : s.collect(Collectors.toSet())){
			String color = node.getAttribute("ui.class");
			try {
				if (color.equals(c.getAttribute("ui.class"))) {
					resNodes.add(node);
					p.getEdgeBetween(node).addAttribute("ui.class", "ok");
				}else {
					p.getEdgeBetween(node).addAttribute("ui.class", "error");
				}
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}
		return resNodes;
	}

	static int getDp(Node p, Node c){
		return getNp(p, c).size();
	}

	static void generateSF(int threadNum, int n, int d, Graph graph) throws InterruptedException {
        final Integer[]  x = new Integer[1];
        x[0] = threadNum;
        for (int t = 0; t < threadNum; t++) {
            int block = n / (threadNum - (n % threadNum == 0 ? 0 : 1));
            final int start = t * block;
            final int end = Math.min((t + 1) * block, n);
            final String markLabel = "marke" + t;
            System.out.println("job" + t + ": " + start + " => " + end);
            final int ft = t;
            new Thread(() -> {
                for (int i = start; i < end; i++) {
                    int s = i / d;
                    int e = generateEdge(i) / (2 * d);
                    Node node = graph.addNode("A" + s);

                    int k = i%(threadNum*2);
                    if (k>=threadNum)  k =(threadNum*2)-k-1;

                    node.setAttribute("ui.class", "marke"+k);
                    node.addAttribute("t", new Double(2));
//                    hostList.get(ft).add(node);
                    graph.addEdge("E" + i, "A" + s, "A" + e);
                    graph.getNode(0).getNeighborNodeIterator();
                }
                synchronized (graph) {
                    if (--x[0]==0) {
                        graph.notify();
                    }
                    System.out.println(Thread.currentThread().getName()+" finish!");
                }
            }).start();
        }

        synchronized (graph) {
            graph.wait();
        }
    }


	public static int generateEdge(int i) {
		i = 2 * i + 1;
		while (i % 2 != 0)
			i = new Random(i + seed).nextInt(i);
		return i;
	}
}
