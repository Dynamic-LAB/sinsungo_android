package com.dlab.sinsungo.data.repository

import com.dlab.sinsungo.data.RemoteDataSource

object NotificationRepository {
    private val service = RemoteDataSource.service

    suspend fun getNotification(refId: Int) = service.getNotification(refId)
}
