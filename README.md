## Android架构组件

<font size = 4>[Android架构组件（Android Architecture components)](https://developer.android.com/topic/libraries/architecture/guide.html)是2017谷歌Io大会发布的一个Anroid开发的推荐框架,目前还是Alpha版本。

###Handing Lifecyles

[android.arch.lifecycle](https://developer.android.com/topic/libraries/architecture/lifecycle.html)包内提供了用来创建生命周期可知组件(`lifecycle-aware components`)的接口和类,生命周期可知指的是可以获取到activity和fragment的生命周期方法，并根据生命周期方法执行相应的逻辑方法。

Android中Activity或者fragment的生命周期都是framework层控制的，应用层在适当的生命周期实现相应的逻辑。假如现在有个Activity用来显示当前的位置信息，一般的实现可能是在onStart()方法开启定位，onStop()方法中停止定位，一般的代码实现如下：

	class MyLocationListener {
	    public MyLocationListener(Context context, Callback callback) {
	        // ...
	    }
	
	    void start() {
	        // connect to system location service
	    }
	
	    void stop() {
	        // disconnect from system location service
	    }
	}
	
	class MyActivity extends AppCompatActivity {
	    private MyLocationListener myLocationListener;
	
	    public void onCreate(...) {
	        myLocationListener = new MyLocationListener(this, (location) -> {
	            // update UI
	        });
	  }
	
	    public void onStart() {
	        super.onStart();
	        myLocationListener.start();
	    }
	
	    public void onStop() {
	        super.onStop();
	        myLocationListener.stop();
	    }
	}

这个逻辑还是比较简单的，但在实际的应用中，这些生命周期方法里面可能有很多类似的代码，这些生命周期代码会变得很长。还有一些情况，比如在开启位置监控的时候，可能要异步检查一些设置，而检查设置的回调可能会在onStop()执行之后，也就是说在onStop()执行之后会调用myLocationListener.start()方法，这种情况下定位服务会一直开启
	
	class MyActivity extends AppCompatActivity {
	    private MyLocationListener myLocationListener;
	
	    public void onCreate(...) {
	        myLocationListener = new MyLocationListener(this, location -> {
	            // update UI
	        });
	    }
	
	    public void onStart() {
	        super.onStart();
	        Util.checkUserStatus(result -> {
	            // what if this callback is invoked AFTER activity is stopped?
	            if (result) {
	                myLocationListener.start();
	            }
	        });
	    }
	
	    public void onStop() {
	        super.onStop();
	        myLocationListener.stop();
	    }
	}

[android.arch.lifecycle](https://developer.android.com/topic/libraries/architecture/lifecycle.html)包提供了一些类和接口来解决这种问题，下面接收一下这些类和接口：

###Lifecycle

 [Lifecycle](https://developer.android.com/reference/android/arch/lifecycle/Lifecycle.html)是持有某种组件的生命周期状态信息，比如Activity和Fragment，并且允许其他对象观察这些状态信息。它使用两个枚举来跟踪组件的生命周期状态。
 
	public enum Event {
	        ON_CREATE,
	        ON_START,
	        ON_RESUME,
	        ON_PAUSE,
	        ON_STOP,
	        ON_DESTROY,
	        ON_ANY
	}

	 public enum State {
	        DESTROYED,
	        INITIALIZED,
	        CREATED,
	        STARTED,
	        RESUMED;
	        public boolean isAtLeast(State state) {
	            return compareTo(state) >= 0;
	        }
	    }


<center>![](http://i.imgur.com/xeezBwl.png)</center>

可以把states想象成图的节点，events想象成图的节点之间的边
一个监听具有生命周期的类可以这样写：

	public class MyObserver implements LifecycleObserver {
	    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
	    public void onResume() {
	    }
	
	    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
	    public void onPause() {
	    }
	}
	aLifecycleOwner.getLifecycle().addObserver(new MyObserver());


###LifecycleOwner

LifecycleOwner是只有一个 getLifecycle()方法的接口，表示继承该接口的类拥有生命周期。一般的实现是在调用某个回调时会判断下当前的Lifecycle的当前state是否是合适的状态，这样之前的定位代码可以重写为：

	class MyActivity extends LifecycleActivity {
	    private MyLocationListener myLocationListener;
	
	    public void onCreate(...) {
	        myLocationListener = new MyLocationListener(this, getLifecycle(), location -> {
	            // update UI
	        });
	        Util.checkUserStatus(result -> {
	            if (result) {
	                myLocationListener.enable();
	            }
	        });
	  }
	}
	
	class MyLocationListener implements LifecycleObserver {
	    private boolean enabled = false;
	    public MyLocationListener(Context context, Lifecycle lifecycle, Callback callback) {
	       ...
	    }
	
	    @OnLifecycleEvent(Lifecycle.Event.ON_START)
	    void start() {
	        if (enabled) {
	           // connect
	        }
	    }
	
	    public void enable() {
	        enabled = true;
	        if (lifecycle.getState().isAtLeast(STARTED)) {
	            // connect if not connected
	        }
	    }
	
	    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
	    void stop() {
	        // disconnect if connected
	    }
	}



###LiveData

LiveData是一个data holder，一个里面保存了一个value，并且这个value是可以被观察的，和传统的observable不一样的是，LiveData可以对app组件的生命周期做出响应，LiveData可以指定一个可以观察的Lifecycle，LiveData认为当 Observer的Lifecycle是STARTED或者RESUMED状态的时候，才算是激活状态，再onChaged的时候，才会通知这个Observer。

	public class LocationLiveData extends LiveData<Location> {
	    private LocationManager locationManager;
	
	    private SimpleLocationListener listener = new SimpleLocationListener() {
	        @Override
	        public void onLocationChanged(Location location) {
	            setValue(location);
	        }
	    };
	
	    public LocationLiveData(Context context) {
	        locationManager = (LocationManager) context.getSystemService(
	                Context.LOCATION_SERVICE);
	    }
	
	    @Override
	    protected void onActive() {
	        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
	    }
	
	    @Override
	    protected void onInactive() {
	        locationManager.removeUpdates(listener);
	    }
	}


onActive()

这个方法会在LiveData有处于Active状态的observer时调用

onInactive()

这个方法会在LiveData没有任何处于Active状态的Observer调用

setValue()

使用这个方法更新LiveData所持有的value值，并通知Active状态的observers value改变了

	public class MyFragment extends LifecycleFragment {
	    public void onActivityCreated (Bundle savedInstanceState) {
	        LiveData<Location> myLocationListener = ...;
	        Util.checkUserStatus(result -> {
	            if (result) {
	                myLocationListener.Observer(this, location -> {
	                    // update UI
	                });
	            }
	        });
	    }
	}

observer方法的方法声明是public void observe(LifecycleOwner owner, Observer<T> observer)，可以看到第一个参数是LifecycleOwner，这表明这个observer是和一个Lifecycle绑定的在中情况下：

- 如果Lifecycle处于非激活状态，即使在value改变的时候，observer也不会被调用
- 如果Lifecycle处于destroyed，该observer就会被自动移除

LiveData既然是生命周期可知的，我们可以在多个activity和fragments里共享一个LiveData
	
	public class LocationLiveData extends LiveData<Location> {
	    private static LocationLiveData sInstance;
	    private LocationManager locationManager;
	
	    @MainThread
	    public static LocationLiveData get(Context context) {
	        if (sInstance == null) {
	            sInstance = new LocationLiveData(context.getApplicationContext());
	        }
	        return sInstance;
	    }
	
	    private SimpleLocationListener listener = new SimpleLocationListener() {
	        @Override
	        public void onLocationChanged(Location location) {
	            setValue(location);
	        }
	    };
	
	    private LocationLiveData(Context context) {
	        locationManager = (LocationManager) context.getSystemService(
	                Context.LOCATION_SERVICE);
	    }
	
	    @Override
	    protected void onActive() {
	        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
	    }
	
	    @Override
	    protected void onInactive() {
	        locationManager.removeUpdates(listener);
	    }
	}
	
	public class MyFragment extends LifecycleFragment {
	    public void onActivityCreated (Bundle savedInstanceState) {
	        Util.checkUserStatus(result -> {
	            if (result) {
	                LocationLiveData.get(getActivity()).observe(this, location -> {
	                   // update UI
	                });
	            }
	        });
	  }
	}

LiveData还支持转换：

	LiveData<User> userLiveData = ...;
	LiveData<String> userName = Transformations.map(userLiveData, user -> {
	    user.name + " " + user.lastName
	});

LiveData有以下几个优点：

- 没有内存泄露：因为每个observer都绑定到它们自己的Lifecycle对象，当Lifecycle状态是destroyed的时候会自动清除的个observer
- stop activity的时候不会引起crash：如果observer的Lifcycle处于inactive状态的时候，不会收到change事件
- 始终保持最新的数据:如果Lifecycle从inactive到active时，会收到最新的data
- 如果activity或者fragment重新创建，比如屏幕旋转的时候，它会立即收到最新的data
- 资源共享：比如定位，可以实现一个LocationListener，仅连接一次服务，就可以为app中的所有observer提供数据
- 不用再手动的处理生命周期事件：在需要观察数据的时候，不用担心start或者stop的调用，LiveData自动的处理了这些流程

###ViewModel

ViewModel是用来存储和管理ui相关的数据，以保证在configuration改变的时候，数据也能存活下来
activity和fragment都有被Android Framework管理的生命周期，fragment可以决定，什么什么时候destroy或者re-created它们。这样的话在activity或者fragmnet中保存的数据就会丢失。比如activity中有一个users list，当activity重建或者configuration改变的时候，新的activity就得重新获取user list。Activity虽然可以用 onSaveInstanceState()来存储数据，然后再Oncreate方法中从bundle恢复数据，但是这种方式只适合简单的数据，不太适合大量的数据。

另一个问题是这些ui控件经常需要做一些比较耗时的异步操作，需要的destroy的时候清理这些异步操作，以防止内存泄露。而且在重建这些节目的时候，会重新发起相同的请求。
这些ui组件也需要处理用户交互操作，和与操作系统交互，一个类可能要处理很多的工作而不是把工作代理的其他类里面，导致类里面代码量膨胀，可维护性变差。

	public class MyViewModel extends ViewModel {
	    private MutableLiveData<List<User>> users;
	    public LiveData<List<User>> getUsers() {
	        if (users == null) {
	            users = new MutableLiveData<List<Users>>();
	            loadUsers();
	        }
	        return users;
	    }
	
	    private void loadUsers() {
	        // do async operation to fetch users
	    }
	}

	public class MyActivity extends AppCompatActivity {
	    public void onCreate(Bundle savedInstanceState) {
	        MyViewModel model = ViewModelProviders.of(this).get(MyViewModel.class);
	        model.getUsers().observe(this, users -> {
	            // update UI
	        });
	    }
	}
当activity重建的时候，它获取到上一个activity创建的MyViewModel对象，当activity finished的时候，framework会调用ViewModel的onClear()方法来释放资源

###在Fragment之间共享数据

	public class SharedViewModel extends ViewModel {
	    private final MutableLiveData<Item> selected = new MutableLiveData<Item>();
	
	    public void select(Item item) {
	        selected.setValue(item);
	    }
	
	    public LiveData<Item> getSelected() {
	        return selected;
	    }
	}
	
	public class MasterFragment extends Fragment {
	    private SharedViewModel model;
	    public void onActivityCreated() {
	        model = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
	        itemSelector.setOnClickListener(item -> {
	            model.select(item);
	        });
	    }
	}
	
	public class DetailFragment extends LifecycleFragment {
	    public void onActivityCreated() {
	        SharedViewModel model = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
	        model.getSelected().observe(this, { item ->
	           // update UI
	        });
	    }
	}


###ViewModel的生命周期

<center>![](http://i.imgur.com/iQvS6ze.png)</center>

#项目中使用Architecture components中的Lifecycles

	allprojects {
	    repositories {
	        jcenter()
	        maven { url 'https://maven.google.com' }
	    }
	}

	dependencies{
	    compile "android.arch.lifecycle:runtime:1.0.0-alpha1"
	    compile "android.arch.lifecycle:extensions:1.0.0-alpha1"
	    annotationProcessor "android.arch.lifecycle:compiler:1.0.0-alpha1"
	}
</font>