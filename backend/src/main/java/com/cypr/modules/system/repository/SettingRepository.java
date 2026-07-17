package com.cypr.modules.system.repository;

import com.cypr.modules.system.entity.Setting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SettingRepository extends JpaRepository<Setting, UUID>, JpaSpecificationExecutor<Setting> {

    Optional<Setting> findBySettingKey(String settingKey);

    Page<Setting> findByGroup(String group, Pageable pageable);

    @org.springframework.data.jpa.repository.Query(value = "SELECT * FROM settings s WHERE " +
           "to_tsvector('english', coalesce(s.setting_key, '') || ' ' || coalesce(s.setting_value, '') || ' ' || coalesce(s.setting_group, '')) @@ websearch_to_tsquery('english', :query) " +
           "ORDER BY ts_rank(to_tsvector('english', coalesce(s.setting_key, '') || ' ' || coalesce(s.setting_value, '') || ' ' || coalesce(s.setting_group, '')), websearch_to_tsquery('english', :query)) DESC",
           countQuery = "SELECT count(*) FROM settings s WHERE " +
           "to_tsvector('english', coalesce(s.setting_key, '') || ' ' || coalesce(s.setting_value, '') || ' ' || coalesce(s.setting_group, '')) @@ websearch_to_tsquery('english', :query)",
           nativeQuery = true)
    Page<Setting> searchGlobal(@org.springframework.data.repository.query.Param("query") String query, Pageable pageable);
}
