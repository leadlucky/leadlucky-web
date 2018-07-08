package com.leadlucky.api.model

import com.fasterxml.jackson.annotation.JsonIgnore


import javax.persistence.*
import javax.validation.constraints.Size
import java.util.ArrayList
import java.util.List

@Entity
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id


    @ElementCollection(fetch = FetchType.EAGER)
    List<Role> roles

    @Size(min = 4, max = 255, message = "Minimum username length: 4 characters")
    @Column(unique = true, nullable = false)
    String username

    @Column(unique = true, nullable = false)
    String email

    @Column(unique = true)
    @JsonIgnore
    String stripeCustomerId

    @Column
    String premiumStatus

    @Size(min = 8, message = "Minimum password length: 8 characters")
    String password

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
    List<Page> pages = new ArrayList<>()


    void addPage(Page page) {
        page.owner = this
        this.pages.add(page)
    }
}
