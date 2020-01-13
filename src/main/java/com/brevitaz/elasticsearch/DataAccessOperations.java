package com.brevitaz.elasticsearch;


import com.brevitaz.model.Identifiable;
import com.brevitaz.model.SearchResponseModel;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.index.query.QueryBuilder;

import java.util.Collection;
import java.util.List;

public interface DataAccessOperations {
    boolean saveAll(Collection<? extends Identifiable> source, String indexName);

    //void rollIndexAndAlias(String alias, String newIndexName, Optional<String> oldIndexName, Collection<? extends Runnable> tasks);

    String save(String indexName, String id, Object source);

    String save(String indexName, String id, Object source, WriteRequest.RefreshPolicy refreshPolicy);

    void saveOrUpdate(String indexName, String id, Object source, WriteRequest.RefreshPolicy refreshPolicy);

    void update(String indexName, String id, Object source);

    <T> SearchResponseModel<T> searchQuery(String indexName, QueryBuilder query, Integer pageSize, Integer pageNumber, Class<T> clazz);

    <T> List<T> findByQuery(String indexName, QueryBuilder query, Integer pageSize, Class<T> clazz, String... includeFields);

    <T> T getById(String indexName, String id, Class<T> clazz);

    boolean deleteIndex(String indexName);

    List<String> getIndexNamesByAlias(String alias);
}
