package com.anand.flink;



import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;

import java.util.StringTokenizer;

/*
   command to run this program
   ./bin/flink run -c com.anand.flink.WordCount /Users/nithiyanandhan/IdeaProjects/flink/HelloWorldFlink/target/HelloWorldFlink-1.0-SNAPSHOT.jar --input /Users/nithiyanandhan/IdeaProjects/flink/HelloWorldFlink/src/main/resources/wordcount --output /Users/nithiyanandhan/IdeaProjects/flink/HelloWorldFlink/target/output
 */

public class WordCount {
    public static void main(String[] args) throws Exception{
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        ParameterTool params = ParameterTool.fromArgs(args);
        env.getConfig().setGlobalJobParameters(params);

        //Read the file line by line.
        //dataset will be of string type
        DataSet<String> text =env.readTextFile(params.get("input"));


        //DataSet<String> filtered = text.filter(new FilterFunction<String>() {
        //   public boolean filter(String value){
        //       return value.startsWith("N");
        //   }
        //});
        //above function replaced by lambda expression
        DataSet<String> filtered = text.filter(s -> s.startsWith("N"));


        //DataSet<Tuple2<String,Integer>> tokenized = filtered.map(new Tokenizer());
        //above statement replaced by lambda expression.
        DataSet<Tuple2<String,Integer>> tokenized = filtered.map(value -> new Tuple2(value,Integer.valueOf(1)));


        DataSet<Tuple2<String,Integer>> counts = tokenized.groupBy(0).sum(1);

        if(params.has("output")){
          counts.writeAsCsv(params.get("output"),"\n"," ");
          env.execute("workcount example");
        }

    }

    public static final class Tokenizer implements MapFunction<String, Tuple2<String,Integer>>{
        public Tuple2<String,Integer> map(String value){
            return new Tuple2(value,Integer.valueOf(1));
        }
    }
}


