package com.ifs21014.lostfounds.presentation.lostfound

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ifs18005.delcomtodo.data.remote.response.DataAddLostFoundResponse

import com.ifs18005.delcomtodo.data.remote.response.DelcomLostFoundResponse
import com.ifs18005.delcomtodo.data.remote.response.DelcomResponse
import com.ifs21014.lostfounds.data.local.entity.DelcomLostFoundEntity

import com.ifs21014.lostfounds.data.remote.MyResult
import com.ifs21014.lostfounds.data.repository.LocalLostFoundRepository
import com.ifs21014.lostfounds.data.repository.LostFoundRepository
import com.ifs21014.lostfounds.presentation.ViewModelFactory
import okhttp3.MultipartBody

class LostFoundViewModel (
    private val lostFoundRepository : LostFoundRepository,
    private val LocalLostFoundRepository: LocalLostFoundRepository
) : ViewModel() {

    fun getLostFound(lostfoundId: Int) : LiveData<MyResult<DelcomLostFoundResponse>> {
        return lostFoundRepository.getDetail(lostfoundId).asLiveData()
    }

    fun postLostFound(
        title: String,
        description : String,
        status: String,
    ) : LiveData<MyResult<DataAddLostFoundResponse>> {
        return lostFoundRepository.postLostFound(
            title,
            description,
            status
        ).asLiveData()
    }

    fun putLostFound(
        lostfoundId: Int,
        title: String,
        description: String,
        status: String,
        isCompleted: Boolean,
    ) : LiveData<MyResult<DelcomResponse>> {
        return lostFoundRepository.putLostFound(
            lostfoundId,
            title,
            description,
            status,
            isCompleted
        ).asLiveData()
    }

    fun delete(lostfoundId: Int) : LiveData<MyResult<DelcomResponse>> {
        return lostFoundRepository.delete(lostfoundId).asLiveData()
    }

    fun getLocalLostFounds(): LiveData<List<DelcomLostFoundEntity>?> {
        return LocalLostFoundRepository.getAllLostFounds()
    }
    fun getLocalLostFound(lostfoundId: Int): LiveData<DelcomLostFoundEntity?> {
        return LocalLostFoundRepository.get(lostfoundId)
    }
    fun insertLocalTodo(todo: DelcomLostFoundEntity) {
        LocalLostFoundRepository.insert(todo)
    }
    fun deleteLocalTodo(todo: DelcomLostFoundEntity) {
        LocalLostFoundRepository.delete(todo)
    }
    fun addCoverLostFound(
        lostfoundId: Int,
        cover: MultipartBody.Part,
    ): LiveData<MyResult<DelcomResponse>> {
        return lostFoundRepository.addCoverLostFound(lostfoundId, cover).asLiveData()
    }

    companion object {
        @Volatile
        private var INSTANCE: LostFoundViewModel? = null
        fun getInstance (
            lostFoundRepository: LostFoundRepository,
            LocalLostFoundRepository: LocalLostFoundRepository,
        ) : LostFoundViewModel {
            synchronized(ViewModelFactory::class.java) {
                INSTANCE = LostFoundViewModel(
                    lostFoundRepository,
                    LocalLostFoundRepository
                )
            }
            return INSTANCE as LostFoundViewModel
        }
    }
}