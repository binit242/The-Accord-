package com.scm.repsitories;

import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scm.entities.Contact;
import com.scm.entities.User;

@Repository
public interface ContactRepo extends JpaRepository<Contact, String> {
    // find the contact by user
    // custom finder method
    Page<Contact> findByUser(User user, Pageable pageable);

    

    // custom query method
    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId")
    List<Contact> findByUserId(@Param("userId") String userId);

    Page<Contact> findByUserAndNameContaining(User user, String namekeyword, Pageable pageable);

    Page<Contact> findByUserAndEmailContaining(User user, String emailkeyword, Pageable pageable);

    Page<Contact> findByUserAndPhoneNumberContaining(User user, String phonekeyword, Pageable pageable);
     
   @Query("SELECT COUNT(c) FROM Contact c WHERE c.user.userId = :userId AND c.favorite = true")
long countFavoriteContactsByUserId(@Param("userId") String userId);

@Query("SELECT COUNT(c) FROM Contact c WHERE c.user.userId = :userId")
long countContactsByUserId(@Param("userId") String userId);


@Query("SELECT COUNT(c) FROM Contact c WHERE c.user.userId = :userId AND " +
       "(c.websiteLink IS NOT NULL AND c.websiteLink <> '' " +
       "OR c.linkedInLink IS NOT NULL AND c.linkedInLink <> '' " +
       "OR c.facebookLink IS NOT NULL AND c.facebookLink <> '' " +
       "OR c.instaLink IS NOT NULL AND c.instaLink <> '')")
long countContactsWithAnySocialLink(@Param("userId") String userId);

@Query("SELECT c FROM Contact c WHERE c.user.userId = :userId AND c.favorite = true")
List<Contact> findFavoriteContactsByUserId(@Param("userId") String userId);

}


