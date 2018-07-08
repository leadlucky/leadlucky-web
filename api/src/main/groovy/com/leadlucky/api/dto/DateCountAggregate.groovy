package com.leadlucky.api.dto

class DateCountAggregate {

    DateCountAggregate(){

    }

    DateCountAggregate(long count, Date timestamp){
        this.count = count
        this.timestamp = timestamp
    }

    long count
    Date timestamp

}
