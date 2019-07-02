package com.user.elasticsearch.elasticsearchService.impl;

import com.user.elasticsearch.elasticsearchDao.UserElasticsearchRepository;
import com.user.elasticsearch.elasticsearchService.UserElasticsearchService;
import com.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "userElasticsearchImpl")
public class UserElasticsearchImpl implements UserElasticsearchService {

    @Autowired
    private UserElasticsearchRepository userElasticsearchRepository;


    public Object test(){
        userElasticsearchRepository.findAll();
        return null;
    }

    public void elasticsearchSave(User user){
        userElasticsearchRepository.save(user);
    }

}
