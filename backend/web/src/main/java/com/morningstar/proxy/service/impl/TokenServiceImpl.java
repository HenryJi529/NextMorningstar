package com.morningstar.proxy.service.impl;

import com.morningstar.exception.BaseException;
import com.morningstar.proxy.dao.mapper.TokenMapper;
import com.morningstar.proxy.pojo.po.Token;
import com.morningstar.proxy.pojo.vo.req.CreateTokenRequestVo;
import com.morningstar.proxy.pojo.vo.req.UpdateTokenRequestVo;
import com.morningstar.proxy.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenServiceImpl implements TokenService {

    private final TokenMapper tokenMapper;

    @Override
    public Token create(CreateTokenRequestVo vo) {
        String value = !StringUtils.isBlank(vo.getValue()) ? vo.getValue() : UUID.randomUUID().toString();
        Token token = Token.builder().name(vo.getName()).value(value).build();
        tokenMapper.insert(token);
        return token;
    }

    @Override
    public void delete(Integer id) {
        int count = tokenMapper.deleteById(id);
        if (count != 1) {
            throw new BaseException(String.format("令牌\"%d\"删除失败", id));
        }
    }

    @Override
    public List<Token> getAll() {
        return tokenMapper.selectList(null);
    }

    @Override
    public Token update(UpdateTokenRequestVo vo) {
        int count = tokenMapper.updateById(Token.builder().id(vo.getId()).name(vo.getName()).value(vo.getValue()).build());
        if (count != 1) {
            throw new BaseException(String.format("令牌\"%d\"更新失败", vo.getId()));
        }
        return tokenMapper.selectById(vo.getId());
    }

    @Override
    public Token get(Integer id) {
        return tokenMapper.selectById(id);
    }
}
