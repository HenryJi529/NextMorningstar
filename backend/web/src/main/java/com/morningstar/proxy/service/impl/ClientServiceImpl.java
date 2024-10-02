package com.morningstar.proxy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morningstar.proxy.dao.mapper.NodeMapper;
import com.morningstar.proxy.dao.mapper.TokenMapper;
import com.morningstar.proxy.pojo.po.Node;
import com.morningstar.proxy.pojo.po.Token;
import com.morningstar.proxy.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final TokenMapper tokenMapper;
    private final NodeMapper nodeMapper;

    @Override
    public List<Node> getAssociatedNodes(String token) {
        LambdaQueryWrapper<Token> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Token::getValue, token);
        if (tokenMapper.selectOne(queryWrapper) == null) {
            return null;
        }
        return nodeMapper.selectList(null);
    }
}
