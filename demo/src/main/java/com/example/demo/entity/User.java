package com.example.demo.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Optional;

/**
 * @copyright (C),  2019-2019, 创蓝253
 * @Description demo
 * @FileName User
 * @auther cw
 * @create 2019-10-12 12:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Document
public class User {

    @Id
    private String id;
    private String userName;
    private String password;
    private Integer age;
    private Long createTime;


    public static void main(String[] args) {
    }
}
