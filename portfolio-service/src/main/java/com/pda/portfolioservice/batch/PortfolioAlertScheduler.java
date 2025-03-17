package com.pda.portfolioservice.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PortfolioAlertScheduler {

    private final JobLauncher jobLauncher;
    private final Job portfolioAlertJob;

    /**
     *  매일 아침 8시 배치 실행
     */
//    @Scheduled(cron = "0 0 8 * * ?")
    @Scheduled(cron = "0 0 8 * * 1-5")
    public void runBatchJob() {
        try {
            System.out.println("🔔 [Scheduler] 배치 실행 시작");

            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())  // 매 실행마다 다른 값 추가
                    .toJobParameters();

            jobLauncher.run(portfolioAlertJob, jobParameters);

            System.out.println("[Scheduler] 포트폴리오 배치 작업 실행 완료");
        } catch (Exception e) {
            System.out.println("x [Scheduler] 포트폴리오 배치 작업 실행 실패");
            e.printStackTrace();
        }
    }
}