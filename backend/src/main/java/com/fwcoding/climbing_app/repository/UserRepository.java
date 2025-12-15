package com.fwcoding.climbing_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fwcoding.climbing_app.model.User;

// No need for creating UserStats and UserPreferences Repo, this can fetch due to OnetoOne relationship and Cascade

public interface UserRepository extends JpaRepository<User, Long> {

}
