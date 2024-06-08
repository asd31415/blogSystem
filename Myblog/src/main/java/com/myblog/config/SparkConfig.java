package com.myblog.config;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig {

    @Bean
    public SparkSession sparkSession() {
        SparkConf sparkConf = new SparkConf()
                .setAppName("Spring Boot Spark App")
                .setMaster("local[4]");
                //.setMaster("spark://192.168.154.131:7077");


        return SparkSession.builder()
                .config(sparkConf)
                .getOrCreate();
    }

}
