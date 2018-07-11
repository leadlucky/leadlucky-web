package com.leadlucky.api.util

class MapUtil {

    static Map merge(Map... sources) {
        if (sources.length == 0) return [:]
        if (sources.length == 1) return sources[0] as Map

        sources.inject([:]) { Map result, Map source ->
            source.each { k, v ->
                result[k] = result[k] instanceof Map ? merge(result[k], v) : v
            }
            result
        } as Map
    }

}
