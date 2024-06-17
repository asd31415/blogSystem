package com.myblog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.myblog.mapper.BlogMapper;
import org.apache.commons.text.similarity.CosineSimilarity;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.ml.feature.*;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.*;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.expressions.Window;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import scala.Function1;
import scala.collection.mutable.WrappedArray;

import javax.annotation.PostConstruct;
import javax.xml.crypto.Data;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.spark.sql.functions.*;

@Service
public class DataAnalysisService  implements Serializable {

    private static final long serialVersionUID = 1L;

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

    private static Map<Integer, Map<Integer, Double>> similarityMap;

    public List<Integer> getRecommandList(String username){

        ArrayNode readList = this.getUserArticleViews(username);

        Map<Integer, Double> mergedMap = new HashMap<>();
        for(JsonNode node : readList){
            Integer blogId = node.get("article_id").asInt();
            Integer readCnt = node.get("count").asInt();

            for(Map.Entry<Integer, Double> entry : similarityMap.get(blogId).entrySet()){
                int key = entry.getKey();
                double value = entry.getValue();
                // 将对应键的值累加,阅读次数就是权重
                mergedMap.merge(key, value*readCnt, Double::sum);
            }
        }
        // 根据权值排序，得到推荐的博客id列表
        List<Integer> sortedKeys = mergedMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return sortedKeys;
    }


    // 统计某用户搜索的文章
    public ArrayNode getUserArticleViews(String name) {
        // 构建 SQL 查询，从日志表中选择指定用户的点击浏览文章的请求数据
        String query = "(SELECT requesturl,username FROM t_log) AS user_article_views";

        // 从数据库中加载数据
        Dataset<Row> df = sparkSession.read()
                .format("jdbc")
                .option("url", jdbcUrl)
                .option("dbtable", query)
                .option("user", username)
                .option("password", password)
                .load();

        // 创建临时视图
        df.createOrReplaceTempView("user_article_views");

        // 使用 SQL 查询获取文章ID，并统计每个文章ID出现的次数
        Dataset<Row> result = sparkSession.sql(
                "SELECT " +
                        "COALESCE(" +
                        "    REGEXP_EXTRACT(requesturl, '/article/like/([0-9]+)', 1), " +
                        "    REGEXP_EXTRACT(requesturl, '/article/([0-9]+)\\.html', 1) " +
                        ") AS article_id, " +
                        "COUNT(*) AS view_count " +
                        "FROM user_article_views " +
                        "WHERE (requesturl LIKE '/article/like/%' OR requesturl LIKE '/article/%.html') " +
                        "AND `username` = '" + name + "' " +
                        "GROUP BY article_id " +
                        "ORDER BY view_count DESC"
        );

        // 将结果转换为 JSON 格式
        ArrayNode arrayNode = objectMapper.createArrayNode();
        result.collectAsList().forEach(row -> {
            String articleIdStr = row.getString(0);
            if (articleIdStr != null && !articleIdStr.isEmpty()) {
                try {
                    Integer articleId = Integer.parseInt(articleIdStr);
                    Long viewCount = row.getLong(1);
                    ObjectNode obj = objectMapper.createObjectNode();
                    obj.put("article_id", articleId);
                    obj.put("count", viewCount);
                    arrayNode.add(obj);
                } catch (NumberFormatException e) {
                    // 处理文章ID解析错误的情况
                    System.err.println("Invalid article ID: " + articleIdStr);
                }
            }
        });

        return arrayNode;
    }

