package com.morningstar.proxy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morningstar.constant.MqConstant;
import com.morningstar.constant.R;
import com.morningstar.exception.BaseException;
import com.morningstar.proxy.dao.mapper.NodeMapper;
import com.morningstar.proxy.dao.mapper.SubMapper;
import com.morningstar.proxy.lib.Country;
import com.morningstar.proxy.lib.NodeParser;
import com.morningstar.proxy.pojo.po.Node;
import com.morningstar.proxy.pojo.po.Sub;
import com.morningstar.proxy.pojo.vo.req.CreateSubRequestVo;
import com.morningstar.proxy.pojo.vo.req.UpdateSubRequestVo;
import com.morningstar.proxy.service.SubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SubServiceImpl implements SubService {

    private final SubMapper subMapper;

    private final NodeMapper nodeMapper;

    private final RabbitTemplate rabbitTemplate;

    private final RestTemplate restTemplate;

    @Override
    public R<Sub> create(CreateSubRequestVo vo) {
        Sub sub = Sub.builder().name(vo.getName()).link(vo.getLink()).build();
        subMapper.insert(sub);

        rabbitTemplate.convertAndSend(MqConstant.proxyExchangeName, MqConstant.subSyncQueueRoutingKey, sub);

        return R.ok(sub);
    }

    @Override
    public R<List<Sub>> getAll() {
        return R.ok(subMapper.selectList(null));
    }

    @Override
    public R<Sub> get(Integer id) {
        return R.ok(subMapper.selectById(id));
    }

    @Override
    public R<Sub> update(UpdateSubRequestVo vo) {
        int count = subMapper.updateById(Sub.builder().id(vo.getId()).name(vo.getName()).link(vo.getLink()).build());
        if (count != 1) {
            throw new BaseException(String.format("订阅\"%d\"更新失败", vo.getId()));
        }
        return R.ok(subMapper.selectById(vo.getId()));
    }

    @Override
    public R<Object> delete(Integer id) {
        int count = subMapper.deleteById(id);
        if (count != 1) {
            throw new BaseException(String.format("订阅\"%d\"删除失败", id));
        }
        return R.ok();
    }

    @Override
    public void sync(Sub sub) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(sub.getLink(), String.class);
            String[] originLinks = new String(Base64.getDecoder().decode(response.getBody())).split("\n");

            // 获取新节点数组
            com.morningstar.proxy.lib.Node[] configs = Arrays.stream(originLinks).map(NodeParser::parse).toArray(com.morningstar.proxy.lib.Node[]::new);

            // 重新设置节点名称
            Map<Country, Integer> map = new HashMap<>();
            for(com.morningstar.proxy.lib.Node config : configs) {
                Country country = config.getCountry();
                if(country == null) {
                    continue;
                }
                if(map.containsKey(country)) {
                    map.put(country, map.get(country) + 1);
                }else {
                    map.put(country, 1);
                }
                config.setName("Morningstar" + sub.getId() + "_" + country.name() + map.get(country));
            }

            // 删除旧节点
            LambdaQueryWrapper<Node> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Node::getId, sub.getId());
            nodeMapper.delete(queryWrapper);

            // 添加新节点
            String[] newLinks = Arrays.stream(configs).map(com.morningstar.proxy.lib.Node::format).toArray(String[]::new);
            nodeMapper.insert(
                    Arrays
                            .stream(newLinks)
                            .map(link -> Node.builder().link(link).subId(sub.getId()).build()).toList()
            );

        } catch (IllegalArgumentException e){
            log.error(e.getMessage());
        }
    }
}
