package com.ifs21014.lostfounds.helper

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.ifs18005.delcomtodo.data.remote.response.LostFoundsItemResponse
import com.ifs21014.lostfounds.data.local.entity.DelcomLostFoundEntity
import com.ifs21014.lostfounds.data.remote.MyResult

class Utils {
    companion object{
        fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
            val observerWrapper = object : Observer<T> {
                override fun onChanged(value: T) {
                    observer(value)
                    if (value is MyResult.Success<*> || value is MyResult.Error) {
                        removeObserver(this)
                    }
                }
            }
            observeForever(observerWrapper)
        }

        fun entitiesToResponses(entities: List<DelcomLostFoundEntity>):
                List<LostFoundsItemResponse> {
            val responses = ArrayList<LostFoundsItemResponse>()
            entities.map {
                val response = LostFoundsItemResponse(
                    cover = it.cover,
                    updatedAt = it.updatedAt,
                    description = it.description,
                    createdAt = it.createdAt,
                    id = it.id,
                    title = it.title,
                    isCompleted = it.isCompleted,
                    isMe = 1,
                    status = it.status
                )
                responses.add(response)
            }
            return responses
        }
    }
}



