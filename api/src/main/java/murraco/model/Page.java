package murraco.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    @ManyToOne
    @JsonIgnore
    private User owner;

    @Size(min = 3, max = 255)
    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String themeName;

    @Size(max = 5000)
    private String data;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "page")
    @JsonIgnore
    private List<CollectedEmail> emails = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "page")
    @JsonIgnore
    private List<PageView> views = new ArrayList<>();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public int getViewCount(){
        return views.size();
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public int getEmailCount(){
        return emails.size();
    }

    public void addView(PageView pageView) {
        pageView.setPage(this);
        views.add(pageView);
    }

    public void addEmail(CollectedEmail email) {
        email.setPage(this);
        emails.add(email);
    }

}

