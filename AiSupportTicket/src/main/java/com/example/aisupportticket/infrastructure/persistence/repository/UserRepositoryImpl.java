package com.example.aisupportticket.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.aisupportticket.domain.user.entity.User;
import com.example.aisupportticket.domain.user.repository.UserRepository;
import com.example.aisupportticket.infrastructure.persistence.converter.UserConverter;
import com.example.aisupportticket.infrastructure.persistence.mapper.UserMapper;
import com.example.aisupportticket.infrastructure.persistence.po.UserPO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户仓储实现类
 *实现用户仓储接口，负责用户数据的持久化操作
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    
    private final UserMapper userMapper;
    private final UserConverter userConverter;
    
    @Override
    public User save(User user) {
        UserPO po = userConverter.toPO(user);
        if (user.getId() == null) {
            userMapper.insert(po);
            user.setId(po.getId());
        } else {
            userMapper.updateById(po);
        }
        return user;
    }
    
    @Override
    public Optional<User> findById(Long id) {
        UserPO po = userMapper.selectById(id);
        return Optional.ofNullable(userConverter.toDomain(po));
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getUsername, username);
        UserPO po = userMapper.selectOne(wrapper);
        return Optional.ofNullable(userConverter.toDomain(po));
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getEmail, email);
        UserPO po = userMapper.selectOne(wrapper);
        return Optional.ofNullable(userConverter.toDomain(po));
    }
    
    @Override
    public org.springframework.data.domain.Page<User> findAll(Pageable pageable) {
        Page<UserPO> poPage = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        IPage<UserPO> result = userMapper.selectPage(poPage, null);
        
        java.util.List<User> users = result.getRecords().stream()
                .map(userConverter::toDomain)
                .collect(Collectors.toList());
        
        return new PageImpl<>(users, pageable, result.getTotal());
    }
    
    @Override
    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getUsername, username);
        return userMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public boolean existsByEmail(String email) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getEmail, email);
        return userMapper.selectCount(wrapper) > 0;
    }
}
