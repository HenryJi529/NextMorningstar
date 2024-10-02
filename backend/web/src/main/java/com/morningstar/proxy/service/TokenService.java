package com.morningstar.proxy.service;

import com.morningstar.proxy.pojo.po.Token;
import com.morningstar.proxy.pojo.vo.req.CreateTokenRequestVo;
import com.morningstar.proxy.pojo.vo.req.UpdateTokenRequestVo;

import java.util.List;

public interface TokenService {
    Token create(CreateTokenRequestVo vo);

    void delete(Integer id);

    List<Token> getAll();

    Token update(UpdateTokenRequestVo vo);

    Token get(Integer id);
}
