package com.morningstar.common.service;

import com.morningstar.common.pojo.vo.req.EmailContactRequestVo;

public interface ContactService {
    void contactByEmail(EmailContactRequestVo vo);
}
