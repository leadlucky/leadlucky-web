package murraco.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roles;

    @Size(min = 4, max = 255, message = "Minimum username length: 4 characters")
    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    @JsonIgnore
    private String stripeCustomerId;

    @Column
    private String premiumStatus;

    @Size(min = 8, message = "Minimum password length: 8 characters")
    private String password;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
    private List<Page> pages = new ArrayList<>();


    public void addPage(Page page) {
        page.setOwner(this);
        this.pages.add(page);
    }
}
