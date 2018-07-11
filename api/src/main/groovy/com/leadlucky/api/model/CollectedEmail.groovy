package com.leadlucky.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty


import javax.persistence.*
import java.util.Date

@Entity
class CollectedEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    long id

    @Column(nullable = false)
    String email

    String gaClientId

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Date createdDate

    @ManyToOne
    @JsonIgnore
    Page page

    @PrePersist
    protected void onCreate(){
        createdDate = new Date()
    }

}