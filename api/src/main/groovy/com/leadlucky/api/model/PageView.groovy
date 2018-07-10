package com.leadlucky.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.CreatedDate

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.PrePersist

@Entity
class PageView {

    @Id
    @JsonIgnore
    long id

    @ManyToOne
    Page page

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @CreatedDate
    Date createdDate

    String ipAddress

    @PrePersist
    void onCreate(){
        this.createdDate = new Date()
    }


}
