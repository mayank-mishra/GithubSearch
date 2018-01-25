package com.git.search.Objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayank on 18/01/2018.
 */

public class ResponseObject implements Serializable{
    public List<RepoObject> items=new ArrayList<RepoObject>();
}
