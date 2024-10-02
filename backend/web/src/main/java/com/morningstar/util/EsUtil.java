package com.morningstar.util;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EsUtil {

    private final ElasticsearchClient client;

    public boolean createIndex(String indexName) throws IOException {
        String indexDefFilePath = "/elasticsearch/" + indexName + ".json";
        try(InputStream input = this.getClass().getResourceAsStream(indexDefFilePath)) {
            CreateIndexRequest req = CreateIndexRequest.of(b -> b
                    .index(indexName)
                    .withJson(input)
            );
            CreateIndexResponse createIndexResponse = client.indices().create(req);
            return createIndexResponse.acknowledged();
        } catch (ElasticsearchException e){
            return false;
        }
    }

    public boolean existIndex(String indexName) throws IOException {
        return client.indices().exists(c -> c.index(indexName)).value();
    }

    public boolean deleteIndex(String indexName) throws IOException {
        try {
            DeleteIndexResponse deleteIndexResponse = client.indices().delete(c -> c.index(indexName));
            return deleteIndexResponse.acknowledged();
        } catch (ElasticsearchException e){
            return false;
        }
    }

    public <D> Result indexDocument(String indexName, String id, D document) throws IOException {
        try{
            IndexRequest<D> request = IndexRequest.of(i -> i
                    .index(indexName)
                    .id(id)
                    .document(document)
            );

            IndexResponse response = client.index(request);

            return response.result();
        }catch (ElasticsearchException e){
            return Result.NoOp;
        }
    }

    public <D> D getDocument(String indexName, String id, Class<D> clazz) throws IOException {
        try{
            GetResponse<D> response = client.get(g -> g.index(indexName).id(id), clazz);
            if(response.found()){
                return response.source();
            }else{
                return null;
            }
        }catch (ElasticsearchException e){
            return null;
        }
    }

    private boolean isAllFieldValid(Map<String, Object> map, Class<?> clazz){
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

    public <T> boolean updateDocument(String indexName, String id, Map<String, Object> partial, Class<T> clazz) throws IOException {
        // 如果partial中存在clazz中没用的字段，那么直接返回false
        if(!isAllFieldValid(partial, clazz)){
            return false;
        }

        UpdateRequest<T, Map<String, Object>> request = UpdateRequest.of(r -> r
                .index(indexName)
                .id(id)
                .doc(partial) // 使用partial文档更新
        );

        UpdateResponse<T> response = client.update(request, clazz);
        return response.result().equals(Result.Updated);
    }

    public boolean existDocument(String indexName, String id) throws IOException {
        ExistsRequest existsRequest = new ExistsRequest.Builder().index(indexName).id(id).build();
        return client.exists(existsRequest).value();
    }

    public boolean deleteDocument(String indexName, String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest.Builder().index(indexName).id(id).build();
        DeleteResponse deleteResponse = client.delete(deleteRequest);
        return deleteResponse.result().equals(Result.Deleted);
    }

    public <T> boolean bulkIndexDocument(String indexName, List<String> ids, List<T> documents) throws IOException {
        if(ids.size() != documents.size()){
            log.error("ids.size() != documents.size()");
            return false;
        }

        BulkRequest.Builder br = new BulkRequest.Builder();
        for(int i = 0; i < ids.size(); i++){
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
        BulkResponse result = client.bulk(br.build());

        if (result.errors()) {
            log.error("Bulk had errors");
            for (BulkResponseItem item: result.items()) {
                if (item.error() != null) {
                    log.error(item.error().reason());
                }
            }
            return false;
        }
        return true;
    }
}
