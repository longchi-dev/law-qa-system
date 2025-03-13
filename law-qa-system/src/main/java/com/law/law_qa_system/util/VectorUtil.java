package com.law.law_qa_system.util;

import java.util.List;

public class VectorUtil {
    public static double cosineSimilarity(List<Double> vec1, List<Double> vec2) {
        if (vec1.size() != vec2.size()) {
            throw new IllegalArgumentException("Vector size mismatch");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vec1.size(); i++) {
            dotProduct += vec1.get(i) * vec2.get(i);
            normA += Math.pow(vec1.get(i), 2);
            normB += Math.pow(vec2.get(i), 2);
        }

        return (normA == 0 || normB == 0) ? 0 : dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
