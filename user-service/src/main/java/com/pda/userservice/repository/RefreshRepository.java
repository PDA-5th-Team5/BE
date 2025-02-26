package com.pda.userservice.repository;

import com.pda.userservice.entity.Refresh;
import org.springframework.data.repository.CrudRepository;

public interface RefreshRepository extends CrudRepository<Refresh, String> {

//    boolean existsByRefresh(String refresh);
}