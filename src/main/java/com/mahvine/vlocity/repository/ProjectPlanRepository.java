package com.mahvine.vlocity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mahvine.vlocity.model.ProjectPlan;

@Repository
public interface ProjectPlanRepository extends JpaRepository<ProjectPlan, Long>{
}
