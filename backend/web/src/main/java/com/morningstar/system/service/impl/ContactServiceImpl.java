package com.morningstar.system.service.impl;

import com.morningstar.system.pojo.vo.req.EmailContactRequestVo;
import com.morningstar.system.service.ContactService;
import com.morningstar.infra.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {
    private final EmailUtil emailUtil;

    @Override
    public void contactByEmail(EmailContactRequestVo vo) {
        String subject = String.format("访客联系 - %s - %s", vo.getEmail(), vo.getSubject());
        emailUtil.sendSimpleEmailToAdmin(subject, vo.getContent());
    }
}
