package com.dlab.sinsungo

open class Event<out T>(private val content: T) {
    /**
     * ViewModel에서 View로 이벤트 보내야 할 때 쓰는 EventWrapper class
     */
    var hasBeenHandled = false // 처리 확인 프로퍼티
        private set // 외부에서 읽기만 가능


    // 이벤트 처리했으면 null 반환, 안했으면 content 반환하고 프로퍼티 변경
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    // 이벤트 처리 여부 상관없이 content 반환
    fun peekContent(): T = content
}
