package murraco.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class PageView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private long id;

    @ManyToOne
    @JsonIgnore
    private Page page;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date createdDate;

    private String ipAddress;

    @PrePersist
    protected void onCreate(){
        createdDate = new Date();
    }
}
