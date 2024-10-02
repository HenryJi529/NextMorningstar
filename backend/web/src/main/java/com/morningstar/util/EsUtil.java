package com.morningstar.util;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.ErrorCause;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.util.ObjectBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EsUtil {

    private final ElasticsearchClient client;

    public boolean createIndex(String indexName) {
        String indexDefFilePath = "/elasticsearch/" + indexName + ".json";
        try (InputStream input = this.getClass().getResourceAsStream(indexDefFilePath)) {
            CreateIndexRequest req = CreateIndexRequest.of(b -> b
                    .index(indexName)
                    .withJson(input)
            );
            CreateIndexResponse createIndexResponse = client.indices().create(req);
            return createIndexResponse.acknowledged();
        } catch (ElasticsearchException e) {
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existIndex(String indexName) {
        try {
            return client.indices().exists(c -> c.index(indexName)).value();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteIndex(String indexName) {
        try {
            DeleteIndexResponse deleteIndexResponse = client.indices().delete(c -> c.index(indexName));
            return deleteIndexResponse.acknowledged();
        } catch (ElasticsearchException e) {
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshIndex(String indexName) {
        try {
            client.indices().refresh(r -> r.index(indexName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <D> Result indexDocument(String indexName, String id, D document) {
        try {
            IndexRequest<D> request = IndexRequest.of(i -> i
                    .index(indexName)
                    .id(id)
                    .document(document)
            );

            IndexResponse response = client.index(request);

            return response.result();
        } catch (ElasticsearchException e) {
            return Result.NoOp;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <D> D getDocument(String indexName, String id, Class<D> clazz) {
        try {
            GetResponse<D> response = client.get(g -> g.index(indexName).id(id), clazz);
            if (response.found()) {
                return response.source();
            } else {
                return null;
            }
        } catch (ElasticsearchException e) {
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isAllFieldValid(Map<String, Object> map, Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (String key : map.keySet()) {
            boolean isFieldValid = false;
            // 检查Map中的键是否是clazz中的一个字段名
            for (Field field : fields) {
                if (field.getName().equals(key)) {
                    isFieldValid = true;
                    break;
                }
            }
            // 如果键不是clazz中的字段名，则返回false
            if (!isFieldValid) {
                return false;
            }
        }
        return true;
    }

    public <T> boolean updateDocument(String indexName, String id, Map<String, Object> partial, Class<T> clazz) {
        // 如果partial中存在clazz中没用的字段，那么直接返回false
        if (!isAllFieldValid(partial, clazz)) {
            return false;
        }

        UpdateRequest<T, Map<String, Object>> request = UpdateRequest.of(r -> r
                .index(indexName)
                .id(id)
                .doc(partial) // 使用partial文档更新
        );

        UpdateResponse<T> response;
        try {
            response = client.update(request, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response.result().equals(Result.Updated);
    }

    public boolean existDocument(String indexName, String id) {
        ExistsRequest existsRequest = new ExistsRequest.Builder().index(indexName).id(id).build();
        try {
            return client.exists(existsRequest).value();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteDocument(String indexName, String id) {
        DeleteRequest deleteRequest = new DeleteRequest.Builder().index(indexName).id(id).build();
        DeleteResponse deleteResponse;
        try {
            deleteResponse = client.delete(deleteRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return deleteResponse.result().equals(Result.Deleted);
    }

    public <T> boolean bulkIndexDocument(String indexName, List<String> ids, List<T> documents) {
        if (ids.size() != documents.size()) {
            log.error("ids.size() != documents.size()");
            return false;
        }

        BulkRequest.Builder br = new BulkRequest.Builder();
        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i);
            T document = documents.get(i);
            br.operations(op -> op
                    .index(idx -> idx
                            .index(indexName)
                            .id(id)
                            .document(document)
                    )
            );
        }
        BulkResponse result;
        try {
            result = client.bulk(br.build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (result.errors()) {
            log.error("Bulk had errors");
            for (BulkResponseItem item : result.items()) {
                ErrorCause error = item.error();
                if (error != null) {
                    log.error(error.reason());
                }
            }
            return false;
        }
        return true;
    }

    public <T> SearchResult<T> searchDocument(Function<SearchRequest.Builder, ObjectBuilder<SearchRequest>> fn, Class<T> clazz) {
        log.info("ES: searchDocument -- {}", fn.apply(new SearchRequest.Builder()).build());
        SearchResponse<T> response;
        try {
            response = client.search(fn, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Hit<T>> hits = response.hits().hits();
        List<T> records = hits.stream().map(Hit::source).toList();
        List<List<String>> sorts = hits.stream().map(Hit::sort).toList();
        List<Map<String, List<String>>> highlights = hits.stream().map(Hit::highlight).toList();

        long totalRecordNum = Objects.requireNonNull(response.hits().total()).value();

        return new SearchResult<>(totalRecordNum, records, sorts, highlights);
    }

    @AllArgsConstructor
    @Data
    public static class SearchResult<T> {
        private long totalRecordNum;
        private List<T> records;
        private List<List<String>> sorts;
        private List<Map<String, List<String>>> highlights;
    }
}
