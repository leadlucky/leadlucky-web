package com.leadlucky.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty


import javax.persistence.*
import javax.validation.constraints.Size
import java.util.ArrayList
import java.util.List

@Entity
class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    long id

    @ManyToOne
    @JsonIgnore
    User owner

    @Size(min = 3, max = 255)
    @Column(unique = true, nullable = false)
    String name

    @Column(nullable = false)
    String themeName

    @Size(max = 5000)
    String data

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "page")
    @JsonIgnore
    List<CollectedEmail> emails = new ArrayList<>()

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    int getEmailCount(){
        return emails.size()
    }

    void addEmail(CollectedEmail email) {
        email.page = this
        emails.add(email)
    }

}