    // 统计所有的请求用时情况
    public ArrayNode getRequestDurationStatistics() {
        // 从日志表中加载数据
        Dataset<Row> df = sparkSession.read()
                .format("jdbc")
                .option("url", jdbcUrl)
                .option("dbtable", "t_log")
                .option("user", username)
                .option("password", password)
                .load();

        // 创建临时视图
        df.createOrReplaceTempView("user_requests");

        // 使用 SQL 查询统计每个请求的时长，并按降序排序
        Dataset<Row> result = sparkSession.sql(
                "SELECT requesturl, costtime FROM (" +
                        "  SELECT requesturl, costtime, " +
                        "    ROW_NUMBER() OVER (PARTITION BY requesturl ORDER BY costtime DESC) as row_num " +
                        "  FROM user_requests" +
                        ") tmp WHERE row_num = 1 " +
                        "ORDER BY costtime DESC"
        );

        // 将结果转换为 JSON 格式
        ArrayNode arrayNode = objectMapper.createArrayNode();
        result.collectAsList().forEach(row -> {
            ObjectNode obj = objectMapper.createObjectNode();
            obj.put("requesturl", row.getString(0));
            obj.put("count", row.getString(1));
            arrayNode.add(obj);
        });

        return arrayNode;
    }


    // 统计某个用户行为
    public ArrayNode getUserActivityStatistics(String name) {
        // 构建 SQL 查询，从日志表中选择指定用户的行为数据
        String query = "(SELECT id, ip, ipinfo, requesturl, createtime FROM t_log WHERE `username` = '" + name + "') AS user_actions";

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
                        "createtime, " + // 统计用户行为的小时
                        "ip, " + // 用户的登录IP
                        "ipinfo, " +
                        "requesturl " + // 用户的请求URL
                        "FROM user_actions "
        );

        // 将结果转换为 JSON 格式
        ArrayNode arrayNode = objectMapper.createArrayNode();
        result.collectAsList().forEach(row -> {
            ObjectNode obj = objectMapper.createObjectNode();
            obj.put("createtime", row.getTimestamp(0).toString());
            obj.put("ip", row.getString(1));
            obj.put("ipinfo", row.getString(2));
            obj.put("requesturl", row.getString(3));
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
        ObjectMapper localObjectMapper = this.objectMapper; // 局部变量
        Dataset<String> keywords = searchKeywords.flatMap(
                (FlatMapFunction<String, String>) param -> {
                    JsonNode jsonNode = localObjectMapper.readTree(param);
                    JsonNode keywordNode = jsonNode.get("keyword");
                    if (keywordNode != null && !keywordNode.asText().isEmpty()) {
                        return Arrays.asList(keywordNode.asText()).iterator();
                    } else {
                        return Collections.<String>emptyList().iterator();
                    }
                }, Encoders.STRING());

        // 根据关键字统计搜索次数
        Dataset<Row> keywordCounts = keywords.groupBy("value")
                .agg(count("*").alias("searchCount"))
                .orderBy(col("searchCount").desc());

        // 转换结果为ArrayNode
        ArrayNode arrayNode = objectMapper.createArrayNode();
        keywordCounts.collectAsList().forEach(row -> {
            ObjectNode obj = objectMapper.createObjectNode();
            obj.put("keyword", row.getString(0));
            obj.put("count", row.getLong(1));
            arrayNode.add(obj);
        });

        return arrayNode;
    }


    // 每月/每天/每小时的登录用户总数
    public ArrayNode getLoginCount(String mode) {
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
                "SELECT " + mode +
                        "(createtime) AS "+ mode + ", " +
                        "COUNT(*) AS login_count " +
                        "FROM logins " +
                        "GROUP BY "+ mode + "(createtime) " +
                        "ORDER BY " + mode
        );

        ArrayNode arrayNode = objectMapper.createArrayNode();
        result.collectAsList().forEach(row -> {
            ObjectNode obj = objectMapper.createObjectNode();
            obj.put(mode, row.getInt(0));
            obj.put("count", row.getLong(1));
            arrayNode.add(obj);
        });

        return arrayNode;
    }

