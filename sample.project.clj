(defproject org.example/sample "1.0.0-SNAPSHOT"

  ;;; 入口
  ;; 这个命名空间的 -main 函数将会在启动的时候调用
  ;; (无论是通过 `lein run` 或者从一个 uberjar ). 它应该有变长参数:
  ;;
  ;; (ns my.service.runner
  ;;   (:gen-class))
  ;;
  ;; (defn -main
  ;;   "应用入口"
  ;;   [& args]
  ;;   (应用的初始化应该在这里))
  ;;
  ;; 默认是提前编译的(AOT); 可以使用元数据 ^:skip-aot 来关闭它.
  ;; 当 :main 的 :aot 是 :all 或者包含 main 类时， ^:skip-aot 将不起作用.
  ;; 明确的 :aot 比依赖自动编译更好. 把 :main 设置成 nil 是有用的当一个
  ;; 项目包含好几个 main 函数. nil 将会产生一个带有 mainifest.mf 的 jar 包,
  ;; 它含有 `Main-Class` 属性
  :main my.service.runner
  ;; 项目支持任务别名. lein 命令行会用相同的方法来执行.
  ;; 如果别名是 vector , 那么它将会使用并行应用.
  ;; 例如, "lein with-magic run -m hi.core" 和 "lein assoc :magic true run -m hi.core".
  ;; 记住, 逗号并不是分割参数解析的关键, 它们只是参数的一部分形式.
  :aliases {"launch" ["run" "-m" "myproject.main"]
            ;; 项目配置的键值对能够品接到参数中, 用 :project/key 关键字
            "launch-version" ["run" "-m" "myproject.main" :project/version]
            "dumbrepl" ["trampoline" "run" "-m" "clojure.main/main"]
            ;; :pass-through-help 确保 `lein my-alias help` 不会转换到 `lein help my-alias`
            "go" ^:pass-through-help ["run" "-m"]
            ;; 为了复杂的别名, 可以增加一个文档字符. 这个文档字符串在 `lein help` 会被打印
            "deploy!" ^{:doc "Recompile sources, then deploy if tests succeed."}
            ;; "do" 任务支持嵌套的 vectors
            ["do" "clean" ["test" ":integration"] ["deploy" "clojars"]]}

  ;;; 运行项目代码
  :prep-tasks [["protobuf" "compile"] "javac" "compile"]
  :aot [org.example.sample]
  :injections [(require 'clojure.pprint)]
  :java-agents [[nodisassemble "0.1.1" :options "extra"]]
  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
  :warn-on-reflection true
  :global-vars {*warn-on-reflection* true
                *assert* false}

  ;;; 文件系统
  ;; 如果你用了一个不同的目录结构, 你可以设置这些.
  ;; 输入路径是包含字符串的 vector, 输出是字符串.
  :source-paths ["src" "src/main/clojure"]
  :java-source-paths ["src/main/java"]
  :test-paths ["test" "test/main/clojure"]
  :resource-paths ["src/main/resource"] ; 非代码类的文件 包含在 classpath/jar .
  ;; 所有生成的文件被放置在 :target-path. 为了避免跨配置污染, 建议加上 %s 到你定义的路径中,
  ;; 它可以标识当前的活跃配置文件
  :target-path "target/%s/"
  ;; 提前编译文件存放路径 %s 表示当前配置名称
  :compile-path "%s/classy-files"
  ;; 该目录是依赖中所包含的原声组件的解压缩.
  ;; %s 表示当前配置名称
  ;; 注意 这不是查找本地库的地方, 使用 :jvm-opts 来替代 -Djava.library.path=...
  :native-path "%s/bits-n-stuff"
  ;; `lein clean` 命令清除的文件夹.
  ;; 用关键字和关键字链来指定.
  ;; 单个路径和路径的集合都是被接受的.
  ;; 例如
  ;;   :target-path "target"
  ;;   :compile-path "classes"
  ;;   :foobar-paths ["foo" "bar"]
  ;;   :baz-config {:qux-path "qux"}
  ;; :clean-targets 会在 `lein clean` 移除文件
  ;; "classes", "foo", "bar", "qux", and "out".
  ;; 默认会保护 lein 的文件 ("src", "test", "resources", "doc", "project.clj")
  ;; 但是, 这个保护会被 :clean-targets 的元数据覆盖 ^{:protect false}
  :clean-targets [:target-path :compile-path :foobar-paths
                  [:baz-config :qiu-path] "out"]
  ;; 这是一个依赖的问题 (让我先看看)
  :clean-non-project-classes true
  ;; 在 checkout 中的路径会包含在每个项目的 classpath 中.
  ;; (在FAQ有详细的信息.)
  ;; 项目会把这个 vector 中的函数当做参数. 默认值为 [:source-paths :compile-path :resource-paths],
  ;; 你可以使用下面的来共享代码到你的测试套件中:
  :checkout-deps-shares [:source-paths :test-paths
                         ~(fn [p] (str (:root p) "/lib/dev/*"))]
  )
