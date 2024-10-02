package com.morningstar.common.service.impl;

import com.morningstar.common.pojo.vo.req.EmailContactRequestVo;
import com.morningstar.common.service.ContactService;
import com.morningstar.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ContactServiceImpl implements ContactService {
    private final EmailUtil emailUtil;

    @Override
    public void contactByEmail(EmailContactRequestVo vo) {
        String subject = String.format("访客联系 - %s - %s", vo.getEmail(), vo.getSubject());
        emailUtil.sendSimpleEmailToAdmin(subject, vo.getContent());
    }
}
