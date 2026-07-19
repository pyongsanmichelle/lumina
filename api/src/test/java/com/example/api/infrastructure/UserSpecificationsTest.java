package com.example.api.infrastructure;

import com.example.api.domain.User;
import com.example.api.domain.UserStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserSpecificationsのテスト")
class UserSpecificationsTest {

    @Mock
    private Root<User> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Test
    @DisplayName("nameContains_値ありの場合はlike述語を生成する")
    void nameContains_値ありの場合はlike述語を生成する() {
        @SuppressWarnings("unchecked")
        Path<String> namePath = mock(Path.class);
        @SuppressWarnings("unchecked")
        Expression<String> lowered = mock(Expression.class);
        Predicate likePredicate = mock(Predicate.class);

        when(root.<String>get("name")).thenReturn(namePath);
        when(cb.lower(namePath)).thenReturn(lowered);
        when(cb.like(lowered, "%管理%")).thenReturn(likePredicate);

        Predicate result = UserSpecifications.nameContains("管理").toPredicate(root, query, cb);

        assertThat(result).isSameAs(likePredicate);
        verify(cb).like(lowered, "%管理%");
    }

    @Test
    @DisplayName("nameContains_nullの場合は常に真の述語を返す")
    void nameContains_nullの場合は常に真の述語を返す() {
        Predicate conjunction = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(conjunction);

        Predicate result = UserSpecifications.nameContains(null).toPredicate(root, query, cb);

        assertThat(result).isSameAs(conjunction);
        verifyNoInteractions(root);
    }

    @Test
    @DisplayName("nameContains_空文字の場合は常に真の述語を返す")
    void nameContains_空文字の場合は常に真の述語を返す() {
        Predicate conjunction = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(conjunction);

        Predicate result = UserSpecifications.nameContains("").toPredicate(root, query, cb);

        assertThat(result).isSameAs(conjunction);
    }

    @Test
    @DisplayName("emailStartsWith_値ありの場合は前方一致like述語を生成する")
    void emailStartsWith_値ありの場合は前方一致like述語を生成する() {
        @SuppressWarnings("unchecked")
        Path<String> emailPath = mock(Path.class);
        @SuppressWarnings("unchecked")
        Expression<String> lowered = mock(Expression.class);
        Predicate likePredicate = mock(Predicate.class);

        when(root.<String>get("email")).thenReturn(emailPath);
        when(cb.lower(emailPath)).thenReturn(lowered);
        when(cb.like(lowered, "admin@%")).thenReturn(likePredicate);

        Predicate result =
            UserSpecifications.emailStartsWith("Admin@").toPredicate(root, query, cb);

        assertThat(result).isSameAs(likePredicate);
    }

    @Test
    @DisplayName("emailStartsWith_nullの場合は常に真の述語を返す")
    void emailStartsWith_nullの場合は常に真の述語を返す() {
        Predicate conjunction = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(conjunction);

        Predicate result = UserSpecifications.emailStartsWith(null).toPredicate(root, query, cb);

        assertThat(result).isSameAs(conjunction);
    }

    @Test
    @DisplayName("statusEquals_値ありの場合は等価述語を生成する")
    void statusEquals_値ありの場合は等価述語を生成する() {
        @SuppressWarnings("unchecked")
        Path<Object> statusPath = mock(Path.class);
        Predicate equalPredicate = mock(Predicate.class);

        when(root.get("status")).thenReturn(statusPath);
        when(cb.equal(statusPath, UserStatus.ENABLED)).thenReturn(equalPredicate);

        Predicate result =
            UserSpecifications.statusEquals(UserStatus.ENABLED).toPredicate(root, query, cb);

        assertThat(result).isSameAs(equalPredicate);
    }

    @Test
    @DisplayName("statusEquals_nullの場合は常に真の述語を返す")
    void statusEquals_nullの場合は常に真の述語を返す() {
        Predicate conjunction = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(conjunction);

        Predicate result = UserSpecifications.statusEquals(null).toPredicate(root, query, cb);

        assertThat(result).isSameAs(conjunction);
        verifyNoInteractions(root);
    }

    @Test
    @DisplayName("Specificationとして生成される")
    void Specificationとして生成される() {
        Specification<User> spec = UserSpecifications.nameContains("x");
        assertThat(spec).isNotNull();
    }
}
