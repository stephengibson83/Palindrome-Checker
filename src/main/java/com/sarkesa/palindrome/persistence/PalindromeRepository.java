package com.sarkesa.palindrome.persistence;

import com.sarkesa.palindrome.model.PalindromeCheck;

import java.util.List;

public interface PalindromeRepository {

    void save(PalindromeCheck palindromeCheck);

    List<PalindromeCheck> findAll();

}
