package com.morningstar.practice.lib;

import co.elastic.clients.elasticsearch._types.SortOrder;
import com.morningstar.util.EsUtil;
import lombok.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EsTest {
    private final EsUtil esUtil;
    private final String indexName = "new_index";

    @BeforeEach
    public void before() {
        System.out.println("索引库创建成功? " + esUtil.createIndex(indexName));
    }

    @AfterEach
    public void after() {
        System.out.println("索引库删除成功? " + esUtil.deleteIndex(indexName));
    }

    @Test
    public void testIndexOps() {
        System.out.println("索引库存在? " + esUtil.existIndex(indexName));
    }

    @Test
    public void testBulkIndexDocument() {
        List<Document> documentList = new ArrayList<>();
        documentList.add(Document.builder().field1("test1").field2(1).build());
        documentList.add(Document.builder().field1("test2").field2(2).build());
        List<String> idList = new ArrayList<>();
        idList.add("1");
        idList.add("2");

        System.out.println("批量索引文档: " + esUtil.bulkIndexDocument(indexName, idList, documentList));
        System.out.println(esUtil.getDocument(indexName, "1", Document.class));
        System.out.println(esUtil.getDocument(indexName, "2", Document.class));
    }

    @Test
    public void testDocumentOps() {
        System.out.println("存在文档1: " + esUtil.existDocument(indexName, "1"));
        System.out.println("索引文档1: " + esUtil.indexDocument(indexName, "1", Document.builder().field1("test1").field2(1).build()));
        System.out.println("存在文档1: " + esUtil.existDocument(indexName, "1"));
        System.out.println("再次索引文档1: " + esUtil.indexDocument(indexName, "1", Document.builder().field1("test2").field2(2).build()));
        System.out.println(esUtil.getDocument(indexName, "1", Document.class));
        System.out.println(esUtil.getDocument(indexName, "2", Document.class));
        HashMap<String, Object> map = new HashMap<>();
        map.put("field1", "test3");
        map.put("field3", "append");
        System.out.println("部分更新文档1: " + esUtil.updateDocument(indexName, "1", map, Document.class));
        System.out.println(esUtil.getDocument(indexName, "1", Document.class));
        System.out.println("删除文档1: " + esUtil.deleteDocument(indexName, "1"));
    }

    @Test
    public void testSearchOps() {
        esUtil.indexDocument(indexName, "1", Document.builder().field1("test 1").field2(1).build());
        esUtil.indexDocument(indexName, "2", Document.builder().field1("test 2").field2(1).build());
        esUtil.indexDocument(indexName, "3", Document.builder().field1("test 3").field2(1).build());
        esUtil.indexDocument(indexName, "4", Document.builder().field1("test 4").field2(1).build());
        esUtil.indexDocument(indexName, "5", Document.builder().field1("test 1").field2(2).build());
        esUtil.indexDocument(indexName, "6", Document.builder().field1("test 2").field2(2).build());
        esUtil.indexDocument(indexName, "7", Document.builder().field1("test 3").field2(2).build());
        esUtil.indexDocument(indexName, "8", Document.builder().field1("test 4").field2(3).build());

        esUtil.refreshIndex(indexName);

        List<Document> list = esUtil.searchDocument(
                s -> s
                        .index(indexName)
                        .query(q -> q
                                .match(t -> t
                                        .field("field1")
                                        .query("test")
                                )).sort(so -> so // 排序操作项
                                .field(f -> f // 排序字段规则
                                        .field("field2")
                                        .order(SortOrder.Desc)
                                )), Document.class).getRecords();

        System.out.println(Arrays.toString(list.toArray()));
    }

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    static class Document {
        private String field1;
        private Integer field2;
    }
}
