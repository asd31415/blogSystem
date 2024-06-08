package com.myblog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.myblog.mapper.BlogMapper;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.expressions.Window;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import scala.Function1;

import javax.xml.crypto.Data;

import static org.apache.spark.sql.functions.*;

@Service
public class DataAnalysisService {

    @Autowired
    private SparkSession sparkSession;

    @Autowired
    private  ObjectMapper objectMapper;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Autowired
    private BlogMapper blogMapper;

    // 统计某用户搜索的文章
    public ArrayNode getUserArticleViews(String username) {
        // 构建 SQL 查询，从日志表中选择指定用户的点击浏览文章的请求数据
        String query = "(SELECT id, url FROM your_log_table WHERE username = '" + username + "') AS user_article_views";

        // 从数据库中加载数据
        Dataset<Row> df = sparkSession.read()
                .format("jdbc")
                .option("url", jdbcUrl)
                .option("dbtable", query)
                .option("user", this.username)
                .option("password", this.password)
                .load();

        // 创建临时视图
        df.createOrReplaceTempView("user_article_views");

        // 使用 SQL 查询获取文章ID
        Dataset<Row> result = sparkSession.sql(
                "SELECT " +
                        "SUBSTRING_INDEX(url, '/', -1) AS article_id " + // 提取文章ID
                        "FROM user_article_views"
        );

        // 将结果转换为 JSON 格式
        ArrayNode arrayNode = objectMapper.createArrayNode();
        result.collectAsList().forEach(row -> {
            ObjectNode obj = objectMapper.createObjectNode();
            Integer articleId = Integer.parseInt(row.getString(0));
            String articleTitle = blogMapper.getBlogById(articleId).getTitle();
            obj.put("article_id", articleId);
            obj.put("article_title", articleTitle);
            arrayNode.add(obj);
        });

        return arrayNode;
    }


    // 统计所有的请求用时情况
    public ArrayNode getRequestDurationStatistics() {
        // 从日志表中加载数据
        Dataset<Row> df = sparkSession.read()
                .format("jdbc")
                .option("url", jdbcUrl)
                .option("dbtable", "your_log_table")
                .option("user", this.username)
                .option("password", this.password)
                .load();

        // 创建临时视图
        df.createOrReplaceTempView("user_requests");

        // 使用 SQL 查询统计每个请求的时长，并按降序排序
        Dataset<Row> result = sparkSession.sql(
                "SELECT " +
                        "requesturl, " + // 请求URL
                        "costtime " + // 请求时长
                        "FROM user_requests " +
                        "ORDER BY costtime DESC"
        );

        // 将结果转换为 JSON 格式
        ArrayNode arrayNode = objectMapper.createArrayNode();
        result.collectAsList().forEach(row -> {
            ObjectNode obj = objectMapper.createObjectNode();
            obj.put("requesturl", row.getString(0));
            obj.put("costtime", row.getString(1));
            arrayNode.add(obj);
        });

        return arrayNode;
    }


    // 统计某个用户行为
    public ArrayNode getUserActivityStatistics(String username) {
        // 构建 SQL 查询，从日志表中选择指定用户的行为数据
        String query = "(SELECT id, ip, requesturl, createtime FROM your_log_table WHERE username = '" + username + "') AS user_actions";

        // 从数据库中加载数据
        Dataset<Row> df = sparkSession.read()
                .format("jdbc")
                .option("url", jdbcUrl)
                .option("dbtable", query)
                .option("user", this.username)
                .option("password", this.password)
                .load();

        // 创建临时视图
        df.createOrReplaceTempView("user_actions");

        // 使用 SQL 查询统计用户行为
        Dataset<Row> result = sparkSession.sql(
                "SELECT " +
                        "HOUR(createtime) AS hour, " + // 统计用户行为的小时
                        "ip, " + // 用户的登录IP
                        "requesturl " + // 用户的请求URL
                        "FROM user_actions " +
                        "ORDER BY hour"
        );

        // 将结果转换为 JSON 格式
        ArrayNode arrayNode = objectMapper.createArrayNode();
        result.collectAsList().forEach(row -> {
            ObjectNode obj = objectMapper.createObjectNode();
            obj.put("hour", row.getInt(0));
            obj.put("ip", row.getString(1));
            obj.put("requesturl", row.getString(2));
            arrayNode.add(obj);
        });
        return arrayNode;
    }



