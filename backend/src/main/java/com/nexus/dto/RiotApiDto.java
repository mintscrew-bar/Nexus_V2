// backend/src/main/java/com/nexus/dto/RiotApiDto.java
package com.nexus.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class RiotApiDto {

    // Tournament Provider 등록 요청 DTO
    @Getter
    @Setter
    public static class ProviderRegistrationRequest {
        private String region; // "KR"
        private String url;    // 콜백을 받을 URL (Nexus 서버의 특정 엔드포인트)

        public ProviderRegistrationRequest(String callbackUrl) {
            this.region = "KR";
            this.url = callbackUrl;
        }
    }

    // Tournament 생성 요청 DTO
    @Getter
    @Setter
    public static class TournamentRegistrationRequest {
        private String name;
        private long providerId;

        public TournamentRegistrationRequest(String name, long providerId) {
            this.name = name;
            this.providerId = providerId;
        }
    }

    // Tournament Code 생성 요청 DTO
    @Getter
    @Setter
    public static class TournamentCodeRequest {
        private String mapType;
        private String pickType;
        private String spectatorType;
        private int teamSize;
        private List<String> allowedParticipants; // PUUID 목록 (선택 사항)
        private String metadata;
    }
}