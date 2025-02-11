package com.java3y.austin.handler.pending;

import com.java3y.austin.handler.config.ThreadPoolConfig;
import com.java3y.austin.handler.utils.GroupIdMappingUtils;
import com.java3y.austin.support.config.ThreadPoolExecutorShutdownDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;


/**
 * 存储 每种消息类型 与 TaskPending 的关系
 * @author 3y
 */
@Component
public class TaskPendingHolder {

    @Autowired
    private ThreadPoolExecutorShutdownDefinition threadPoolExecutorShutdownDefinition;
    /**
     * 线程池的参数
     */
    private Integer coreSize = 3;
    private Integer maxSize = 3;
    private Integer queueSize = 100;
    private Map<String, ExecutorService> taskPendingHolder = new HashMap<>(32);

    /**
     * 获取得到所有的groupId
     */
    private static List<String> groupIds = GroupIdMappingUtils.getAllGroupIds();
    /**
     * 给每个渠道，每种消息类型初始化一个线程池
     *
     * TODO 不同的 groupId 分配不同的线程和队列大小
     *
     */
    @PostConstruct
    public void init() {
        for (String groupId : groupIds) {
            ExecutorService threadPool = ThreadPoolConfig.getThreadPool(coreSize, maxSize, queueSize);
            threadPoolExecutorShutdownDefinition.registryExecutor(threadPool);
            taskPendingHolder.put(groupId, threadPool);
        }
    }
    /**
     * 得到对应的线程池
     * @param groupId
     * @return
     */
    public ExecutorService route(String groupId) {
        return taskPendingHolder.get(groupId);
    }


}
