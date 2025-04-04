package inkspiration.backend.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inkspiration.backend.repository.TokenRevogadoRepository;

@Service
public class TokenLimpezaService {

    private final TokenRevogadoRepository tokenRevogadoRepository;
    private static final long DEZ_HORAS_EM_MS = 36000000; // 10 horas em milissegundos

    @Autowired
    public TokenLimpezaService(TokenRevogadoRepository tokenRevogadoRepository) {
        this.tokenRevogadoRepository = tokenRevogadoRepository;
    }

    @Scheduled(initialDelay = DEZ_HORAS_EM_MS, fixedDelay = DEZ_HORAS_EM_MS)
    @Transactional
    public void limparTokensExpirados() {
        LocalDateTime limiteExpiracao = LocalDateTime.now().minusHours(10);
        System.out.println("Iniciando limpeza de tokens expirados antes de: " + limiteExpiracao);
        tokenRevogadoRepository.deleteByDataRevogacaoBefore(limiteExpiracao);
        System.out.println("Limpeza de tokens concluída");
    }
} 