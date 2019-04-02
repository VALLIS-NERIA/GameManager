* ListActivity：这是一个复用的Activity，用来显示游戏列表或者DLC列表。app启动时默认进入ListActivity，并且显示游戏列表。

# 在Activity之间传送数据：
## 父activity
1. 创建intent，第2个参数是要调用的子activity的类

``` java
Intent intent = new Intent(this, GameDetailActivity.class);
```

2. 向intent中放数据，类似于键值对，第1个参数是字符串类型的Key，第2个参数可以是基本类型、String、或者任意实现Serializable接口的类。因为要传递Game和DLCInfo，所以这两个类都实现了Serializable

``` java
intent.putExtra(MSG_ITEM, new Game());
```

3. 启动子activity

``` java
startActivity(intent);
```

### 如果要读取子activity传回来的数据：

将上面启动子activity的代码改成这样，第2个参数CODE_ADD_GAME是一个int，称为requestCode，标识你启动子activity的目的

``` java
startActivityForResult(intent, CODE_ADD_GAME);
```

#### 读取数据：
requestCode就是之前父activity发出的，用来鉴别是哪个子activity返回
resultCode是子activity的返回代码
data是子activity返回的数据

```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CODE_ADD_GAME:
                if (resultCode == RESULT_OK) {
                    Game returnedGame = (Game) data.getSerializableExtra(MSG_RETURN_DATA);
                    this.games.add(returnedGame);
                    this.updateAndSave();
                }
                break;
            }
        }
    }
```

## 子activity
### 读取数据：
1. 获取传入的intent

```java
Intent intent = getIntent();
```

2. 从intent中获取数据
```java
String type = intent.getStringExtra(REQUEST_TYPE);
currentGame = (Game) intent.getSerializableExtra(MSG_ITEM); //类似的还有getIntExtra等等
```

### 返回到父activity：
1. 创建intent
```java
Intent intent = new Intent();
```

2. 设置resultCode

```java
setResult(RESULT_OK, intent);
```

3. 如有必要也可以放数据
```java
intent.putExtra(MSG_RETURN_DATA, currentGame);
```

4. 销毁当前子activity

```java
finish();
```


# lambda表达式

参考：http://www.runoob.com/java/java8-lambda-expressions.html

Java当中经常会这么写：
``` java
button.setOnClickListener(
    new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }
);
```

在java8中可以这么写：
``` java
button.setOnClickListener(
    (View view) -> {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
);
```

这两种写法本质上是一样的，只是能够少敲点代码。

lambda表达式可以进一步简化：
``` java
button.setOnClickListener(
    view -> {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
);

// 把函数体封装成一个方法
button.setOnClickListener(
    view -> {
        DlcEditActivity.this.clickCancel(view);
    }
);

// 终极简化
button.setOnClickListener(this::clickCancel);
```

# RecyclerView

## Adapter (MyAdapter.java)

### ItemView
adapter里首先要有一个ViewHolder类，比如MyAdapter.java里的

``` java
public class ItemView extends RecyclerView.ViewHolder
```

recycler view中一个**item**对应一个**ItemView**对应一个**item.xml的layout**

在Adapter类中将layout和ItemView绑定：
``` java
    @Override
    public ItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ItemView(v);
    }
```
这里绑定layout之后，ItemView就可以通过findViewById获得layout中的元素，在onBindViewHolder方法里就能通过ItemView来修改这些元素。

### factory method

因为DLC列表和游戏列表的外观稍有不同，所以用了两个factory method来创建adapter

``` java
public static MyAdapter ForGames(List<Game> games, ListActivity.ItemClickListener callback)
public static MyAdapter ForDlcs(List<DlcInfo> dlcs, ListActivity.ItemClickListener callback)
```

### ItemClickListener

点击列表中的某个游戏时，我们要向GameDetailActivity传入游戏在gameList中的index。但是默认的OnClickListener只有一个View的参数，所以这里自定义了一个接口来包装一下。

ListActivity在这里将函数体传入
``` java
adapter = MyAdapter.ForGames(this.games, this::clickViewGame);
```

MyAdapter收到之后，存放到this.callback中
``` java
// 
public static MyAdapter ForGames(List<Game> games, ItemClickListener callback) {
    MyAdapter a = new MyAdapter();
    a.games = games;
    a.dlcs = null;
    a.callback = callback;
    a.dlcMode = false;
    return a;
}
```

最后包装一层传给OnClickListener，实际调用的是ListActivity里的clickViewGame方法


``` java            
holder.itemView.setOnClickListener(v -> this.callback.Invoke(position));
```