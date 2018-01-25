package com.git.search.Objects;

import java.io.Serializable;

/**
 * Created by mayank on 18/01/2018.
 */

public class RepoObject implements Serializable {

    public String id="",name="",full_name="",watchers="",watchers_count="",url="",contributors_url="",description="",stargazers_count="",forks_count="";

    public OwnerObject owner=new OwnerObject();


    @Override
    public String toString() {
        return "RepoObject{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", full_name='" + full_name + '\'' +
                ", watchers='" + watchers + '\'' +
                ", watchers_count='" + watchers_count + '\'' +
                ", url='" + url + '\'' +
                ", contributors_url='" + contributors_url + '\'' +
                ", owner=" + owner +
                '}';
    }
}
