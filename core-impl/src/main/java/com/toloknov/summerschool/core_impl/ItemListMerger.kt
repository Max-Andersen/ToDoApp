package com.toloknov.summerschool.core_impl

import com.toloknov.summerschool.domain.model.TodoItem
import java.time.ZonedDateTime

class ItemListMerger {
    fun merge(
        localData: List<TodoItem>,
        remoteData: List<TodoItem>,
        lastUpdateTime: ZonedDateTime
    ): List<TodoItem> {

        val remoteMap = remoteData.associateBy { item -> item.id }

        val compareMap = localData.associateWith { localItem -> remoteMap[localItem.id] }

        val remoteRemainder = remoteData - localData.toSet()

        val resultData = mutableListOf<TodoItem>()

        compareMap.forEach { (localItem, remoteItem) ->
            if (remoteItem == null) {
                if (localItem.updateTs >= lastUpdateTime) {
                    resultData.add(localItem)
                }
            } else {
                val itemToSync =
                    if (localItem.updateTs >= remoteItem.updateTs) localItem else remoteItem
                resultData.add(itemToSync)
            }
        }

        resultData.addAll(remoteRemainder.filter { it.updateTs >= lastUpdateTime })

        return resultData
    }
}