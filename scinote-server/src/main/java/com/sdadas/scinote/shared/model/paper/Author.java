package com.sdadas.scinote.shared.model.paper;

import java.io.Serializable;

/**
 * @author Sławomir Dadas
 */
public class Author implements Serializable {

    private String id;

    private String firstName;

    private String lastName;

    private String affiliation;

    private Integer order;

    public Author(String firstName, String lastName, int order) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.order = order;
    }

    public Author() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