    // 统计用户搜索过的关键字
    public ArrayNode  getCountOfSearch() {
        // 从日志表中读取搜索日志数据
        Dataset<Row> dataset = sparkSession.read()
                .format("jdbc")
                .option("url", jdbcUrl)
                .option("dbtable", "t_log") // 假设日志表名为 "t_log"
                .option("user", username)
                .option("password", password)
                .load();

        // 过滤出搜索日志，并解析搜索关键字
        Dataset<String> searchKeywords = dataset.select(dataset.col("requestparam")).as(Encoders.STRING());

        // 将搜索关键字解析为JSON对象，并提取关键字
        Dataset<String> keywords = searchKeywords.map(
                (MapFunction<String, String>) param -> {
            JsonNode jsonNode = objectMapper.readTree(param);
            return jsonNode.get("keyword").asText();
        }, Encoders.STRING());

        // 根据关键字统计搜索次数
        Dataset<Row> keywordCounts = keywords.groupBy("keyword")
                .agg(count("*").alias("searchCount"))
                .orderBy(col("searchCount").desc());

        // 转换结果为ArrayNode
        ArrayNode arrayNode = objectMapper.createArrayNode();
        keywordCounts.collectAsList().forEach(row -> {
            ObjectNode obj = objectMapper.createObjectNode();
            obj.put("keyword", row.getString(0));
            obj.put("searchCount", row.getLong(1));
            arrayNode.add(obj);
        });

        return arrayNode;
    }


    // 每月登录用户总数
    public ArrayNode getLoginCountByMonth() {
        // 从数据库中读取登录日志数据
        Dataset<Row> dataset = sparkSession.read()
                .format("jdbc")
                .option("url", jdbcUrl)
                .option("dbtable", "t_log") // 假设登录日志表名为 "t_log"
                .option("user", username)
                .option("password", password)
                .load();

        // 根据用户名统计登录次数
        Dataset<Row> loginUserCountOfUser = dataset.filter(dataset.col("logtype").equalTo("LOGIN"))
                .groupBy(dataset.col("username"))
                .agg(countDistinct("*").alias("loginCount"));

        // 转换结果为ArrayNode
        ArrayNode arrayNode = objectMapper.createArrayNode();
        loginUserCountOfUser.collectAsList().forEach(row -> {
            ObjectNode obj = objectMapper.createObjectNode();
            obj.put("username", row.getString(0));
            obj.put("loginCount", row.getLong(1));
            arrayNode.add(obj);
        });

        return arrayNode;
    }

    //每个小时登录用户总数
    public ArrayNode getLoginCountByHour() {
        String query = "(SELECT id, createtime, logtype FROM t_log WHERE logtype IN ('LOGIN', 'REGISTER')) AS logins";

        Dataset<Row> df = sparkSession.read()
                .format("jdbc")
                .option("url", jdbcUrl)
                .option("dbtable", query)
                .option("user", username)
                .option("password", password)
                .load();

        df.createOrReplaceTempView("logins");

        Dataset<Row> result = sparkSession.sql(
                "SELECT " +
                        "HOUR(createtime) AS hour, " +
                        "COUNT(*) AS login_count " +
                        "FROM logins " +
                        "GROUP BY HOUR(createtime) " +
                        "ORDER BY hour"
        );

        ArrayNode arrayNode = objectMapper.createArrayNode();
        result.collectAsList().forEach(row -> {
            ObjectNode obj = objectMapper.createObjectNode();
            obj.put("hour", row.getInt(0));
            obj.put("login_count", row.getLong(1));
            arrayNode.add(obj);
        });

        return arrayNode;
    }
}