    // 加载中文停用词表
    private static String[] loadChineseStopWords() {
        String[] stopWords = {
                "的", "地", "得", "是", "在", "和", "了", "有", "个", "也",
                "这", "那", "就", "与", "及", "不", "无", "要", "为", "以",
                "对", "与", "中", "等", "时", "到", "与", "由", "于", "对于",
                "可以", "能", "但", "并", "之", "之间", "与", "个", "有", "下",
                "并且", "但是", "而且", "或者", "所以", "因此", "虽然", "然而", "因为", "即使",
                "例如", "因为", "然后", "还是", "这样", "一些", "一定", "一般", "什么", "仍然",
                "继续", "不同", "可能", "需要", "我们", "你们", "他们", "自己", "这些", "那些"
        };
        return stopWords;
    }

    // 计算所有博客之间的相似度，提前执行,在更新文章/修改文章/删除文章时异步执行
    @PostConstruct
    @Async
    public void reFlushSimilarity(){
        // 从数据库中加载数据
        Dataset<Row> df = sparkSession.read()
                .format("jdbc")
                .option("url", jdbcUrl)
                .option("query", "select id,content from t_blog")
                .option("user", this.username)
                .option("password", this.password)
                .load();

        // 分词
        Tokenizer tokenizer = new Tokenizer().setInputCol("content").setOutputCol("words");
        Dataset<Row> wordsData = tokenizer.transform(df);

        // 去除停用词
        StopWordsRemover remover = new StopWordsRemover().setInputCol("words").setOutputCol("filteredWords").setStopWords(loadChineseStopWords());;
        Dataset<Row> filteredWordsData = remover.transform(wordsData);

        // 计算每个词的词频
        CountVectorizerModel cvModel = new CountVectorizer()
                .setInputCol("filteredWords")
                .setOutputCol("features")
                .fit(filteredWordsData);

        // 将词频向量进行 TF-IDF 转换
        Dataset<Row> featurizedData = cvModel.transform(filteredWordsData);
        IDF idf = new IDF().setInputCol("features").setOutputCol("tfidfFeatures");
        IDFModel idfModel = idf.fit(featurizedData);
        Dataset<Row> tfidfData = idfModel.transform(featurizedData);

        // 特征向量
        Dataset<Row> result = tfidfData.select("id", "tfidfFeatures");

        // 对features列进行规范化
        Normalizer normalizer = new Normalizer().setInputCol("tfidfFeatures").setOutputCol("normalizedFeatures").setP(2.0);
        Dataset<Row> normalizedData = normalizer.transform(result);

        // 获取规范化后的数据
        List<Row> rows = normalizedData.collectAsList();
        int numRows = rows.size();
        Map<Integer, Map<Integer, Double>> TmpsimilarityMap = new HashMap<>();

        // 遍历每个博客ID
        for (int i = 0; i < numRows; i++) {
            int id1 = (int) rows.get(i).getLong(0); // 获取博客ID
            Vector tfidfFeatures1 = (Vector) rows.get(i).get(1); // 获取规范化后的TF-IDF特征向量
            Map<Integer, Double> idSimilarityMap = new HashMap<>();

            // 遍历剩余的博客ID
            for (int j = i + 1; j < numRows; j++) {
                int id2 = (int)rows.get(j).getLong(0); // 获取另一个博客ID
                Vector tfidfFeatures2 = (Vector) rows.get(j).get(1); // 获取另一个博客的规范化的TF-IDF特征向量

                // 计算余弦相似度

                double dotProduct = tfidfFeatures1.dot(tfidfFeatures2);
                // 计算向量模
                double normV1 = Vectors.norm(tfidfFeatures1, 2.0);
                double normV2 = Vectors.norm(tfidfFeatures2, 2.0);

                double similarity = dotProduct / (normV1 * normV2);

                // 将相似度存储到映射表中
                idSimilarityMap.put(id2, similarity);
            }
            // 将博客ID和相似度映射表存储到最终的相似度映射表中
            TmpsimilarityMap.put(id1, idSimilarityMap);
        }
        // 返回最终的相似度映射表
        this.similarityMap = TmpsimilarityMap;
    }
}
