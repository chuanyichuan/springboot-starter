package cc.kevinlu.logger.starter.annotation.registrar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.CollectionUtils;

import cc.kevinlu.logger.starter.annotation.PSlf4jEx;

/**
 * @author chuan
 */
public class EnablePSlf4jExRegistrar
        implements ImportBeanDefinitionRegistrar, BeanFactoryAware, EnvironmentAware, ResourceLoaderAware {

    public static final String              PSLF4J_PARAM = "cc.pslf4j.packages";

    private ConfigurableListableBeanFactory beanFactory;

    private List<String>                    basePackages;

    private Environment                     environment;

    private ResourceLoader                  resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 构建扫描器
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(this.resourceLoader);
        TypeFilter typeFilter = new AnnotationTypeFilter(PSlf4jEx.class);
        scanner.addIncludeFilter(typeFilter);
        if (CollectionUtils.isEmpty(basePackages)) {
            basePackages = new ArrayList<>();
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata metadata = beanDefinition.getMetadata();
                    try {
                        registerLoggerBean(registry, metadata);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void registerLoggerBean(BeanDefinitionRegistry registry, AnnotationMetadata metadata)
            throws ClassNotFoundException {
        String clazz = metadata.getClassName();
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(PSlf4jLoggerFactoryBean.class);
        beanDefinitionBuilder.addPropertyValue("type", clazz);
        beanDefinitionBuilder.addPropertyValue("target", beanFactory.getBean(Class.forName(clazz)));
        beanDefinitionBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

        AbstractBeanDefinition definition = beanDefinitionBuilder.getBeanDefinition();
        BeanDefinition b = registry.getBeanDefinition(simpleBeanName(clazz));
        definition.setPropertyValues(b.getPropertyValues());
        BeanDefinitionHolder holder = new BeanDefinitionHolder(definition, simpleBeanName(clazz), null);
        removeBean(clazz, registry);
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    private void removeBean(String clazz, BeanDefinitionRegistry registry) {
        if (registry.containsBeanDefinition(clazz)) {
            registry.removeBeanDefinition(clazz);
        } else if (registry.containsBeanDefinition(simpleBeanName(clazz))) {
            registry.removeBeanDefinition(simpleBeanName(clazz));
        }
    }

    private String simpleBeanName(String clazz) {
        if (clazz.contains(".")) {
            String name = clazz.substring(clazz.lastIndexOf(".") + 1);
            return new StringBuilder().append(Character.toLowerCase(name.charAt(0))).append(name.substring(1))
                    .toString();
        } else if (Character.isUpperCase(clazz.charAt(0))) {
            return new StringBuilder().append(Character.toLowerCase(clazz.charAt(0))).append(clazz.substring(1))
                    .toString();
        }
        return clazz;
    }

    private ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return !beanDefinition.getMetadata().isAnnotation();
            }
        };
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        // 读取基础扫描文件
        String prop = environment.getProperty(PSLF4J_PARAM);
        if (StringUtils.isNotBlank(prop)) {
            basePackages = Arrays.asList(prop.split(","));
        }
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
