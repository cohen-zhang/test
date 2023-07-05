package com.zz.app;

/**
 * @author zz
 */
public class ServiceLayer {
    public void sendAndWait(Entity entity) {
        System.out.println("调用服务层方法: " + entity.getId());
    }
}
