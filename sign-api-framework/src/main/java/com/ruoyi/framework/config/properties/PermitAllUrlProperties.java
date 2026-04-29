package com.ruoyi.framework.config.properties;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ruoyi.common.annotation.Anonymous;

/**
 * 设置Anonymous注解允许匿名访问的url
 * 
 * @author HayDen
 */
@Configuration
public class PermitAllUrlProperties implements InitializingBean, ApplicationContextAware
{
    private static final Logger log = LoggerFactory.getLogger(PermitAllUrlProperties.class);
    
    private List<String> urls = new ArrayList<>();

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception
    {
        Map<String, Object> controllers = applicationContext.getBeansWithAnnotation(Controller.class);
        for (Object bean : controllers.values())
        {
            try
            {
                Class<?> beanClass = getTargetClass(bean);
                if (beanClass == null)
                {
                    continue;
                }
                RequestMapping base = beanClass.getAnnotation(RequestMapping.class);
                String[] baseUrl = {};
                if (Objects.nonNull(base))
                {
                    baseUrl = base.value();
                }
                Method[] methods = beanClass.getDeclaredMethods();
                for (Method method : methods)
                {
                    if (method.isAnnotationPresent(Anonymous.class) && method.isAnnotationPresent(RequestMapping.class))
                    {
                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                        String[] uri = requestMapping.value();
                        urls.addAll(rebuildUrl(baseUrl, uri));
                    }
                    else if (method.isAnnotationPresent(Anonymous.class) && method.isAnnotationPresent(GetMapping.class))
                    {
                        GetMapping requestMapping = method.getAnnotation(GetMapping.class);
                        String[] uri = requestMapping.value();
                        urls.addAll(rebuildUrl(baseUrl, uri));
                    }
                    else if (method.isAnnotationPresent(Anonymous.class) && method.isAnnotationPresent(PostMapping.class))
                    {
                        PostMapping requestMapping = method.getAnnotation(PostMapping.class);
                        String[] uri = requestMapping.value();
                        urls.addAll(rebuildUrl(baseUrl, uri));
                    }
                    else if (method.isAnnotationPresent(Anonymous.class) && method.isAnnotationPresent(PutMapping.class))
                    {
                        PutMapping requestMapping = method.getAnnotation(PutMapping.class);
                        String[] uri = requestMapping.value();
                        urls.addAll(rebuildUrl(baseUrl, uri));
                    }
                    else if (method.isAnnotationPresent(Anonymous.class) && method.isAnnotationPresent(DeleteMapping.class))
                    {
                        DeleteMapping requestMapping = method.getAnnotation(DeleteMapping.class);
                        String[] uri = requestMapping.value();
                        urls.addAll(rebuildUrl(baseUrl, uri));
                    }
                }
            }
            catch (Exception e)
            {
                // 在 DevTools 环境下，某些 bean 的类加载可能会失败，记录日志并继续处理其他 bean
                log.warn("Failed to process controller bean: {}, error: {}", bean.getClass().getName(), e.getMessage());
                if (log.isDebugEnabled())
                {
                    log.debug("Stack trace:", e);
                }
            }
        }
    }

    /**
     * 安全地获取目标类，兼容 Spring Boot DevTools 的 RestartClassLoader
     * 
     * @param bean bean 实例
     * @return 目标类，如果获取失败返回 null
     */
    private Class<?> getTargetClass(Object bean)
    {
        if (bean == null)
        {
            return null;
        }
        
        try
        {
            // 优先使用 Spring AOP 工具类获取目标类
            if (AopUtils.isAopProxy(bean))
            {
                return AopUtils.getTargetClass(bean);
            }
            
            // 如果是 Advised 类型，尝试获取目标类
            if (bean instanceof Advised)
            {
                try
                {
                    Advised advised = (Advised) bean;
                    Object target = advised.getTargetSource().getTarget();
                    if (target != null)
                    {
                        return target.getClass();
                    }
                }
                catch (Exception e)
                {
                    // 在 DevTools 环境下，getTarget() 可能会失败，使用 bean 的类
                    log.debug("Failed to get target from Advised, using bean class: {}", e.getMessage());
                }
            }
            
            // 直接使用 bean 的类，处理 CGLIB 代理
            Class<?> clazz = bean.getClass();
            // 如果是 CGLIB 代理类，尝试获取父类
            if (clazz.getName().contains("$$"))
            {
                Class<?> superClass = clazz.getSuperclass();
                if (superClass != null && !Object.class.equals(superClass))
                {
                    return superClass;
                }
            }
            return clazz;
        }
        catch (Exception e)
        {
            log.warn("Failed to get target class for bean: {}, error: {}", bean.getClass().getName(), e.getMessage());
            return null;
        }
    }

    private List<String> rebuildUrl(String[] bases, String[] uris)
    {
        List<String> urls = new ArrayList<>();
        for (String base : bases)
        {
            for (String uri : uris)
            {
                urls.add(prefix(base) + prefix(uri));
            }
        }
        return urls;
    }

    private String prefix(String seg)
    {
        return seg.startsWith("/") ? seg : "/" + seg;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException
    {
        this.applicationContext = context;
    }

    public List<String> getUrls()
    {
        return urls;
    }

    public void setUrls(List<String> urls)
    {
        this.urls = urls;
    }
}
