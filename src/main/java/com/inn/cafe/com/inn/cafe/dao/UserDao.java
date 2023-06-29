package com.inn.cafe.com.inn.cafe.dao;

import com.inn.cafe.com.inn.cafe.POJO.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User, Integer> {
}
