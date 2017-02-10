package com.hm.camerademo.network;

import android.text.TextUtils;

import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by shucc on 17/1/22.
 * cc@cchao.org
 */
public class NetWork {

    private static Api api;

    public static Api getApi() {
        if (api == null) {
            api = RetrofitUtil.create(Api.class);
        }
        return api;
    }

    public static <T> Observable<T> getData(Map body, final Class<T> classType) {
        return getApi().getData(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<HttpResult<Object>, Observable<Object>>() {
                    @Override
                    public Observable<Object> call(HttpResult<Object> objectHttpResult) {
                        return flatResponse(objectHttpResult);
                    }
                })
                .map(new Func1<Object, T>() {
                    @Override
                    public T call(Object o) {
                        String temp = JsonUtil.toString(o);
                        if (TextUtils.isEmpty(temp) || "[]".equals(temp)) {
                            return null;
                        }
                        return JsonUtil.toObject(JsonUtil.toString(o), classType);
                    }
                });
    }

    /**
     * 默认返回Object
     * @param body
     * @return
     */
    public static Observable<Object> getData(Map body) {
        return getApi().getData(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<HttpResult<Object>, Observable<Object>>() {
                    @Override
                    public Observable<Object> call(HttpResult<Object> objectHttpResult) {
                        return flatResponse(objectHttpResult);
                    }
                });
    }

    /**
     * 对网络接口返回的Response进行分割操作
     *
     * @param response
     * @param <T>
     * @return
     */
    public static <T> Observable<T> flatResponse(final HttpResult<T> response) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                if (response.isSuccess()) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(response.getBody());
                    }
                } else {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(new APIException(response.getResultCode(), response.getResultMessage()));
                    }
                    return;
                }
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }

            }
        });
    }

    /**
     * 自定义异常，当接口返回的{link Response#code}不为{link Constant#RESULT_SUCCESS}时，需要跑出此异常
     * eg：登陆时验证码错误；参数为传递等
     */
    public static class APIException extends Exception {

        public int code;

        public String message;

        public APIException(int code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }

        public int getCode() {
            return code;
        }
    }
}
