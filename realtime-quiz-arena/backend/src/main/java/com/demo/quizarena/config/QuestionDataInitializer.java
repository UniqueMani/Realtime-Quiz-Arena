package com.demo.quizarena.config;

import com.demo.quizarena.domain.Question;
import com.demo.quizarena.repo.QuestionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

@Configuration
public class QuestionDataInitializer {

    @Bean
    CommandLineRunner initQuestions(QuestionRepository questionRepository) {
        return args -> {
            long count = questionRepository.count();
            System.out.println("[QuestionDataInitializer] 当前数据库中的题目数量: " + count);
            if (count > 0) {
                System.out.println("[QuestionDataInitializer] 已有题目，跳过初始化");
                return; // 已有题目，跳过初始化
            }

            System.out.println("[QuestionDataInitializer] 开始初始化题目数据...");

            // 趣味知识题目（25道）
            createQuestion(questionRepository, "趣味知识", 
                "世界上最小的国家是？", 
                new String[]{"梵蒂冈", "摩纳哥", "列支敦士登", "圣马力诺"}, 
                "梵蒂冈", 
                "梵蒂冈是世界上最小的国家，面积仅0.44平方公里，位于意大利罗马城内。");

            createQuestion(questionRepository, "趣味知识", 
                "哪种动物被称为'沙漠之舟'？", 
                new String[]{"骆驼", "马", "驴", "羊"}, 
                "骆驼", 
                "骆驼被称为'沙漠之舟'，因为它能在极端干旱的沙漠环境中生存，可以长时间不喝水。");

            createQuestion(questionRepository, "趣味知识", 
                "地球的自转周期是？", 
                new String[]{"24小时", "12小时", "48小时", "36小时"}, 
                "24小时", 
                "地球自转一周需要约24小时，这就是为什么我们的一天是24小时。");

            createQuestion(questionRepository, "趣味知识", 
                "世界上最长的河流是？", 
                new String[]{"尼罗河", "亚马逊河", "长江", "密西西比河"}, 
                "尼罗河", 
                "尼罗河全长约6650公里，是世界上最长的河流，流经11个国家。");

            createQuestion(questionRepository, "趣味知识", 
                "哪种颜色在彩虹中位于最外层？", 
                new String[]{"红色", "紫色", "蓝色", "绿色"}, 
                "红色", 
                "彩虹的颜色从外到内依次是：红、橙、黄、绿、蓝、靛、紫，红色位于最外层。");

            createQuestion(questionRepository, "趣味知识", 
                "人类有多少块骨头？", 
                new String[]{"206块", "300块", "150块", "250块"}, 
                "206块", 
                "成年人的骨骼系统由206块骨头组成，包括头骨、躯干骨和四肢骨。");

            createQuestion(questionRepository, "趣味知识", 
                "哪种水果被称为'水果之王'？", 
                new String[]{"榴莲", "苹果", "香蕉", "葡萄"}, 
                "榴莲", 
                "榴莲被称为'水果之王'，因其独特的气味和丰富的营养价值。");

            createQuestion(questionRepository, "趣味知识", 
                "世界上最高的山峰是？", 
                new String[]{"珠穆朗玛峰", "乔戈里峰", "干城章嘉峰", "洛子峰"}, 
                "珠穆朗玛峰", 
                "珠穆朗玛峰海拔8848.86米，是地球上海拔最高的山峰，位于中国和尼泊尔边界。");

            createQuestion(questionRepository, "趣味知识", 
                "哪种动物是唯一会飞的哺乳动物？", 
                new String[]{"蝙蝠", "飞鼠", "飞狐", "鸟类"}, 
                "蝙蝠", 
                "蝙蝠是唯一真正会飞的哺乳动物，它们使用膜状翅膀进行飞行。");

            createQuestion(questionRepository, "趣味知识", 
                "太阳系中最大的行星是？", 
                new String[]{"木星", "土星", "海王星", "天王星"}, 
                "木星", 
                "木星是太阳系中最大的行星，质量是其他所有行星总和的2.5倍。");

            createQuestion(questionRepository, "趣味知识", 
                "哪种金属在常温下是液态？", 
                new String[]{"汞", "铁", "铜", "铝"}, 
                "汞", 
                "汞（水银）是唯一在常温常压下呈液态的金属，熔点为-38.87°C。");

            createQuestion(questionRepository, "趣味知识", 
                "世界上最大的海洋是？", 
                new String[]{"太平洋", "大西洋", "印度洋", "北冰洋"}, 
                "太平洋", 
                "太平洋是世界上最大的海洋，面积约1.65亿平方公里，占地球表面积的三分之一。");

            createQuestion(questionRepository, "趣味知识", 
                "哪种植物被称为'活化石'？", 
                new String[]{"银杏", "松树", "竹子", "橡树"}, 
                "银杏", 
                "银杏被称为'活化石'，因为它在地球上已经存在了2.7亿年，几乎没有变化。");

            createQuestion(questionRepository, "趣味知识", 
                "人类大脑的重量大约是多少？", 
                new String[]{"1.4公斤", "2.5公斤", "0.8公斤", "3.0公斤"}, 
                "1.4公斤", 
                "成年人的大脑平均重量约为1.4公斤，约占体重的2%，但消耗约20%的能量。");

            createQuestion(questionRepository, "趣味知识", 
                "哪种动物是陆地上跑得最快的？", 
                new String[]{"猎豹", "狮子", "马", "羚羊"}, 
                "猎豹", 
                "猎豹是陆地上跑得最快的动物，最高时速可达120公里/小时。");

            createQuestion(questionRepository, "趣味知识", 
                "世界上最大的沙漠是？", 
                new String[]{"撒哈拉沙漠", "阿拉伯沙漠", "戈壁沙漠", "塔克拉玛干沙漠"}, 
                "撒哈拉沙漠", 
                "撒哈拉沙漠是世界上最大的沙漠，面积约920万平方公里，横跨11个国家。");

            createQuestion(questionRepository, "趣味知识", 
                "哪种气体占地球大气的主要成分？", 
                new String[]{"氮气", "氧气", "二氧化碳", "氩气"}, 
                "氮气", 
                "氮气约占地球大气的78%，氧气约占21%，其他气体占1%。");

            createQuestion(questionRepository, "趣味知识", 
                "世界上最大的哺乳动物是？", 
                new String[]{"蓝鲸", "大象", "长颈鹿", "河马"}, 
                "蓝鲸", 
                "蓝鲸是地球上最大的哺乳动物，体长可达30米，体重可达200吨。");

            createQuestion(questionRepository, "趣味知识", 
                "哪种颜色是可见光中波长最短的？", 
                new String[]{"紫色", "红色", "蓝色", "绿色"}, 
                "紫色", 
                "紫色光的波长最短（约400-450纳米），红色光的波长最长（约620-750纳米）。");

            createQuestion(questionRepository, "趣味知识", 
                "地球的年龄大约是多少？", 
                new String[]{"46亿年", "10亿年", "100亿年", "20亿年"}, 
                "46亿年", 
                "根据放射性同位素测年法，地球的年龄约为46亿年。");

            createQuestion(questionRepository, "趣味知识", 
                "哪种动物是唯一会使用工具的鸟类？", 
                new String[]{"乌鸦", "鹦鹉", "老鹰", "鸽子"}, 
                "乌鸦", 
                "乌鸦是少数会使用工具的鸟类之一，它们能够使用树枝、石头等工具获取食物。");

            createQuestion(questionRepository, "趣味知识", 
                "世界上最深的海洋是？", 
                new String[]{"马里亚纳海沟", "汤加海沟", "日本海沟", "菲律宾海沟"}, 
                "马里亚纳海沟", 
                "马里亚纳海沟是世界上最深的海沟，最深处达11034米，位于太平洋西部。");

            createQuestion(questionRepository, "趣味知识", 
                "哪种水果含有最多的维生素C？", 
                new String[]{"猕猴桃", "橙子", "柠檬", "草莓"}, 
                "猕猴桃", 
                "猕猴桃是维生素C含量最高的水果之一，每100克含有约92.7毫克的维生素C。");

            createQuestion(questionRepository, "趣味知识", 
                "人类有多少颗牙齿？", 
                new String[]{"32颗", "28颗", "36颗", "30颗"}, 
                "32颗", 
                "成年人通常有32颗牙齿，包括8颗门齿、4颗犬齿、8颗前臼齿和12颗臼齿。");

            createQuestion(questionRepository, "趣味知识", 
                "哪种动物是唯一会发光的昆虫？", 
                new String[]{"萤火虫", "蝙蝠", "猫", "狗"}, 
                "萤火虫", 
                "萤火虫是唯一会发光的昆虫，它们通过生物发光来吸引配偶。");

            // JAVA知识题目（25道）
            createQuestion(questionRepository, "JAVA知识", 
                "Java中哪个关键字用于继承？", 
                new String[]{"extends", "implements", "inherit", "super"}, 
                "extends", 
                "在Java中，使用'extends'关键字来实现类的继承，例如：class Child extends Parent {}");

            createQuestion(questionRepository, "JAVA知识", 
                "Java中基本数据类型有多少种？", 
                new String[]{"8种", "6种", "10种", "12种"}, 
                "8种", 
                "Java有8种基本数据类型：byte、short、int、long、float、double、char、boolean。");

            createQuestion(questionRepository, "JAVA知识", 
                "哪个是Java的垃圾回收机制？", 
                new String[]{"GC", "JVM", "JIT", "JDK"}, 
                "GC", 
                "GC（Garbage Collection）是Java的自动垃圾回收机制，由JVM自动管理内存。");

            createQuestion(questionRepository, "JAVA知识", 
                "Java中String类是可变的吗？", 
                new String[]{"不可变", "可变", "取决于情况", "部分可变"}, 
                "不可变", 
                "Java中的String类是不可变的（immutable），一旦创建就不能修改，任何修改操作都会创建新对象。");

            createQuestion(questionRepository, "JAVA知识", 
                "Java中哪个接口用于实现多线程？", 
                new String[]{"Runnable", "Thread", "Executor", "Callable"}, 
                "Runnable", 
                "Runnable接口是Java中实现多线程的标准方式，需要实现run()方法。Thread类也实现了Runnable接口。");

            createQuestion(questionRepository, "JAVA知识", 
                "Java中哪个关键字用于定义常量？", 
                new String[]{"final", "static", "const", "constant"}, 
                "final", 
                "在Java中，使用'final'关键字来定义常量，例如：final int MAX_SIZE = 100;");

            createQuestion(questionRepository, "JAVA知识", 
                "Java中ArrayList和LinkedList的主要区别是？", 
                new String[]{"ArrayList基于数组，LinkedList基于链表", "没有区别", "ArrayList更快", "LinkedList更简单"}, 
                "ArrayList基于数组，LinkedList基于链表", 
                "ArrayList基于动态数组实现，随机访问快；LinkedList基于双向链表实现，插入删除快。");

            createQuestion(questionRepository, "JAVA知识", 
                "Java中哪个注解用于标记方法为覆盖父类方法？", 
                new String[]{"@Override", "@Deprecated", "@SuppressWarnings", "@SafeVarargs"}, 
                "@Override", 
                "@Override注解用于标记方法覆盖了父类或接口的方法，有助于编译时检查。");

            createQuestion(questionRepository, "JAVA知识", 
                "Java中HashMap的默认初始容量是？", 
                new String[]{"16", "8", "32", "64"}, 
                "16", 
                "HashMap的默认初始容量是16，负载因子是0.75，当元素数量超过容量*负载因子时会扩容。");

            createQuestion(questionRepository, "JAVA知识", 
                "Java中哪个关键字用于处理异常？", 
                new String[]{"try-catch", "error", "exception", "fail"}, 
                "try-catch", 
                "Java中使用try-catch-finally块来处理异常，try块包含可能抛出异常的代码，catch块捕获异常。");

            createQuestion(questionRepository, "JAVA知识", 
                "Java中接口可以包含方法实现吗（Java 8之前）？", 
                new String[]{"不可以", "可以", "取决于版本", "部分可以"}, 
                "不可以", 
                "在Java 8之前，接口只能包含抽象方法。Java 8引入了default方法和static方法，允许接口包含实现。");

            createQuestion(questionRepository, "JAVA知识", 
                "Java中哪个类是所有类的父类？", 
                new String[]{"Object", "Class", "Super", "Parent"}, 
                "Object", 
                "Object类是Java中所有类的根类，每个类都直接或间接继承自Object类。");

            createQuestion(questionRepository, "JAVA知识", 
                "Java中volatile关键字的作用是？", 
                new String[]{"保证可见性和有序性", "保证原子性", "提高性能", "防止死锁"}, 
                "保证可见性和有序性", 
                "volatile关键字保证变量的可见性（一个线程的修改对其他线程立即可见）和有序性（禁止指令重排序）。");

            createQuestion(questionRepository, "JAVA知识", 
                "Java中哪个集合类不允许重复元素？", 
                new String[]{"Set", "List", "Map", "Queue"}, 
                "Set", 
                "Set接口的实现类（如HashSet、TreeSet）不允许重复元素，List允许重复元素。");

            createQuestion(questionRepository, "JAVA知识", 
                "Java中synchronized关键字可以修饰哪些？", 
                new String[]{"方法和代码块", "只能方法", "只能代码块", "只能类"}, 
                "方法和代码块", 
                "synchronized可以修饰方法（实例方法和静态方法）和代码块，用于实现线程同步。");

            createQuestion(questionRepository, "JAVA知识", 
                "Java中哪个方法用于比较两个对象是否相等？", 
                new String[]{"equals()", "==", "compare()", "same()"}, 
                "equals()", 
                "equals()方法用于比较两个对象的内容是否相等，而'=='比较的是引用是否指向同一对象。");

            createQuestion(questionRepository, "JAVA知识", 
                "Java中泛型的作用是？", 
                new String[]{"类型安全和代码复用", "提高性能", "简化代码", "防止错误"}, 
                "类型安全和代码复用", 
                "泛型提供了编译时类型安全，避免了类型转换，同时提高了代码的复用性和可读性。");

            createQuestion(questionRepository, "JAVA知识", 
                "Java中哪个关键字用于调用父类的构造方法？", 
                new String[]{"super", "this", "parent", "extends"}, 
                "super", 
                "super关键字用于调用父类的构造方法或成员，例如：super()调用父类构造方法。");

            createQuestion(questionRepository, "JAVA知识", 
                "Java中StringBuilder和StringBuffer的区别是？", 
                new String[]{"StringBuffer是线程安全的", "没有区别", "StringBuilder更快", "StringBuffer更简单"}, 
                "StringBuffer是线程安全的", 
                "StringBuffer是线程安全的（方法使用synchronized），StringBuilder不是线程安全的，但性能更好。");

            createQuestion(questionRepository, "JAVA知识", 
                "Java中哪个注解用于标记方法已过时？", 
                new String[]{"@Deprecated", "@Override", "@SuppressWarnings", "@Retention"}, 
                "@Deprecated", 
                "@Deprecated注解用于标记类、方法或字段已过时，不推荐使用，编译器会给出警告。");

            createQuestion(questionRepository, "JAVA知识", 
                "Java中TreeMap的底层实现是？", 
                new String[]{"红黑树", "哈希表", "数组", "链表"}, 
                "红黑树", 
                "TreeMap基于红黑树（Red-Black Tree）实现，保证了元素的有序性和O(log n)的查找性能。");

            createQuestion(questionRepository, "JAVA知识", 
                "Java中哪个接口用于实现函数式编程？", 
                new String[]{"函数式接口（如Function、Consumer）", "Runnable", "Comparable", "Serializable"}, 
                "函数式接口（如Function、Consumer）", 
                "Java 8引入了函数式接口（只有一个抽象方法的接口），如Function、Consumer、Supplier等，支持Lambda表达式。");

            createQuestion(questionRepository, "JAVA知识", 
                "Java中哪个关键字用于定义抽象类？", 
                new String[]{"abstract", "interface", "class", "virtual"}, 
                "abstract", 
                "使用'abstract'关键字定义抽象类，抽象类可以包含抽象方法和具体方法，不能直接实例化。");

            createQuestion(questionRepository, "JAVA知识", 
                "Java中哪个方法用于获取当前线程？", 
                new String[]{"Thread.currentThread()", "Thread.getCurrent()", "Thread.this()", "this.thread()"}, 
                "Thread.currentThread()", 
                "Thread.currentThread()是静态方法，返回当前正在执行的线程对象的引用。");
            
            long finalCount = questionRepository.count();
            System.out.println("[QuestionDataInitializer] 题目初始化完成！共创建 " + finalCount + " 道题目");
        };
    }

    private void createQuestion(QuestionRepository repository, String category, String stem, 
                                String[] options, String correctAnswer, String explanation) {
        Question q = new Question();
        q.setStem(stem);
        q.setOptionsJson(toJsonArray(options));
        q.setCorrectAnswer(correctAnswer);
        q.setExplanation(explanation);
        q.setCategory(category);
        q.setTimeLimitSec(15);
        q.setBasePoints(1000);
        // createdAt 字段在 Question 类中已自动初始化为 Instant.now()，无需手动设置
        q.setUpdatedAt(Instant.now());
        repository.save(q);
    }

    private String toJsonArray(String[] options) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < options.length; i++) {
            String opt = options[i].replace("\\", "\\\\").replace("\"", "\\\"");
            sb.append("\"").append(opt).append("\"");
            if (i < options.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}

