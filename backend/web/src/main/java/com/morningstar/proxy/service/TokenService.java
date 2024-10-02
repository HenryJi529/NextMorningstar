package com.morningstar.proxy.service;

import com.morningstar.constant.R;
import com.morningstar.proxy.pojo.po.Token;
import com.morningstar.proxy.pojo.vo.req.CreateTokenRequestVo;
import com.morningstar.proxy.pojo.vo.req.UpdateTokenRequestVo;

import java.util.List;

public interface TokenService {
    R<Token> create(CreateTokenRequestVo vo);

    R<Object> delete(Integer id);

    R<List<Token>> getAll();

    R<Token> update(UpdateTokenRequestVo vo);

    R<Token> get(Integer id);
}
