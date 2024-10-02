package com.morningstar.proxy.scheduled;

import com.morningstar.proxy.dao.mapper.SubMapper;
import com.morningstar.proxy.pojo.po.Sub;
import com.morningstar.proxy.service.SubService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProxyCronTask {
    private final SubService subService;

    private final SubMapper subMapper;

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 6)
    public void syncSub() {
        List<Sub> subs = subMapper.selectList(null);
        for (Sub sub : subs) {
            subService.sync(sub);
        }
    }
}
