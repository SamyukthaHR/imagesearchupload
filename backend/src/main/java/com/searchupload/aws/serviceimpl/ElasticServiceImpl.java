package com.searchupload.aws.serviceimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchupload.aws.model.Fileupload;
import com.searchupload.aws.service.ElasticService;

@Service
public class ElasticServiceImpl implements ElasticService{

	private RestHighLevelClient client;

    private ObjectMapper objectMapper;
    
    String index = "index";
    String type = "_doc";

    @Autowired
    public ElasticServiceImpl(RestHighLevelClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }
    
	
	@SuppressWarnings("deprecation")
	@Override
	public String CreateImageDocument(Fileupload fileupload) throws IOException {
		UUID uuid = UUID.randomUUID();
        fileupload.setId(uuid.toString());

        IndexRequest indexRequest = new IndexRequest(index, type, fileupload.getId())
                .source(convertfileDocumentToMap(fileupload), XContentType.JSON);

        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        return "Created";
        
	}
	
	public List<Fileupload> findfileByName(String name) throws Exception{
		
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchRequest.types(type);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        MatchQueryBuilder matchQueryBuilder = QueryBuilders
                .matchQuery("name",name)
                .operator(Operator.AND);

        searchSourceBuilder.query(matchQueryBuilder);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse =
                client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);

    }
	
	
	public List<Fileupload> findAll() throws Exception {

		SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchRequest.types(type);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse =
                client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);
    }
	
	
	private Map<String, Object> convertfileDocumentToMap(Fileupload fileupload) {
        return objectMapper.convertValue(fileupload, Map.class);
    }
	
	
	private List<Fileupload> getSearchResult(SearchResponse response) {

        SearchHit[] searchHit = response.getHits().getHits();

        List<Fileupload> fileDocuments = new ArrayList<>();

        for (SearchHit hit : searchHit){
            fileDocuments
                    .add(objectMapper
                            .convertValue(hit
                                    .getSourceAsMap(), Fileupload.class));
        }

        return fileDocuments;
    }

}
