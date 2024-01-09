package com.startsteps.Final.Project.ECommerce.Repository;

import com.startsteps.Final.Project.ECommerce.Models.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
