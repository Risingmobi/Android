package com.alex.tur.data.net;

import android.content.Context;
import android.support.annotation.NonNull;

import com.alex.tur.R;
import com.alex.tur.error.MyException;

import org.reactivestreams.Publisher;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import timber.log.Timber;

public class RxErrorHandlingCallAdapterFactory extends CallAdapter.Factory {

    private final Context appContext;

    private final RxJava2CallAdapterFactory originalAdapter;

    private RxErrorHandlingCallAdapterFactory(Context appContext, Scheduler scheduler) {
        this.appContext = appContext;
        originalAdapter = RxJava2CallAdapterFactory.createWithScheduler(scheduler);
    }

    public static RxErrorHandlingCallAdapterFactory createWithScheduler(Context context, Scheduler scheduler) {
        return new RxErrorHandlingCallAdapterFactory(context, scheduler);
    }




    @Override
    public CallAdapter<?, ?> get(@NonNull Type returnType, @NonNull Annotation[] annotations, @NonNull Retrofit retrofit) {
        return new RxCallAdapterWrapper(appContext, originalAdapter.get(returnType, annotations, retrofit));
    }







    private static class RxCallAdapterWrapper<T> implements CallAdapter<T, Object> {

        private final Context appContext;

        private final CallAdapter<T, ?> wrapped;

        public RxCallAdapterWrapper(Context appContext, CallAdapter<T, ?> wrapped) {
            this.appContext = appContext;
            this.wrapped = wrapped;
        }

        @Override
        public Type responseType() {
            return wrapped.responseType();
        }

        @Override
        public Object adapt(@NonNull Call<T> call) {
            Object adaptedCall = wrapped.adapt(call);

            if (adaptedCall instanceof Completable) {
                return ((Completable) adaptedCall).onErrorResumeNext(new Function<Throwable, CompletableSource>() {
                    @Override
                    public CompletableSource apply(Throwable throwable) throws Exception {
                        return Completable.error(asRetrofitException(throwable));
                    }
                });
            } else if (adaptedCall instanceof Single) {
                return ((Single) adaptedCall).onErrorResumeNext(new Function<Throwable, SingleSource>() {
                    @Override
                    public SingleSource apply(Throwable throwable) throws Exception {
                        return Single.error(asRetrofitException(throwable));
                    }
                });
            } else if (adaptedCall instanceof Observable) {
                return ((Observable) adaptedCall).onErrorResumeNext(new Function<Throwable, ObservableSource>() {
                    @Override
                    public ObservableSource apply(Throwable throwable) throws Exception {
                        return Observable.error(asRetrofitException(throwable));
                    }
                });
            } else if (adaptedCall instanceof Flowable) {
                return ((Flowable) adaptedCall).onErrorResumeNext(new Function<Throwable, Publisher>() {
                    @Override
                    public Publisher apply(Throwable throwable) throws Exception {
                        return Flowable.error(asRetrofitException(throwable));
                    }
                });
            } else if (adaptedCall instanceof Maybe) {
                return ((Maybe) adaptedCall).onErrorResumeNext(new Function<Throwable, MaybeSource>() {
                    @Override
                    public MaybeSource apply(Throwable throwable) throws Exception {
                        return Maybe.error(asRetrofitException(throwable));
                    }
                });
            }

            throw new RuntimeException("Observable Type not supported");
        }

        private MyException asRetrofitException(Throwable throwable) {

            int messageRes = R.string.some_error;

            if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;
                Response response = httpException.response();
                switch (response.code()) {
                    case 500: {
                        messageRes = R.string.internal_server_error;
                        break;
                    }
                    case 400: {
                        try {
                            Timber.d("errpr %s", ((HttpException) throwable).response().errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        messageRes = R.string.internal_server_error;
                        break;
                    }
                }
            } else if (throwable instanceof UnknownHostException || throwable instanceof SocketTimeoutException) {
                messageRes = R.string.error_connection;
            }

            MyException myException = new MyException(throwable);
            myException.setLocalizedMessage(appContext.getString(messageRes));
            return myException;
        }
    }
}