package com.hmwl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmwl.entity.Menu;
import com.hmwl.mapper.MenuMapper;
import com.hmwl.service.MenuService;
import org.springframework.stereotype.Service;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
}
