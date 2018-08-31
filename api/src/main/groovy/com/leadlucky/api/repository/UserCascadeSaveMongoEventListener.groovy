package com.leadlucky.api.repository

import com.leadlucky.api.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent

class UserCascadeSaveMongoEventListener extends AbstractMongoEventListener<User> {

    @Autowired
    private MongoOperations mongoOperations

    @Override
    void onBeforeConvert(BeforeConvertEvent<User> event) {
        event.getSource().with {
            pages.forEach { page ->
                page.ownerUsername = username
                mongoOperations.save(page)
            }
        }
    } 

}
