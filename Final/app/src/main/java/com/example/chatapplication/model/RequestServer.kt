package com.example.chatapplication.model

import com.example.chatapplication.constant.AppConstants
import com.hjq.http.config.IRequestBodyStrategy
import com.hjq.http.config.IRequestServer
import com.hjq.http.model.RequestBodyType


class RequestServer : IRequestServer {

    override fun getHost(): String {
        return AppConstants.IP_ADDRESS
    }

    fun getPath(): String {
        return "api/"
    }

    fun getType(): IRequestBodyStrategy {
        // 以表单的形式提交参数
        return RequestBodyType.JSON
    }
}