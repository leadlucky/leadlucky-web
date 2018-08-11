package com.leadlucky.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.domain.Persistable

import javax.persistence.*
import java.util.Date

class CollectedEmail implements Persistable<String>{

    @JsonIgnore
    String id

    @JsonIgnore
    String pageName

    String email

    @CreatedDate
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Date createdDate

    @Override
    boolean isNew() {
        return createdDate == null
    }

}