package cn.keepfight.spark;

import cn.keepfight.spark.Scala.SC;
import cn.keepfight.spark.Scala.SCTest;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.graphx.Edge;
import scala.Array;
import scala.Int;

import java.util.List;

/**
 * Created by tom on 17-4-18.
 */
public class SparkTest {
    public static void main(String[] args){
//        SparkConf conf = new SparkConf();
//        conf.setAppName("testx  xxx");
////        conf.setMaster("spark://10.10.6.30:7077");
//        conf.setMaster("local[1]");
//        JavaSparkContext sc = new JavaSparkContext(conf);
//        sc.addJar("/home/tom/spark-test-1.0-SNAPSHOT.jar");
//        JavaRDD<String> rdd = sc.textFile("/home/tom/share/goods_sale_row_44.csv");
//        System.out.println(rdd.count());
//        new SCTest().test();
//        new SCTest().generate();
        SC.x();
//        SC.haha();
//        List<Long> s = SC.x();
//        String s= SC.sss();
//        System.out.println("s"+s);

    }
}
