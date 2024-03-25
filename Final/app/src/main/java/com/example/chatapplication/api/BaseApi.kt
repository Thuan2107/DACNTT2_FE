package com.example.chatapplication.api

import com.example.chatapplication.cache.CurrentUser
import com.example.chatapplication.cache.UserCache
import com.example.chatapplication.constant.AppConstants
import com.google.gson.Gson
import com.hjq.http.EasyLog
import com.hjq.http.annotation.HttpHeader
import com.hjq.http.annotation.HttpRename
import com.hjq.http.config.IRequestApi
import com.hjq.http.config.IRequestBodyStrategy
import com.hjq.http.config.IRequestHost
import com.hjq.http.config.IRequestInterceptor
import com.hjq.http.config.IRequestType
import com.hjq.http.model.HttpHeaders
import com.hjq.http.model.HttpParams
import com.hjq.http.model.RequestBodyType
import com.hjq.http.request.HttpRequest
import org.json.JSONObject


open class BaseApi : IRequestApi, IRequestType, IRequestHost, IRequestInterceptor  {
    override fun getApi(): String {
        return ""
    }

    @HttpHeader
    @HttpRename("Method")
    var method: Int = AppConstants.HTTP_METHOD_GET

    @HttpHeader
    @HttpRename("Authorization")
    var authorization: String = UserCache.getAccessToken()
    override fun getBodyType(): IRequestBodyStrategy {
        return RequestBodyType.JSON
    }

    override fun getHost():String{
        return AppConstants.IP_ADDRESS
    }

    override fun interceptArguments(
        httpRequest: HttpRequest<*>,
        params: HttpParams,
        headers: HttpHeaders
    ) {
        super.interceptArguments(httpRequest, params, headers)
        EasyLog.printJson(httpRequest,JSONObject(params.params).toString())
    }
}