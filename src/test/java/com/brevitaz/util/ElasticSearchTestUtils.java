package com.brevitaz.util;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component
public class ElasticSearchTestUtils {

    @Autowired
    private Client client;

    @Value("${app.elasticsearch.index-name}")
    private String indexName;

    @Value("${app.elasticsearch.type-name}")
    private String typeName;

    public void setUpIndex() throws Exception
    {
        URL settingsURL = Resources.getResource("elasticsearch/index-settings.json");
        String settings = Resources.toString(settingsURL, Charsets.UTF_8);

        URL mappingURL = Resources.getResource("elasticsearch/index-mapping.json");
        String mappings = Resources.toString(mappingURL, Charsets.UTF_8);

        deleteIndexIfExist();
        client.admin().indices().prepareCreate(indexName).setSettings(settings, XContentType.JSON).addMapping(typeName, mappings, XContentType.JSON).get();

    }

    public void setUpTestData() throws Exception
    {
        URL dataFileUrl = Resources.getResource("elasticsearch/sample-search-data.json");
        JSONArray dataArray = new JSONArray(Resources.toString(dataFileUrl, Charsets.UTF_8));

        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (int i = 0 ; i < dataArray.length(); i++)
        {
            bulkRequest.add(client.prepareIndex(indexName, typeName)
                    .setSource(dataArray.getString(i), XContentType.JSON));
        }
        bulkRequest.execute().get();
        refreshIndex();
    }

    public void deleteIndexIfExist()
    {
        IndicesExistsResponse indicesExistsResponse = client.admin().indices().exists(new IndicesExistsRequest(indexName)).actionGet();
        if(indicesExistsResponse.isExists())
        {
            client.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet();
        }
    }

    public void refreshIndex()
    {
        client.admin().indices().refresh(new RefreshRequest(indexName)).actionGet();
    }
}
