# FAQ

**Q:** Leiningen 怎么发音？
**A:** LINE-ing-en. ['laɪnɪŋən]

**Q:** 组ID是什么？ 快照是怎么工作的？
**A:** 浏览[教程](https://github.com/technomancy/leiningen/blob/stable/doc/TUTORIAL.md)

**Q:** 我是如何选择版本数字的？
**A:** 使用[semantic versioning](http://semver.org).

**Q:** 如果我的工程依赖的jar包没有在仓库中怎么办？
**A:** 你应该把他们放入仓库中。
  [部署指南](https://github.com/technomancy/leiningen/blob/stable/doc/DEPLOY.md)
  解释了如果部署一个私有仓库。通常，很容易把它们部署在一个静态服务器或者是
  S3 bucket（亚马逊的云服务）。一旦仓库部署完毕，执行
  `lein deploy private-repo com.mycorp/somejar 1.0.0 somejar.jar pom.xml`
  将会把需要用到的jar下载下来。如果没有pom文件，那么可以通过`lein new`来新建一个，
  然后构造一个pom文件。如果仅仅是做个实验性质的，那么可以直接使用文件系统仓库
  `file:///$HOME/.m/repository`，让jar包在本地就可以用。

**Q:** 我想要修改项目里面的依赖，却怕搞乱了它们了。
**A:** Leiningen提供这么一个特性，叫做 *checkout dependencies* ，它可以实现上述的功能。
  浏览[教程](https://github.com/technomancy/leiningen/blob/stable/doc/TUTORIAL.md)
  来获得更多信息。

**Q:** 可以排除间接的依赖吗？
**A:** 可以。一些库，比如log4j，依赖项目里那些没有存在公共仓库的和非必要的基础函数。
  项目依赖列表 `:dependencies` 可以用关键 `:exclusions` 来排除一些依赖。
  浏览 `lein help sample` 来获得更多信息.

**Q:** 我指定了依赖库的版本X,为什么获得了版本Y?
**A:** 你依赖的依赖定义了一个版本范围，它们覆盖了你的软定义。
  运行 `lein deps :tree` 来浏览你依赖的版本范围。你可以使用
  `:exclusions` 来避免影响到你接下来的依赖。浏览 `lein help sample`
  来了解它是怎么工作的。你可以来提bug来说明你遇到的版本原因。

**Q:** 我有两个依赖，X和Y，它们都依赖Z。那么Z应该怎么定义版本？
**A:** `:dependencies` 列表里面的依赖最终是采取最小版本的。如果是多重定义，
  那么选择在前的。例如：

    [Z "1.0.9"]
    [X "1.3.2"]
      [Z "2.0.1"]

  `[Z "1.0.9"]` 会被选择，因为它跟更小。
  例如：

    [X "1.3.2"]
      [Z "2.0.1"]
    [Y "1.0.5"]
      [Z "2.1.3"]

  X依赖在前，所以 `[Z "2.0.1"]` 被选择。如果我们把Y提前，那么 `[Z "2.1.3"]` 会被选择。
  注意这些都是软依赖， `lein deps :tree` 将会给出提示，哪些没有被选择。

**Q:** 我在一个HTTP代理后，我应该怎么样获取依赖？
**A:** 在Leiningen 2.x 中设置 `$http_proxy` 环境变量。你也可以设置
  `$http_no_proxy` 来避免代理一些可以直接访问的服务器。这是一个已
  `|` 来分割的，可能以 `*` 来通用匹配的列表。比如: `localhost|*.mydomain.com`。
  如果是Leiningen 1.x 版本，看这里[配置Maven代码](http://maven.apache.org/guides/mini/guide-proxies.html)。
  相关文件 `~/.m2/settings.xml`。

**Q:** 怎么样加快加载速度？
**A:** Leiningen的主要延迟来自JVMs两点:一个是你的项目一个是Leiningen自身。
  更多的人会保持单个项目的REPL来进行开发。你可以依赖你的编辑器来集成Clojure。
  浏览 [nrepl.el](https://github.com/clojure-emacs/cider) 或者
  [fireplace](https://github.com/tpope/vim-fireplace)。
  当然你也可以使用 `lein repl`。

**Q:** 如果仍然很慢，还可以做点其他吗？
**A:** 这篇页面涉及到一些资料
  [提高加载速度时间的方法](https://github.com/technomancy/leiningen/wiki/Faster)

**Q:** 如果我更关注长期运行性能而不是启动时间？
**A:** Leiningen 2.1.0 可以关闭可选项来提高启动速度(它更适合长期运行的处理)。
  它会影响一些长期运行的性能问题，并且会导致错误的基准测试结果。
  如果想要了解所有的JVM优化选项，你可以导入配置文件 `lein with-profiles production run ...` 。

**Q:** "Unrecognized VM option 'TieredStopAtLevel=1'"是什么意思？
**A:** 老版本的JVM不支持Leiningen的指令优化来使得JVM启动的更快。
  你可以用 `export LEIN_JVM_OPTS=` 来关闭这些行为，或者更新JVM。

**Q:** 我尝试运行一个后台处理的项目（`lein run &`），但是进程在前台的时候就暂停了。
  我应该怎么样运行一个后台程序？
**A:** 为了持久的处理，最好还是创建一个uberjar并且用 `lein trampoline run &` 来使用。
  短期程序，使用 `lein run <&- &` 和 `bash -c "lein run &"` 都工作的很好。

**Q:** "could not transfer artifact ... peer not authenticated" 错误意味着？
**A:** 这意味着你的JVM不能有效的认证，或者你正在经历一个
  [man-in-the-middle attack](https://github.com/technomancy/leiningen/issues/1028#issuecomment-32732452)
  攻击。Leiningen 会使用当前的Clojars公共证书，所以你就可以使用 `:certificates` ["clojures.pem"]
  在你 `:user` 章节中，假设证书没有过期。

**Q:** 我应该在项目运行时怎么决定版本号？
**A:** Leiningen 写了一个叫做 `pom.properties` 到 `target/classes`，
  它包含了版本号和git版本。在之前的Leiningen版本中，这个只有从jar文件运行才有用，
  但是从 2.4.1开始在 `lein run ...` 也有效。你可以读取它：

```clj
(doto (java.util.Properties.)
  (.load (io/reader (io/resource "META-INF/maven/group/artifact/pom.properties"))))
```

**Q:** 我需要为uberjar设置AOT，我能够在开发的时候避免它吗？
**A:** 这是一个合理的需求。 Leiningen 支持隔离不同的配置通过目标文件夹。
  简单的指定 `:target-path "target/%s"` 来给不同的配置文件可以生成相应的文件。
  你可以使用 `:aot` 在 `:uberjar` 配置中，那么 .class文件不会影响你的日常开发。
  你能够指定配置隔离 `:target-path` 在你 `:user` 配置文件中。

**Q:** 有没有一种方法不使用AOT的uberjar？
**A:** 从2.4.0开始，如果你在 `project.clj` 文件里面省略 `:main` ,
  你的uberjars将会使用 `clojure.main` 当做它们的入口。你可以加载
  `java -jar my-app-standalone.jar -m my.entry.namespace arg1 arg2 [...]`
  不需要AOT的，但是会浪费很多时间。
