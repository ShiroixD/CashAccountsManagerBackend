package org.dev.cash_accounts_manager_backend.services;

import org.dev.cash_accounts_manager_backend.dtos.LogDto;
import org.dev.cash_accounts_manager_backend.dtos.PagedResponse;
import org.dev.cash_accounts_manager_backend.enums.ActionsEnum;
import org.dev.cash_accounts_manager_backend.models.Log;
import org.dev.cash_accounts_manager_backend.models.User;
import org.dev.cash_accounts_manager_backend.repositories.LogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogService {
    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public List<LogDto> allLogs() {
        List<Log> logs = (List<Log>) logRepository.findAll();
        List<LogDto> logsDtos = logs.stream().map(x -> x.toDto()).collect(Collectors.toList());

        return logsDtos;
    }

    public PagedResponse<LogDto> allLogs(Pageable pageable) {
        Page<Log> page = logRepository.findAll(pageable);
        List<LogDto> pageLogs = page.getContent().stream().map(x -> x.toDto()).collect(Collectors.toList());

        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int currentPageElementsCount = pageLogs.size();
        int totalElementsCount = Math.abs(Math.toIntExact(logRepository.count()));
        int totalPagesCount = (totalElementsCount / pageSize) + (totalElementsCount % pageSize > 0 ? 1 : 0);

        return new PagedResponse<LogDto>(pageLogs, pageNumber, pageSize, totalPagesCount, currentPageElementsCount, totalElementsCount);
    }

    public LogDto createLog(ActionsEnum name, User user, String objects, String description) {
        Log log = new Log(name, user, objects, description);

        return logRepository.save(log).toDto();
    }
}
