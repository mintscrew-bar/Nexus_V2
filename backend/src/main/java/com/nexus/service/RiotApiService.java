package com.nexus.service;

import com.nexus.config.RiotApiProperties;
import com.nexus.dto.RiotApiDto.ProviderRegistrationRequest;
import com.nexus.dto.RiotApiDto.TournamentCodeRequest;
import com.nexus.dto.RiotApiDto.TournamentRegistrationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class RiotApiService {

    private final WebClient webClient;
    private final RiotApiProperties riotApiProperties;

    public RiotApiService(WebClient.Builder webClientBuilder, @Value("${RIOT_API_KEY}") String riotApiKey, RiotApiProperties riotApiProperties) {
        this.webClient = webClientBuilder
                .baseUrl("https://americas.api.riotgames.com")
                .defaultHeader("X-Riot-Token", riotApiKey)
                .build();
        this.riotApiProperties = riotApiProperties;
    }

    public Mono<Long> createProvider() {
        ProviderRegistrationRequest request = new ProviderRegistrationRequest(riotApiProperties.getCallbackUrl());
        // useStubApi 값에 따라 동적으로 경로를 결정합니다.
        String path = riotApiProperties.isUseStub() ? "/lol/tournament-stub/v5/providers" : "/lol/tournament/v5/providers";
        
        return this.webClient.post()
                .uri(path)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Long.class);
    }

    /**
     * 토너먼트를 등록합니다.
     * @param providerId Provider ID
     * @param tournamentName 토너먼트 이름
     * @return 생성된 Tournament ID (Mono<Long>)
     */
    public Mono<Long> createTournament(long providerId, String tournamentName) {
        TournamentRegistrationRequest request = new TournamentRegistrationRequest(tournamentName, providerId);
        // useStubApi 값에 따라 동적으로 경로를 결정합니다.
        String path = riotApiProperties.isUseStub() ? "/lol/tournament-stub/v5/tournaments" : "/lol/tournament/v5/tournaments";

        return this.webClient.post()
                .uri(path)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Long.class);
    }

    /**
     * 토너먼트 코드를 생성합니다.
     * @param request DTO
     * @param tournamentId 토너먼트 ID
     * @return 생성된 토너먼트 코드 목록 (Mono<List<String>>)
     */
    public Mono<List<String>> createTournamentCodes(TournamentCodeRequest request, long tournamentId) {
        // useStubApi 값에 따라 동적으로 경로를 결정합니다.
        String path = riotApiProperties.isUseStub() ? "/lol/tournament-stub/v5/codes" : "/lol/tournament/v5/codes";

        return this.webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("count", 1) 
                        .queryParam("tournamentId", tournamentId)
                        .build())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {});
    }
}