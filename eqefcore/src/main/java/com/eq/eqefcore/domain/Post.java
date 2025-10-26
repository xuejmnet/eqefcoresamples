package com.eq.eqefcore.domain;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Navigate;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.enums.RelationTypeEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.eq.eqefcore.domain.proxy.CategoryProxy;
import com.eq.eqefcore.domain.proxy.CommentProxy;
import com.eq.eqefcore.domain.proxy.PostProxy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * create time 2025/10/26 21:21
 * 文件说明
 *
 * @author xuejiaming
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EntityProxy
@Table("t_post")
public class Post implements ProxyEntityAvailable<Post , PostProxy> {
    private String id;
    private String content;
    private String userId;
    private String categoryId;


    @Navigate(value = RelationTypeEnum.ManyToOne, selfProperty = {PostProxy.Fields.categoryId}, targetProperty = {CategoryProxy.Fields.id})
    private Category category;

    @Navigate(value = RelationTypeEnum.OneToMany, selfProperty = {PostProxy.Fields.id}, targetProperty = {CommentProxy.Fields.postId})
    private List<Comment> comments;
}
