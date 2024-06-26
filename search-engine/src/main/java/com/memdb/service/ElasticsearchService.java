package com.memdb.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.memdb.model.Mem;
import com.memdb.repository.MemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ElasticsearchService {

    private final MemRepository memRepository;
    private final ElasticsearchClient elasticsearchClient;

    public void saveMem(String imageId, String type, String description) {
        memRepository.save(new Mem(imageId, type, description, Collections.emptyList()));
    }

    public List<Mem> search(String description, Integer count) {
        Query matchQuery1 = Query.of(q -> q.match(new MatchQuery.Builder().field("description")
                .query(description)
                .fuzziness("1")
                .analyzer("russian")
                .autoGenerateSynonymsPhraseQuery(true)
                .build()));
        Query matchQuery2 = Query.of(q -> q.match(new MatchQuery.Builder().field("description")
                .query(description)
                .fuzziness("1")
                .build()));
        BoolQuery boolQuery = new BoolQuery.Builder().
                should(List.of(matchQuery1, matchQuery2))
                .build();

        Query matchQuery_ = Query.of(q -> q.bool(boolQuery));
        try {
            return elasticsearchClient.search(s -> s.index("mem").query(matchQuery_), Mem.class)
                    .hits().hits().stream()
                    .limit(count)
                    .map(Hit::source)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String id) {
        DeleteRequest deleteRequest = DeleteRequest.of(d -> d.index("mem").id(id));
        try {
            elasticsearchClient.delete(deleteRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
