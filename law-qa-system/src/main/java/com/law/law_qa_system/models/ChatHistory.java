package com.law.law_qa_system.models;

import com.law.law_qa_system.models.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_chat_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatHistory extends BaseEntity {  // Kế thừa từ BaseEntity

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;  // Thời gian bắt đầu cuộc trò chuyện

    @Column(name = "end_time")
    private LocalDateTime endTime;  // Thời gian kết thúc (nếu có)

    @Column(name = "status", nullable = false)
    private String status;  // Trạng thái cuộc trò chuyện (active, ended, ...)

    @Column(name = "title")
    private String title;

}
