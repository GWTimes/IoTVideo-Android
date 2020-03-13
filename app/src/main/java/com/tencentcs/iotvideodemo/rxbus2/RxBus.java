package com.tencentcs.iotvideodemo.rxbus2;


import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * RxBus
 * Created by gorden on 2016/5/12.
 * update 2017/3/1
 */
@SuppressWarnings("unused")
public class RxBus {

    public static final String LOG_BUS = "RXBUS_LOG";
    private static volatile RxBus sDefaultInstance;

    private Map<Class, CopyOnWriteArrayList<Disposable>> mSubscriptionsByEventType = new ConcurrentHashMap<>();

    private Map<Object, CopyOnWriteArrayList<Class>> mEventTypesBySubscriber = new ConcurrentHashMap<>();

    private Map<Class, CopyOnWriteArrayList<SubscriberMethod>> mSubscriberMethodByEventType = new ConcurrentHashMap<>();

    private final Subject<Object> mBus;

    private RxBus() {
        this.mBus = PublishSubject.create().toSerialized();
    }

    public static RxBus getDefault() {
        if (sDefaultInstance == null) {
            synchronized (RxBus.class) {
                if (sDefaultInstance == null) {
                    sDefaultInstance = new RxBus();
                }
            }
        }
        return sDefaultInstance;
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     *
     * @param eventType 事件类型
     * @return return
     */
    public <T> Flowable<T> toObservable(Class<T> eventType) {
        return mBus.toFlowable(BackpressureStrategy.BUFFER).ofType(eventType);
    }

    /**
     * 根据传递的code和 eventType 类型返回特定类型(eventType)的 被观察者
     *
     * @param code      事件code
     * @param eventType 事件类型
     */
    private <T> Flowable<T> toObservable(final int code, final Class<T> eventType) {
        return mBus.toFlowable(BackpressureStrategy.BUFFER).ofType(Message.class)
                .filter(new Predicate<Message>() {
                    @Override
                    public boolean test(Message o) throws Exception {
                        return o.getCode() == code && eventType.isInstance(o.getObject());
                    }
                }).map(new Function<Message, Object>() {
                    @Override
                    public Object apply(Message o) throws Exception {
                        return o.getObject();
                    }
                }).cast(eventType);
    }

    /**
     * 注册
     *
     * @param subscriber 订阅者
     */
    public void register(Object subscriber) {
        if (isRegistered(subscriber)) {
            return;
        }
        Class<?> subClass = subscriber.getClass();
        Method[] methods = subClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Subscribe.class)) {
                //获得参数类型
                Class[] parameterType = method.getParameterTypes();
                //参数不为空 且参数个数为1
                if (parameterType != null && parameterType.length == 1) {

                    Class eventType = parameterType[0];

                    addEventTypeToMap(subscriber, eventType);
                    Subscribe sub = method.getAnnotation(Subscribe.class);
                    int code = sub.code();
                    ThreadMode threadMode = sub.threadMode();

                    SubscriberMethod subscriberMethod = new SubscriberMethod(subscriber, method, eventType, code, threadMode);
                    addSubscriberToMap(eventType, subscriberMethod);

                    addSubscriber(subscriberMethod);
                } else if (parameterType == null || parameterType.length == 0) {

                    Class eventType = BusData.class;

                    addEventTypeToMap(subscriber, eventType);
                    Subscribe sub = method.getAnnotation(Subscribe.class);
                    int code = sub.code();
                    ThreadMode threadMode = sub.threadMode();

                    SubscriberMethod subscriberMethod = new SubscriberMethod(subscriber, method, eventType, code, threadMode);
                    addSubscriberToMap(eventType, subscriberMethod);

                    addSubscriber(subscriberMethod);

                }
            }
        }
    }


    /**
     * 将event的类型以订阅中subscriber为key保存到map里
     *
     * @param subscriber 订阅者
     * @param eventType  event类型
     */
    private void addEventTypeToMap(Object subscriber, Class eventType) {
        CopyOnWriteArrayList<Class> eventTypes = mEventTypesBySubscriber.get(subscriber);
        if (eventTypes == null) {
            eventTypes = new CopyOnWriteArrayList<>();
            mEventTypesBySubscriber.put(subscriber, eventTypes);
        }

        if (!eventTypes.contains(eventType)) {
            eventTypes.add(eventType);
        }
    }

    /**
     * 将注解方法信息以event类型为key保存到map中
     *
     * @param eventType        event类型
     * @param subscriberMethod 注解方法信息
     */
    private void addSubscriberToMap(Class eventType, SubscriberMethod subscriberMethod) {
        CopyOnWriteArrayList<SubscriberMethod> subscriberMethods = mSubscriberMethodByEventType.get(eventType);
        if (subscriberMethods == null) {
            subscriberMethods = new CopyOnWriteArrayList<>();
            mSubscriberMethodByEventType.put(eventType, subscriberMethods);
        }

        if (!subscriberMethods.contains(subscriberMethod)) {
            subscriberMethods.add(subscriberMethod);
        }
    }

    /**
     * 将订阅事件以event类型为key保存到map,用于取消订阅时用
     *
     * @param eventType  event类型
     * @param disposable 订阅事件
     */
    private void addSubscriptionToMap(Class eventType, Disposable disposable) {
        CopyOnWriteArrayList<Disposable> disposables = mSubscriptionsByEventType.get(eventType);
        if (disposables == null) {
            disposables = new CopyOnWriteArrayList<>();
            mSubscriptionsByEventType.put(eventType, disposables);
        }

        if (!disposables.contains(disposable)) {
            disposables.add(disposable);
        }
    }

    /**
     * 用RxJava添加订阅者
     *
     * @param subscriberMethod d
     */
    @SuppressWarnings("unchecked")
    private void addSubscriber(final SubscriberMethod subscriberMethod) {
        Flowable flowable;
        if (subscriberMethod.code == -1) {
            flowable = toObservable(subscriberMethod.eventType);
        } else {
            flowable = toObservable(subscriberMethod.code, subscriberMethod.eventType);
        }
        Disposable subscription = postToObservable(flowable, subscriberMethod)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        callEvent(subscriberMethod, o);
                    }
                });

        addSubscriptionToMap(subscriberMethod.subscriber.getClass(), subscription);
    }

    /**
     * 用于处理订阅事件在那个线程中执行
     *
     * @param observable       d
     * @param subscriberMethod d
     * @return Observable
     */
    private Flowable postToObservable(Flowable observable, SubscriberMethod subscriberMethod) {
        Scheduler scheduler;
        switch (subscriberMethod.threadMode) {
            case MAIN:
                scheduler = AndroidSchedulers.mainThread();
                break;

            case NEW_THREAD:
                scheduler = Schedulers.newThread();
                break;

            case CURRENT_THREAD:
                scheduler = Schedulers.trampoline();
                break;
            default:
                throw new IllegalStateException("Unknown thread mode: " + subscriberMethod.threadMode);
        }
        return observable.observeOn(scheduler);
    }

    /**
     * 回调到订阅者的方法中
     *
     * @param method code
     * @param object obj
     */
    private void callEvent(SubscriberMethod method, Object object) {
        Class eventClass = object.getClass();
        CopyOnWriteArrayList<SubscriberMethod> methods = mSubscriberMethodByEventType.get(eventClass);
        if (methods != null && methods.size() > 0) {
            ListIterator<SubscriberMethod> listIterator = methods.listIterator();
            while (listIterator.hasNext()) {
                SubscriberMethod subscriberMethod = listIterator.next();
                Subscribe sub = subscriberMethod.method.getAnnotation(Subscribe.class);
                int c = sub.code();
                if (c == method.code && method.subscriber.equals(subscriberMethod.subscriber) && method.method.equals(subscriberMethod.method)) {
                    subscriberMethod.invoke(object);
                }
            }
        }
    }


    /**
     * 是否注册
     *
     * @param subscriber
     * @return
     */
    public synchronized boolean isRegistered(Object subscriber) {
        return mEventTypesBySubscriber.containsKey(subscriber);
    }

    /**
     * 取消注册
     *
     * @param subscriber object
     */
    public void unregister(Object subscriber) {
        CopyOnWriteArrayList<Class> subscribedTypes = mEventTypesBySubscriber.get(subscriber);
        if (subscribedTypes != null) {
            for (Class<?> eventType : subscribedTypes) {
                unSubscribeByEventType(subscriber.getClass());
                unSubscribeMethodByEventType(subscriber, eventType);
            }
            mEventTypesBySubscriber.remove(subscriber);
        }
    }

    /**
     * subscriptions unsubscribe
     *
     * @param eventType eventType
     */
    private void unSubscribeByEventType(Class eventType) {
        CopyOnWriteArrayList<Disposable> disposables = mSubscriptionsByEventType.get(eventType);
        if (disposables != null) {
            Iterator<Disposable> iterator = disposables.iterator();
            while (iterator.hasNext()) {
                Disposable disposable = iterator.next();
                if (disposable != null && !disposable.isDisposed()) {
                    disposable.dispose();
                    disposables.remove(disposable);
                }
            }
        }
    }

    /**
     * 移除subscriber对应的subscriberMethods
     *
     * @param subscriber subscriber
     * @param eventType  eventType
     */
    private void unSubscribeMethodByEventType(Object subscriber, Class eventType) {
        CopyOnWriteArrayList<SubscriberMethod> subscriberMethods = mSubscriberMethodByEventType.get(eventType);
        if (subscriberMethods != null) {
            Iterator<SubscriberMethod> iterator = subscriberMethods.iterator();
            while (iterator.hasNext()) {
                SubscriberMethod subscriberMethod = iterator.next();
                if (subscriberMethod.subscriber.equals(subscriber)) {
                    subscriberMethods.remove(subscriberMethod);
                }
            }
        }
    }

    public void send(int code, Object o) {
        mBus.onNext(new Message(code, o));
    }

    public void post(Object o) {
        mBus.onNext(o);
    }

    public void send(int code) {
        mBus.onNext(new Message(code, new BusData()));
    }

    private class Message {

        private int code;
        private Object object;

        public Message() {
        }

        private Message(int code, Object o) {
            this.code = code;
            this.object = o;
        }

        private int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        private Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }
    }
}
