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
  )
