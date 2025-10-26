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

import java.time.LocalDateTime;

/**
 * create time 2025/10/26 21:23
 * 文件说明
 *
 * @author xuejiaming
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EntityProxy
@Table("t_comment")
public class Comment implements ProxyEntityAvailable<Comment, CommentProxy> {

    private String id;
    private String userId;
    private String postId;
    private String text;
    private LocalDateTime createAt;

    @Navigate(value = RelationTypeEnum.ManyToOne, selfProperty = {CommentProxy.Fields.postId}, targetProperty = {PostProxy.Fields.id})
    private Post post;
}
