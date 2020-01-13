package com.brevitaz.elasticsearch;

import com.brevitaz.model.Identifiable;
import com.brevitaz.model.SearchResponseModel;
import com.brevitaz.util.ProductObjectMapper;
import org.apache.commons.collections4.IteratorUtils;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequestBuilder;
import org.elasticsearch.action.admin.indices.alias.exists.AliasesExistRequestBuilder;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Component
public class ElasticOperations implements DataAccessOperations {
    final static private Logger LOGGER = LoggerFactory.getLogger(ElasticOperations.class);

    @Value("${app.elasticsearch.type-name}")
    private String type;

    @Autowired
    private Client client;

    @Autowired
    private ProductObjectMapper objectMapper;

    @Override
    public boolean saveAll(Collection<? extends Identifiable> source, String indexName) {
        try {
            BulkRequestBuilder bulkRequest = client.prepareBulk();

            for (Identifiable obj : source) {
                String json = objectMapper.writeValueAsString(obj);
                bulkRequest.add(client.prepareUpdate(indexName, type, obj.generateId())
                        .setDoc(json, XContentType.JSON)
                        .setUpsert(json, XContentType.JSON));
            }

            BulkResponse bulkResponse = bulkRequest.get();
            if (bulkResponse.hasFailures()) {
                Iterator<BulkItemResponse> iterator = bulkResponse.iterator();
                while (iterator.hasNext()) {
                    BulkItemResponse response = iterator.next();
                    LOGGER.error(response.getFailureMessage());
                }
                return Boolean.FALSE;
            }
        } catch (Exception e) {
            LOGGER.error("Error while bulk update for index: "+indexName , e);
            return Boolean.FALSE;
        }
        LOGGER.info("Save Record Successfully");
        return Boolean.TRUE;
    }


    public void createIndex(String indexName, String typeName){
        try{
            LOGGER.info("Creating index with name {}",indexName);
            client.admin().indices().prepareCreate(indexName).get();
        }catch (Exception e){
            LOGGER.error("Unable to create index {}",indexName,e);
        }
    }

    private <T> SearchResponseModel<T> getResultsWithTotalPage(Class<T> responseClass, SearchResponse response, int pageSize ) {
        List<T> result = getResults(responseClass,response);
        int totalPages = (int) Math.ceil((double) response.getHits().totalHits / (double) pageSize);
        SearchResponseModel<T> searchResponse = new SearchResponseModel<>();
        searchResponse.setResult(result);
        searchResponse.setTotalPage(totalPages);
        return searchResponse;
    }

