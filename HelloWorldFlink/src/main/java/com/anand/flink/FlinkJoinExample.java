package com.anand.flink;

import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;


/*
   command to run this program
   ./bin/flink run -c com.anand.flink.FlinkJoinExample /Users/nithiyanandhan/IdeaProjects/flink/HelloWorldFlink/target/HelloWorldFlink-1.0-SNAPSHOT.jar --input1 /Users/nithiyanandhan/IdeaProjects/flink/HelloWorldFlink/src/main/resources/join1 --input2 /Users/nithiyanandhan/IdeaProjects/flink/HelloWorldFlink/src/main/resources/join2 --output /Users/nithiyanandhan/IdeaProjects/flink/HelloWorldFlink/target/outputjoin
 */


public class FlinkJoinExample {

    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        ParameterTool params = ParameterTool.fromArgs(args);
        env.getConfig().setGlobalJobParameters(params);

        DataSet<String> input1 = env.readTextFile(params.get("input1"));
        DataSet<String> input2 = env.readTextFile(params.get("input2"));

        DataSet<Tuple2<Integer,String>> map1 = input1.map(s -> {
            String[] tmp = s.split(",");
            return new Tuple2<Integer,String>(Integer.parseInt(tmp[0].toString()),tmp[1].toString());
        });

        DataSet<Tuple2<Integer,String>> map2 = input2.map(s -> {
            String[] tmp = s.split(",");
            return new Tuple2<Integer,String>(Integer.parseInt(tmp[0].toString()),tmp[1].toString());
        });

        DataSet<Tuple3<Integer,String,String>> output =
                map1.join(map2).where(0).equalTo(0)
                        .with(new JoinFunction<Tuple2<Integer, String>, Tuple2<Integer, String>,Tuple3<Integer,String,String>>(){
                            public Tuple3<Integer,String,String> join(Tuple2<Integer,String> map1,Tuple2<Integer,String> map2){
                                return new Tuple3<Integer,String,String>(Integer.parseInt(map1.f0.toString()),map1.f1.toString(), map2.f1.toString());
                            }
                        });
        output.writeAsCsv(params.get("output"),"\n"," ");
        env.execute("join example");
    }
}
