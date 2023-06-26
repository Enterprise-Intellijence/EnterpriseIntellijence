package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ReportStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
public class ReportDTO {

    private String id;

    private UserBasicDTO reporterUser;

    private String description;

    private UserBasicDTO reportedUser;

    private ProductBasicDTO reportedProduct;

    private LocalDateTime date;

    private LocalDateTime lastUpdate;

    private ReportStatus status;

    private UserBasicDTO adminFollowedReport;
}
