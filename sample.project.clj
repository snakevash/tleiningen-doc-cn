(defproject org.example/sample "1.0.0-SNAPSHOT"
  ;;; 配置
  ;; 每个激活的配置文件会在项目配置里面合并.
  ;; :dev 和 :user 配置是默认激活的, 后者需要到 ~/.lein/profiles.clj
  ;; 里面查看. 使用带配置的任务更具优先级,
  ;; `lein help profiles` 来查看详细解释.
  :profiles {:debug {:debug true
                     :injections [(prn (into {} (System/getProperties)))]}
             :1.4 {:dependencies [[org.clojure/clojure "1.4.0"]]}
             :1.5 {:dependencies [[org.clojure/clojure "1.5.0"]]}
             ;; 默认激活
             :dev {:resource-paths ["dummy-data"]
                   :dependencies [[clj-stacktrace "0.2.4"]]}
             ;; 在uberjar过程中自动执行
             :uberjar {:aot :all}
             ;; 在repl模式中自动激活
             :repl {:plugins [[cider/cider-nrepl "0.7.1"]]}}
  ;; 加载这些命名空间中的hooks节点
  :hooks [leiningen.hooks.difftest]
  ;; 当它们加载时应用这些来自插件的函数到你的项目中
  ;; hooks和中间件都可以在这里罗列然后悄悄加载
  :middleware [lein-xml.plugin/middleware]
  ;; 关闭悄悄加载
  :implicit-middleware false
  :implicit-hooks false

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
  ;; 通常Leiningen在执行项目代码之前会执行编译任务
  ;; 但是你可以使用 :prep-tasks 来覆盖, 并且做一些其他任务比如编译protocol buffers
  :prep-tasks [["protobuf" "compile"] "javac" "compile"]
  ;; 这些命名空间将会被提前编译.
  ;; 需要被生成class或者交互函数之类的.
  ;; 可以使用正则表达式来覆盖一些命名空间.
  ;; 如果你仅仅在生成uberjar的时候生成class, `:aot :all` 在:uberjar 配置里面,
  ;; 查看 :target-path 来开始基于配置文件的目标.
  :aot [org.example.sample]
  ;; 在你项目里面每个form都需要执行的.
  ;; 允许和 Gilardi Scenario 合作: http://technomancy.us/143
  :injections [(require 'clojure.pprint)]
  ;; Java 代码能够操作 拦截 VM 的一些特性.
  ;; 包括 :bootclasspath 来往classpath里面增加jar包.
  :java-agents [[nodisassemble "0.1.1" :options "extra"]]
  ;; 可选项可以传递给java编译器, 跟 命令行额外参数一样.
  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
  ;; 反射调用时触发警告 - 不建议
  :warn-on-reflection true
  ;; 设置clojure全局变量. 范例是禁止pre-和post-conditions和反射调用警告.
  ;; 查看clojure帮助文档来知道那些有用的全局变量
  :global-vars {*warn-on-reflection* true
                *assert* false}
  ;; 使用一个不同的java可执行文件
  ;; Leiningen自己的JVM是设置在 LEIN_JAVA_CMD 环境变量中
  :java-cmd "/home/phil/bin/java1.7"
  ;; 你能够设置 JVM-level 可选参数.
  ;; :java-opts 是这个的重命名
  :jvm-opts ["-Xmx1g"]
  ;; 在项目执行时设置上下文.
  ;; 默认是 :subprocess 但也可以是 :leinigen 或者 :nrepl
  ;; 来连接一个存在的项目执行进程. 一个项目的nREPL服务能够通过 `lein repl`来执行.
  ;; 如果没有连接被建立, 那么 :subprocess 被回退
  :eval-in :leinigen
  ;; 开启bootclasspath优化.
  ;; 它可以提高启动时间假定核心库都被加载好了.
  :bootclasspath true

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

  ;;; 测试
  ;; 判断是否需要执行一个测试, 获取测试的元数据.
  ;; `lein help tutorial` 来获得更多信息.
  :test-selectors {:default (fn [m] (not (or (:integration m) (:regression m))))
                   :integration :integration
                   :regression :regression}
  ;; 为了 `retest` 任务, 需要使用 clojure.test 库.
  ;; `lein retask` 关闭.
  :monkeypath-clojure-test false

  ;;; Repl
  ;; repl 可选项
  :repl-options {;; 自定义前缀字符串
                 ;; 默认字符串 user=>
                 :prompt (fn [ns] (str "your command for <" ns ">,master? "))
                 ;; 当repl会话开始打印的字符串
                 :welcome (println "Welcome to the magical world of the repl!")
                 ;; 指定命名空间开始时 初始化函数
                 :init-ns foo.bar
                 ;; 第一次打开repl将要执行的表达式,
                 :init (println "here we are in" *ns*)
                 ;; 打印错误的堆栈信息
                 :caught clj-stacktrace.repl/pst+
                 ;; 跳过默认加载并且打印帮助消息
                 :skip-default-init false
                 ;; 定义repl host和port
                 :host "0.0.0.0"
                 :port 4001
                 ;; 如果nRepl任务如果加载太长时间, 那么它会超时
                 :timeout 40000
                 ;; nREPL服务自定义
                 ;; 两者只能有一个能有效
                 ;; 使用一个不同的服务端
                 :nrepl-handler (clojure.tools.nrepl.server/default-handler)
                 ;; 往服务端增加中间件
                 :nrepl-middleware [my.nrepl.thing/wrap-amazingness
                                    ;; 连接文档的更多细节
                                    (fn [handler]
                                      (fn [& args]
                                        (prn :middle args)
                                        (apply handler args)))]}

  ;;; Jar包
  ;; jar包文件产生.
  ;; 将会被生成在 :target-path.
  ;; 包括版本号
  :jar-name "sample.jar"
  ;; uberjar 配置
  :uberjar-name "sample-standalone.jar"
  ;; 包括 :source-paths 之外的jars
  :omit-source true
  ;; 排除文件
  :jar-execlusions [#"(?:^|/).svn/"]
  ;; uberjar 排除的文件
  :uberjar-execlusion [#"META-INF/DUMMY.SF"]
  ;; 在执行打包之前会执行清理任务来保护那些未定义AOT的
  ;; 关闭这些行为
  :auto-clean false
  ;; 当有多个相同名称的文件和依赖时的文件合并规则.
  ;; 用文件名称字符串映射和规则表达式来序列化三个函数:
  ;; 1. 从输入拿取并且解析输出
  ;; 2. 新的和当前的对比 合并它们
  ;; 3. 拿取输出的
  ;;
  :uberjar-merge-with {#"\.properties$" [slurp str spit]}
  :filespecs [{:type :path :path "config/base.clj"}
              {:type :path :paths ["config/web" "config/clj"]}
              {:type :bytes :path "project.clj"
               :bytes ~(slurp "project.clj")}
              {:type :fn :fn (fn [p]
                               {:type :bytes :path "git-log"
                                :bytes (:out (clojure.java.shell/sh
                                              "git" "log" "-n" "1"))})}]
  :manifest {"Project-awesome-level" "super-great"
             "CLass-Path" ~#(clojure.string/join
                             \space
                             (leiningen.core.classpath/get-classpath %))
             :my-section-1 [["MyKey1" "MyValue1"] ["MyKey2" "MyValue2"]]
             :my-section-2 {"MyKey3" "MyValue3" "MyKey4" "MyValue4"}
             "Grunge-level" my.plugin/calculate-grunginess}
  )
