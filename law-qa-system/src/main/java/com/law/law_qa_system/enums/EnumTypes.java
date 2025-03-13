package com.law.law_qa_system.enums;

public class EnumTypes {
    public enum AccountRole {
        ADMIN, USER
    }

    public enum AccountStatus {
        ACTIVE, INACTIVE, BANNED
    }

    public enum DocumentTypeEnum {
        DECREES("Nghị định"),
        CIRCULARS("Thông tư"),
        DECISIONS("Quyết định"),
        LAWS("Luật"),
        ORDERS("Lệnh"),
        OFFICIAL_LETTERS("Công văn"),
        UBND("Ủy ban nhân dân"),
        DRAFT("Dự thảo"),
        RESOLUTIONS("Nghị quyết");

        private final String label;

        DocumentTypeEnum(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static DocumentTypeEnum fromTitle(String title) {
            for (DocumentTypeEnum type : DocumentTypeEnum.values()) {
                if (type.getLabel().equalsIgnoreCase(title)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown document type: " + title);
        }
    }


    public enum TransactionType {
        DEPOSIT, DEDUCTION
    }

    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED
    }

    public enum AuthProvider {
        LOCAL, GOOGLE, FACEBOOK
    }

}
