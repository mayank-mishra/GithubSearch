package com.git.search.Objects;

import java.io.Serializable;

/**
 * Created by mayank on 19/01/2018.
 */

public class ContributerResponseObject implements Serializable {

    public String id="",avatar_url="",repos_url="",login="",url="",contributions="";

    @Override
    public String toString() {
        return "ContributerResponseObject{" +
                "id='" + id + '\'' +
                ", avatar_url='" + avatar_url + '\'' +
                ", repos_url='" + repos_url + '\'' +
                ", login='" + login + '\'' +
                ", url='" + url + '\'' +
                ", contributions='" + contributions + '\'' +
                '}';
    }
}
