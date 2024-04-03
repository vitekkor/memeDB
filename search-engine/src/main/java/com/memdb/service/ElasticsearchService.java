package com.memdb.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.FuzzyQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.memdb.model.Mem;
import com.memdb.repository.MemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ElasticsearchService {

    private final MemRepository memRepository;
    private final ElasticsearchClient elasticsearchClient;

    public void saveMem(String imageId, String type, String description) {
        memRepository.save(new Mem(imageId, type, description));
    }

    public List<Mem> search(String description, Integer count) {
        FuzzyQuery query1 = new FuzzyQuery.Builder().field("description")
                .value(description)
                .build();
        Query query2 = Query.of(q -> q.fuzzy(query1));
        try {
            return elasticsearchClient.search(s -> s.index("mem").query(query2), Mem.class)
                    .hits().hits().stream()
                    .limit(count)
                    .map(Hit::source)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Mem searchOne(String description) {
        FuzzyQuery query1 = new FuzzyQuery.Builder().field("description")
                .value(description)
                .build();
        Query query2 = Query.of(q -> q.fuzzy(query1));
        try {
            return elasticsearchClient.search(s -> s.index("mem").query(query2), Mem.class)
                    .hits().hits().stream()
                    .map(Hit::source)
                    .findFirst().get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
