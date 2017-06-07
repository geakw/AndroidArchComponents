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
LifecycleOwner是只有一个 getLifecycle()方法的接口，表示继承该接口的类拥有生命周期。一般的实现是在调用某个回调时会判断下当前的Life这样之前的定位代码可以重写为：

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
###ViewModel
###项目中使用Architecture components中的Lifecycles

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