package com.nexus.service;

import com.nexus.dto.RiotApiDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference; // ParameterizedTypeReference를 import 합니다.
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class RiotApiService {

    private final WebClient webClient;
    // riotApiKey 필드는 더 이상 필요 없으므로 삭제합니다.

    public RiotApiService(WebClient.Builder webClientBuilder, @Value("${RIOT_API_KEY}") String riotApiKey) {
        this.webClient = webClientBuilder
                .baseUrl("https://americas.api.riotgames.com")
                // 생성자에서 받은 riotApiKey를 직접 사용하여 헤더를 설정합니다.
                .defaultHeader("X-Riot-Token", riotApiKey)
                .build();
    }
    
    /**
     * STUB API를 사용하여 가짜 토너먼트 코드를 생성합니다.
     * @param request DTO
     * @param tournamentId 토너먼트 ID
     * @return 생성된 토너먼트 코드 목록 (Mono<List<String>>)
     */
    public Mono<List<String>> createStubTournamentCodes(RiotApiDto.TournamentCodeRequest request, long tournamentId) {
        return this.webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/lol/tournament-stub/v5/codes")
                        .queryParam("count", 1)
                        .queryParam("tournamentId", tournamentId)
                        .build())
                .bodyValue(request)
                .retrieve()
                // bodyToMono(List.class) 대신 ParameterizedTypeReference를 사용하여 정확한 타입 정보를 전달합니다.
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {});
    }

    // TODO: 실제 토너먼트 Provider, Tournament를 등록하는 메서드도 여기에 추가됩니다.
}