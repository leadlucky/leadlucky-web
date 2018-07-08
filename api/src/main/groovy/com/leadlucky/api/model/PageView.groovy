package com.leadlucky.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty




import javax.persistence.*
import java.util.Date

@Entity
class PageView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    long id

    @ManyToOne
    @JsonIgnore
    Page page

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Date createdDate

    String ipAddress

    @PrePersist
    protected void onCreate(){
        createdDate = new Date()
    }
}
