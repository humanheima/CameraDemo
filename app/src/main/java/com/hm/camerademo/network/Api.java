package com.hm.camerademo.network;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

public interface Api {

    @POST("index.php")
    Observable<HttpResult<Object>> getData(@Body Map body);

    /**
     * 上传头像
     */
    @Multipart
    @POST("http://ylbook.xun-ao.com/api/upload.php")
    Observable<HttpResult<String>> uploadAvatar(@Part MultipartBody.Part file);

}
