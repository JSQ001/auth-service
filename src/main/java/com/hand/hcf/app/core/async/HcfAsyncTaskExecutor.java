package com.hand.hcf.app.core.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@Slf4j
public class HcfAsyncTaskExecutor implements AsyncTaskExecutor,
    InitializingBean, DisposableBean {

    private final ThreadPoolTaskExecutor executor;

    public ThreadPoolTaskExecutor getExecutor() {
        return executor;
    }

    public HcfAsyncTaskExecutor(ThreadPoolTaskExecutor executor) {

        this.executor = executor;
        log.info("hcf-executor-pool,PoolSize:" + executor.getPoolSize()
                + " CorePoolSize:" + executor.getCorePoolSize()
                + " ActiveCount:" + executor.getActiveCount()
                + " MaxPoolSize:" + executor.getMaxPoolSize()
                + " KeepAliveSeconds:" + executor.getKeepAliveSeconds());

    }

    @Override
    public void execute(Runnable task) {

        log.info(Thread.currentThread().getName()+" ,PoolSize:" + executor.getPoolSize()
            + " PoolSize:" + executor.getCorePoolSize()
            + " ActiveCount:" + executor.getActiveCount()
            + " MaxPoolSize:" + executor.getMaxPoolSize()
            + " KeepAliveSeconds:" + executor.getKeepAliveSeconds());
//        Arrays.stream((new Throwable()).getStackTrace()).forEach(st ->
//            log.error("class:" + st.getClassName() + "->" + st.getMethodName() + " :line:" + st.getLineNumber()+", file:"+st.getFileName() ));
        executor.execute(task);
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        executor.execute(createWrappedRunnable(task), startTimeout);
    }

    private <T> Callable<T> createCallable(final Callable<T> task) {
        return () -> {
            try {
                return task.call();
            } catch (Exception e) {
                handle(e);
                throw e;
            }
        };
    }

    private Runnable createWrappedRunnable(final Runnable task) {
        return () -> {
            try {
                task.run();
            } catch (Exception e) {
                handle(e);
            }
        };
    }

    protected void handle(Exception e) {
        log.error("Caught async exception", e);
    }

    @Override
    public Future<?> submit(Runnable task)
    {
//        Arrays.stream(Thread.currentThread().getStackTrace()).forEach(st ->
//            log.error("class:" + st.getClassName() + "->" + st.getMethodName() + " :line:" + st.getLineNumber()+", file:"+st.getFileName() ));
        return executor.submit(createWrappedRunnable(task));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task)
    {
//        Arrays.stream(Thread.currentThread().getStackTrace()).forEach(st ->
//            log.error("class:" + st.getClassName() + "->" + st.getMethodName() + " :line:" + st.getLineNumber()+", file:"+st.getFileName() ));
        return executor.submit(createCallable(task));
    }

    @Override
    public void destroy() throws Exception {

        if (executor instanceof DisposableBean) {

            DisposableBean bean = (DisposableBean) executor;

            bean.destroy();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (executor instanceof InitializingBean) {
            InitializingBean bean = (InitializingBean) executor;
            bean.afterPropertiesSet();
        }
    }
}
