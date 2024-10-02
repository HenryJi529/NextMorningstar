package com.morningstar.config;

import com.morningstar.util.EsUtil;
import lombok.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestEs {
   @Data
   @NoArgsConstructor
   @Builder
   @AllArgsConstructor
   static class Document {
        private String field1;
    }

    private final EsUtil esUtil;

    private final String indexName = "new_index";

    @BeforeEach
    public void before() throws IOException {
        System.out.println("索引库创建成功? " + esUtil.createIndex(indexName));
    }

    @AfterEach
    public void after() throws IOException {
        System.out.println("索引库删除成功? " + esUtil.deleteIndex(indexName));
    }

    @Test
    public void testIndexOps() throws IOException {
        System.out.println("索引库存在? " + esUtil.existIndex(indexName));
    }

    @Test
    public void testBulkIndexDocument() throws IOException {
        List<Document> documentList = new ArrayList<>();
        documentList.add(Document.builder().field1("test4").build());
        documentList.add(Document.builder().field1("test5").build());
        List<String> idList = new ArrayList<>();
        idList.add("4");
        idList.add("5");

        System.out.println("批量索引文档: " + esUtil.bulkIndexDocument(indexName, idList, documentList));
        System.out.println(esUtil.getDocument(indexName, "4", Document.class));
        System.out.println(esUtil.getDocument(indexName, "5", Document.class));
    }

    @Test
    public void testDocumentOps() throws IOException {
        System.out.println("存在文档1: " + esUtil.existDocument(indexName, "1"));
        System.out.println("索引文档1: " + esUtil.indexDocument(indexName, "1", Document.builder().field1("test1").build()));
        System.out.println("存在文档1: " + esUtil.existDocument(indexName, "1"));
        System.out.println("再次索引文档1: " + esUtil.indexDocument(indexName, "1", Document.builder().field1("test2").build()));
        System.out.println(esUtil.getDocument(indexName, "1", Document.class));
        System.out.println(esUtil.getDocument(indexName, "2", Document.class));
        HashMap<String, Object> map = new HashMap<>();
        map.put("field1", "test3");
        map.put("field2", "test4");
        System.out.println("部分更新文档1: " + esUtil.updateDocument(indexName, "1", map, Document.class));
        System.out.println(esUtil.getDocument(indexName, "1", Document.class));
        System.out.println("删除文档1: " + esUtil.deleteDocument(indexName, "1"));
    }
}
