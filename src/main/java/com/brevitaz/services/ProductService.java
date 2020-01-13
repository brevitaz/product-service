package com.brevitaz.services;


import com.brevitaz.elasticsearch.ElasticOperations;
import com.brevitaz.model.Product;
import com.brevitaz.model.SearchRequestModel;
import com.brevitaz.model.SearchResponseModel;
import com.brevitaz.util.ElasticSearchUtils;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    final static Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ElasticOperations elasticOperations;

    @Value("${app.elasticsearch.index-name}")
    private String indexName;

    public Product getProductById(String id)
    {
        return elasticOperations.getById(indexName,id,Product.class);
    }

    public String saveProduct(Product newProduct)
    {
        return elasticOperations.save(indexName,"",newProduct);
    }

    public SearchResponseModel<Product> searchProduct(SearchRequestModel productSearchRequest)
    {
        String searchText = productSearchRequest.getSearchText();
        QueryBuilder query = QueryBuilders.matchAllQuery();

        if(searchText != null && !searchText.isEmpty())
        {
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            boolQuery.should(QueryBuilders.matchPhraseQuery(ElasticSearchUtils.TITLE,searchText).slop(100).boost(5));
            boolQuery.should(QueryBuilders.matchQuery(ElasticSearchUtils.TITLE,searchText).boost(4).fuzziness(Fuzziness.ONE));
            boolQuery.should(QueryBuilders.matchPhraseQuery(ElasticSearchUtils.DESCRIPTION,searchText).slop(3).boost(3));
            boolQuery.should(QueryBuilders.matchQuery(ElasticSearchUtils.DESCRIPTION,searchText).boost(1).fuzziness(Fuzziness.ONE));
            boolQuery.should(QueryBuilders.multiMatchQuery(searchText,ElasticSearchUtils.ALL_SPECIFICATIONS).boost(3).lenient(true).
                    type(MultiMatchQueryBuilder.Type.MOST_FIELDS).fuzziness(Fuzziness.ONE));
            boolQuery.should(QueryBuilders.matchQuery(ElasticSearchUtils.CODE,searchText).boost(10));
            boolQuery.should(QueryBuilders.matchQuery(ElasticSearchUtils.CATEGORIES,searchText).boost(1));
            boolQuery.should(QueryBuilders.matchQuery(ElasticSearchUtils.TAGS,searchText).boost(3));

            FunctionScoreQueryBuilder.FilterFunctionBuilder reviewScoreFilterFunction = new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders
                    .fieldValueFactorFunction(ElasticSearchUtils.REVIEW_SCORE).missing(0).factor((float) 0.01));
            FunctionScoreQueryBuilder.FilterFunctionBuilder modelYearFilterFunction = new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders
                    .fieldValueFactorFunction(ElasticSearchUtils.MODEL_YEAR).missing(0).factor((float) 0.005));

            FunctionScoreQueryBuilder.FilterFunctionBuilder[] functionArray = {reviewScoreFilterFunction, modelYearFilterFunction};

            query = QueryBuilders.functionScoreQuery(boolQuery,functionArray).boostMode(CombineFunction.SUM).scoreMode(FunctionScoreQuery.ScoreMode.SUM);
        }
        return elasticOperations.searchQuery(indexName, query, productSearchRequest.getPageSize(), productSearchRequest.getPage(),Product.class);
    }
}
