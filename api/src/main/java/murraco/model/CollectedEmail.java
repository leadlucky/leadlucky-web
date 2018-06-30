package murraco.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class CollectedEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private long id;

    @Column(nullable = false)
    private String email;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date createdDate;

    @ManyToOne
    @JsonIgnore
    private Page page;

    @PrePersist
    protected void onCreate(){
        createdDate = new Date();
    }

}
