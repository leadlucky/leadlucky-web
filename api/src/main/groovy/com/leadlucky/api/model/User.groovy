package com.leadlucky.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    String id

    @NotBlank
    @Size(min = 4, message = "Minimum password length: 4 characters")
    @Column(unique = true, nullable = false)
    String username

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, message = "Minimum password length: 8 characters")
    String password

    @NotBlank
    @Column(unique = true, nullable = false)
    String email

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    boolean emailVerified

    @JsonIgnore
    String emailToken

    @JsonIgnore
    String stripeCustomerId

    String premiumStatus

    @ElementCollection(fetch = FetchType.EAGER)
    List<Role> roles = []

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
    List<Page> pages = []

    void addPage(Page page) {
        page.owner = this
        this.pages.add(page)
    }
}