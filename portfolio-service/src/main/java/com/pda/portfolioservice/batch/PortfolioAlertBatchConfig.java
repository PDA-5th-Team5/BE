package com.pda.portfolioservice.batch;

import com.pda.portfolioservice.dto.response.StockResponseDTO;
import com.pda.portfolioservice.dto.response.StockSearchResponseDTO;
import com.pda.portfolioservice.entity.PortfolioAlert;
import com.pda.portfolioservice.feign.UserServiceClient;
import com.pda.portfolioservice.feign.StockServiceClient;
import com.pda.portfolioservice.model.Portfolio;
import com.pda.portfolioservice.repository.PortfolioAlertRepository;
import com.pda.portfolioservice.service.PortfolioService;
import com.pda.portfolioservice.service.TelegramBotService;
import com.pda.utilservice.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static org.apache.commons.lang.time.DateUtils.round;

@Configuration
@RequiredArgsConstructor
public class PortfolioAlertBatchConfig {

    private final PortfolioAlertRepository portfolioAlertRepository;
    private final TelegramBotService telegramBotService;
    private final UserServiceClient userServiceClient;
    private final PortfolioService portfolioService;

    /**
     *  알림 데이터를 읽는 Reader (ListItemReader 사용)
     */
    @Bean
    @StepScope
    public ListItemReader<PortfolioAlert> portfolioAlertReader() {
        List<PortfolioAlert> alerts;
        try {
            alerts = portfolioAlertRepository.findAll();
        } catch (Exception e) {
            System.err.println("❌ [Reader] 포트폴리오 알림 데이터를 조회하는 중 오류 발생: " + e.getMessage());
            alerts = List.of();  // 빈 리스트 반환
        }

        System.out.println(" [배치 시작] 총 " + alerts.size() + "개의 포트폴리오 알림을 읽음.");
        return new ListItemReader<>(alerts);
    }

    /**
     *  텔레그램 메시지 변환 Processor
     */
    @Bean
    public ItemProcessor<PortfolioAlert, String> portfolioAlertProcessor() {
        return alert -> {
            if (alert.getMyPortfolio() == null) {
                System.err.println("⚠️ [Processor] 포트폴리오 ID가 존재하지 않는 알림: " + alert.getAlertId());
                return null; // 예외 발생 방지 및 필터링
            }

            LocalDateTime now = LocalDateTime.now(TimeZone.getTimeZone("Asia/Seoul").toZoneId());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.KOREA);
            String formattedDateTime = now.format(formatter);

            System.out.println("🔄 [Processor] 포트폴리오 ID: " + alert.getMyPortfolio().getMyPortfolioId());

            Portfolio portfolio;
            try {
                portfolio = portfolioService.getPortfolio("my", alert.getMyPortfolio().getMyPortfolioId());
            } catch (Exception e) {
                System.err.println("❌ [Processor] 포트폴리오 ID " + alert.getMyPortfolio().getMyPortfolioId() + " 정보를 가져오는 중 오류 발생: " + e.getMessage());
                return null;
            }

            System.out.println("[Processor] 포트폴리오 제목: " + portfolio.getTitle());

            StockSearchResponseDTO stockSearch;
            try {
                stockSearch = portfolioService.getPortfolioStock(portfolio, 0, null, 24);
            } catch (Exception e) {
                System.err.println("❌ [Processor] 포트폴리오 종목 데이터를 가져오는 중 오류 발생: " + e.getMessage());
                return null;
            }

            List<StockResponseDTO> stocks = stockSearch.getStocks();
            Long totalCount = stockSearch.getTotalCount();

            StringBuilder sb = new StringBuilder();
            sb.append(formattedDateTime).append("\n");
            sb.append("[포트폴리오 업데이트 : ").append(portfolio.getTitle()).append("]\n");

            if (stocks != null) {
                System.out.println("📊 [Processor] " + totalCount + "개의 종목 데이터를 가져옴.");
                for (StockResponseDTO stock : stocks) {
                    sb.append(stock.getCompanyName()).append(" : ")
                            .append(stock.getCurrentPrice()).append("원  (")
                            .append((int) Math.round(stock.getChangeRate() * 1000.0) / 10.0).append("%)\n");
                }
            } else {
                sb.append(" 종목 정보를 가져올 수 없음\n");
                System.out.println("⚠️ [Processor] 포트폴리오 ID: " + alert.getMyPortfolio().getMyPortfolioId() + "의 종목 데이터를 가져올 수 없음.");
            }

            return sb.toString().trim();
        };
    }
    /**
     *  메시지를 텔레그램으로 전송하는 Writer
     */
    @Bean
    public ItemWriter<String> portfolioAlertWriter() {
        return messages -> {
            List<PortfolioAlert> alerts;
            try {
                alerts = portfolioAlertRepository.findAll();
            } catch (Exception e) {
                System.err.println("❌ [Writer] 포트폴리오 알림 데이터를 조회하는 중 오류 발생: " + e.getMessage());
                return; // 오류 발생 시 실행 중단
            }

            System.out.println("📩 [Writer] 총 " + messages.size() + "개의 메시지를 전송할 예정.");

            for (PortfolioAlert alert : alerts) {
                String userId = alert.getUserId();
                ApiResponse<String> response;
                try {
                    response = userServiceClient.getTelegramChatId(userId);
                } catch (Exception e) {
                    System.err.println("❌ [Writer] 유저 ID: " + userId + " | Telegram Chat ID 조회 중 오류 발생: " + e.getMessage());
                    continue;
                }

                if (response.getStatus() == 200 && response.getData() != null) {
                    String chatId = response.getData();
                    System.out.println("📨 [Writer] 유저 ID: " + userId + " | Chat ID: " + chatId);

                    for (String message : messages) {
                        try {
                            System.out.println("📤 [Writer] 메시지 전송 -> " + message);
                            telegramBotService.sendMessage(chatId, message);
                        } catch (Exception e) {
                            System.err.println("❌ [Writer] 텔레그램 메시지 전송 중 오류 발생: " + e.getMessage());
                        }
                    }
                } else {
                    System.out.println("⚠️ [Writer] 유저 " + userId + "의 텔레그램 Chat ID를 찾을 수 없음");
                }
            }
        };
    }
    /**
     *  스텝 설정 (Spring Boot 3.x 이상)
     */
    @Bean
    public Step portfolioAlertStep(JobRepository jobRepository,
                                   PlatformTransactionManager transactionManager,
                                   ListItemReader<PortfolioAlert> reader,
                                   ItemProcessor<PortfolioAlert, String> processor,
                                   ItemWriter<String> writer) {
        return new StepBuilder("portfolioAlertStep", jobRepository)
                .<PortfolioAlert, String>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skip(Exception.class) // 예외 발생 시 Step이 중단되지 않음
                .skipLimit(10) // 최대 10개의 예외 허용
                .build();
    }

    /**
     *  배치 작업 등록 (Spring Boot 3.x 이상)
     */
    @Bean
    public Job portfolioAlertJob(JobRepository jobRepository, Step portfolioAlertStep) {
        return new JobBuilder("portfolioAlertJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(portfolioAlertStep)
                .build();
    }
}