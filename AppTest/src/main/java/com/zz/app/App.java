package com.zz.app;


import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
/**
 * @author zz
 * 用于各种简单的测试验证
 * 1. Entity 对象有4个属性，id，name，age，sex
 *  2. 数据库中有1000条记录，查询返回 List<Entity>
 *   3. 按 age 和 sex 组合的字符串“age_sex”进行分组
 *  4. 对 List<Entity> 按规则；同一组按id递增顺序串行调用 service 层的 sendAndWait 方法，不同组异步调用 service 层的 sendAndWait 方法
 */
public class App {

    // 模拟 service 层的方法
    private static ServiceLayer service = new ServiceLayer();

    public static void main(String[] args) {
        // 数据库查询返回的 List<Entity>
        List<Entity> entityList = fetchDataFromDatabase();

        // 按 age 和 sex 进行分组
        Map<String, List<Entity>> groupedEntities = entityList.stream()
                .collect(Collectors.groupingBy(entity -> entity.getAge() + "_" + entity.getSex()));

        // 对各组进行串行或异步调用
        groupedEntities.values().forEach(group -> {
            group.stream()
                    .sorted(Comparator.comparing(Entity::getId))
                    .forEachOrdered(entity -> {
                        if (isSameGroupSync(group)) {
                            // 同一组同步调用
                            service.sendAndWait(entity);
                        } else {
                            // 不同组异步调用
                            CompletableFuture.runAsync(() -> service.sendAndWait(entity));
                        }
                    });
        });


    }

    // 模拟从数据库中获取数据的方法
    private static List<Entity> fetchDataFromDatabase() {
        // 构造3组测试数据，其中有1组具有不同id的3条数据
        Entity entity1 = new Entity(1, "Alice", 25, "Female");
        Entity entity2 = new Entity(2, "Bob", 30, "Male");
        Entity entity3 = new Entity(3, "Charlie", 35, "Male");
        Entity entity4 = new Entity(4, "David", 25, "Male");
        Entity entity5 = new Entity(5, "Eve", 30, "Female");
        Entity entity6 = new Entity(6, "Frank", 25, "Male");

        return Arrays.asList(entity1, entity2, entity3, entity4, entity5, entity6);
    }

    // 判断是否为同一组
    private static boolean isSameGroupSync(List<Entity> group) {
        // 已经按照 age 和 sex 分组，不需要重新判断
        return true;
    }

    @Test
    public void testAsyncCalls() throws InterruptedException {
        // 数据库查询返回的 List<Entity>
        List<Entity> entityList = fetchDataFromDatabase();

        // 按 age 和 sex 进行分组
        Map<String, List<Entity>> groupedEntities = entityList.stream()
                .collect(Collectors.groupingBy(entity -> entity.getAge() + "_" + entity.getSex()));

        // 对各组进行串行或异步调用
        groupedEntities.values().forEach(group -> {
            group.stream()
                    .sorted(Comparator.comparing(Entity::getId))
                    .forEachOrdered(entity -> {
                        if (!isSameGroupSync(group)) {
                            // 不同组异步调用
                            CompletableFuture.runAsync(() -> service.sendAndWait(entity));
                        }
                    });
        });

        // 等待异步调用结束
        Thread.sleep(1000);  // 假设1秒足够执行异步调用了
    }

}
