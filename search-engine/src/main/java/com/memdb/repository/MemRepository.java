package com.memdb.repository;

import com.memdb.model.Mem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemRepository extends ElasticsearchRepository<Mem, String> {

    List<Mem> findAllByDescription(String description);


    Mem findByDescription(String description);


}
