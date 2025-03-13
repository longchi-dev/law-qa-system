package com.law.law_qa_system.models;

import com.law.law_qa_system.models.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_chat_detail_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatDetailHistory extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "chat_id", referencedColumnName = "id")
    private ChatHistory chatHistory;

    private String sessionId;

    @Lob
    private String question;

    @Lob
    private String response;
}
