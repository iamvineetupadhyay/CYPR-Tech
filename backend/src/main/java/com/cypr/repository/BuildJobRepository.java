package com.cypr.repository;

import com.cypr.entity.BuildJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildJobRepository extends JpaRepository<BuildJob, String> {
}
