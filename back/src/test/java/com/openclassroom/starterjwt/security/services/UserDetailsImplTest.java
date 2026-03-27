package com.openclassroom.starterjwt.security.services;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    @Test
    void getAuthorities_ShouldReturnEmptyCollection() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .firstName("Test")
                .lastName("User")
                .password("secret")
                .admin(false)
                .build();

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void accountStatusFlags_ShouldAlwaysReturnTrue() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .firstName("Test")
                .lastName("User")
                .password("secret")
                .admin(false)
                .build();

        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void equals_ShouldBeTrue_WhenIdsAreEqual() {
        UserDetailsImpl left = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl right = UserDetailsImpl.builder().id(1L).build();

        assertEquals(left, right);
    }

    @Test
    void equals_ShouldBeFalse_WhenIdsAreDifferent() {
        UserDetailsImpl left = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl right = UserDetailsImpl.builder().id(2L).build();

        assertNotEquals(left, right);
    }

    @Test
    void equals_ShouldBeTrue_WhenSameObject() {
        // Test case: this == o (comparaison avec le même objet)
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .firstName("Test")
                .lastName("User")
                .password("secret")
                .admin(false)
                .build();

        // L'objet doit être égal à lui-même
        assertEquals(userDetails, userDetails);
        assertTrue(userDetails.equals(userDetails));
    }

    @Test
    void equals_ShouldBeFalse_WhenObjectIsNull() {
        // Test case: o == null (l'objet comparé est null)
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .build();

        // Un objet ne doit jamais être égal à null
        assertNotEquals(userDetails, null);
        assertFalse(userDetails.equals(null));
    }

    @Test
    void equals_ShouldBeFalse_WhenDifferentClass() {
        // Test case: getClass() != o.getClass() (l'objet n'est pas de la même classe)
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .build();

        // Un objet ne doit pas être égal à un objet d'une classe différente
        String differentClassObject = "not a UserDetailsImpl";
        assertNotEquals(userDetails, differentClassObject);
        assertFalse(userDetails.equals(differentClassObject));
    }

    @Test
    void equals_ShouldBeTrue_WhenBothHaveSameId() {
        // Test case: Objects.equals(id, user.id) retourne true
        UserDetailsImpl user1 = UserDetailsImpl.builder()
                .id(5L)
                .username("user1@test.com")
                .firstName("John")
                .lastName("Doe")
                .password("pass123")
                .admin(true)
                .build();

        UserDetailsImpl user2 = UserDetailsImpl.builder()
                .id(5L)
                .username("different@test.com")
                .firstName("Jane")
                .lastName("Smith")
                .password("pass456")
                .admin(false)
                .build();

        // Même si les autres propriétés diffèrent, l'ID identique doit rendre les objets égaux
        assertEquals(user1, user2);
        assertTrue(user1.equals(user2));
    }

    @Test
    void equals_ShouldBeFalse_WhenDifferentIds() {
        // Test case: Objects.equals(id, user.id) retourne false
        UserDetailsImpl user1 = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .build();

        UserDetailsImpl user2 = UserDetailsImpl.builder()
                .id(2L)
                .username("test@test.com")
                .build();

        // Des IDs différents doivent rendre les objets inégaux
        assertNotEquals(user1, user2);
        assertFalse(user1.equals(user2));
    }

    @Test
    void equals_ShouldBeFalse_WhenOneIdIsNull() {
        // Test case spécial: un ID est null, l'autre non
        UserDetailsImpl user1 = UserDetailsImpl.builder()
                .id(null)
                .username("test@test.com")
                .build();

        UserDetailsImpl user2 = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.com")
                .build();

        // Objets avec des IDs (l'un null, l'autre non) ne doivent pas être égaux
        assertNotEquals(user1, user2);
        assertFalse(user1.equals(user2));
    }

    @Test
    void equals_ShouldBeTrue_WhenBothIdsAreNull() {
        // Test case spécial: les deux IDs sont null
        UserDetailsImpl user1 = UserDetailsImpl.builder()
                .id(null)
                .username("test@test.com")
                .build();

        UserDetailsImpl user2 = UserDetailsImpl.builder()
                .id(null)
                .username("other@test.com")
                .build();

        // Deux objets avec IDs null doivent être égaux
        assertEquals(user1, user2);
        assertTrue(user1.equals(user2));
    }
    
    
}