    private <T> List<T> getResults(Class<T> responseClass, SearchResponse response) {
        List<T> results = new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            try {
                T result = objectMapper.readValue(hit.getSourceAsString(), responseClass);
                if (result instanceof Identifiable) {
                    ((Identifiable) result).setId(hit.getId());
                }
                results.add(result);
            } catch (Exception e) {
                LOGGER.error("Error while fetching results", e);
            }
        }
        return results;
    }

    @Override
    public String save(String indexName, String id, Object source) {
        return save(indexName, id, source, WriteRequest.RefreshPolicy.WAIT_UNTIL);
    }

    @Override
    public String save(String indexName, String id, Object source, WriteRequest.RefreshPolicy refreshPolicy) {
        try {
            String json = objectMapper.writeValueAsString(source);

            LOGGER.info("Saving object = {}");

            IndexRequestBuilder builder = client.prepareIndex(indexName, type).setSource(json, XContentType.JSON);
            if (!StringUtils.isEmpty(id))
                builder.setId(id);

            builder.setRefreshPolicy(refreshPolicy);
            IndexResponse response = builder.get();
            return response.getId();
        } catch (Exception e) {
            LOGGER.error("Error while saving object : ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveOrUpdate(String indexName, String id, Object source, WriteRequest.RefreshPolicy refreshPolicy) {
        try {
            String json = objectMapper.writeValueAsString(source);

            IndexRequest indexRequest = new IndexRequest(indexName, type, id);
            indexRequest.source(json, XContentType.JSON);
            indexRequest.setRefreshPolicy(refreshPolicy);

            UpdateRequest updateRequest = new UpdateRequest(indexName, type, id);
            updateRequest.doc(json, XContentType.JSON);
            updateRequest.upsert(indexRequest);
            updateRequest.setRefreshPolicy(refreshPolicy);
            client.update(updateRequest).get();


        } catch (Exception e) {
            LOGGER.error("Error while saveWeatherInfo or update document", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(String indexName, String id, Object source) {
        try {
            String json = objectMapper.writeValueAsString(source);
            UpdateRequest updateRequest = new UpdateRequest(indexName, type, id);
            updateRequest.doc(json, XContentType.JSON);
            client.update(updateRequest).get();

        } catch (Exception e) {
            LOGGER.error("Error while updating document", e);
            throw new RuntimeException(e);
        }
    }

    private Boolean documentExists(QueryBuilder query, String indexName) {
        try {
            LOGGER.info("Checking if document Exists");
            SearchResponse response = client.prepareSearch(indexName).setTypes(type).setQuery(query).setSize(0).execute().actionGet();
            long totalResult = response.getHits().getTotalHits();
            return totalResult > 0;
        } catch (Exception e) {
            LOGGER.error("Error occurred while checking document existence", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> SearchResponseModel<T> searchQuery(String indexName, QueryBuilder query, Integer pageSize, Integer pageNumber, Class<T> clazz) {
        try{
            int from = ((pageNumber - 1) * pageSize);
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName);
            LOGGER.info("Running query : {}", query.toString());
            SearchResponse response = searchRequestBuilder.setTypes(type).setSize(pageSize).setFrom(from).
                    setSearchType("dfs_query_then_fetch").
                    setQuery(query).
                    execute().actionGet();
            return getResultsWithTotalPage(clazz, response, pageSize);
        }catch (IndexNotFoundException e){
            LOGGER.warn(indexName + " index is not available.",e);
            return new SearchResponseModel<>();
        }
    }


    @Override
    public <T> List<T> findByQuery(String indexName, QueryBuilder query, Integer pageSize, Class<T> clazz, String... includeFields) {
        try{
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName);
            if (includeFields != null && includeFields.length > 0) {
                searchRequestBuilder.setFetchSource(includeFields, null);
            }
            SearchResponse response = searchRequestBuilder.setTypes(type).setSize(pageSize).
                    setQuery(query).
                    execute().actionGet();
            return getResults(clazz, response);
        }catch (IndexNotFoundException e){
            LOGGER.warn(indexName + " index is not available.",e);
            return Collections.emptyList();
        }

    }

    @Override
    public <T> T getById(String indexName, String id, Class<T> clazz) {
        try {

            GetResponse response = client.prepareGet(indexName, type, id).get();
            String source = response.getSourceAsString();

            if (StringUtils.isEmpty(source)) {
                LOGGER.info("Document not found for Id : " + id);
                return null;
            }

            T result = objectMapper.readValue(source, clazz);
            if (result instanceof Identifiable) {
                ((Identifiable) result).setId(response.getId());
            }
            return result;
        } catch (IOException e) {
            LOGGER.error("Unable to parse json to class: {}.", clazz.getName());
            throw new RuntimeException(e);
        }catch (IndexNotFoundException in){
            LOGGER.error("Unable to find index {} to get document with Id {}",indexName,id);
            throw new RuntimeException(in);
        }
    }

    private Long deleteByQuery(QueryBuilder query, String indexName, String type) {

        DeleteByQueryRequestBuilder req = new DeleteByQueryRequestBuilder(client, DeleteByQueryAction.INSTANCE);
        req.source()
                .setIndices(indexName)
                .setTypes(type)
                .setQuery(query);
        BulkByScrollResponse response = req.get();
        long totalDeleted = response.getDeleted();
        LOGGER.info("total deleted records are : " + totalDeleted);
        return totalDeleted;
    }


    @Override
    public boolean deleteIndex(String indexName) {
        try {
            client.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet();
            return true;
        } catch (IndexNotFoundException e) {
            LOGGER.warn("Delete attempt failed as index doesn't exist, index={}", indexName);
            return false;
        }
    }

    private void switchAlias(String aliasName, Optional<String> oldIndexName, String newIndexName) {
        try {
            IndicesAliasesRequestBuilder indicesAliasesRequestBuilder = client.admin().indices().prepareAliases();
            oldIndexName.ifPresent( old -> indicesAliasesRequestBuilder.removeAlias(old, aliasName));
            indicesAliasesRequestBuilder.addAlias(newIndexName, aliasName).execute().actionGet();
        } catch (IndexNotFoundException e) {
            LOGGER.error("Can not assign alias {} to new index {} because it does not exist", aliasName, newIndexName);
        }
    }

    public void addAlias(String alias, String indexName){
        try {
            LOGGER.info("Adding alias {} for indexname {}",alias,indexName);
            IndicesAliasesRequestBuilder indicesAliasesRequestBuilder = client.admin().indices().prepareAliases();
            indicesAliasesRequestBuilder.addAlias(indexName, alias).execute().actionGet();
        } catch (IndexNotFoundException e) {
            LOGGER.error("Can not assign alias {} to new index {} because it does not exist", alias, indexName);
        }
    }

    public Boolean isAliasExist(String alias){
            AliasesExistRequestBuilder aliasesExistRequestBuilder = client.admin().indices().prepareAliasesExist(alias);
            return aliasesExistRequestBuilder.execute().actionGet().isExists();
    }

    @Override
    public List<String> getIndexNamesByAlias(String alias) {
        GetAliasesResponse r = null;
        try {
            r = client.admin()
                    .indices()
                    .getAliases(new GetAliasesRequest()
                                    .aliases(alias))
                    .get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return IteratorUtils.toList(r.getAliases().keysIt());
    }
}