package com.peelie.questionnaire.infra.category;

import com.peelie.questionnaire.domain.category.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    // 서브카테고리 ID와 부모 카테고리 ID가 모두 일치하는 레코드 조회
    @Query("SELECT sc " +
            "FROM SubCategory sc " +
            "WHERE sc.id = :subCategoryId AND sc.category.id = :categoryId")
    Optional<SubCategory> findByIds(@Param("categoryId") Long categoryId, @Param("subCategoryId") Long subCategoryId);
}
