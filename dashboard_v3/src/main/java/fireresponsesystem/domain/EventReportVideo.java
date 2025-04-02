package fireresponsesystem.domain;

import jakarta.persistence.*;
import java.util.List;
import java.util.Date;
import lombok.Data;
import java.time.LocalDate;


//<<< EDA / CQRS
@Entity
@Table(name="EventReportVideo_table")
@Data
public class EventReportVideo {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
        
        private Long reportId;
        private Long eventId;
        private Long videoId;
        private Long videoAnalysisId;
        private String userId;
        private Float longitude;
        private Float latitude;
        private String description;
        private String status;
        private String eventType;
        private Date resolvedAt;
        private Boolean analyzeSuccess;
        private Boolean fireDetected;
        private String severity;
        private String analyzedData;
        private String originalVideoUri;
        private String encodedVideoUri;
        private List<String> tags;
        private Date createdAt;
        private Date updatedAt;
        private Boolean verified;
        private Long postId;
        private Integer reatCount;
        private Integer commentCount;


}
