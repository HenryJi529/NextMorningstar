package com.morningstar.common.service;

import com.morningstar.common.pojo.vo.req.EmailContactRequestVo;
import com.morningstar.constant.R;

public interface ContactService {
    R<Object> contactByEmail(EmailContactRequestVo vo);
}
