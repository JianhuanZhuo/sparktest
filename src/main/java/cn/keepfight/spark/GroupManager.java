package cn.keepfight.spark;

import org.graphstream.graph.Node;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by tom on 17-4-12.
 */
public class GroupManager {

    // color string =>
    // nodeNum int =>
    // status string => List<Node>
    static Map<String, Map<Integer, Map<String, List<Node>>>> statusGroup = new HashMap<>();

    static {
        for (int i=0; i<PB3.threadNum; i++){
            statusGroup.put("marke"+i, new HashMap<>());
        }
    }

    static Set<Node> removeAll(Node node1, Node node2){
        Set<Node> nebos = new HashSet<>();

        Iterator<Node> it = node1.getNeighborNodeIterator();
        while (it.hasNext()) nebos.add(it.next());
        it = node2.getNeighborNodeIterator();
        while (it.hasNext()) nebos.add(it.next());

        nebos.add(node1);
        nebos.add(node2);

        nebos.forEach(n->{
            try {
                List<Node> list = n.getAttribute("list");
                list.remove(n);
                n.removeAttribute("list");
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        return nebos;
    }


    static String getStatus(Node node){
        Iterator<Node> it = node.getNeighborNodeIterator();
        // get status
        Map<String, Integer> statusMap = new HashMap<>();
        while (it.hasNext()){
            Node nebo = it.next();
            int n = nebo.getAttribute("n");
            String x = nebo.getAttribute("ui.class");
            Integer oldN =statusMap.get(x);
            if (oldN!=null){
                n+=oldN;
            }
            statusMap.put(x, n);
        }
        return statusMap.keySet().stream().map(k->k+statusMap.get(k)).collect(Collectors.joining(";"));
    }

    static Set<Node> getSamples(Node p){
        Set<Node> sample = new HashSet<>();
        statusGroup.forEach((k, v)->{
            if (!k.equals(p.getAttribute("ui.class"))){
                Integer n = p.getAttribute("n");
                Set<Node> s = sampleNum(v, n);
                if (s!=null)
                    sample.addAll(s);
            }
        });
        return  sample;
    }

    static Set<Node> sampleNum(Map<Integer, Map<String, List<Node>>> map3, Integer i){
        return map3==null?null:sampleStatus(map3.get(i));
    }

    static Set<Node> sampleStatus(Map<String, List<Node>> map4){
        try {
            return map4==null?null:map4.values()
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(l->!l.isEmpty())
                    .map(l->l.get(0))
                    .collect(Collectors.toSet());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    static void putGroup(Node node){
        String t = node.getAttribute("ui.class");
        Map<Integer, Map<String, List<Node>>> numMapStatus = statusGroup.get(t);

        numMapStatus.compute(node.getAttribute("n"),(statusK, statusV)->{
            if (statusV==null) statusV = new HashMap<>();
            statusV.compute(getStatus(node), (listK, listV)->{
                if(listV==null) listV = new ArrayList<>();
                listV.add(node);
                node.setAttribute("list", listV);
                return listV;
            });
            return statusV;
        });
    }

    static void print(){
        statusGroup.forEach((k,v)->System.out.println(k+" -> "+v.size()));
    }
}
