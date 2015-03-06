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
**A:**
