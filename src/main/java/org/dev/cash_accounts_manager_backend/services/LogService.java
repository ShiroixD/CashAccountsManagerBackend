package org.dev.cash_accounts_manager_backend.services;

import org.dev.cash_accounts_manager_backend.dtos.LogDto;
import org.dev.cash_accounts_manager_backend.dtos.PagedResponse;
import org.dev.cash_accounts_manager_backend.dtos.UserDto;
import org.dev.cash_accounts_manager_backend.enums.ActionsEnum;
import org.dev.cash_accounts_manager_backend.models.Log;
import org.dev.cash_accounts_manager_backend.models.User;
import org.dev.cash_accounts_manager_backend.repositories.LogRepository;
import org.dev.cash_accounts_manager_backend.utils.Extensions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogService {
    private final LogRepository logRepository;
    private final UserService userService;

    public LogService(LogRepository logRepository, UserService userService) {
        this.logRepository = logRepository;
        this.userService = userService;
    }

    public List<LogDto> allLogs() {
        List<Log> logs = (List<Log>) logRepository.findAll();

        return logs.stream().map(Extensions::asDto).collect(Collectors.toList());
    }

    public PagedResponse<LogDto> allLogs(Pageable pageable) {
        Page<Log> page = logRepository.findAll(pageable);
        List<LogDto> pageLogs = page.getContent().stream().map(Extensions::asDto).collect(Collectors.toList());

        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int currentPageElementsCount = pageLogs.size();
        int totalElementsCount = Math.abs(Math.toIntExact(logRepository.count()));
        int totalPagesCount = (totalElementsCount / pageSize) + (totalElementsCount % pageSize > 0 ? 1 : 0);

        return new PagedResponse<>(pageLogs, pageNumber, pageSize, totalPagesCount, currentPageElementsCount, totalElementsCount);
    }

    public void createLog(ActionsEnum name, String objects, String description) {
        logRepository.save(new Log(name, Extensions.asUser(userService.getCurrentUser()), objects, description));
    }
}
