# android-object-box
[![Ebert](https://ebertapp.io/github/potikorn/android-object-box.svg)](https://ebertapp.io/github/potikorn/android-object-box)

try objectBox library for easy manage local db.

# How to setup project

### first must have any entitiy class (data class model or POJO)
```
// look like this

@Entity // Must mark with this annotation.
data class TodoModel(
        @Id var id: Long = 0,
        var topic: String? = null,
        var detail: String? = null,
        var date: Date? = null
)
```

then make sure you build your project once.

for example you can **Build > Make Project**

then in **app** folder will be generated **objectbox-models.**

after that in your application must crete box store like this.

```
class App : Application() {

    lateinit var boxStore: BoxStore
    
    override fun onCreate() {
        super.onCreate()
        boxStore = MyObjectBox.builder().androidContext(this).build()
    }
}
```

now you can use any box model that you created

for example.
```
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // ...
        val todoBox = (application as App).boxStore.boxFor(TodoModel::class.java)
        val todoQuery = todoBox.query().build()
```

for addition how to query or tarverse in your data collections please visit [Docs](http://objectbox.io/documentation/).
