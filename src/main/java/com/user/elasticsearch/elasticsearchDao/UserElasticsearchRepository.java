package com.user.elasticsearch.elasticsearchDao;

import com.user.entity.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserElasticsearchRepository extends ElasticsearchRepository<User,String> {

    /*
    *
    * */
}