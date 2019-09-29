package com.user.vo;

import com.user.entity.Article;

public class GetElasticsearchResultVo {

    private Article result;
    private String id;
    private String type;
    private String shardInfo;
    private String version;

    public Article getResult() {
        return result;
    }

    public void setResult(Article result) {
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getShardInfo() {
        return shardInfo;
    }

    public void setShardInfo(String shardInfo) {
        this.shardInfo = shardInfo;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
