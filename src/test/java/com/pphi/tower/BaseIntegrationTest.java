package com.pphi.tower;

import com.pphi.tower.repository.GoogleDriveRepository;
import com.pphi.tower.repository.GoogleSheetsRepository;
import com.pphi.tower.service.ReportFetcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class BaseIntegrationTest {

    @Autowired protected MockMvc mvc;
    @Autowired protected JdbcTemplate jdbc;

    // Replace these with mocks so lazy OAuth/Drive/Sheets init is never triggered
    @MockBean protected GoogleDriveRepository googleDriveRepository;
    @MockBean protected GoogleSheetsRepository googleSheetsRepository;
    @MockBean protected ReportFetcherService reportFetcherService;
}