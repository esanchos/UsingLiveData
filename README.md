# Using Live Data

This repo will show you some examples regarding to Live Data, this is what we are goint to cover:

  - Simple use of Live Data
  - Using transformations (map and switchMap)
  - Mediator Live Data

# Simple Live Data

```kotlin
fun performSearch(search: String) {
    ApiUtil.searchReddit(search, 20).observe(this, Observer { searchResult ->
        if (searchResult != null) {
            setupRecyclerView(searchResult.data?.children!!)
        }
    })
}
```

  Here we can see a simple example of using Live Data. But, what happens if we call it twice?
  Well, we will have a problem, since the app will be observing the Live Data forever.
  So, a better way to do that is by stopping observing the Live Data after we are done with it:

```kotlin
fun performSearch(search: String) {
    val liveData = ApiUtil.searchReddit(search, 20)
        liveData.observe(this, object : Observer<SearchModel> {
        override fun onChanged(searchResult: SearchModel?) {
            if (searchResult != null) {
                setupRecyclerView(searchResult.data?.children!!)
            }
            liveData.removeObserver(this)
        }
    })
}
```

# Adding Resouce

  What if we want to adding "Loading" feature? Or handle erros in a easy way. A Wrapper class can be handy. (Sorry about the JAVA code)
  
```java
...
public class Resource<T> {
    @NonNull
    public final Status status;
    @Nullable
    public final T data;
    @Nullable
    public final String message;
    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(SUCCESS, data, null);
    }

    public static <T> Resource<T> error(String msg, @Nullable T data) {
        return new Resource<>(ERROR, data, msg);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(LOADING, data, null);
    }
}
```

  And our code would be something like this:

```kotlin
private fun performSearch(search: String) {
    val liveData = ApiUtil.searchReddit(search, 20)
    liveData.observe(this, object : Observer<Resource<SearchModel>> {
        override fun onChanged(searchResult: Resource<SearchModel>?) {
            when (searchResult!!.status) {
                LOADING -> {
                    et_title.visibility = View.VISIBLE
                    et_title.text = "Loading..."
                }
                SUCCESS -> {
                    setupRecyclerView(searchResult.data?.data?.children!!)
                    et_title.visibility = View.GONE
                    liveData.removeObserver(this)
                }
                ERROR -> {
                    et_title.visibility = View.GONE
                    liveData.removeObserver(this)
                }
            }
        }
    })
}
```

# Transformations

## switchMap

  Use this when you want to observe a single Live Data, but it can trigger different Live Data inside of it:

```kotlin
private var searchTransform = Transformations.switchMap(searchRequest) {
    ApiUtil.searchReddit(it, 20)
}

private fun performSearch(search: String) {
    this.searchRequest.value = search
}
```

  So, we can observe now the __searchTransform__, but nothing will happen. The "ApiUtil.searchReddit(it, 20)" will only be executed when __searchRequest__ value is changed. This way, we do not need to remove the obeserver from the Live Data.
  
```kotlin
private fun installSearch() {
    searchTransform.observe(this, Observer { searchResult ->
        when (searchResult!!.status) {
            LOADING -> {
                tv_title.visibility = View.VISIBLE
                tv_title.text = "Loading..."
            }
            SUCCESS -> {
                setupRecyclerView(searchResult.data?.data?.children!!)
                tv_title.visibility = View.GONE
            }
            ERROR -> {
                tv_title.visibility = View.GONE
            }
        }
    })
}
```

## map

  Use this when you want to convert the result of a Live Data into another. For instance, if your API returns a Model with lots of information, but your view is expection only a String of it.
  
```kotlin
private var titleSearchTransform = Transformations.map(searchTransform) {
    if (it?.status == SUCCESS) {
        it?.data?.title
    }
}
```

# Mediator Live Data

  Very usefull when you want to take control over when your Live Data should trigger your observer, also it can handle multiple Live Datas.
  There are too many uses of it, but here I will show the Zip Live Data.
  
  Lets suppose our view needs to call 2 different API to fetch all the data te be shown to the user. To make it simple to the UI, lets make use of Zip Live Data.
  
```kotlin
fun <A, B> zipLiveData(a: LiveData<A>, b: LiveData<B>): LiveData<Pair<A, B>> {
    return MediatorLiveData<Pair<A, B>>().apply {
        var lastA: A? = null
        var lastB: B? = null

        fun update() {
            val localLastA = lastA
            val localLastB = lastB
            if (localLastA != null && localLastB != null)
                this.value = Pair(localLastA, localLastB)
        }

        addSource(a) {
            lastA = it
            update()
        }
        addSource(b) {
            lastB = it
            update()
        }
    }
}
```

  So, the Live Data Object would be:
  
```kotlin
val twoRequests = zipLiveData(
    ApiUtil.searchReddit("ferrari", 1),
    ApiUtil.searchReddit("fiat", 1)
)
```

  And our observer will be like:
```kotlin
twoRequests.observe(this, Observer { twoSearchResult ->
    val finalList = mutableListOf<Child>()
    twoSearchResult?.first?.data?.children?.get(0)?.let { finalList.add(it) }
    twoSearchResult?.second?.data?.children?.get(0)?.let { finalList.add(it) }
    setupRecyclerView(finalList)
})
```
  
# References

  https://developer.android.com/topic/libraries/architecture/livedata
  https://medium.com/@gauravgyal/combine-results-from-multiple-async-requests-90b6b45978f7

