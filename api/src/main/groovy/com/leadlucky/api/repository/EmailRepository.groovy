package com.leadlucky.api.repository

import com.leadlucky.api.model.CollectedEmail
import org.springframework.data.repository.CrudRepository

interface EmailRepository extends CrudRepository<CollectedEmail, Long> {



}