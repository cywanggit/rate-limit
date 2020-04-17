//package com.example.demo;
//
//import org.springframework.beans.factory.BeanFactory;
//import org.springframework.beans.factory.NoSuchBeanDefinitionException;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.convert.CustomConversions;
//import org.springframework.data.mongodb.MongoDbFactory;
//import org.springframework.data.mongodb.core.convert.DbRefResolver;
//import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
//import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
//import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
//import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
//
///**
// * @copyright (C),  2019-2019, 创蓝253
// * @Description mongo配置
// * @FileName MongoConfoig
// * @auther cw
// * @create 2019-10-23 11:04
// */
//@Configuration
//public class MongoConfoig {
//
//    @Value("${mogonport}")
//    private String port;
//
//    @Bean
//    public MappingMongoConverter mappingMongoConverter(MongoDbFactory factory, MongoMappingContext context, BeanFactory beanFactory) {
//        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
//        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
//        try {
//            mappingConverter.setCustomConversions(beanFactory.getBean(CustomConversions.class));
//        } catch (NoSuchBeanDefinitionException ignore) {
//        }
//        System.out.println(port);
//        // Don't save _class to mongo
//        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
//
//        return mappingConverter;
//    }
//
//
//}
