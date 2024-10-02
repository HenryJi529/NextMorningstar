package com.morningstar.system.service;

import com.morningstar.system.pojo.vo.req.EmailContactRequestVo;

public interface ContactService {
    void contactByEmail(EmailContactRequestVo vo);
}
