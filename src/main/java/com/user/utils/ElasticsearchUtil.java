package com.user.utils;

import com.user.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class ElasticsearchUtil {
        private static final Logger logger = LoggerFactory.getLogger(ElasticsearchUtil.class);

        @Autowired
        @Qualifier(value = "myTransportClient")
        private TransportClient myTransportClient;

        @Autowired
        private BulkProcessor bulkProcessor;

        private static TransportClient client;

        private static BulkProcessor bulk;

        /**
         * @PostContruct是spring框架的注解 spring容器初始化的时候执行该方法
         */
        @PostConstruct
        public void init() {
            client = this.myTransportClient;
        }

        /**
         * @PostContruct是spring框架的注解 spring容器初始化的时候执行该方法
         */
        @PostConstruct
        public void initBulk() {
            bulk = this.bulkProcessor;
        }

        /**
         * 创建索引以及映射mapping，并给索引某些字段指定iK分词，以后向该索引中查询时，就会用ik分词。
         * @param: [indexName, esTpye]
         * @return: boolean
         * @auther: LHL
         * @date: 2018/10/15 17:04
         */
        public static boolean createIndex(String indexName, String esTpye) {
            if (!isIndexExist(indexName)) {
                logger.info("Index is not exits!");
            }
            //创建映射
            XContentBuilder mapping = null;
            try {
                mapping = XContentFactory.jsonBuilder()
                        .startObject()
                        .startObject("properties") //      .startObject("m_id").field("type","keyword").endObject()  //title:字段名，  type:文本类型       analyzer ：分词器类型
                        .startObject("userId").field("type", "text").endObject()   //该字段添加的内容，查询时将会使用ik_smart分词
                        .startObject("userName").field("type", "text").field("analyzer", "standard").endObject()  //ik_smart  ik_max_word  standard
                        .startObject("userEmail").field("type", "text").field("analyzer", "standard").endObject()
                        .startObject("userPhone").field("type", "float").endObject()
                        .startObject("userPassword").field("type", "date").endObject()
                        .startObject("userRole").field("type", "date").endObject()
                        .startObject("userQuestion").field("type", "date").field("analyzer", "standard").endObject()
                        .startObject("userAnswer").field("type", "date").field("analyzer", "standard").endObject()
                        .startObject("createTime").field("type", "date").endObject()
                        .startObject("updateTime").field("type", "date").endObject()
                        .endObject()
                        .endObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //index：索引名   type：类型名
            PutMappingRequest putmap = Requests.putMappingRequest(indexName).type(esTpye).source(mapping);
            //创建索引
            client.admin().indices().prepareCreate(indexName).execute().actionGet();
            //为索引添加映射
            AcknowledgedResponse acknowledgedResponse = client.admin().indices().putMapping(putmap).actionGet();
            logger.info("执行建立成功？" + acknowledgedResponse.isAcknowledged());
            return acknowledgedResponse.isAcknowledged();
        }

        /**
         * 创建索引
         * @param index
         * @return
         */
        public static boolean createIndex(String index) {
            if (!isIndexExist(index)) {
                logger.info("Index is not exits!");
            }
            CreateIndexResponse indexResponse = client.admin().indices().prepareCreate(index).execute().actionGet();
            logger.info("执行建立成功？" + indexResponse.isAcknowledged());
            return indexResponse.isAcknowledged();
        }

        /**
         * 删除索引
         * @param index
         * @return
         */
        public static boolean deleteIndex(String index) {
            if (!isIndexExist(index)) {
                logger.info("Index is not exits!");
            }
            AcknowledgedResponse dResponse = client.admin().indices().prepareDelete(index).execute().actionGet();
            if (dResponse.isAcknowledged()) {
                logger.info("delete index " + index + "  successfully!");
            } else {
                logger.info("Fail to delete index " + index);
            }
            return dResponse.isAcknowledged();
        }

        /**
         * 判断索引是否存在
         * @param index
         * @return
         */
        public static boolean isIndexExist(String index) {
            IndicesExistsResponse inExistsResponse = client.admin().indices().exists(new IndicesExistsRequest(index)).actionGet();
            if (inExistsResponse.isExists()) {
                logger.info("Index [" + index + "] is exist!");
            } else {
                logger.info("Index [" + index + "] is not exist!");
            }
            return inExistsResponse.isExists();
        }

        /**
         * 数据添加，正定ID
         * @param jsonObject 要增加的数据
         * @param index      索引，类似数据库
         * @param type       类型，类似表
         * @param id         数据ID(为空默认生成)
         * @return
         */
        public static String addData(JSONObject jsonObject, String index, String type, String id) {
            IndexResponse response = client.prepareIndex(index, type, id).setSource(jsonObject).get();
            logger.info("addData response status:{},id:{}", response.status().getStatus(), response.getId());
            return response.getId();
        }

        /**
         * 数据添加
         * @param jsonObject 要增加的数据
         * @param index      索引，类似数据库
         * @param type       类型，类似表
         * @return
         */
        public static String addData(JSONObject jsonObject, String index, String type) {
            return addData(jsonObject, index, type, UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
        }

        /**
         * 通过ID删除数据
         * @param index 索引，类似数据库
         * @param type  类型，类似表
         * @param id    数据ID
         */
        public static void deleteDataById(String index, String type, String id) {
            DeleteResponse response = client.prepareDelete(index, type, id).execute().actionGet();
            logger.info("deleteDataById response status:{},id:{}", response.status().getStatus(), response.getId());
        }

        /**
         * 通过ID 更新数据
         * @param jsonObject 要增加的数据
         * @param index      索引，类似数据库
         * @param type       类型，类似表
         * @param id         数据ID
         * @return
         */
        public static void updateDataById(JSONObject jsonObject, String index, String type, String id) {
            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.index(index).type(type).id(id).doc(jsonObject);
            client.update(updateRequest);
        }

        /**
         * 通过ID获取数据
         * @param index  索引，类似数据库
         * @param type   类型，类似表
         * @param id     数据ID
         * @param fields 需要显示的字段，逗号分隔（缺省为全部字段）
         * @return 结果
         */
        public static Map<String, Object> searchDataById(String index, String type, String id, String fields) {
            GetRequestBuilder getRequestBuilder = client.prepareGet(index, type, id);
            if (StringUtils.isNotEmpty(fields)) {
                getRequestBuilder.setFetchSource(fields.split(","), null);
            }
            GetResponse getResponse = getRequestBuilder.execute().actionGet();
            return getResponse.getSource();
        }

        /**
         * 批量增加
         * @param: [index, type, bookList]
         * @return: org.elasticsearch.action.bulk.BulkResponse
         * @auther: LHL
         * @date: 2018/10/15 13:12
         */
        public static void bulkAddDocument(String index, String type, List<User> userList) {
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            userList.stream().forEach(
                    book -> {
                        try {
                            bulkRequest.add(client.prepareIndex(index, type, book.getId())
                                    .setSource(XContentFactory.jsonBuilder()
                                            .startObject()
                                            .field("id", book.getId())
                                            .field("name", book.getName())
                                            .field("creatDate", book.getCreatDate())
                                            .field("price", book.getPrice())
                                            .field("message", book.getMessage())
                                            .endObject()
                                    )
                            );
                            if (Integer.valueOf(book.getId()) % 100000 == 0) {
                                BulkResponse responses = bulkRequest.execute().actionGet();
                                if (responses.hasFailures()) {
                                    System.out.println("bulk error:" + responses.buildFailureMessage());
                                }
                                LOGGER.info("insert status?" + responses.status());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            );
            BulkResponse responses = bulkRequest.execute().actionGet();
            if (responses.hasFailures()) {
                System.out.println("bulk error:" + responses.buildFailureMessage());
            }
            LOGGER.info("insert status?" + responses.status());
        }


        /**
         * bulkProcessor 批量增加
         * @param: [indexName, type, bookList]
         * @return: void
         * @auther: LHL
         * @date: 2018/10/16 11:46
         */
        public static void bulkProcessorAdd(String indexName, String type, List<Book> bookList) {
            bookList.stream().parallel().forEach(
                    book -> {
                        try {
                            bulk.add(new IndexRequest(indexName, type, book.getId()).source(XContentFactory.jsonBuilder()
                                    .startObject()
                                    .field("id", book.getId())
                                    .field("name", book.getName())
                                    .field("creatDate", book.getCreatDate())
                                    .field("price", book.getPrice())
                                    .field("message", book.getMessage())
                                    .endObject()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            );
        }

        /**
         * 批量删除
         * @param: [index, type, ids]
         * @return: org.elasticsearch.action.bulk.BulkResponse
         * @auther: LHL
         * @date: 2018/10/15 11:42
         */
        public static BulkResponse bulkDeleteDocument(String index, String type, List<String> ids) {
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            ids.stream().forEach(
                    id -> bulkRequest.add(client.prepareDelete(index, type, id))
            );
            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
            return bulkResponse;
        }

        /**
         * 查询所有
         * @param: [indexName]
         * @return: void
         * @auther: LHL
         * @date: 2018/10/15 14:56
         */
        public static List<String> searchAll(String indexName) {
            List<String> resultList = new ArrayList<>();
            String result = null;
            SearchResponse response = client.prepareSearch(indexName).setQuery(matchAllQuery()).setFrom(0).setSize(10).addSort("price", SortOrder.ASC).get();
            for (SearchHit searchHit : response.getHits()) {
                result = searchHit.getSourceAsString();
                resultList.add(result);
            }
            return resultList;
        }

        /**
         * 使用分词查询  高亮 排序 ,并分页
         * @param index          索引名称
         * @param type           类型名称,可传入多个type逗号分隔
         * @param startPage      当前页
         * @param pageSize       每页显示条数
         * @param query          查询条件
         * @param fields         需要显示的字段，逗号分隔（缺省为全部字段）
         * @param sortField      排序字段
         * @param highlightField 高亮字段
         * @return 结果
         */
        public static EsPage searchDataPage(String index, String type, int startPage, int pageSize, QueryBuilder query, String fields, String sortField, String highlightField) {
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
            if (StringUtils.isNotEmpty(type)) {
                searchRequestBuilder.setTypes(type.split(","));
            }
            // 需要显示的字段，逗号分隔（缺省为全部字段）
            if (StringUtils.isNotEmpty(fields)) {
                searchRequestBuilder.setFetchSource(fields.split(","), null);
            }//排序字段
            if (StringUtils.isNotEmpty(sortField)) {
                searchRequestBuilder.addSort(sortField, SortOrder.ASC);
            }// 高亮（xxx=111,aaa=222）
            if (StringUtils.isNotEmpty(highlightField)) {
                HighlightBuilder highlightBuilder = new HighlightBuilder();
                highlightBuilder.preTags("<span style='color:red' >");//设置前缀
                highlightBuilder.postTags("</span>");//设置后缀
                // 设置高亮字段
                highlightBuilder.field(highlightField);
                searchRequestBuilder.highlighter(highlightBuilder);
            }
            // 设置是否按查询匹配度排序
            searchRequestBuilder.setExplain(true);
            //如果 pageSize是10 那么startPage>9990 (10000-pagesize) 如果 20  那么 >9980 如果 50 那么>9950
            //深度分页  TODO
            if (startPage > (10000-pageSize)) {
                searchRequestBuilder.setQuery(query);
                searchRequestBuilder
                        .setScroll(TimeValue.timeValueMinutes(1))
                        .setSize(10000);
                //打印的内容 可以在 Elasticsearch head 和 Kibana  上执行查询
                LOGGER.info("\n{}", searchRequestBuilder);
                // 执行搜索,返回搜索响应信息
                SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
                long totalHits = searchResponse.getHits().totalHits;
                if (searchResponse.status().getStatus() == 200){
                    //使用scrollId迭代查询
                    List<Map<String, Object>> result = disposeScrollResult(searchResponse, highlightField);
                    List<Map<String, Object>> sourceList = result.stream().parallel().skip((startPage - 1- (10000/pageSize)) * pageSize).limit(pageSize).collect(Collectors.toList());
                    return new EsPage(startPage, pageSize, (int) totalHits, sourceList);
                }
            } else {//浅度分页
                searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);     // QUERY_THEN_FETCH    QUERY_AND_FETCH  DFS_QUERY_THEN_FETCH
                searchRequestBuilder.setQuery(matchAllQuery());
                searchRequestBuilder.setQuery(query);
                // 分页应用
                searchRequestBuilder
                        .setFrom(startPage)
                        .setSize(pageSize);
                //打印的内容 可以在 Elasticsearch head 和 Kibana  上执行查询
                LOGGER.info("\n{}", searchRequestBuilder);
                // 执行搜索,返回搜索响应信息
                SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
                long totalHits = searchResponse.getHits().totalHits;
                long length = searchResponse.getHits().getHits().length;
                LOGGER.debug("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);
                if (searchResponse.status().getStatus() == 200) {
                    // 解析对象
                    List<Map<String, Object>> sourceList = setSearchResponse(searchResponse, highlightField);
                    return new EsPage(startPage, pageSize, (int) totalHits, sourceList);
                }
            }
            return null;
        }
        /**
         * 深度分页  从当前页为1001开始
         * @param: [indexName, esType, startPage, pageSize, highlightField]
         * @return: com.aqh.utils.EsPage
         * @auther: LHL
         * @date: 2018/10/17 15:07
         */
        public static EsPage deepPageing(String indexName, String esType, int startPage, int pageSize, String highlightField) {
            System.out.println("scroll 模式启动！");
            long begin = System.currentTimeMillis();
            //初始化查询，获取scrollId
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.matchQuery("name", "名"));
//        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").from("1").to("999821"));
            SearchResponse response = client.prepareSearch(indexName)//对应索引
                    .setTypes(esType)//对应索引type
                    .setQuery(boolQueryBuilder)
                    .addSort("price", SortOrder.ASC)
                    .setScroll(TimeValue.timeValueMinutes(1))
                    .setSize(10000) //第一次不返回size条数据
                    .highlighter(new HighlightBuilder().preTags("<span style='color:red' >").postTags("</span>").field(highlightField))
                    .setExplain(true)
                    .execute()
                    .actionGet();
            long totalHits = response.getHits().totalHits;
            List<Map<String, Object>> result = disposeScrollResult(response, highlightField);
            List<Map<String, Object>> sourceList = result.stream().parallel().skip((startPage - 1-(10000/pageSize)) * pageSize).limit(pageSize).collect(Collectors.toList());
            long end = System.currentTimeMillis();
            System.out.println("耗时: " + (end - begin) + "ms");
            System.out.println("耗时: " + (end - begin) / 1000 + "s");
            System.out.println("查询"+totalHits+"条数据");
            return new EsPage(startPage, pageSize, (int) totalHits, sourceList);
        }

        /**
         * 处理scroll结果
         * @param: [response, highlightField]
         * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
         * @auther: LHL
         * @date: 2018/10/17 11:17
         */
        private static   List<Map<String, Object>>   disposeScrollResult(SearchResponse response ,String highlightField){
            List<Map<String, Object>> sourceList = new ArrayList<Map<String, Object>>();
            //使用scrollId迭代查询
            while (response.getHits().getHits().length > 0) {
                String scrollId = response.getScrollId();
                response = client.prepareSearchScroll(scrollId)
                        .setScroll(TimeValue.timeValueMinutes(1))//设置查询context的存活时间
                        .execute()
                        .actionGet();
                SearchHits hits = response.getHits();
                for (SearchHit hit : hits.getHits()) {
                    Map<String, Object> resultMap =getResultMap(hit, highlightField);
                    sourceList.add(resultMap);
//                System.out.println(JSON.toJSONString(resultMap));
                }
            }
            ClearScrollRequest request = new ClearScrollRequest();
            request.addScrollId(response.getScrollId());
            client.clearScroll(request);
            return sourceList;
        }

        /**
         * 使用分词查询  排序 高亮
         * @param index          索引名称
         * @param type           类型名称,可传入多个type逗号分隔
         * @param query          查询条件
         * @param size           文档大小限制
         * @param fields         需要显示的字段，逗号分隔（缺省为全部字段）
         * @param sortField      排序字段
         * @param highlightField 高亮字段
         * @return 结果
         */
        public static List<Map<String, Object>> searchListData(String index, String type, QueryBuilder query, Integer size, String fields, String sortField, String highlightField) {
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
            if (StringUtils.isNotEmpty(type)) {
                searchRequestBuilder.setTypes(type.split(","));
            }
            if (StringUtils.isNotEmpty(highlightField)) {
                HighlightBuilder highlightBuilder = new HighlightBuilder();
                // 设置高亮字段
                highlightBuilder.field(highlightField);
                searchRequestBuilder.highlighter(highlightBuilder);
            }
            searchRequestBuilder.setQuery(query);
            if (StringUtils.isNotEmpty(fields)) {
                searchRequestBuilder.setFetchSource(fields.split(","), null);
            }
            searchRequestBuilder.setFetchSource(true);
            if (StringUtils.isNotEmpty(sortField)) {
                searchRequestBuilder.addSort(sortField, SortOrder.ASC);
            }
            if (size != null && size > 0) {
                searchRequestBuilder.setSize(size);
            }//打印的内容 可以在 Elasticsearch head 和 Kibana  上执行查询
            LOGGER.info("\n{}", searchRequestBuilder);
            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
            long totalHits = searchResponse.getHits().totalHits;
            long length = searchResponse.getHits().getHits().length;
            LOGGER.info("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);
            if (searchResponse.status().getStatus() == 200) {
                // 解析对象
                return setSearchResponse(searchResponse, highlightField);
            }
            return null;
        }

        /**
         * 高亮结果集 特殊处理
         * @param searchResponse 搜索的结果集
         * @param highlightField 高亮字段
         */
        private static List<Map<String, Object>> setSearchResponse(SearchResponse searchResponse, String highlightField) {
            List<Map<String, Object>> sourceList = new ArrayList<Map<String, Object>>();
            for (SearchHit searchHit : searchResponse.getHits().getHits()) {
                Map<String, Object> resultMap = getResultMap(searchHit, highlightField);
                sourceList.add(resultMap);
            }
            return sourceList;
        }

        /**
         * 获取高亮结果集
         * @param: [hit, highlightField]
         * @return: java.util.Map<java.lang.String,java.lang.Object>
         * @auther: LHL
         * @date: 2018/10/17 10:54
         */
        private  static   Map<String, Object>  getResultMap(SearchHit hit,String highlightField){
            hit.getSourceAsMap().put("id", hit.getId());
            if (StringUtils.isNotEmpty(highlightField)) {
                Text[] text = hit.getHighlightFields().get(highlightField).getFragments();
                String hightStr = null;
                if (text != null) {
                    for (Text str : text) {
                        hightStr = str.string();
                    }
                    //遍历 高亮结果集，覆盖 正常结果集
                    hit.getSourceAsMap().put(highlightField, hightStr);
                }
            }
            return  hit.getSourceAsMap();
        }

        /**
         * ik分词测试
         * @param: []
         * @return: void
         * @auther: LHL
         * @date: 2018/10/15 11:31
         */
        public static String ik() {
            StringBuilder stringBuilder = new StringBuilder();
            AnalyzeRequest analyzeRequest = new AnalyzeRequest("entity")
                    .text("我是程序媛")
                    .analyzer("ik_max_word");  //ik_smart  ik_max_word  standard
            List<AnalyzeResponse.AnalyzeToken> tokens = client.admin().indices()
                    .analyze(analyzeRequest)
                    .actionGet()
                    .getTokens();
            for (AnalyzeResponse.AnalyzeToken token : tokens) {
                stringBuilder.append(token.getTerm() + "\\r\\n");
            }
            return stringBuilder.toString();
        }
    }

}
