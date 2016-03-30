package com.cegeka.tetherj.pojo;

import java.io.Serializable;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class UserDocMethods implements Serializable {

    private static final long serialVersionUID = -3872541122451142285L;

    private HashMap<String, UserDocMethod> methods;

    public UserDocMethods() {
        methods = new HashMap<>();
    }

    @JsonAnySetter
    public void setUserDocMethod(String name, UserDocMethod method) {
        methods.put(name, method);
    }

    @JsonAnyGetter
    public HashMap<String, UserDocMethod> getMethods() {
        return methods;
    }
}
