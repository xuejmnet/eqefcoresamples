package com.eq.eqefcore.domain;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Navigate;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.enums.RelationTypeEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.eq.eqefcore.domain.proxy.CommentProxy;
import com.eq.eqefcore.domain.proxy.PostProxy;
import com.eq.eqefcore.domain.proxy.UserProxy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * create time 2025/10/26 21:19
 * 文件说明
 *
 * @author xuejiaming
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EntityProxy
@Table("t_user")
public class User implements ProxyEntityAvailable<User, UserProxy> {

    private String id;
    private String username;

    @Navigate(value = RelationTypeEnum.OneToMany, selfProperty = {UserProxy.Fields.id}, targetProperty = {PostProxy.Fields.userId})
    private List<Post> posts;

    @Navigate(value = RelationTypeEnum.OneToMany, selfProperty = {UserProxy.Fields.id}, targetProperty = {CommentProxy.Fields.userId})
    private List<Comment> comments;
}
