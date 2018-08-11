package com.leadlucky.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.gson.JsonObject
import org.springframework.data.annotation.Id

import javax.validation.constraints.NotEmpty

class Page {

    @Id
    @JsonIgnore
    String id

    @JsonIgnore
    String ownerUsername

    @NotEmpty
    String name

    @NotEmpty
    String themeName

    Map<String, Object> data

}
