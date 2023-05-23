package com.enterpriseintellijence.enterpriseintellijence.data.repository;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.ProductImage;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserImageRepository extends JpaRepository<UserImage,String>, JpaSpecificationExecutor<UserImage> {
}
