package com.law.law_qa_system.models.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentDTO {
    @JsonProperty("SoHieu")
    private String soHieu; // number

    @JsonProperty("TrichYeu")
    private String trichYeu; // content

    @JsonProperty("NgayBanHanh")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime ngayBanHanh; // issueDate

    @JsonProperty("NgayHieuLuc")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime ngayHieuLuc; // effectiveDate

    @JsonProperty("IsEn")
    private boolean isEn; // isEn

    @JsonProperty("CoQuanBanHanh")
    private List<Agency> coQuanBanHanh;

    @JsonProperty("NguoiKy")
    private List<Signer> nguoiKy; // signer

    @JsonProperty("LinhVuc")
    private List<Field> linhVuc;

    @JsonProperty("Poster")
    private String poster;

    @JsonProperty("LoaiVanBan")
    private DocumentType loaiVanBan;

    @JsonProperty("KieuVanBan")
    private Object kieuVanBan;

    @JsonProperty("TrinhTrangHieuLuc")
    private DocumentStatus trinhTrangHieuLuc;

    @JsonProperty("LuocDo")
    private Object luocDo;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("UID")
    private String uid;

    @JsonProperty("Updated")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updated;

    @Data
    public static class Agency {
        @JsonProperty("Title")
        private String title;

        @JsonProperty("UID")
        private String uid;
    }

    @Data
    public static class Signer {
        @JsonProperty("Title")
        private String title;

        @JsonProperty("UID")
        private String uid;
    }

    @Data
    public static class Field {
        @JsonProperty("Title")
        private String title;

        @JsonProperty("UID")
        private String uid;
    }

    @Data
    public static class DocumentType {
        @JsonProperty("Title")
        private String title;

        @JsonProperty("UID")
        private String uid;
    }

    @Data
    public static class DocumentStatus {
        @JsonProperty("Title")
        private String title;

        @JsonProperty("UID")
        private String uid;
    }
}
