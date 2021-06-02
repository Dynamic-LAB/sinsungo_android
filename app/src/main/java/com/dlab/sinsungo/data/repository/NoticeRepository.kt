package com.dlab.sinsungo.data.repository

import com.dlab.sinsungo.data.RemoteDataSource

object NoticeRepository {
    private val service = RemoteDataSource.service

    suspend fun getNotice() = service.getNotice()
}
